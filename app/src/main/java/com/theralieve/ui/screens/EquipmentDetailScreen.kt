package com.theralieve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.domain.model.EquipmentDetail
import com.theralieve.ui.components.Header
import com.theralieve.ui.components.NetworkImage
import com.theralieve.ui.components.TheraGradientBackground
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.theme.TheraColorTokens

@Composable
fun EquipmentDetailScreen(
    equipmentDetail: EquipmentDetail,
    isMember: Boolean = false,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSelect: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    
    // Responsive sizing for kiosk
    val imageSize = when {
        screenHeight > 1000 -> 350.dp
        screenHeight > 800 -> 300.dp
        else -> 250.dp
    }
    
    val titleSize = when {
        screenHeight > 1000 -> 48.sp
        screenHeight > 800 -> 42.sp
        else -> 36.sp
    }
    
    TheraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Header("Equipment Details", isMember = false, onBack = onBack, onHome = onHome)

            // Equipment Image
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NetworkImage(
                    imageUrl = equipmentDetail.image,
                    contentDescription = equipmentDetail.equipmentName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(imageSize)
                )
                
                // Equipment Name
                Text(
                    text = equipmentDetail.equipmentName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TheraColorTokens.TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                // Equipment Modal (if available)
                equipmentDetail.equipmentModal?.let { modal ->
                    Text(
                        text = modal,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = when {
                                screenHeight > 1000 -> 28.sp
                                screenHeight > 800 -> 24.sp
                                else -> 20.sp
                            }
                        ),
                        color = TheraColorTokens.TextSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = AnnotatedString.fromHtml(equipmentDetail.description),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = when {
                            screenHeight > 1000 -> 28.sp
                            screenHeight > 800 -> 24.sp
                            else -> 20.sp
                        }
                    ),
                    color = TheraColorTokens.TextSecondary,
                    lineHeight = when {
                        screenHeight > 1000 -> 40.sp
                        screenHeight > 800 -> 36.sp
                        else -> 32.sp
                    }
                )
            }
            
            // Equipment Point and Time (if available)
            if (!equipmentDetail.equipmentPoint.isNullOrEmpty() || !equipmentDetail.equipmentTime.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    equipmentDetail.equipmentPoint?.let { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Equipment Point:",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 26.sp
                                        screenHeight > 800 -> 22.sp
                                        else -> 18.sp
                                    },
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TheraColorTokens.TextPrimary
                            )
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 26.sp
                                        screenHeight > 800 -> 22.sp
                                        else -> 18.sp
                                    }
                                ),
                                color = TheraColorTokens.TextSecondary
                            )
                        }
                    }
                    equipmentDetail.equipmentTime?.let { time ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Equipment Time:",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 26.sp
                                        screenHeight > 800 -> 22.sp
                                        else -> 18.sp
                                    },
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TheraColorTokens.TextPrimary
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = when {
                                        screenHeight > 1000 -> 26.sp
                                        screenHeight > 800 -> 22.sp
                                        else -> 18.sp
                                    }
                                ),
                                color = TheraColorTokens.TextSecondary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Select Button
//            TheraPrimaryButton(
//                label = "Select Equipment",
//                onClick = onSelect,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(
//                        when {
//                            screenHeight > 1000 -> 100.dp
//                            screenHeight > 800 -> 90.dp
//                            else -> 80.dp
//                        }
//                    )
//            )
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewEquipmentDetailScreen(){
    EquipmentDetailScreen(
        equipmentDetail = EquipmentDetail(
            id = 1,
            equipmentName = "Test Equipment",
            equipmentModal = "Test Modal",
            image = "https://via.placeholder.com/150",
            equipmentTime = "10 mins",
            equipmentPoint = "100",
            description = "Dummy data, also known as test or fake data, is fictitious information used to simulate real-world data for testing, development, and demonstration purposes. It mimics the structure and characteristics of actual data without containing sensitive or meaningful information.",
            status = 1,
            updatedDate = "2023-08-01 12:00:00",
            createdDate = "2023-08-01 12:00:00"
        ),
        onBack = {},
        onSelect = {},
        onHome = {}
    )

}
