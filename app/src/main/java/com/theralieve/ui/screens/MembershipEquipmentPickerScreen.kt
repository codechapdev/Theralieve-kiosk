package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.LocationEquipment
import com.theralieve.domain.model.Plan
import com.theralieve.ui.components.EquipmentCarousel
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.TheraSecondaryButton
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.utils.calculateDiscount
import com.theralieve.utils.formatPriceTo2Decimal
import com.theralieve.utils.getCurrencySymbol
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun MembershipGridScreen(
    plans: List<Plan>,
    locationEquipments: List<LocationEquipment>,
    isLoading: Boolean,
    onViewDetail: (Plan) -> Unit,
    onPlanSelected: (Plan, Boolean) -> Unit,
    onBack: () -> Unit,
    showQuestionnaire: Boolean = false,
    isVerifying: Boolean = false,
    verificationError: String? = null,
    memberIdError: String? = null,
    employeeIdError: String? = null,
    isVerifyingMemberId: Boolean = false,
    isVerifyingEmployeeId: Boolean = false,
    onQuestionnaireSubmit: (Boolean, String?, String?) -> Unit = { _, _, _ -> },
    onQuestionnaireCancel: () -> Unit = {},
    onMemberIdChange: (String) -> Unit = {},
    onEmployeeIdChange: (String) -> Unit = {},
    isForEmployee: Boolean = false,
    location: Location? = null,
) {
    var selectedFilter by remember { mutableStateOf("All") }

    var title  by remember { mutableStateOf("Pricing Plans") }
    val filteredList = remember(selectedFilter, plans) {
        if (selectedFilter == "All"){
            title = "Pricing Plans"
            plans
        }
        else plans.filter { plan ->
            when (selectedFilter) {
                "Session Plan" ->{
                    title = "Session Plans"
                    plan.detail?.plan_type?.contains("session", true)
                }
                "Credit Plan" ->{
                    title = "Credit Plans"
                    plan.detail?.plan_type?.contains(
                        "credit",
                        true
                    ) == true && plan.detail?.is_vip_plan != 1
                }

                else ->{
                    title = "Membership Plans"
                    plan.detail?.is_vip_plan == 1
                }
            } == true
        }
    }


    // Show questionnaire dialog
    if (showQuestionnaire) {
        QuestionnaireDialog(
            onDismiss = onQuestionnaireCancel,
            onSubmit = { isMember, memberNumber, employeeNumber ->
                onQuestionnaireSubmit(isMember, memberNumber, employeeNumber)
            },
            isVerifying = isVerifying,
            verificationError = verificationError,
            memberIdError = memberIdError,
            employeeIdError = employeeIdError,
            isVerifyingMemberId = isVerifyingMemberId,
            isVerifyingEmployeeId = isVerifyingEmployeeId,
            onMemberIdChange = onMemberIdChange,
            onEmployeeIdChange = onEmployeeIdChange,
            onMemberIdFocus = {
                // Clear employee field when member field gets focus
            },
            onEmployeeIdFocus = {
                // Clear member field when employee field gets focus
            },
            location = location
        )
    }

    TheraGradientBackground {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (filteredList.isNullOrEmpty() && !showQuestionnaire) {
            Text(
                text = "No Plans Found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            // Back button
            MembershipGridHeader(
                title = title,
                location = location,
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                },
                onBack = onBack,
                onHome = onBack
            )

            Spacer(modifier = Modifier.height(2.dp))

            val gridState = rememberLazyGridState()

            val scope = rememberCoroutineScope()

            // detect if we can scroll further down
            val canScrollDown by remember {
                derivedStateOf {
                    val layoutInfo = gridState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    val lastVisibleItemIndex =
                        layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                    lastVisibleItemIndex < totalItems - 1
                }
            }

            Box (modifier = Modifier.fillMaxSize()){
                LazyVerticalGrid(
                    state=gridState,
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (!isLoading && !showQuestionnaire && plans.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EquipmentCarousel(locationEquipments)
                        }
                    }
                    items(filteredList) { plan ->

                        var checked by remember { mutableStateOf(true) }

                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            tonalElevation = 4.dp,
                            modifier = Modifier
                                .throttledClickable { onPlanSelected(plan, checked) }
                                .fillMaxWidth()
                                .shadow(
                                    4.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    ambientColor = TheraColorTokens.Accent,
                                    spotColor = TheraColorTokens.PrimaryDark
                                )) {
                            Box {

                                Column(
                                    modifier = Modifier
                                        .background(TheraColorTokens.Background)
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Membership Image

                                    NetworkImage(
                                        imageUrl = plan.detail?.image,
                                        contentDescription = plan.detail?.image,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(0.6f),
                                            text = plan.detail?.plan_name ?: "",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.Black,
                                            fontSize = 22.sp,
                                            maxLines = 2,
                                            minLines = 2
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxWidth(0.5f),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            val discountResult = calculateDiscount(
                                                planPrice = plan.detail?.plan_price,
                                                discount = plan.detail?.discount,
                                                discountType = plan.detail?.discount_type,
                                                discountValidity = plan.detail?.discount_validity,
                                                employeeDiscount = plan.detail?.employee_discount,
                                                isForEmployee = isForEmployee
                                            )

                                            if (discountResult.hasDiscount) {
                                                Text(
                                                    text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                        DecimalFormat("0.##").format(
                                                            discountResult.originalPrice
                                                        )
                                                    }",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = Color.Gray,
                                                    textDecoration = TextDecoration.LineThrough,
                                                )
                                                // Show discounted price
                                                Text(
                                                    text = "${getCurrencySymbol(plan.detail?.currency)}${
                                                        DecimalFormat("0.##").format(
                                                            discountResult.discountedPrice
                                                        )
                                                    }",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = TheraColorTokens.Primary,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                // Show discount percentage
                                                /* Text(
                                         text = discountResult.discountPercentage,
                                         style = MaterialTheme.typography.bodySmall,
                                         color = TheraColorTokens.Primary
                                     )*/
                                            } else {
                                                // Show regular price
                                                Text(
                                                    text = "${getCurrencySymbol(plan.detail?.currency)}${formatPriceTo2Decimal(plan.detail?.plan_price)}",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = TheraColorTokens.Primary,
                                                )
                                            }
                                        }
                                    }


                                    if (plan.detail?.plan_type == "Session Pack") {
                                        val text = com.theralieve.utils.calculateValidity(
                                            plan.detail?.frequency, plan.detail?.frequency_limit
                                        ).takeIf { it.isNotEmpty() } ?: "N/A"
                                        Text(
                                            text = "Session Pack : $text",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                    } else {
                                        Text(
                                            text = "Credit Pack : ${plan.detail?.points}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                    }

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        plan.detail?.bullet_points?.split(",")?.take(4)
                                            ?.forEach { feature ->
                                                Text(
                                                    text = "• ${feature.trim()}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontSize = 16.sp,
                                                    color = Color.Black
                                                )
                                            }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))

                                    /*Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto Renew :",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                IosLikeSwitch(
                                    checked = checked, onCheckedChange = {
                                        if ((plan.detail?.is_vip_plan ?: 0) != 1) checked =
                                            it
                                    })

                            }*/

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TheraSecondaryButton(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(70.dp),
                                            label = stringResource(id = R.string.action_view_details),
                                            onClick = {
                                                onViewDetail(plan)
                                            })
                                        TheraPrimaryButton(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(70.dp),
                                            label = stringResource(id = R.string.action_enroll_now),
                                            onClick = {
                                                onPlanSelected(plan, checked)
                                            })
                                    }
                                }

                                if ((plan.detail?.is_vip_plan ?: 0) == 1) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
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
                                }
                            }
                        }
                    }
                }

                if (canScrollDown) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                gridState.animateScrollBy(400f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        containerColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Scroll Down",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            }


        }
    }
}

@Composable
fun MembershipGridHeader(
    title: String,
    location: Location? = null,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit = {},
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color(0xFFE8F5FE)) // Light wellness blue
            .padding(vertical = 16.dp),
    ) {

        Row(
            modifier = Modifier, verticalAlignment = Alignment.Top
        ) {

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                NetworkImage(
                    imageUrl = location?.image,
                    contentDescription = location?.locationName ?: "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(126.dp)
                        .height(64.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (location?.locationName ?: "").uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.width(220.dp)
                )
            }


        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                text = "Best Values... Cancel Anytime",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        // Title


        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterDropdown(
                selectedFilter = selectedFilter, onFilterSelected = onFilterSelected
            )
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
                    contentDescription = "Back",
                    tint = Color(0xFF1A73E8), // Theralieve Blue
                    modifier = Modifier.size(32.dp)
                )
            }
        }

    }


}

@Composable
fun FilterDropdown(
    selectedFilter: String, onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "Session Plan", "Credit Plan", "Membership Plan")
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Filter button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .throttledClickable { expanded = true }
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedFilter, color = Color.Black, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            filters.forEach { filter ->
                DropdownMenuItem(text = {
                    Text(
                        text = filter, color = Color.Black
                    )
                }, onClick = {
                    onFilterSelected(filter)
                    expanded = false
                })
            }
        }
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewMembershipGridScreen() {
    TheraGradientBackground {
        MembershipGridScreen(
            plans = emptyList(),
            isLoading = true,
            onViewDetail = {},
            onPlanSelected = { _, _ -> },
            onBack = {},
            locationEquipments = emptyList()
        )

    }
}

