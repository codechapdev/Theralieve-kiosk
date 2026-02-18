package com.theralieve.ui.screens.singleSession

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.theralieve.domain.model.Equipment
import com.theralieve.domain.model.EquipmentDataItem
import com.theralieve.domain.model.UserPlan
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.SuccessDialog
import com.theralieve.ui.components.TheraAlertState
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.EquipmentStatusHelper
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.formatPriceTo2Decimal
import kotlinx.coroutines.launch

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
fun SingleSessionScreen(
    error: String?,
    showDialog: String?,
    equipmentData: List<Equipment>,
    onBack: () -> Unit = {},
    onSelect: (Equipment, Equipment, Int?) -> Unit = { _, _, _ -> },
    onViewDetail: (Equipment) -> Unit = {},
    onStartMachine: (Equipment, Equipment, Int, String?) -> Unit = { _, _, _, _ -> },
    onErrorConsumed: () -> Unit = {}
) {

    TheraGradientBackground { alert ->

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
                    onBack()
                })
        }

        if (equipmentData.isNullOrEmpty()) {
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
            SingleSessionHeader(onBack = onBack, onHome = onBack)

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())

            ) {
                equipmentData.forEachIndexed { index, type ->
                    var selectedUnit by remember { mutableStateOf<Equipment?>(null) }

                    // Auto-select first available (online) unit when equipment data loads

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
                        // Single session + is_one_minute_according == "Yes" -> show selector
                        isOneMinuteAccording -> true
                        // Single session + is_one_minute_according == "No" -> show selector with equipment_data
                        !isOneMinuteAccording && !equipmentData.isNullOrEmpty() -> true
                        else -> false
                    }

                    val initialDuration = when {
                        !equipmentData.isNullOrEmpty() -> equipmentData.first().equipment_time   // ALWAYS from plans
                        isEquipmentInSession > 0 -> isEquipmentInSession
                        isOneMinuteAccording -> 10
                        else -> 10
                    }


                    var selectedDuration by remember { mutableStateOf(initialDuration) }

                    // Calculate price based on selected duration
                    val calculatePrice: (Int) -> Double = { duration ->
                        when {
                            // Single session + is_one_minute_according == "Yes" -> equipment_price * duration
                            isOneMinuteAccording -> {
                                val price =
                                    if (equipmentPrice > 0.0) equipmentPrice * duration else 0.0
                                price
                            }
                            // Single session + is_one_minute_according == "No" -> use price from equipment_data
                            !isOneMinuteAccording -> {
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


                    // Calculate price function that updates when dependencies change
                    val currentPrice = remember(
                        selectedDuration, isOneMinuteAccording, equipmentPrice, equipmentData
                    ) {
                        calculatePrice(selectedDuration)
                    }

                    var selectedPrice by remember { mutableStateOf(currentPrice) }

                    // Update price when duration or equipment data changes
                    LaunchedEffect(
                        selectedDuration, isOneMinuteAccording, equipmentPrice, equipmentData
                    ) {
                        selectedPrice = calculatePrice(selectedDuration)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White, RoundedCornerShape(24.dp)
                            )
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = type.equipment_name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 20.dp)
                            ) {

                                val rowState = rememberLazyListState()
                                val scope = rememberCoroutineScope()


                                val showLeftArrow by remember {
                                    derivedStateOf { rowState.canScrollBackward }
                                }

                                val showRightArrow by remember {
                                    derivedStateOf { rowState.canScrollForward }
                                }


                                LazyRow(
                                    state = rowState,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {


                                    item {
                                        ViewDetailCard { onViewDetail(type) }
                                    }
                                }


                                if (showLeftArrow) {
                                    Surface(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .align(Alignment.CenterStart)
                                            .throttledClickable {
                                                scope.launch {
                                                    val prevIndex =
                                                        (rowState.firstVisibleItemIndex - 1).coerceAtLeast(
                                                            0
                                                        )
                                                    rowState.animateScrollToItem(prevIndex)
                                                }
                                            },
                                        shape = RoundedCornerShape(28.dp),
                                        shadowElevation = 12.dp,
                                        tonalElevation = 0.dp,
                                        border = BorderStroke(1.dp, TheraColorTokens.StrokeColor),
                                        color = Color.White
                                    ) {
                                        Box(
                                            modifier = Modifier.size(56.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                                contentDescription = "Scroll Left",
                                                tint = Color(0xFF1A73E8),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }

//
// RIGHT ARROW (show only if can scroll forward)
//
                                if (showRightArrow) {
                                    Surface(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .align(Alignment.CenterEnd)
                                            .throttledClickable {
                                                scope.launch {
                                                    val nextIndex =
                                                        (rowState.firstVisibleItemIndex + 1).coerceAtMost(
                                                            rowState.layoutInfo.totalItemsCount - 1
                                                        )
                                                    rowState.animateScrollToItem(nextIndex)
                                                }
                                            },
                                        shape = RoundedCornerShape(28.dp),
                                        shadowElevation = 12.dp,
                                        tonalElevation = 0.dp,
                                        border = BorderStroke(1.dp, TheraColorTokens.StrokeColor),
                                        color = Color.White
                                    ) {
                                        Box(
                                            modifier = Modifier.size(56.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = "Scroll Right",
                                                tint = Color(0xFF1A73E8),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }


                            // RIGHT: Selection Panel
                            Box(
                                modifier = Modifier
                                    .weight(0.55f)
                                    .height(214.dp)
                            ) {
                                SingleSelectionPanel(
                                    duration = selectedDuration,
                                    onDurationChanged = {
                                        selectedDuration = it
                                        selectedPrice = calculatePrice(it)
                                    },
                                    price = selectedPrice,
                                    onSelect = {
                                        if (selectedUnit == null) {
                                            alert.show("Please Select Equipment")
                                        }
                                        selectedUnit?.let { unit ->
                                            onSelect(type, unit, selectedDuration)
                                        }
                                    },
                                    unit = selectedUnit,
                                    shouldShowDurationSelector = shouldShowDurationSelector,
                                    isOneMinuteAccording = isOneMinuteAccording,
                                    equipmentData = equipmentData,
                                    onStartMachine = { points ->
                                        if (selectedUnit == null) {
                                            alert.show("Please Select Equipment")
                                        } else {
                                            selectedUnit?.let { unit ->
                                                onStartMachine(type, unit, selectedDuration, points)
                                            }
                                        }
                                    },
                                    alert = alert
                                )
                            }
                        }


                    }

                    Spacer(
                        Modifier
                            .height(20.dp)
                            .fillMaxWidth()
                    )

                }
            }

            Spacer(Modifier.height(36.dp))
        }

    }
}


@Composable
private fun SingleSessionHeader(
    onBack: () -> Unit, onHome: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(56.dp) // large for kiosk touch
                    .clip(CircleShape)
                    .background(Color.White)
                    .throttledClickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A73E8), // Theralieve Blue
                    modifier = Modifier.size(32.dp)
                )
            }

        }

        // Title
        Text(
            text = "Equipment",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

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


/* -------------------------------------------------------------------------
 * EQUIPMENT CARD
 * ------------------------------------------------------------------------- */

@Composable
fun SingleItemCard(
    name: String,
    image: String,
    status: String,
    statusColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .background(TheraColorTokens.Background, RoundedCornerShape(18.dp))
            .border(3.dp, borderColor, RoundedCornerShape(18.dp))
            .throttledClickable { onClick() }
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = status, color = statusColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold
        )


        NetworkImage(
            imageUrl = image,
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .padding(vertical = 2.dp)
        )


        Text(
            name, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold
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
fun SingleSelectionPanel(
    duration: Int,
    onDurationChanged: (Int) -> Unit,
    price: Double,
    onSelect: () -> Unit,
    shouldShowDurationSelector: Boolean = true,
    isOneMinuteAccording: Boolean = false,
    equipmentData: List<EquipmentDataItem>? = null,
    unit: Equipment? = null,
    onStartMachine: (String?) -> Unit = {},
    alert: TheraAlertState? = null
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TheraColorTokens.Background, RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // ===========================
        // TOP ROW (Duration – Dropdown – Price)
        // ===========================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Duration
            Column {
                Text("Duration", color = Color.Black.copy(0.7f), fontSize = 18.sp)
                Text(
                    if (duration > 5) "$duration min" else " - - - ",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (shouldShowDurationSelector) {
                SingleDurationPopupSelector(
                    value = duration,
                    onValueChange = { onDurationChanged(it ?: 10) },
                    modifier = Modifier.height(60.dp),
                    isOneMinuteAccording = isOneMinuteAccording,
                    equipmentData = equipmentData,
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
                        text = "$duration min", fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

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
        }

        // Spacer pushes button to bottom (for kiosk layout)
        Spacer(modifier = Modifier.height(24.dp))

        // ===========================
        // BIG SELECT BUTTON (Bottom)
        // ===========================
        TheraPrimaryButton(
            label = "Proceed", onClick = {

                if (unit != null) {
                    onSelect()
                }

            }, modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }
}


@Composable
fun SingleDurationPopupSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isOneMinuteAccording: Boolean = false,
    equipmentData: List<EquipmentDataItem>? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    val availableDurations = remember(isOneMinuteAccording, equipmentData) {
        when {
            isOneMinuteAccording -> (10..60 step 5).toList()
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
                .clickable {
                    expanded = true
                }
                .padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (value > 0) "$value min" else "Select duration",
                fontSize = 18.sp,
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
                            SingleDurationOption(
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
private fun SingleDurationOption(
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
            fontSize = 16.sp,
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
        SingleSessionScreen(
            showDialog = null,
            error = null,
            equipmentData = emptyList(),
        )
    }
}
