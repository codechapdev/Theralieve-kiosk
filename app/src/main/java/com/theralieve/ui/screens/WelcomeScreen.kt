package com.theralieve.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.ui.utils.throttledClickable
import com.theralieve.ui.viewmodel.WelcomeUiState
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    modifier: Modifier = Modifier,
    onNewClick: () -> Unit,
    onExistingClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF7F9FC), Color(0xFFEDEFF3)
                    )
                )
            )
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // =======================
            // TOP HEADER BAR
            // =======================
            KioskHeader(uiState.locationName)

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            // =======================
            // MAIN CONTENT
            // =======================
            Row(modifier = Modifier.fillMaxSize()) {

                // LEFT IMAGE PANEL
                LeftImagePanel(
                    modifier = Modifier.weight(1f),
                )

                // RIGHT CONTENT PANEL
                RightContentPanel(
                    modifier = Modifier.weight(1f),
                    onNewClick = onNewClick,
                    onExistingClick = onExistingClick
                )
            }
        }
    }
}


@Composable
private fun KioskHeader(locationName: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color.White)
            .padding(horizontal = 40.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.height(60.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Welcome to ${locationName.uppercase()}", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 22.sp
            )
        )
    }
}


@Composable
private fun LeftImagePanel(
    modifier: Modifier = Modifier
) {

    val images = remember {
        listOf(
//            R.drawable.slider1,
//            R.drawable.slider2,
            R.drawable.slider3,
            R.drawable.slider4
        )
    }

    var currentIndex by remember { mutableStateOf(0) }

    // Auto slide effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Change image every 3 seconds
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Box(
        modifier = modifier.fillMaxHeight()
    ) {

        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(1200)
                ) togetherWith fadeOut(
                    animationSpec = tween(1200)
                )
            },
            label = "ImageFadeTransition"
        ) { index ->

            Image(
                painter = painterResource(id = images[index]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Soft premium overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f)
                        )
                    )
                )
        )
    }
}


@Composable
private fun RightContentPanel(
    modifier: Modifier = Modifier, onNewClick: () -> Unit, onExistingClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), Color(0xFFF4F6F9)
                    )
                )
            )
            .padding(horizontal = 46.dp, vertical = 56.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp), // Prevent over-stretch on large screens
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // =========================
            // TOP CONTENT BLOCK
            // =========================
            Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {

                // Headline with accent underline

                Text(
                    text = "Restoring the Real You!",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp, fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color(0xFF1A1A1A)
                )

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(120.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1976D2), Color(0xFF42A5F5)
                                )
                            ), RoundedCornerShape(50)
                        )
                )
            }

            Text(
                text = "Don’t let pain or soreness keep you from feeling your best and doing what you love. Our best-in-class therapies help revive and restore the real you.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp, lineHeight = 30.sp
                ),
                color = Color(0xFF555555)
            )

            // =========================
            // CTA SECTION (Grouped)
            // =========================
            Column(
                modifier = Modifier, verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    GradientPrimaryButton(
                        text = "NEW? See Plans", onClick = onNewClick
                    )

                    GreySecondaryButton(
                        text = "Existing Client", onClick = onExistingClick
                    )
                }

            }

            BulletGrid()

        }
    }
}



@Composable
fun BulletGrid() {

    val items = listOf(
        "Affordable Wellness",
        "Convenient access",
        "Powerful therapies",
        "Customize your perfect plan"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 columns
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { text ->
            StyledBullet(text)
        }
    }
}


@Composable
fun StyledBullet(text: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF1976D2))
        )

        Text(
            text = text, fontSize = 18.sp, color = Color(0xFF333333)
        )
    }
}


@Composable
fun GradientPrimaryButton(
    text: String, onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .height(80.dp)
            .width(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1976D2), Color(0xFF1565C0)
                    )
                )
            )
            .throttledClickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
        )
    }
}


@Composable
fun GreySecondaryButton(
    text: String, onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .height(80.dp)
            .width(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF22C55E))
            .throttledClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text, color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 18.sp
        )
    }
}


@Composable
fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•  ", fontSize = 18.sp)
        Text(text, fontSize = 18.sp)
    }
}


@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun WelcomeScreenPreview() {
    TheraJetTabTheme {
        WelcomeScreen(uiState = WelcomeUiState(), onNewClick = {}, onExistingClick = {})
    }
}