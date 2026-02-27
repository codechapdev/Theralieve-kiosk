package com.theralieve.ui.screens.creditSession

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.theralieve.R
import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDataItem
import com.theralieve.domain.model.UserPlan
import com.theralieve.ui.components.InfoDialog
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.PlanDataButton
import com.theralieve.ui.components.SuccessDialog
import com.theralieve.ui.components.TheraAlertState
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraGradientBackgroundInActivity
import com.theralieve.ui.components.TheraPrimaryButton2
import com.theralieve.ui.components.TheraSecondaryButton2
import com.theralieve.ui.screens.SelectedEquipment
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.formatPriceTo2Decimal
import kotlinx.coroutines.delay

@Composable
fun EquipmentCreditListScreen(
    error: String?,
    showDialog: String?,
    userPlan: UserPlan?,
    memberName: String?,
    isLoading: Boolean?,
    creditPlan: CreditPlan?,
    hasSessionPlan: Boolean?,
    equipmentList: List<Equipment>,
    onBack: () -> Unit = {},
    onSelect: (Equipment, Int?) -> Unit = { _, _ -> },
    onViewDetail: (Equipment) -> Unit = {},
    onStartMachine: (Equipment, Int, String?) -> Unit = { _, _, _ -> },
    onProfileClicked: () -> Unit = {},
    onPlanData: () -> Unit = {},
    onErrorConsumed: () -> Unit = {},
    onDialogConsumed: () -> Unit = {},
    onYes: (List<SelectedEquipment>) -> Unit = { _ -> },
    onHome: () -> Unit = {},
    onCreditScreen: () -> Unit = {}
) {

    val isMember = true
    LocalContext.current

//    if (planExpired) {
//        PlanExpiredDialog(onBack)
//    }

    var showPurchaseCreditPlanDialog by remember { mutableStateOf(false) }
    if (showPurchaseCreditPlanDialog) {
        InfoDialog(
            title = "Purchase Credit Points",
            message = "You don't have any credit points please purchase one from our store",
            onDismiss = {
                showPurchaseCreditPlanDialog = false
                onCreditScreen()
            },
        )
    }
    val selectedEquipments = remember { mutableStateListOf<SelectedEquipment>() }

    TheraGradientBackgroundInActivity(onAutoLogout = onHome) { alert ->

        if (!error.isNullOrEmpty()) {
            LaunchedEffect(error) {
                alert.show(error)
                onErrorConsumed() // clear error after showing
            }
        }

        if (showDialog != null) {
            SuccessDialog(
                title = "Enjoy Your Session!",
                message = "Your session has been confirmed. Please proceed directly to your selected device.",
                onDismiss = {
                    selectedEquipments.clear()
                    onDialogConsumed()
                })
        }

        if (isLoading == true) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (equipmentList.isNullOrEmpty()) {
            Text(
                text = "No Equipment Found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with back button and logo
            SingleSessionHeader(
                onBack,
                userPlan = userPlan,
                hasSessionPlan = hasSessionPlan,
                creditPlan = creditPlan,
                onHome = onHome,
                onProfileClicked = onProfileClicked,
                memberName = memberName,
                onPlanData = onPlanData
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .verticalScroll(rememberScrollState())
                ) {
//                    val list = equipmentList.filter { it.units.firstOrNull()?.sessionTime.isNullOrEmpty() }
                    equipmentList.forEachIndexed { index, type ->

                        var selectedUnit by remember { mutableStateOf<Equipment?>(null) }


                        // Get equipment properties from first unit (all units in a type have same properties)
                        val firstUnit = type
                        val isOneMinuteAccording =
                            firstUnit?.is_one_minute_according?.lowercase() == "yes"
                        val equipmentData = firstUnit?.equipment_data
                        val equipmentPrice =
                            firstUnit?.equipment_price?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                                ?: 0.0
                        firstUnit?.equipment_time?.toIntOrNull() ?: 0
                        val isEquipmentInSession = firstUnit?.sessionTime?.toIntOrNull() ?: 0

                        // Determine initial duration and if selector should be shown
                        val shouldShowDurationSelector = when {
                            // For members, always show fixed equipment_time (no selector)
                            isMember -> isEquipmentInSession <= 0
                            // Single session + is_one_minute_according == "Yes" -> show selector
                            !isMember && isOneMinuteAccording -> true
                            // Single session + is_one_minute_according == "No" -> show selector with equipment_data
                            !equipmentData.isNullOrEmpty() -> true
                            else -> false
                        }


                        val initialDuration = when {

                            !equipmentData.isNullOrEmpty() -> equipmentData.first().equipment_time   // ALWAYS from plans

                            isMember && isEquipmentInSession > 0 -> isEquipmentInSession

                            !isMember && isOneMinuteAccording -> 10

                            else -> 10
                        }


                        var selectedDuration by remember { mutableStateOf(initialDuration) }

                        // Calculate price based on selected duration
                        val calculatePrice: (Int) -> Double = { duration ->
                            when {
                                // Single session + is_one_minute_according == "Yes" -> equipment_price * duration
                                !isMember && isOneMinuteAccording -> {
                                    val price =
                                        if (equipmentPrice > 0.0) equipmentPrice * duration else 0.0
                                    price
                                }
                                // Single session + is_one_minute_according == "No" -> use price from equipment_data
                                !isMember && !isOneMinuteAccording -> {
                                    (equipmentData?.find { it.equipment_time == duration }
                                        ?.let { item ->
                                            item.equipment_price.takeIf { it.isNotBlank() }
                                                ?.toDoubleOrNull()
                                                ?: item.equipment_points.takeIf { it.isNotBlank() }
                                                    ?.toDoubleOrNull() ?: 0.0
                                        } ?: 0.0) * duration
                                }
                                // Membership -> no price
                                else -> 0.0
                            }
                        }

                        // Calculate points based on selected duration (for members with no active session)
                        val calculatePoints: (Int) -> Int = { duration ->
                            when {
                                // For members with no active session, get points from equipment_data
                                isMember && isEquipmentInSession == 0 -> {
                                    if (equipmentData.isNullOrEmpty()) (firstUnit?.equipment_points
                                        ?: 1) * duration
                                    else equipmentData.find { it.equipment_time == duration }
                                        ?.let { item ->
                                            item.equipment_points.takeIf { it.isNotBlank() }
                                                ?.toIntOrNull() ?: 0
                                        } ?: 0
                                }

                                else -> 0
                            }
                        }

                        // Calculate price function that updates when dependencies change
                        val currentPrice = remember(
                            selectedDuration,
                            isMember,
                            isOneMinuteAccording,
                            equipmentPrice,
                            equipmentData
                        ) {
                            calculatePrice(selectedDuration)
                        }

                        var selectedPoints by remember { mutableStateOf(0) }
                        var selectedPrice by remember { mutableStateOf(currentPrice) }

                        // Update price when duration or equipment data changes
                        LaunchedEffect(
                            selectedDuration,
                            isMember,
                            isOneMinuteAccording,
                            equipmentPrice,
                            equipmentData
                        ) {
                            selectedPrice = calculatePrice(selectedDuration)
                            selectedPoints = calculatePoints(selectedDuration)
                        }

                        LaunchedEffect(selectedDuration, selectedPrice, selectedPoints) {
                            val index = selectedEquipments.indexOfFirst {
                                it.equipment.equipment_id == firstUnit?.equipment_id && it.equipment.sessionTime == firstUnit?.sessionTime
                            }

                            if (index != -1) {
                                selectedEquipments[index] = selectedEquipments[index].copy(
                                    duration = selectedDuration,
                                    price = selectedPrice,
                                    points = selectedPoints
                                )
                            }
                        }


                        val hasNoActiveSession = isMember && isEquipmentInSession == 0
                        val showPoints = hasNoActiveSession


                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    firstUnit?.equipment_name ?: "",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    EquipmentItemCard(
                                        name = firstUnit?.equipment_name ?: "",
                                        image = type.image,
                                        onClick = {
                                            // Allow selection of online/available equipment
                                            if (firstUnit?.status?.lowercase() == "idle") {
                                                selectedUnit = firstUnit
                                            }
                                            onViewDetail(firstUnit)
                                        })

                                    Spacer(Modifier.width(20.dp))

                                    HorizontalSelectionPanel(
                                        duration = selectedDuration,
                                        onDurationChanged = {
                                            selectedDuration = it
                                            selectedPrice = calculatePrice(it)
                                            selectedPoints = calculatePoints(it)
                                        },
                                        onBack = { onBack() },
                                        price = selectedPrice,
                                        points = selectedPoints,
                                        showPoints = showPoints,
                                        onSelect = {
                                            if (selectedUnit == null) {
                                                alert.show("Please Select Equipment")
                                            }
                                            selectedUnit?.let { unit ->
                                                onSelect(type, selectedDuration)
                                            }
                                        },
                                        unit = selectedUnit,
                                        isMember = true,
                                        shouldShowDurationSelector = shouldShowDurationSelector,
                                        hasNoActiveSession = hasNoActiveSession,
                                        isOneMinuteAccording = isOneMinuteAccording,
                                        equipmentData = equipmentData,
                                        onStartMachine = { points ->
                                            if (selectedUnit == null) {
                                                alert.show("Please Select Equipment")
                                            } else {
                                                selectedUnit?.let { unit ->
                                                    onStartMachine(
                                                        type, selectedDuration, points
                                                    )
                                                }
                                            }
                                        },
                                        alert = alert
                                    )
                                    Spacer(Modifier.width(20.dp))

                                    val isSelected = selectedEquipments.any {
                                        it.equipment.equipment_id == firstUnit.equipment_id && it.equipment.sessionTime == firstUnit.sessionTime
                                    }
                                    if (!isSelected) {
                                        TheraSecondaryButton2(
                                            modifier = Modifier
                                                .height(60.dp)
                                                .width(140.dp),
                                            label = "Select"
                                        ) {
                                            val creditPoints =
                                                userPlan?.totalCreditPoints?.toIntOrNull() ?: 0
                                            if(isLoading  == false) {
                                                firstUnit?.let {
                                                    if ((userPlan?.hasVipPlan == false || creditPoints == 0)) {
                                                        showPurchaseCreditPlanDialog = true
                                                    } else {
                                                        selectedEquipments.add(
                                                            SelectedEquipment(
                                                                equipment = it,
                                                                duration = selectedDuration,
                                                                price = selectedPrice,
                                                                points = selectedPoints
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        TheraPrimaryButton2(
                                            modifier = Modifier
                                                .height(60.dp)
                                                .width(140.dp),
                                            label = "Selected"
                                        ) {
                                            firstUnit?.let {
                                                selectedEquipments.removeAll { sel ->
                                                    sel.equipment.equipment_id == it.equipment_id && sel.equipment.sessionTime == it.sessionTime
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(20.dp))

                if(isLoading == false){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            // ======================
                            // HEADER
                            // ======================

                            Column(horizontalAlignment = Alignment.Start) {
                                val noEquipmentsSelected = selectedEquipments.isNullOrEmpty()
                                Text(
                                    text = if (noEquipmentsSelected) "PLEASE SELECT YOUR THERAPY(S) FOR USE TODAY" else "Selected Equipment",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = if (noEquipmentsSelected) "(Be sure to confirm device availability prior to selection)" else "Review before proceeding",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(0.6f)
                                )
                            }

                            // ======================
                            // CONTENT
                            // ======================
                            if (selectedEquipments.isEmpty()) {

                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "No equipment selected",
                                        fontSize = 16.sp,
                                        color = Color.Black.copy(0.6f)
                                    )
                                }

                            } else {

                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    // Selected list
                                    selectedEquipments.forEach {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                it.equipment.equipment_name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.Black.copy(0.1f))
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    // Divider
//                                    Box(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(1.dp)
//                                            .background(Color.Black.copy(0.1f))
//                                    )

                                    Spacer(Modifier.height(6.dp))

                                    // TOTAL (Price OR Points)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Total",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        val totalPoints = selectedEquipments.sumOf { it.points }
                                        selectedEquipments.sumOf { it.price }

                                        Text(
                                            text = "${totalPoints} Points",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TheraColorTokens.PrimaryDark
                                        )

                                    }
                                }
                            }

                            // ======================
                            // ACTIONS
                            // ======================

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                TheraSecondaryButton2(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    label = "Cancel"
                                ) {
                                    selectedEquipments.clear()
                                }

                                TheraPrimaryButton2(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    label = "Book Session"
                                ) {
                                    onYes(selectedEquipments)
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}


@Composable
private fun SingleSessionHeader(
    onBack: () -> Unit,
    userPlan: UserPlan?,
    creditPlan: CreditPlan?,
    hasSessionPlan: Boolean?,
    memberName: String?,
    onHome: () -> Unit,
    onProfileClicked: () -> Unit,
    onPlanData: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (hasSessionPlan == true) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp) // large for kiosk touch
                            .clip(CircleShape)
                            .background(Color.White)
                            .throttledClickable { onBack() }, contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A73E8), // Theralieve Blue
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(56.dp) // large for kiosk touch
                    .clip(CircleShape)
                    .background(Color.White)
                    .throttledClickable {
                        onProfileClicked()
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Back",
                    tint = Color(0xFF1A73E8), // Theralieve Blue
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),

                ) {
                Row {
                    Text(
                        text = "Welcome, ", style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = memberName ?: "Member",
                        color = TheraColorTokens.PrimaryDark,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                creditPlan?.let { plan ->
                    Row {
                        Text(
                            text = "Plan : ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TheraColorTokens.TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = plan.plan_name ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TheraColorTokens.TextSecondary,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            /*if((userPlan?.vipDiscount?:"0").toInt()>0) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFFFA000)  // Orange-gold
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "VIP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }*/

        }


        // Title
        Text(
            text = "Credit Equipment",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CreditPoints(
                modifier = Modifier, userPlan = userPlan, onClick = {})

            PlanDataButton { onPlanData() }

            Box(
                modifier = Modifier
                    .size(56.dp) // large for kiosk touch
                    .clip(CircleShape)
                    .background(Color.White)
                    .throttledClickable { onHome() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(32.dp)
                )
            }


        }
    }


}

@Composable
fun CreditPoints(
    modifier: Modifier = Modifier, userPlan: UserPlan?, onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(TheraColorTokens.Surface) // Red logout color
        .throttledClickable {
            onClick()
        }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "Credit Points : ${userPlan?.totalCreditPoints ?: "0"}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

/* -------------------------------------------------------------------------
 * EQUIPMENT CARD
 * ------------------------------------------------------------------------- */

@Composable
fun EquipmentItemCard(
    name: String,
    image: String,
    onClick: () -> Unit
) {
    var showOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(240.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // show overlay immediately
                        showOverlay = true

                        try {
                            // wait until finger is released
                            awaitRelease()
                        } finally {
                            // keep overlay visible briefly
                            delay(300)
                            showOverlay = false
                        }
                    },
                    onTap = {
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        NetworkImage(
            imageUrl = image,
            contentDescription = name,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit
        )

        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Detail",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}




/* -------------------------------------------------------------------------
 * VIEW DETAILS CARD
 * ------------------------------------------------------------------------- */

@Composable
fun ViewDetailCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .height(214.dp)
            .background(Color(0xFF1F2F4E), RoundedCornerShape(18.dp))
            .throttledClickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Icon(
            Icons.Default.Info,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "View Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )
    }
}


/* -------------------------------------------------------------------------
 * COUNTER BOX
 * ------------------------------------------------------------------------- */

@Composable
fun CounterBox(label: String, value: String) {
    Row(
        modifier = Modifier
            .background(TheraColorTokens.Background, RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label + " :", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            value,
            color = TheraColorTokens.PrimaryDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


/* -------------------------------------------------------------------------
 * SELECTION PANEL
 * ------------------------------------------------------------------------- */

@Composable
fun HorizontalSelectionPanel(
    duration: Int,
    onDurationChanged: (Int) -> Unit,
    onBack: () -> Unit,
    price: Double,
    onSelect: () -> Unit,
    points: Int = 0,  // NEW
    showPoints: Boolean = false,  // NEW
    isMember: Boolean = true,
    shouldShowDurationSelector: Boolean = true,
    isOneMinuteAccording: Boolean = false,
    equipmentData: List<EquipmentDataItem>? = null,
    unit: Equipment? = null,
    onStartMachine: (String?) -> Unit = {},
    hasNoActiveSession: Boolean = false,
    userPlan: UserPlan? = null, // NEW
    alert: TheraAlertState? = null
) {


    Column(
        modifier = Modifier
            .background(TheraColorTokens.Background, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // ===========================
        // TOP ROW (Duration – Dropdown – Price)
        // ===========================
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            // Duration
            Column {
                Text("Duration", color = Color.Black.copy(0.7f), fontSize = 18.sp)
//                    Text(
//                        if (duration > 5) "$duration min" else " - - - ",
//                        color = Color.Black,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold
//                    )
            }

            if (shouldShowDurationSelector) {
                DurationPopupSelector(
                    value = duration,
                    isMember = isMember,
                    hasNoActiveSession = hasNoActiveSession,  // NEW
                    onValueChange = { onDurationChanged(it ?: 10) },
                    modifier = Modifier.height(60.dp),
                    isOneMinuteAccording = isOneMinuteAccording,
                    equipmentData = equipmentData,
                    userPlan = userPlan
                )
            } else {
                // Show fixed duration for membership with is_one_minute_according == "Yes"
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$duration min", fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            // Show price for non-members, show points for members with no active session
            if (!isMember) {
                // Price for non-members
                Column(horizontalAlignment = Alignment.End) {
                    Text("Price", color = Color.Black.copy(0.7f), fontSize = 18.sp)
                    Text(
                        if (price > 0.0 && duration > 0) "$${
                            formatPriceTo2Decimal(price)
                        }" else "- - -",
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (showPoints) {
                // Points for members with no active session
                Column(horizontalAlignment = Alignment.End) {
                    Text("Points", color = Color.Black.copy(0.7f), fontSize = 18.sp)
                    Text(
                        if (points > 0 && duration > 0) "$points" else "- - -",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }

        /*TheraPrimaryButton(
            label = if (isMember) "Start Machine" else "Proceed", onClick = {
                if (isMember) {
                    if (unit != null) {
                        if(showPoints){
                            val totalPoints =  userPlan?.totalCreditPoints?.toIntOrNull()?:0
                            if(totalPoints >= points){
                                onStartMachine(points.toString())
                            }else{
                                alert?.show("Insufficient Credit Points")
                            }
                        }else {
                            onStartMachine(null)
                        }
                    }
                } else {
                    if (unit != null) {
                        onSelect()
                    }
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )*/

    }
}


@Composable
fun DurationPopupSelector(
    value: Int,
    isMember: Boolean,
    hasNoActiveSession: Boolean = false,  // NEW
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isOneMinuteAccording: Boolean = false,
    equipmentData: List<EquipmentDataItem>? = null,
    userPlan: UserPlan? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val availableDurations = remember(isOneMinuteAccording, equipmentData, isMember) {
        when {
            isOneMinuteAccording && !isMember -> (10..60 step 5).toList()
            !equipmentData.isNullOrEmpty() -> equipmentData.map { it.equipment_time }
            else -> listOf(10, 15, 20, 30, 40)
        }
    }

    Box(modifier) {

        // ---- Trigger ----
        Row(
            modifier = Modifier
                .height(60.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .clickable(enabled = !isMember || (isMember && hasNoActiveSession) || (isMember && !isOneMinuteAccording)) {
                    expanded = true
                }
                .padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (value > 0) "$value min" else "Select duration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TheraColorTokens.TextSecondary
            )
        }

        // ---- Popup ----
        if (expanded) {
            val listState = rememberLazyListState()
            val selectedIndex = availableDurations.indexOf(value).coerceAtLeast(0)

            // Scroll so selected item appears roughly in the center
            LaunchedEffect(Unit) {
                val centerOffset = 3 // number of items above selected
                listState.scrollToItem(
                    index = (selectedIndex - centerOffset).coerceAtLeast(0)
                )
            }

            Popup(
                alignment = Alignment.Center, onDismissRequest = { expanded = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 12.dp,
                    color = Color.White
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .width(160.dp)
                            .heightIn(max = 240.dp)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(availableDurations) { duration ->
                            DurationOption(
                                label = "$duration min", selected = duration == value, onClick = {
                                    onValueChange(duration)
                                    expanded = false
                                })
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DurationOption(
    label: String, selected: Boolean, isPlaceholder: Boolean = false, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent, RoundedCornerShape(12.dp)
            )
            .throttledClickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = when {
                selected -> MaterialTheme.colorScheme.primary
                isPlaceholder -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}


/* -------------------------------------------------------------------------
 * PREVIEW
 * ------------------------------------------------------------------------- */

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewEquipmentList() {
    TheraGradientBackground {
        EquipmentCreditListScreen(
            showDialog = null,
            error = null,
            userPlan = null,
            memberName = null,
            creditPlan = null,
            equipmentList = listOf(),
            hasSessionPlan = false,
            isLoading = true
        )
    }
}
