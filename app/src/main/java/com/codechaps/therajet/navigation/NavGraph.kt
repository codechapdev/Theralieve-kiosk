package com.codechaps.therajet.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.codechaps.therajet.ui.components.SuccessDialog
import com.codechaps.therajet.ui.components.TheraGradientBackground
import com.codechaps.therajet.ui.components.TheraPrimaryButton
import com.codechaps.therajet.ui.screens.AddOnPlanDetailScreen
import com.codechaps.therajet.ui.screens.AddonPlanCheckoutScreen
import com.codechaps.therajet.ui.screens.AddonPlanListScreen
import com.codechaps.therajet.ui.screens.CheckoutScreen
import com.codechaps.therajet.ui.screens.EquipmentDetailScreen
import com.codechaps.therajet.ui.screens.EquipmentListScreen
import com.codechaps.therajet.ui.screens.MemberLoginDialog
import com.codechaps.therajet.ui.screens.MemberRegistrationDialog
import com.codechaps.therajet.ui.screens.MembershipGridScreen
import com.codechaps.therajet.ui.screens.MyProfile
import com.codechaps.therajet.ui.screens.PlanDataScreen
import com.codechaps.therajet.ui.screens.PlanDetailScreen
import com.codechaps.therajet.ui.screens.WelcomeScreen
import com.codechaps.therajet.ui.theme.TheraColorTokens
import com.codechaps.therajet.ui.viewmodel.AddonPlanCheckoutViewModel
import com.codechaps.therajet.ui.viewmodel.AddonPlanDetailViewModel
import com.codechaps.therajet.ui.viewmodel.AddonPlansViewModel
import com.codechaps.therajet.ui.viewmodel.CheckoutViewModel
import com.codechaps.therajet.ui.viewmodel.EquipmentDetailViewModel
import com.codechaps.therajet.ui.viewmodel.EquipmentListViewModel
import com.codechaps.therajet.ui.viewmodel.MemberLoginViewModel
import com.codechaps.therajet.ui.viewmodel.MembershipDetailViewModel
import com.codechaps.therajet.ui.viewmodel.MembershipListViewModel
import com.codechaps.therajet.ui.viewmodel.MyPlanViewModel
import com.codechaps.therajet.ui.viewmodel.PlanDataViewModel
import com.codechaps.therajet.ui.viewmodel.RegistrationViewModel
import com.codechaps.therajet.ui.viewmodel.WelcomeViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.WELCOME,
    preferenceManager: com.codechaps.therajet.data.storage.PreferenceManager
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {

        composable(Routes.WELCOME) {
            val welcomeViewModel: WelcomeViewModel = hiltViewModel()
            val welcomeUiState by welcomeViewModel.uiState.collectAsStateWithLifecycle()

            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(
                    Lifecycle.State.RESUMED
                ) {
                    welcomeViewModel.preloadData()
                }
            }

            WelcomeScreen(uiState = welcomeUiState, onNonMemberClick = {
                // Use cached data for smooth UX - equipment is preloaded on welcome screen
                navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=false")
            }, onMembershipClick = {
                navController.navigate(Routes.MEMBERSHIP_LIST)
            }, onExistingMemberClick = {
                navController.navigate(Routes.MEMBER_LOGIN)
            })
        }

        composable(
            route = "${Routes.EQUIPMENT_LIST}?isMember={isMember}&forceRefresh={forceRefresh}",
            arguments = listOf(navArgument("isMember") {
                type = NavType.BoolType
                defaultValue = false
            }, navArgument("forceRefresh") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val isMember = backStackEntry.arguments?.getBoolean("isMember") ?: false
            backStackEntry.arguments?.getBoolean("forceRefresh") ?: false
            val equipmentListViewModel: EquipmentListViewModel = hiltViewModel()

            LaunchedEffect(isMember) {
                equipmentListViewModel.setIsMember(isMember)
            }

            val uiState by equipmentListViewModel.uiState.collectAsStateWithLifecycle()
            val equipments by equipmentListViewModel.equipmentList.collectAsStateWithLifecycle()

            EquipmentListScreen(
                error = uiState.error,
                showDialog = uiState.showDialog,
                planExpired = uiState.planExpired,
                equipmentData = equipments,
                isMember = uiState.isMember,
                userPlan = uiState.userPlan,
                memberName = uiState.memberName,
                sessionPlan = uiState.sessionPlan,
                creditPlan = uiState.creditPlan,
                onBack = { navController.popBackStack() },
                onSelect = { equipmentType, unit, duration ->
                    val equipmentName =
                        URLEncoder.encode(equipmentType.name, StandardCharsets.UTF_8.toString())
                    val unitName =
                        URLEncoder.encode(unit.device_name, StandardCharsets.UTF_8.toString())

                    navController.navigate("${Routes.CHECKOUT}/$equipmentName/$unitName/$duration/${uiState.isMember}")
                },
                onViewDetail = { equipmentType ->
                    // Get equipment_id from the first unit in the equipment list
                    val equipmentId = equipmentType.units.firstOrNull()?.equipment_id
                    if (equipmentId != null) {
                        navController.navigate("${Routes.EQUIPMENT_DETAIL}/$equipmentId/${uiState.isMember}")
                    }
                },
                onStartMachine = { equipmentType, unit, duration, creditPoints ->
                    equipmentListViewModel.startMachine(equipmentType, unit, duration, creditPoints)
                },
                onProfileClicked = {
                    navController.navigate(Routes.PROFILE)
                },
                onPlanData = {
                    navController.navigate(Routes.PLAN_DATA)
                },
                onErrorConsumed ={
                    equipmentListViewModel.onErrorConsumed()
                }
                )
        }

        composable(Routes.PLAN_DATA) {
            val viewModel: PlanDataViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            PlanDataScreen(uiState = uiState, onDismiss = {
                navController.popBackStack()
            }, onAddSession = {
                navController.navigate(Routes.addonPlanListRoute("session"))
            }, onAddCredit = {
                navController.navigate(Routes.addonPlanListRoute("credit"))
            },
                onAutoRenewal = {
                    viewModel.updateRenewal(it)
                },
                onAutoRenewalCancel = {_,_ ->

                }
            )
        }

        composable(
            route = "${Routes.EQUIPMENT_DETAIL}/{equipmentId}/{isMember}", arguments = listOf(
                navArgument("equipmentId") { type = NavType.IntType },
                navArgument("isMember") { type = NavType.BoolType })
        ) { backStackEntry ->
            val equipmentId = backStackEntry.arguments?.getInt("equipmentId") ?: 0
            val isMember = backStackEntry.arguments?.getBoolean("isMember") ?: false
            val equipmentDetailViewModel: EquipmentDetailViewModel = hiltViewModel()

            LaunchedEffect(equipmentId, isMember) {
                equipmentDetailViewModel.setIsMember(isMember)
                if (equipmentId > 0) {
                    equipmentDetailViewModel.loadEquipmentDetails(equipmentId)
                }
            }

            val uiState by equipmentDetailViewModel.uiState.collectAsStateWithLifecycle()

            when {
                uiState.isLoading -> {
                    // Show loading indicator
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                uiState.error != null -> {
                    // Show error state
                    TheraGradientBackground {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = uiState.error ?: "Failed to load equipment details",
                                color = TheraColorTokens.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TheraPrimaryButton(
                                label = "Retry", onClick = {
                                    equipmentDetailViewModel.loadEquipmentDetails(equipmentId)
                                })
                            Spacer(modifier = Modifier.height(16.dp))
                            TheraPrimaryButton(
                                label = "Back", onClick = { navController.popBackStack() })
                        }
                    }
                }

                uiState.equipmentDetail != null -> {
                    EquipmentDetailScreen(
                        equipmentDetail = uiState.equipmentDetail!!,
                        isMember = uiState.isMember,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSelect = {
                            navController.popBackStack()
                        },
                        onHome = {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        })
                }
            }
        }

        composable(
            route = "${Routes.CHECKOUT}/{equipmentName}/{unitName}/{duration}/{isMember}",
            arguments = listOf(
                navArgument("equipmentName") { type = NavType.StringType },
                navArgument("unitName") { type = NavType.StringType },
                navArgument("duration") { type = NavType.IntType },
                navArgument("isMember") { type = NavType.BoolType })
        ) { backStackEntry ->
            val equipmentName = backStackEntry.arguments?.getString("equipmentName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: ""

            val unitName = backStackEntry.arguments?.getString("unitName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: ""

            val duration = backStackEntry.arguments?.getInt("duration") ?: 0
            val isMember = backStackEntry.arguments?.getBoolean("isMember") ?: false

            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            val equipmentListViewModel: EquipmentListViewModel = hiltViewModel()
            val equipmentListUiState by equipmentListViewModel.uiState.collectAsStateWithLifecycle()

            // Load equipment data if not already loaded
            LaunchedEffect(isMember) {
                equipmentListViewModel.setIsMember(isMember)
            }

            val equipmentItem =
                equipmentListUiState.equipment.firstOrNull { it.name == equipmentName }
            val unitItem = equipmentItem?.units?.firstOrNull { it.device_name == unitName }

            LaunchedEffect(equipmentItem, unitItem) {
                if (equipmentItem != null && unitItem != null) {
                    checkoutViewModel.setCheckoutData(equipmentItem, unitItem, duration, isMember)
                }
            }

            val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

            when {
                equipmentListUiState.isLoading -> {
                    // Show loading state while equipment is being loaded
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                equipmentListUiState.error != null -> {
                    // Show error state
                    TheraGradientBackground {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = equipmentListUiState.error ?: "Failed to load equipment",
                                color = TheraColorTokens.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TheraPrimaryButton(
                                label = "Back", onClick = { navController.popBackStack() })
                        }
                    }
                }

                equipmentItem == null || unitItem == null -> {
                    // Equipment not found - show error
                    TheraGradientBackground {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Equipment not found. Please try again.",
                                color = TheraColorTokens.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TheraPrimaryButton(
                                label = "Back", onClick = { navController.popBackStack() })
                        }
                    }
                }

                uiState.equipment != null && uiState.unit != null -> {
                    CheckoutScreen(
                        equipment = uiState.equipment!!,
                        unit = uiState.unit!!,
                        durationMinutes = uiState.durationMinutes,
                        onPayNow = {
//                        checkoutViewModel.processPayment()
                        },
                        onBack = {
                            navController.popBackStack()
                        })

                    if (uiState.showSuccessDialog) {
                        SuccessDialog(
                            title = "Enjoy Your Session!",
                            message = "Your session has been confirmed. Please proceed directly to your selected device ${uiState.unit?.device_name}.",
                            onDismiss = {
                                checkoutViewModel.dismissSuccessDialog()
                                navController.navigate(Routes.WELCOME) {
                                    popUpTo(Routes.WELCOME) { inclusive = true }
                                }
                            })
                    }
                }

                else -> {
                    // Show loading state while waiting for checkout data to be set
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        composable(Routes.MEMBERSHIP_LIST) {

            val membershipListViewModel: MembershipListViewModel = hiltViewModel()
            val uiState by membershipListViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                Log.d("MEMBERSHIP_LIST", "NavGraph: $uiState")
            }
            MembershipGridScreen(
                plans = uiState.plans,
                onPlanSelected = { plan,checked ->
                    val route = Routes.registrationRoute(
                        plan.detail?.id.toString(),
                        uiState.isForEmployee,
                        uiState.memberNo,
                        uiState.employeeNo,
                        uiState.membershipType,
                        checked
                    )
                    navController.navigate(route)
                },
                onViewDetail = { plan ->
                    val route = Routes.membershipDetailRoute(
                        plan.detail?.id.toString(),
                        uiState.isForEmployee,
                        uiState.memberNo,
                        uiState.employeeNo,
                        uiState.membershipType
                    )
                    navController.navigate(route)
//                    navController.navigate("${Routes.MEMBERSHIP_DETAIL}/${plan.detail?.id}")
                },
                onBack = { navController.popBackStack() },
                showQuestionnaire = uiState.showQuestionnaire,
                isVerifying = uiState.isVerifying,
                verificationError = uiState.verificationError,
                memberIdError = uiState.memberIdError,
                employeeIdError = uiState.employeeIdError,
                isVerifyingMemberId = uiState.isVerifyingMemberId,
                isVerifyingEmployeeId = uiState.isVerifyingEmployeeId,
                onQuestionnaireSubmit = { isMember, memberNumber, employeeNumber ->
                    membershipListViewModel.onQuestionnaireSubmit(
                        isMember, memberNumber, employeeNumber
                    )
                },
                onQuestionnaireCancel = {
                    membershipListViewModel.onQuestionnaireCancel()
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onMemberIdChange = { memberId ->
                    membershipListViewModel.verifyMemberId(memberId)
                },
                onEmployeeIdChange = { employeeId ->
                    membershipListViewModel.verifyEmployeeId(employeeId)
                },
                isForEmployee = uiState.isForEmployee,
                locationName = uiState.locationName ?: "XYZ"
            )
        }

        composable(
            route = "${Routes.MEMBERSHIP_DETAIL}/{planId}?isForEmployee={isForEmployee}?memberNo={memberNo}?employeeNo={employeeNo}?membershipType={membershipType}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val memberNo = backStackEntry.arguments?.getString("memberNo") ?: ""
            val employeeNo = backStackEntry.arguments?.getString("employeeNo") ?: ""
            val membershipType = backStackEntry.arguments?.getString("membershipType") ?: ""

            val membershipDetailViewModel: MembershipDetailViewModel = hiltViewModel()
            LaunchedEffect(planId, isForEmployee) {
                membershipDetailViewModel.loadPlans(planId)
            }

            val uiState by membershipDetailViewModel.uiState.collectAsStateWithLifecycle()

            when {
                uiState.plan == null -> {
                    // Show loading or error state
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                uiState.plan != null -> {
                    val plan = uiState.plan!!
                    PlanDetailScreen(
                        plan = plan,
                        onBack = { navController.popBackStack() },
                        onEnroll = {
                            val route = Routes.registrationRoute(
                                plan.detail?.id.toString(),
                                isForEmployee,
                                memberNo,
                                employeeNo,
                                membershipType,
                                false
                            )
                            navController.navigate(route)
//                            navController.navigate("${Routes.REGISTRATION}/${plan.detail?.id}?isForEmployee=${isForEmployee}")
                        },
                        onHome = {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        })
                }
            }
        }

        composable(
            route = "${Routes.REGISTRATION}/{planId}?isForEmployee={isForEmployee}?memberNo={memberNo}?employeeNo={employeeNo}?membershipType={membershipType}?isRenew={isRenew}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("isRenew") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false

            val memberNo = backStackEntry.arguments?.getString("memberNo") ?: ""
            val employeeNo = backStackEntry.arguments?.getString("employeeNo") ?: ""
            val membershipType = backStackEntry.arguments?.getString("membershipType") ?: ""

            val registrationViewModel: RegistrationViewModel = hiltViewModel()

            val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()

            // Try to load plans if plan is not found
            LaunchedEffect(isRenew) {
                registrationViewModel.setIsRenew(isRenew = isRenew)
            }
            LaunchedEffect(planId, isForEmployee) {
                Log.d(
                    "NavGraph",
                    "REGISTRATION: Plan not found, loading plans for planId: $planId || isForEmployee  $isForEmployee ......"
                )
                registrationViewModel.setData(isForEmployee = isForEmployee, planId = planId)

            }

            // Set plan and other data when available
            // Check if plan has discount data - if not, force refresh from API
            LaunchedEffect(uiState.plan) {
                // Don't set plan while loading (might be refreshing)
                if (uiState.isLoading) {
                    return@LaunchedEffect
                }

                val currentPlan = uiState.plan

                if (currentPlan != null) {
                    registrationViewModel.setPlan(currentPlan)
                    // Pass memberNo and employeeNo from questionnaire
                    registrationViewModel.setMemberAndEmployeeNumbers(
                        memberNo = memberNo, employeeNo = employeeNo
                    )
                    // Pass membership type and isForEmployee from questionnaire
                    registrationViewModel.setMembershipData(
                        membershipType = membershipType, isForEmployee = isForEmployee
                    )
                }
            }

            MemberRegistrationDialog(
                state = uiState.formState,
                isLoading = uiState.isLoading,
                registrationError = uiState.error,
                onStateChanged = { registrationViewModel.updateFormState(it) },
                onRegister = {
                    coroutineScope.launch {
                        val customerId = runCatching {
                            preferenceManager.getCustomerId()
                        }.getOrNull()
                        if (customerId != null) {
                            registrationViewModel.registerMember(customerId)
                        } else {
                            // Handle case where customer ID is not available
                            registrationViewModel.updateFormState(
                                uiState.formState.copy(
                                    emailError = "Customer session expired. Please login again."
                                )
                            )
                        }
                    }
                },
                onClose = {
                    registrationViewModel.resetForm()
                    navController.popBackStack()
                })

            // Handle registration success - check if free plan or regular checkout
            if (uiState.registrationSuccess) {
                LaunchedEffect(Unit) {
                    // Store values before resetting
                    val isFreePlan = uiState.isFreePlan
                    val freePlanPaymentSuccess = uiState.freePlanPaymentSuccess

                    Log.d(
                        "NavGraph",
                        "Registration successful. isFreePlan: $isFreePlan, freePlanPaymentSuccess: $freePlanPaymentSuccess"
                    )
                    registrationViewModel.resetRegistrationSuccess()

                    if (isFreePlan && freePlanPaymentSuccess) {
                        // Free plan - skip checkout, navigate directly to equipment list
                        Log.d(
                            "NavGraph", "Free plan payment successful, navigating to equipment list"
                        )
                        navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=true&forceRefresh=true") {
                            popUpTo(Routes.WELCOME) { inclusive = false }
                        }
                    } else if (!isFreePlan) {
                        // Regular plan with price - store plan in MembershipListViewModel before navigating
                        // This ensures the plan with discount data is available in checkout
                        uiState.plan
                        val isForEmployeeToStore = uiState.isForEmployee
                        // Pass isForEmployee as navigation argument to ensure it's retained
                        navController.navigate("${Routes.MEMBERSHIP_CHECKOUT}/${planId}?isForEmployee=${isForEmployeeToStore}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}?isRenew=${isRenew}") {
                            popUpTo(Routes.WELCOME) { inclusive = false }
                        }
                    }
                }
            }
        }



        composable(
            route = "${Routes.MEMBERSHIP_CHECKOUT}/{planId}?isForEmployee={isForEmployee}?memberNo={memberNo}?employeeNo={employeeNo}?membershipType={membershipType}?isRenew={isRenew}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("isRenew") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployeeFromNav = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false
            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            val membershipListViewModel: MembershipListViewModel = hiltViewModel()
            val membershipListUiState by membershipListViewModel.uiState.collectAsStateWithLifecycle()
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            val registrationUiState by registrationViewModel.uiState.collectAsStateWithLifecycle()

            // Get plan details - prioritize registration plan (we just came from registration)
            // If registration plan exists, use it directly since we know it's the correct one
            val plan = registrationUiState.plan
                ?: membershipListUiState.plans.firstOrNull { it.detail?.id.toString() == planId }

            // If plan is still not found, reload plans from API with correct parameters
            LaunchedEffect(isRenew) {
                checkoutViewModel.setIsRenew(isRenew)
            }

             LaunchedEffect(planId) {
                checkoutViewModel.setPlanCheckoutData(planId, isForEmployeeFromNav)
            }


            val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

            when {
                plan == null && uiState.plan == null -> {
                    // Plan not loaded yet or not found
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                uiState.plan != null -> {
                    LaunchedEffect(Unit) {
                        Log.d(
                            "NavGraph",
                            "MEMBERSHIP_CHECKOUT: Showing CheckoutScreen for plan payment"
                        )
                    }
                    // Show checkout screen for plan payment (not equipment-based)
                    // CheckoutScreen handles payment internally via LaunchedEffect
                    CheckoutScreen(
                        equipment = null,
                        unit = null,
                        durationMinutes = 0,
                        plan = uiState.plan,
                        onPayNow = {
                            // This is called after successful payment dialog is dismissed
                            Log.d(
                                "NavGraph",
                                "MEMBERSHIP_CHECKOUT: Payment successful, dialog dismissed"
                            )
                        },
                        onBack = {
                            Log.d("NavGraph", "MEMBERSHIP_CHECKOUT: onBack called")
                            navController.popBackStack()
                        },
                        viewModel = checkoutViewModel
                    )

                    if (uiState.showSuccessDialog) {
                        SuccessDialog(
                            title = "Payment Successful!",
                            message = "Your payment has been processed successfully. You can now select your equipment.",
                            onDismiss = {
                                checkoutViewModel.dismissSuccessDialog()
                                // Navigate to equipment list with forceRefresh=true to clear cache and reload
                                // This ensures we get equipment data according to the plan after payment
                                navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=true&forceRefresh=true") {
                                    popUpTo(Routes.WELCOME) { inclusive = false }
                                }
                            })
                    }
                }

                else -> {
                    // Show loading state
                    TheraGradientBackground {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        composable(Routes.MEMBER_LOGIN) {
            val memberLoginViewModel: MemberLoginViewModel = hiltViewModel()
            val uiState by memberLoginViewModel.uiState.collectAsStateWithLifecycle()

            MemberLoginDialog(
                state = uiState.formState,
                onStateChanged = { memberLoginViewModel.updateFormState(it) },
                onLogin = {
                    memberLoginViewModel.login()
                },
                onClose = {
                    memberLoginViewModel.resetForm()
                    navController.popBackStack()
                })

            if (uiState.loginSuccess) {
                LaunchedEffect(Unit) {
                    memberLoginViewModel.resetLoginSuccess()
                    navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=true&forceRefresh=true") {
                        popUpTo(Routes.WELCOME) { inclusive = false }
                    }
                }
            }
        }

        composable(Routes.PROFILE) {
            val myPlanViewModel: MyPlanViewModel = hiltViewModel()
            val uiState by myPlanViewModel.uiState.collectAsStateWithLifecycle()
            MyProfile(uiState = uiState, onBackClick = {
                navController.popBackStack()
            }, onLogout = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        composable(
            route = "${Routes.ADDON_PLAN_LIST}/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val vm: AddonPlansViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(type) {
                vm.load(type)
            }

            AddonPlanListScreen(
                type = type,
                plans = uiState.plans,
                vipDiscount = uiState.userProfile?.vipDiscount ?: "0",
                isForEmployee = uiState.isForEmployee,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onBack = { navController.popBackStack() },
                onSelectPlan = { plan,isRenew ->
                    val route = Routes.addonPlanCheckoutRoute(
                        planId = plan.detail?.id?.toString() ?: "",
                        isForEmployee = uiState.isForEmployee,
                        isRenew = isRenew
                    )
                    navController.navigate(route)
                },
                onViewDetail = {
                    navController.navigate("${Routes.ADDON_PLAN_DETAIL}/${it.detail?.id}")
                })
        }

        composable(
            route = "${Routes.ADDON_PLAN_DETAIL}/{planId}",
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val vm: AddonPlanDetailViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(planId) {
                vm.load(planId)
            }

            AddOnPlanDetailScreen(
                plan = uiState.plan,
                onBack = {
                    navController.popBackStack()
                },
                onEnroll = {

                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                )
        }

        composable(
            route = "${Routes.ADDON_PLAN_CHECKOUT}/{planId}?isForEmployee={isForEmployee}?isRenew={isRenew}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("isRenew") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false
            val vm: AddonPlanCheckoutViewModel = hiltViewModel()


            LaunchedEffect( isRenew) {
                vm.setIsRenew(isRenew)
            }

            LaunchedEffect(planId) {
                vm.setPlan(planId, isForEmployee)
            }

            AddonPlanCheckoutScreen(
                onBack = { navController.popBackStack() },
                onSuccessDismissed = {
                    // Go back to profile and refresh downstream (profile VM reloads on init)
                    navController.popBackStack(Routes.EQUIPMENT_LIST, inclusive = false)
                },
                viewModel = vm
            )
        }
    }
}

