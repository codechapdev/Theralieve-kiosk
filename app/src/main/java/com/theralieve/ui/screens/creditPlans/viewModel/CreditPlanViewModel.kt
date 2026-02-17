package com.theralieve.ui.screens.creditPlans.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theralieve.data.storage.PreferenceManager
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.domain.model.Plan
import com.theralieve.domain.usecase.GetPlansUseCase
import com.theralieve.domain.usecase.VerifyMemberOrEmployeeUseCase
import com.theralieve.ui.viewmodel.MembershipListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditPlanViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val preferenceManager: PreferenceManager,
    private val verifyMemberOrEmployeeUseCase: VerifyMemberOrEmployeeUseCase
) : ViewModel() {

    data class ListUiState(
        val plans: List<Plan> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showQuestionnaire: Boolean = false,
        val isVerifying: Boolean = false,
        val verificationError: String? = null,
        val memberIdError: String? = null,
        val employeeIdError: String? = null,
        val isVerifyingMemberId: Boolean = false,
        val isVerifyingEmployeeId: Boolean = false,
        val isForEmployee: Boolean = false,
        val memberNo: String? = null,
        val employeeNo: String? = null,
        val membershipType: String? = "outside_member",
        val location: Location? = null,
        val locationEquipments: List<LocationEquipment> = emptyList(),
    )


    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private var membershipType: String? = null
    private var isForEmployee: Int? = null

    init {
        checkAndLoadPlans()
    }


    private fun checkAndLoadPlans() {
        viewModelScope.launch {
            val location = preferenceManager.getLocationData()?.firstOrNull()
            _uiState.update { it.copy(locationEquipments = location?.equipments?:emptyList()) }
            val isFitness = preferenceManager.getIsFitness()
            if (isFitness) {
                // Show questionnaire dialog
                _uiState.update { it.copy(showQuestionnaire = true, location = location) }
            } else {
                // Load plans with outside_member
                _uiState.update {
                    it.copy(
                        isForEmployee = false,
                        membershipType = "outside_member",
                        location = location
                    )
                }
                loadPlans(membershipType = "outside_member", isForEmployee = null)
            }
        }
    }

    fun onQuestionnaireSubmit(isMember: Boolean, memberNumber: String?, employeeNumber: String?) {
        viewModelScope.launch {
            if (isMember) {
                // User selected Yes - need to verify member/employee ID
                if (memberNumber.isNullOrBlank() && employeeNumber.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(verificationError = "Please enter Member No. or Employee No.")
                    }
                    return@launch
                }

                // Check if there are field-specific errors
                val hasMemberId = !memberNumber.isNullOrBlank()
                val hasEmployeeId = !employeeNumber.isNullOrBlank()
                val currentState = _uiState.value

                if ((hasMemberId && currentState.memberIdError != null) || (hasEmployeeId && currentState.employeeIdError != null)) {
                    // Don't proceed if there are validation errors
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isVerifying = true,
                        verificationError = null,
                        memberIdError = null,
                        employeeIdError = null
                    )
                }

                val customerId = preferenceManager.getCustomerId() ?: ""
                val verifyResult = verifyMemberOrEmployeeUseCase(
                    customerId = customerId,
                    memberId = memberNumber?.takeIf { it.isNotBlank() },
                    employeeId = employeeNumber?.takeIf { it.isNotBlank() })

                verifyResult.fold(onSuccess = { status ->
                    if (status == "valid") {
                        // Determine membership type and isForEmployee
                        membershipType = "club_member"
                        isForEmployee = if (employeeNumber.isNullOrBlank()) null else 1

                        // Hide questionnaire and load plans
                        _uiState.update {
                            it.copy(
                                showQuestionnaire = false,
                                isVerifying = false,
                                verificationError = null,
                                isForEmployee = isForEmployee == 1,
                                memberNo = memberNumber?.takeIf { it.isNotBlank() },
                                employeeNo = employeeNumber?.takeIf { it.isNotBlank() },
                                membershipType = membershipType
                            )
                        }
                        loadPlans(
                            membershipType = membershipType,
                            isForEmployee = isForEmployee,
                            forceRefresh = true
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                isVerifying = false,
                                verificationError = "Invalid Member No. or Employee No."
                            )
                        }
                    }
                }, onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            verificationError = exception.message ?: "Verification failed"
                        )
                    }
                })
            } else {
                // User selected No - use outside_member
                membershipType = "outside_member"
                isForEmployee = null

                _uiState.update {
                    it.copy(
                        showQuestionnaire = false,
                        isVerifying = false,
                        verificationError = null,
                        isForEmployee = false,
                        memberNo = null,
                        employeeNo = null,
                        membershipType = membershipType
                    )
                }
                loadPlans(
                    membershipType = membershipType,
                    isForEmployee = null,
                    forceRefresh = true
                )
            }
        }
    }

    fun onQuestionnaireCancel() {
        _uiState.update { it.copy(showQuestionnaire = false) }
    }

    fun loadPlans(membershipType: String?, isForEmployee: Int?, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Preserve existing plans that have discount data (not from cache)
            val existingPlansWithDiscountData = _uiState.value.plans.filter { plan ->
                val discount = plan.detail?.discount
                val discountType = plan.detail?.discount_type
                val employeeDiscount = plan.detail?.employee_discount
                val discountValidity = plan.detail?.discount_validity
                // Plan has discount data if any field is not empty string (empty string = from cache)
                discount != "" || discountType != "" || employeeDiscount != "" || discountValidity != ""
            }

            getPlansUseCase(
                customerId = preferenceManager.getCustomerId() ?: "",
                membershipType = membershipType,
                isForEmployee = isForEmployee,
                forceRefresh = forceRefresh
            ).fold(onSuccess = { newPlans ->
                // Merge: use new plans, but preserve existing plans with discount data
                val mergedPlans = mutableListOf<Plan>()
                val newPlanIds = newPlans.map { it.detail?.id.toString() }.toSet()

                // Add existing plans with discount data that aren't in new plans
                existingPlansWithDiscountData.forEach { existingPlan ->
                    val existingPlanId = existingPlan.detail?.id.toString()
                    if (!newPlanIds.contains(existingPlanId)) {
                        mergedPlans.add(existingPlan)
                    }
                }

                // Add/update with new plans
                newPlans.forEach { newPlan ->
                    val newPlanId = newPlan.detail?.id.toString()
                    // Check if we have an existing plan with discount data for this ID
                    val existingPlanWithDiscount = existingPlansWithDiscountData.firstOrNull {
                        it.detail?.id.toString() == newPlanId
                    }
                    // Prefer existing plan with discount data over new plan (which might be from cache)
                    if (existingPlanWithDiscount != null) {
                        mergedPlans.add(existingPlanWithDiscount)
                    } else {
                        mergedPlans.add(newPlan)
                    }
                }

                _uiState.update {
                    it.copy(
                        plans = mergedPlans.filter { it.detail?.plan_type?.contains("credit",true) == true && it.detail?.is_vip_plan == 1 }, isLoading = false
                    )
                }
            }, onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        plans = emptyList(), isLoading = false, error = exception.message
                    )
                }
            })
        }
    }

    fun updatePlan(plan: Plan) {
        _uiState.update { state ->
            val planId = plan.detail?.id?.toString()
            val updatedPlans = state.plans.map { existingPlan ->
                if (existingPlan.detail?.id.toString() == planId) {
                    plan // Replace with the updated plan that has discount data
                } else {
                    existingPlan
                }
            }
            state.copy(plans = updatedPlans)
        }
    }


    fun verifyMemberId(memberId: String) {
        if (memberId.isBlank()) {
            _uiState.update { it.copy(memberIdError = null, isVerifyingMemberId = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isVerifyingMemberId = true,
                    memberIdError = null,
                    employeeIdError = null // Clear employee error when verifying member
                )
            }

            val customerId = preferenceManager.getCustomerId() ?: ""
            val verifyResult = verifyMemberOrEmployeeUseCase(
                customerId = customerId, memberId = memberId, employeeId = null
            )

            verifyResult.fold(onSuccess = { status ->
                _uiState.update {
                    it.copy(
                        isVerifyingMemberId = false,
                        memberIdError = if (status == "valid") null else if(status == "already") "Member No. already used" else "Invalid Member No."

                    )
                }
            }, onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        isVerifyingMemberId = false,
                        memberIdError = exception.message ?: "Verification failed"
                    )
                }
            })
        }
    }

    fun verifyEmployeeId(employeeId: String) {
        if (employeeId.isBlank()) {
            _uiState.update { it.copy(employeeIdError = null, isVerifyingEmployeeId = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isVerifyingEmployeeId = true,
                    employeeIdError = null,
                    memberIdError = null // Clear member error when verifying employee
                )
            }

            val customerId = preferenceManager.getCustomerId() ?: ""
            val verifyResult = verifyMemberOrEmployeeUseCase(
                customerId = customerId, memberId = null, employeeId = employeeId
            )

            verifyResult.fold(onSuccess = { status ->
                _uiState.update {
                    it.copy(
                        isVerifyingEmployeeId = false,
                        employeeIdError = if (status == "valid") null else if(status == "already") "Employee No. already used" else "Invalid Employee No."
                    )
                }
            }, onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        isVerifyingEmployeeId = false,
                        employeeIdError = exception.message ?: "Verification failed"
                    )
                }
            })
        }
    }


}