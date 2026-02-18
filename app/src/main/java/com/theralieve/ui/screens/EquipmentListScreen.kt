package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.theralieve.domain.model.CreditPlan
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDataItem
import com.theralieve.domain.model.SessionData
import com.theralieve.domain.model.UserPlan
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.SuccessDialog
import com.theralieve.ui.components.TheraAlertState
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraGradientBackgroundInActivity
import com.theralieve.ui.components.TheraPrimaryButton2
import com.theralieve.ui.components.TheraSecondaryButton2
import com.theralieve.ui.screens.creditSession.DurationPopupSelector
import com.theralieve.ui.screens.creditSession.EquipmentItemCard
import com.theralieve.ui.screens.creditSession.HorizontalSelectionPanel
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.formatPriceTo2Decimal

/* -------------------------------------------------------------------------
 * DATA MODELS
 * ------------------------------------------------------------------------- */

data class EquipmentType(
    val name: String,
    val imageRes: Int,
    val units: List<EquipmentUnit>,
    val defaultDuration: Int = 5,
    val price: Int = 20,
    val onDurationChange: (Int) -> Unit = {}
)

data class SelectedEquipment(
    val equipment: Equipment, val duration: Int, val price: Double = 0.0, val points: Int = 0
)


data class EquipmentUnit(
    val name: String, val status: EquipmentStatus
)

enum class EquipmentStatus {
    Occupied, Available, Cleaning, Offline
}

fun EquipmentUnit.statusLabel(): String = when (status) {
    EquipmentStatus.Occupied -> "In Use"
    EquipmentStatus.Available -> "Available"
    EquipmentStatus.Cleaning -> "Cleaning"
    EquipmentStatus.Offline -> "Offline"
}

fun EquipmentUnit.statusColor(): Color = when (status) {
    EquipmentStatus.Occupied -> Color(0xFFFF6A6A)
    EquipmentStatus.Available -> Color(0xFF18A439)
    EquipmentStatus.Cleaning -> Color(0xFFFFE066)
    EquipmentStatus.Offline -> Color(0xFF9FAEC0)
}

fun EquipmentUnit.borderColor(): Color = statusColor()


/* -------------------------------------------------------------------------
 * MAIN SCREEN
 * ------------------------------------------------------------------------- */

@Composable
fun EquipmentListScreen(
    error: String?,
    showDialog: String?,
    planExpired: Boolean,
    equipmentList: List<Equipment>,
    isMember: Boolean,
    isLoading: Boolean,
    userPlan: UserPlan? = null,
    memberName: String? = null,
    onBack: () -> Unit = {},
    sessionPlan: List<SessionData>? = null,
    creditPlan: List<CreditPlan>? = null,
    onSelect: (Equipment, Int?) -> Unit = { _, _ -> },
    onViewDetail: (Equipment) -> Unit = {},
    onStartMachine: (Equipment, Equipment, Int, String?) -> Unit = { _, _, _, _ -> },
    onProfileClicked: () -> Unit = {},
    onPlanData: () -> Unit = {},
    onErrorConsumed: () -> Unit = {},
    onYes: (List<SelectedEquipment>) -> Unit = { _ -> },
    onCreditData: () -> Unit = { },
    isCreditScreen: Boolean = false,
    onDialogConsumed: ()->Unit =  {}
) {

    LocalContext.current

//    if (planExpired) {
//        PlanExpiredDialog(onBack)
//    }

    var showSessionConflictDialog by remember { mutableStateOf(false) }

    if (showSessionConflictDialog) {
        AlertDialog(
            onDismissRequest = { showSessionConflictDialog = false },
            confirmButton = {
                TextButton(onClick = { showSessionConflictDialog = false }) {
                    Text("OK")
                }
            },
            title = {
                Text("Already Selected")
            },
            text = {
                Text(
                    "You have already selected this device with a different session time. " +
                            "You cannot add the same device multiple times.\n\n" +
                            "Please de-select the previous one to select this."
                )
            }
        )
    }


    val selectedEquipments = remember { mutableStateListOf<SelectedEquipment>() }

    TheraGradientBackgroundInActivity(onAutoLogout = onBack,enableInactivity=isMember) { alert ->

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
        }else if (equipmentList.isNullOrEmpty()) {
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
            Header(
                title = if(isMember) "Session Equipments" else "Equipment",
                isMember = isMember,
                memberName = memberName,
                userPlan = userPlan,
                onBack = onBack,
                onHome = onBack,
                showHome = true,
                onProfileClicked = onProfileClicked,
                onPlanData = onPlanData,
                onCreditData = onCreditData
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
//                    val list = if(isCreditScreen) equipmentList.filter { it.units.firstOrNull()?.sessionTime.isNullOrEmpty() } else if (isMember) equipmentList.filter { !it.units.firstOrNull()?.sessionTime.isNullOrEmpty() } else equipmentList
                    equipmentList.forEachIndexed { index, type ->

                        var selectedUnit by remember { mutableStateOf<Equipment?>(null) }


                        // Get equipment properties from first unit (all units in a type have same properties)
                        val firstUnit = type
                        val isOneMinuteAccording = firstUnit?.is_one_minute_according?.lowercase() == "yes"
                        val equipmentData = firstUnit?.equipment_data
                        val equipmentPrice = firstUnit?.equipment_price?.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
                        firstUnit?.equipment_time?.toIntOrNull() ?: 0
                        val isEquipmentInSession = firstUnit?.sessionTime?.toIntOrNull() ?: 0

                        // Determine initial duration and if selector should be shown
                        val shouldShowDurationSelector = when {
                            // For members, always show fixed equipment_time (no selector)
                            isMember -> isEquipmentInSession <= 0
                            // Single session + is_one_minute_according == "Yes" -> show selector
                            !isMember && isOneMinuteAccording -> true
                            // Single session + is_one_minute_according == "No" -> show selector with equipment_data
                            !isMember && !isOneMinuteAccording && !equipmentData.isNullOrEmpty() -> true
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
                                it.equipment.equipment_id == firstUnit?.equipment_id
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
                                        isMember = isMember,
                                        shouldShowDurationSelector = shouldShowDurationSelector,
                                        isOneMinuteAccording = isOneMinuteAccording,
                                        equipmentData = equipmentData,
                                        onStartMachine = { points ->
                                            if (selectedUnit == null) {
                                                alert.show("Please Select Equipment")
                                            } else {
                                                selectedUnit?.let { unit ->
                                                    onStartMachine(
                                                        type, unit, selectedDuration, points
                                                    )
                                                }
                                            }
                                        },
                                        hasNoActiveSession = hasNoActiveSession,
                                        userPlan = userPlan,
                                        alert = alert
                                    )
                                    Spacer(Modifier.width(20.dp))

                                    val isSelected = selectedEquipments.any {
                                        it.equipment.equipment_id == firstUnit?.equipment_id && it.equipment.sessionTime == firstUnit.sessionTime
                                    }
                                    if (!isSelected) {
                                        TheraSecondaryButton2(
                                            modifier = Modifier
                                                .height(60.dp)
                                                .width(140.dp),
                                            label = "Select"
                                        ) {
                                            val isSameDeviceDifferentSession = selectedEquipments.any {
                                                it.equipment.equipment_id == firstUnit?.equipment_id &&
                                                        it.equipment.sessionTime != firstUnit?.sessionTime
                                            }

                                            if(isSameDeviceDifferentSession){
                                                showSessionConflictDialog  = true
                                            }else {
                                                firstUnit?.let {
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

                                /*Text(
                                    text = "Selected Equipment",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "Review before proceeding",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(0.6f)
                                )*/

                            }

                            // ======================
                            // CONTENT
                            // ======================
                            if (selectedEquipments.isEmpty()) {

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
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
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    // Selected list
                                    selectedEquipments.forEach {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.Black.copy(0.1f))
                                        )
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
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    if (isCreditScreen) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.Black.copy(0.1f))
                                        )
                                    } else if (!isMember) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(Color.Black.copy(0.1f))
                                        )
                                    }


                                    Spacer(Modifier.height(12.dp))

                                    // TOTAL (Price OR Points)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        if (isCreditScreen) {
                                            Text(
                                                text = "Total",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else if (!isMember) {
                                            Text(
                                                text = "Total",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }


                                        val totalPoints = selectedEquipments.sumOf { it.points }
                                        val totalPrice = selectedEquipments.sumOf { it.price }

                                        if (isCreditScreen) {
                                            Text(
                                                text = "${totalPoints} Points",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TheraColorTokens.PrimaryDark
                                            )
                                        } else if (!isMember) {
                                            Text(
                                                text = if (isMember) "${totalPoints} Points"
                                                else "$ ${formatPriceTo2Decimal(totalPrice)}",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TheraColorTokens.PrimaryDark
                                            )
                                        }
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


/* -------------------------------------------------------------------------
 * EQUIPMENT CARD
 * ------------------------------------------------------------------------- */

@Composable
fun EquipmentItemCard(
    name: String, image: String, onClick: () -> Unit
) {
    Column(
        modifier = Modifier.throttledClickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NetworkImage(
            imageUrl = image,
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .padding(2.dp)
        )

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
                Text(
                    if (duration > 5) "$duration min" else " - - - ",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
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
        EquipmentListScreen(
            showDialog = null,
            error = null,
            planExpired = false,
            equipmentList = listOf(),
            isMember = true,
            isLoading = true
        )
    }
}
