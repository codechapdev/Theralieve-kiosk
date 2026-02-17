package com.theralieve.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.theralieve.ui.components.SuccessDialog
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.screens.AddOnPlanDetailScreen
import com.theralieve.ui.screens.AddonPlanCheckoutScreen
import com.theralieve.ui.screens.AddonPlanListScreen
import com.theralieve.ui.screens.CheckoutScreen
import com.theralieve.ui.screens.EquipmentDetailScreen
import com.theralieve.ui.screens.EquipmentListScreen
import com.theralieve.ui.screens.MemberLoginDialog
import com.theralieve.ui.screens.MemberRegistrationDialog
import com.theralieve.ui.screens.MembershipGridScreen
import com.theralieve.ui.screens.MyProfile
import com.theralieve.ui.screens.PlanDataScreen
import com.theralieve.ui.screens.PlanDetailScreen
import com.theralieve.ui.screens.WelcomeScreen
import com.theralieve.ui.screens.creditPacks.CreditPackListScreen
import com.theralieve.ui.screens.creditPacks.viewModel.CreditPackViewModel
import com.theralieve.ui.screens.creditPlans.CreditPlanListScreen
import com.theralieve.ui.screens.creditPlans.viewModel.CreditPlanViewModel
import com.theralieve.ui.screens.creditSession.EquipmentCreditListScreen
import com.theralieve.ui.screens.creditSession.viewModel.EquipmentCreditListViewModel
import com.theralieve.ui.screens.newSeePlan.NewSeePlansScreen
import com.theralieve.ui.screens.newSeePlan.viewModel.NewSeePlanViewModel
import com.theralieve.ui.screens.selectedMembership.SelectedMembershipScreen
import com.theralieve.ui.screens.selectedMembership.viewModel.SelectedMembershipViewModel
import com.theralieve.ui.screens.sessionPacks.SessionPackListScreen
import com.theralieve.ui.screens.sessionPacks.viewModel.SessionPackListViewModel
import com.theralieve.ui.screens.singleSession.CheckoutSingleSessionScreen
import com.theralieve.ui.screens.singleSession.SingleSelectedEquipmentScreen
import com.theralieve.ui.screens.singleSession.viewModel.CheckoutSingleSessionViewModel
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.viewmodel.AddonCreditPlansViewModel
import com.theralieve.ui.viewmodel.AddonPlanCheckoutViewModel
import com.theralieve.ui.viewmodel.AddonPlanDetailViewModel
import com.theralieve.ui.viewmodel.AddonSessionPlansViewModel
import com.theralieve.ui.viewmodel.CheckoutViewModel
import com.theralieve.ui.viewmodel.EquipmentDetailViewModel
import com.theralieve.ui.viewmodel.EquipmentListViewModel
import com.theralieve.ui.viewmodel.MemberLoginViewModel
import com.theralieve.ui.viewmodel.MembershipDetailViewModel
import com.theralieve.ui.viewmodel.MembershipListViewModel
import com.theralieve.ui.viewmodel.MyPlanViewModel
import com.theralieve.ui.viewmodel.PlanDataViewModel
import com.theralieve.ui.viewmodel.RegistrationViewModel
import com.theralieve.ui.viewmodel.WelcomeViewModel
import com.theralieve.utils.PaymentLauncherProvider
import kotlinx.coroutines.launch
import okhttp3.Route
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.WELCOME,
    preferenceManager: com.theralieve.data.storage.PreferenceManager
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

            /*WelcomeScreen(uiState = welcomeUiState, onNonMemberClick = {
                // Use cached data for smooth UX - equipment is preloaded on welcome screen
                navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=false")
//                navController.navigate(Routes.SINGLE_SESSION_SCREEN)
            }, onMembershipClick = {
                navController.navigate(Routes.MEMBERSHIP_LIST)
            }, onExistingMemberClick = {
                navController.navigate(Routes.MEMBER_LOGIN)
            })*/

            WelcomeScreen(uiState = welcomeUiState, onNewClick = {
                navController.navigate(Routes.NEW_SEE_PLAN)
            }, onExistingClick = {
                navController.navigate(Routes.MEMBER_LOGIN)
            })
        }

        composable(Routes.NEW_SEE_PLAN){

            val viewModel : NewSeePlanViewModel = hiltViewModel()
            val locationEquipments by viewModel.locationEquipments.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NewSeePlansScreen(
                uiState = uiState,
                locationEquipments = locationEquipments,
                onSingleClick = {
                    navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=false")
                },
                onSessionPackClick = {
                    navController.navigate(Routes.SESSION_PACK_LIST)
                },
                onPackClick = {
                    navController.navigate(Routes.CREDIT_PACK_LIST)
                },
                onPlanClick = {
                    navController.navigate(Routes.CREDIT_PLAN_LIST)
                },
                onHome = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
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
            val activity = LocalActivity.current as ComponentActivity
            val isMember = backStackEntry.arguments?.getBoolean("isMember") ?: false
            backStackEntry.arguments?.getBoolean("forceRefresh") ?: false
            val equipmentListViewModel: EquipmentListViewModel = hiltViewModel()
            Log.i("CheckoutViewModel", "activity name : ${activity::class.simpleName}")

            val checkoutViewModel: CheckoutSingleSessionViewModel = hiltViewModel(activity)

            LaunchedEffect(isMember) {
                equipmentListViewModel.setIsMember(isMember)
            }

            val uiState by equipmentListViewModel.uiState.collectAsStateWithLifecycle()
            val equipments by equipmentListViewModel.equipmentList.collectAsStateWithLifecycle()

            // If member has no session pack, go directly to credit equipment screen
            LaunchedEffect(uiState.isMember, uiState.isLoading, uiState.sessionPlan) {
                if (uiState.isMember && !uiState.isLoading) {
                    when {
                        (uiState.sessionPlan == null || uiState.sessionPlan.isNullOrEmpty()) && (uiState.creditPlan == null || uiState.creditPlan.isNullOrEmpty()) -> {
                            navController.navigate(Routes.PLAN_DATA) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        (uiState.sessionPlan == null || uiState.sessionPlan.isNullOrEmpty()) -> {
                            navController.navigate(Routes.EQUIPMENT_LIST_CREDIT) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            if (!(uiState.isMember && !uiState.isLoading && (uiState.sessionPlan == null || uiState.sessionPlan.isNullOrEmpty()))) {
                EquipmentListScreen(
                    error = uiState.error,
                    showDialog = uiState.showDialog,
                    planExpired = uiState.planExpired,
                    equipmentList = equipments,
                    isMember = uiState.isMember,
                    isLoading = uiState.isLoading,
                    userPlan = uiState.userPlan,
                    memberName = uiState.memberName,
                    sessionPlan = uiState.sessionPlan,
                    creditPlan = uiState.creditPlan,
                    onBack = { navController.popBackStack() },
                    onSelect = { unit, duration ->

                        Log.i("CheckoutViewModel", "on select() getting triggered here")

                        val equipmentName = URLEncoder.encode(
                            unit.equipment_name, StandardCharsets.UTF_8.toString()
                        )
                        val unitName =
                            URLEncoder.encode(unit.device_name, StandardCharsets.UTF_8.toString())

                        navController.navigate("${Routes.CHECKOUT}/$equipmentName/$unitName/$duration/${uiState.isMember}")
                    },
                    onViewDetail = { equipmentType ->
                        // Get equipment_id from the first unit in the equipment list
                        val equipmentId = equipmentType?.equipment_id
                        if (equipmentId != null) {
                            navController.navigate("${Routes.EQUIPMENT_DETAIL}/$equipmentId/${uiState.isMember}")
                        }
                    },
                    onStartMachine = { equipmentType, unit, duration, creditPoints ->
                        equipmentListViewModel.startMachine(
                            equipmentType, unit, duration, creditPoints
                        )
                    },
                    onProfileClicked = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onPlanData = {
                        navController.navigate(Routes.PLAN_DATA)
                    },
                    onErrorConsumed = {
                        equipmentListViewModel.onErrorConsumed()
                    },
                    onCreditData = {
                        navController.navigate(Routes.EQUIPMENT_LIST_CREDIT)
                    },
                    onYes = { selectedEquipments ->
                        Log.i(
                            "CheckoutViewModel",
                            "on Yes () getting triggered here .. ${selectedEquipments}"
                        )
                        if (uiState.isMember) {
                            equipmentListViewModel.startMachineWithSelectedEquipments(
                                selectedEquipments
                            )
                        } else {
                            if (!selectedEquipments.isNullOrEmpty()) {
                                checkoutViewModel.setCheckoutDataForList(selectedEquipments)
                                navController.navigate(Routes.INFO_SINGLE_SESSION_BEFORE_CHECKOUT)
                            }
                        }
                    },
                    onDialogConsumed = {
                        equipmentListViewModel.hideDialog()
                    }
                )
            }
        }


        composable(
            route = Routes.EQUIPMENT_LIST_CREDIT
        ) { backStackEntry ->

            val equipmentListViewModel: EquipmentCreditListViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                equipmentListViewModel.setIsMember(true)
            }

            val uiState by equipmentListViewModel.uiState.collectAsStateWithLifecycle()
            val equipments by equipmentListViewModel.equipmentList.collectAsStateWithLifecycle()

            EquipmentCreditListScreen(
                error = uiState.error,
                showDialog = uiState.showDialog,
                userPlan = uiState.userPlan,
                memberName = uiState.memberName,
                isLoading = uiState.isLoading,
                hasSessionPlan = !uiState.sessionPlan.isNullOrEmpty(),
                creditPlan = uiState.creditPlan?.firstOrNull(),
                equipmentList = equipments,
                onBack = {
                    if (!uiState.sessionPlan.isNullOrEmpty()) {
                        navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=true&forceRefresh=true") {
                            popUpTo(navController.graph.startDestinationId)
                        }
                    } else navController.popBackStack()

                },
                onSelect = { unit, duration ->
                    val equipmentName =
                        URLEncoder.encode(unit.equipment_name, StandardCharsets.UTF_8.toString())
                    val unitName =
                        URLEncoder.encode(unit.device_name, StandardCharsets.UTF_8.toString())

                    navController.navigate("${Routes.CHECKOUT}/$equipmentName/$unitName/$duration/${uiState.isMember}")
                },
                onViewDetail = { equipmentType ->
                    // Get equipment_id from the first unit in the equipment list
                    val equipmentId = equipmentType?.equipment_id
                    if (equipmentId != null) {
                        navController.navigate("${Routes.EQUIPMENT_DETAIL}/$equipmentId/${uiState.isMember}")
                    }
                },
                onStartMachine = { unit, duration, creditPoints ->
                    equipmentListViewModel.startMachine(unit, duration, creditPoints)
                },
                onProfileClicked = {
                    navController.navigate(Routes.PROFILE)
                },
                onPlanData = {
                    navController.navigate(Routes.PLAN_DATA)
                },
                onErrorConsumed = {
                    equipmentListViewModel.onErrorConsumed()
                },
                onYes = { selectedEquipments ->
                    // here check credit points as well
                    // ccheck credit points as well before it otherwise show error that we showing like insufficient credit points
                    equipmentListViewModel.startMachineWithSelectedEquipments(selectedEquipments)
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                },
                onCreditScreen = {
                    Routes.forCreditPurchase = true
                    navController.navigate(Routes.ADDON_PLAN_LIST_CREDIT)
                },
                onDialogConsumed ={
                    equipmentListViewModel.hideDialog()
                }
            )
        }


        composable(Routes.PLAN_DATA) {
            val viewModel: PlanDataViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            PlanDataScreen(uiState = uiState, onDismiss = {
                navController.popBackStack()
            }, onAddSession = {
                navController.navigate(Routes.ADDON_PLAN_LIST_SESSION)
            }, onAddCredit = {
                navController.navigate(Routes.ADDON_PLAN_LIST_CREDIT)
            }, onAutoRenewal = {
                viewModel.updateRenewal(it)
            }, onAutoRenewalCancel = { planId, reason ->
                viewModel.cancelVip(planId, reason)
            }, onHome = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }
            })
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

            backStackEntry.arguments?.getString("unitName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: ""

            val duration = backStackEntry.arguments?.getInt("duration") ?: 0
            val isMember = backStackEntry.arguments?.getBoolean("isMember") ?: false

            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            val equipmentListViewModel: EquipmentListViewModel = hiltViewModel()
            val equipmentListUiState by equipmentListViewModel.uiState.collectAsStateWithLifecycle()
            val pendingCheckoutEquipments by equipmentListViewModel.pendingCheckoutEquipments.collectAsStateWithLifecycle()

            PaymentLauncherProvider(checkoutViewModel) {
                // Load equipment data if not already loaded
                LaunchedEffect(isMember) {
                    equipmentListViewModel.setIsMember(isMember)
                }

                val equipmentItem = equipmentListUiState.equipment
                val unitItem = equipmentListUiState.equipment.find {
                    it.equipment_name == equipmentName
                }
//            val unit = unitItem?.units?.find { it.device_name == unitName }

                // When coming from non-member Proceed with multiple selected, use the list
                LaunchedEffect(pendingCheckoutEquipments) {
                    if (pendingCheckoutEquipments != null) {
                        checkoutViewModel.setCheckoutDataForList(pendingCheckoutEquipments!!, false)
                        equipmentListViewModel.clearPendingCheckoutEquipments()
                    }
                }

                val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(
                    equipmentItem, unitItem, pendingCheckoutEquipments, uiState.selectedEquipments
                ) {
                    if (pendingCheckoutEquipments == null && equipmentItem != null && unitItem != null && uiState.selectedEquipments == null) {
                        checkoutViewModel.setCheckoutData(unitItem, duration, isMember)
                    }
                }

                when {
                    equipmentListUiState.isLoading -> {
                        // Show loading state while equipment is being loaded
                        TheraGradientBackground {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
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

                    (equipmentItem == null || unitItem == null) && (uiState.selectedEquipments == null || !uiState.selectedEquipments.isNullOrEmpty()) -> {
                        // Equipment not found and no multi-selection - show error
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

                    (uiState.equipment != null && uiState.unit != null) || (uiState.selectedEquipments != null && !uiState.selectedEquipments.isNullOrEmpty()) -> {
                        CheckoutScreen(
                            equipment = uiState.equipment!!,
                            unit = uiState.unit,
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
                                message = "Your session has been confirmed. Please proceed directly to your selected device.",
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }


        composable(
            route = Routes.INFO_SINGLE_SESSION_BEFORE_CHECKOUT
        ) { backStackEntry ->
            val activity = LocalActivity.current as ComponentActivity

            Log.i("CheckoutViewModel", "activity name : ${activity::class.simpleName}")

            val checkoutViewModel: CheckoutSingleSessionViewModel = hiltViewModel(activity)


            val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()


            when {

                (!uiState.selectedEquipments.isNullOrEmpty()) -> {
                    SingleSelectedEquipmentScreen(
                        selectedEquipments = uiState.selectedEquipments
                        ?: emptyList(), onBack = {
                        navController.popBackStack()
                    }, onPurchase = { _ ->
                        navController.navigate(Routes.CHECKOUT_SINGLE_SESSION)
                    }, onHome = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    })

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

        composable(
            route = Routes.CHECKOUT_SINGLE_SESSION
        ) { backStackEntry ->
            val activity = LocalActivity.current as ComponentActivity

            Log.i("CheckoutViewModel", "activity name : ${activity::class.simpleName}")

            val checkoutViewModel: CheckoutSingleSessionViewModel = hiltViewModel(activity)


            val uiState by checkoutViewModel.uiState.collectAsStateWithLifecycle()
            PaymentLauncherProvider(checkoutViewModel) {
                when {

                    (!uiState.selectedEquipments.isNullOrEmpty()) -> {

                        CheckoutSingleSessionScreen(onPayNow = {
//                        checkoutViewModel.processPayment()
                        }, onBack = {
                            navController.popBackStack()
                        }, viewModel = checkoutViewModel)

                        if (uiState.showSuccessDialog) {
                            uiState.selectedEquipments?.map { it.equipment.equipment_name }
                                ?.joinToString(",") { it }
                            SuccessDialog(
                                title = "Enjoy Your Session!",
                                message = "Your session has been confirmed. Please proceed directly to your selected devices.",
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }


        composable(Routes.CREDIT_PACK_LIST){
            val viewModel: CreditPackViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                Log.d("MEMBERSHIP_LIST", "NavGraph: $uiState")
            }
            CreditPackListScreen(
                plans = uiState.plans,
                isLoading = uiState.isLoading,
                locationEquipments = uiState.locationEquipments,
                onPlanSelected = { plan, checked ->
                    val route = Routes.selectedMembershipRoute(
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
                onBack= {
                    navController.popBackStack()
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                showQuestionnaire = uiState.showQuestionnaire,
                isVerifying = uiState.isVerifying,
                verificationError = uiState.verificationError,
                memberIdError = uiState.memberIdError,
                employeeIdError = uiState.employeeIdError,
                isVerifyingMemberId = uiState.isVerifyingMemberId,
                isVerifyingEmployeeId = uiState.isVerifyingEmployeeId,
                onQuestionnaireSubmit = { isMember, memberNumber, employeeNumber ->
                    viewModel.onQuestionnaireSubmit(
                        isMember, memberNumber, employeeNumber
                    )
                },
                onQuestionnaireCancel = {
                    viewModel.onQuestionnaireCancel()
                    navController.popBackStack()
                },
                onMemberIdChange = { memberId ->
                    viewModel.verifyMemberId(memberId)
                },
                onEmployeeIdChange = { employeeId ->
                    viewModel.verifyEmployeeId(employeeId)
                },
                isForEmployee = uiState.isForEmployee,
                location = uiState.location
            )
        }

        composable(Routes.CREDIT_PLAN_LIST){
            val viewModel: CreditPlanViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                Log.d("MEMBERSHIP_LIST", "NavGraph: $uiState")
            }
            CreditPlanListScreen(
                plans = uiState.plans,
                isLoading = uiState.isLoading,
                locationEquipments = uiState.locationEquipments,
                onPlanSelected = { plan, checked ->
                    val route = Routes.selectedMembershipRoute(
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
                onBack ={
                    navController.popBackStack()
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                showQuestionnaire = uiState.showQuestionnaire,
                isVerifying = uiState.isVerifying,
                verificationError = uiState.verificationError,
                memberIdError = uiState.memberIdError,
                employeeIdError = uiState.employeeIdError,
                isVerifyingMemberId = uiState.isVerifyingMemberId,
                isVerifyingEmployeeId = uiState.isVerifyingEmployeeId,
                onQuestionnaireSubmit = { isMember, memberNumber, employeeNumber ->
                    viewModel.onQuestionnaireSubmit(
                        isMember, memberNumber, employeeNumber
                    )
                },
                onQuestionnaireCancel = {
                    viewModel.onQuestionnaireCancel()
                    navController.popBackStack()
                },
                onMemberIdChange = { memberId ->
                    viewModel.verifyMemberId(memberId)
                },
                onEmployeeIdChange = { employeeId ->
                    viewModel.verifyEmployeeId(employeeId)
                },
                isForEmployee = uiState.isForEmployee,
                location = uiState.location
            )
        }

        composable(Routes.SESSION_PACK_LIST){
            val viewModel: SessionPackListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                Log.d("MEMBERSHIP_LIST", "NavGraph: $uiState")
            }
            SessionPackListScreen(
                plans = uiState.plans,
                isLoading = uiState.isLoading,
                locationEquipments = uiState.locationEquipments,
                onPlanSelected = { plan, checked ->
                    val route = Routes.selectedMembershipRoute(
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
                onBack ={
                    navController.popBackStack()
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                showQuestionnaire = uiState.showQuestionnaire,
                isVerifying = uiState.isVerifying,
                verificationError = uiState.verificationError,
                memberIdError = uiState.memberIdError,
                employeeIdError = uiState.employeeIdError,
                isVerifyingMemberId = uiState.isVerifyingMemberId,
                isVerifyingEmployeeId = uiState.isVerifyingEmployeeId,
                onQuestionnaireSubmit = { isMember, memberNumber, employeeNumber ->
                    viewModel.onQuestionnaireSubmit(
                        isMember, memberNumber, employeeNumber
                    )
                },
                onQuestionnaireCancel = {
                    viewModel.onQuestionnaireCancel()
                    navController.popBackStack()
                },
                onMemberIdChange = { memberId ->
                    viewModel.verifyMemberId(memberId)
                },
                onEmployeeIdChange = { employeeId ->
                    viewModel.verifyEmployeeId(employeeId)
                },
                isForEmployee = uiState.isForEmployee,
                location = uiState.location
            )
        }

        composable(Routes.MEMBERSHIP_LIST) {

            val membershipListViewModel: MembershipListViewModel = hiltViewModel()
            val uiState by membershipListViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                Log.d("MEMBERSHIP_LIST", "NavGraph: $uiState")
            }
            MembershipGridScreen(
                plans = uiState.plans,
                isLoading = uiState.isLoading,
                locationEquipments = uiState.locationEquipments,
                onPlanSelected = { plan, checked ->
                    val route = Routes.selectedMembershipRoute(
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
                location = uiState.location
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

                            val route = Routes.selectedMembershipRoute(
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

        // SELECTED MEMBERSHIP
        // FROM HERE NAVIGATE TO RTEGISTRATION SCREEN WITH ALL THE DATA

        composable(
            route = "${Routes.SELECTED_MEMBERSHIP}/{planId}?isForEmployee={isForEmployee}?memberNo={memberNo}?employeeNo={employeeNo}?membershipType={membershipType}?isRenew={isRenew}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("isRenew") {
                    type = NavType.BoolType
                    defaultValue = false
                })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false

            val memberNo = backStackEntry.arguments?.getString("memberNo") ?: ""
            val employeeNo = backStackEntry.arguments?.getString("employeeNo") ?: ""
            val membershipType = backStackEntry.arguments?.getString("membershipType") ?: ""

            val selectedMembershipViewModel: SelectedMembershipViewModel = hiltViewModel()

            val uiState by selectedMembershipViewModel.uiState.collectAsStateWithLifecycle()
            rememberCoroutineScope()

            // Try to load plans if plan is not found
            LaunchedEffect(isRenew) {
                selectedMembershipViewModel.setIsRenew(isRenew = isRenew)
            }
            LaunchedEffect(planId, isForEmployee) {
                selectedMembershipViewModel.setData(isForEmployee = isForEmployee, planId = planId)
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
                    // Pass memberNo and employeeNo from questionnaire
                    selectedMembershipViewModel.setMemberAndEmployeeNumbers(
                        memberNo = memberNo, employeeNo = employeeNo
                    )
                    // Pass membership type and isForEmployee from questionnaire
                    selectedMembershipViewModel.setMembershipData(
                        membershipType = membershipType, isForEmployee = isForEmployee
                    )
                }
            }

            SelectedMembershipScreen(
                plan = uiState.plan,
                isForEmployee = uiState.isForEmployee,
                onBack = {
                    navController.popBackStack()
                },
                onPurchase = { plan ->
                    val route = Routes.registrationRoute(
                        plan?.detail?.id.toString(),
                        uiState.isForEmployee,
                        uiState.memberNo,
                        uiState.employeeNo,
                        uiState.membershipType,
                        uiState.isRenew
                    )
                    navController.navigate(route)
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                })

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
                })
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
                })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployeeFromNav =
                backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
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

            PaymentLauncherProvider(checkoutViewModel) {
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
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
            route = Routes.ADDON_PLAN_LIST_SESSION
        ) { backStackEntry ->

            val vm: AddonSessionPlansViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsStateWithLifecycle()

            AddonPlanListScreen(
                type = "session",
                plans = uiState.plans,
                locationEquipments = uiState.locationEquipments,
                vipDiscount = uiState.userProfile?.vipDiscount ?: "0",
                isForEmployee = uiState.isForEmployee,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onBack = { navController.popBackStack() },
                onSelectPlan = { plan, isRenew ->
                    val route = Routes.addonPlanCheckoutPreviewRoute(
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
            route = Routes.ADDON_PLAN_LIST_CREDIT
        ) { backStackEntry ->

            val vm: AddonCreditPlansViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsStateWithLifecycle()

            AddonPlanListScreen(
                type = "credit",
                plans = uiState.plans,
                locationEquipments = uiState.locationEquipments,
                vipDiscount = uiState.userProfile?.vipDiscount ?: "0",
                isForEmployee = uiState.isForEmployee,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onBack = { navController.popBackStack() },
                onSelectPlan = { plan, isRenew ->
                    val route = Routes.addonPlanCheckoutPreviewRoute(
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

            AddOnPlanDetailScreen(plan = uiState.plan, onBack = {
                navController.popBackStack()
            }, onEnroll = {

            }, onHome = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }



        composable(
            route = "${Routes.ADDON_PLAN_PURCHASE_PREVIEW}/{planId}?isForEmployee={isForEmployee}?isRenew={isRenew}",
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("isForEmployee") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("isRenew") {
                    type = NavType.BoolType
                    defaultValue = false
                })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false

            val selectedMembershipViewModel: SelectedMembershipViewModel = hiltViewModel()

            val uiState by selectedMembershipViewModel.uiState.collectAsStateWithLifecycle()
            rememberCoroutineScope()

            // Try to load plans if plan is not found
            LaunchedEffect(isRenew) {
                selectedMembershipViewModel.setIsRenew(isRenew = isRenew)
            }
            LaunchedEffect(planId, isForEmployee) {
                selectedMembershipViewModel.setData(isForEmployee = isForEmployee, planId = planId)

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
                    selectedMembershipViewModel.setMembershipData(
                        membershipType = "", isForEmployee = isForEmployee
                    )
                }
            }

            SelectedMembershipScreen(
                plan = uiState.plan,
                isForEmployee = uiState.isForEmployee,
                onBack = {
                    navController.popBackStack()
                },
                onPurchase = { plan ->
                    val route = Routes.addonPlanCheckoutRoute(
                        planId, isForEmployee, isRenew
                    )
                    navController.navigate(route)
                },
                onHome = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                })

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
                })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val isForEmployee = backStackEntry.arguments?.getBoolean("isForEmployee") ?: false
            val isRenew = backStackEntry.arguments?.getBoolean("isRenew") ?: false
            val vm: AddonPlanCheckoutViewModel = hiltViewModel()
            PaymentLauncherProvider(vm) {

                LaunchedEffect(isRenew) {
                    vm.setIsRenew(isRenew)
                }

                LaunchedEffect(planId) {
                    vm.setPlan(planId, isForEmployee)
                }

                AddonPlanCheckoutScreen(
                    onBack = { navController.popBackStack() },
                    onSuccessDismissed = {
                        // Go back to profile and refresh downstream (profile VM reloads on init)
                        if (Routes.forCreditPurchase) {
                            Routes.forCreditPurchase = false
                            navController.navigate(Routes.EQUIPMENT_LIST_CREDIT) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate("${Routes.EQUIPMENT_LIST}?isMember=true&forceRefresh=true") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    viewModel = vm
                )
            }
        }
    }
}

