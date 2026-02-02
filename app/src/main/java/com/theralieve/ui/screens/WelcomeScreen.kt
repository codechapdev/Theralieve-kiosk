package com.theralieve.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theralieve.R
import com.theralieve.ui.components.TheraPrimaryButton
import com.theralieve.ui.components.TheraSecondaryButton
import com.theralieve.ui.model.LoginFormState
import com.theralieve.ui.theme.TheraColorTokens
import com.theralieve.ui.theme.TheraJetTabTheme
import com.theralieve.ui.viewmodel.WelcomeUiState
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    modifier: Modifier = Modifier,
    onNonMemberClick: () -> Unit,
    onMembershipClick: () -> Unit,
    onExistingMemberClick: () -> Unit
) {

//    var showLogin by remember { mutableStateOf(false) }
//    var loginState by remember { mutableStateOf<LoginFormState>(LoginFormState("","")) }

//    if (showLogin) {
//        MemberLoginDialog(
//            state = loginState,
//            onStateChanged = { loginState = it },
//            onLogin = {
//                onExistingMemberClick()
//            },
//            onClose = { showLogin = false }
//        )
//    }


    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    
    // Slider images
    val sliderImages = listOf(
//        R.drawable.slider1,
//        R.drawable.slider2,
        R.drawable.slider3,
        R.drawable.slider4
    )

    
    // Responsive sizing based on screen dimensions
    val padding = when {
        screenWidth > 1200 -> 64.dp
        screenWidth > 800 -> 48.dp
        else -> 32.dp
    }
    
    val buttonHeight = when {
        screenHeight > 1000 -> 180.dp
        screenHeight > 800 -> 160.dp
        else -> 140.dp
    }
    
    val buttonSpacing = when {
        screenWidth > 1200 -> 32.dp
        screenWidth > 800 -> 24.dp
        else -> 16.dp
    }
    
    val logoHeight = when {
        screenHeight > 1000 -> 250.dp
        screenHeight > 800 -> 220.dp
        else -> 180.dp
    }
    
    val headlineSize = when {
        screenHeight > 1000 -> 42.sp
        screenHeight > 800 -> 36.sp
        else -> 32.sp
    }
    
    val taglineSize = when {
        screenHeight > 1000 -> 22.sp
        screenHeight > 800 -> 20.sp
        else -> 18.sp
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Animated background images with crossfade
        // Current image

        AutoImageSlider(
            sliderImages = sliderImages
        )

        // Semi-transparent overlay to reduce contrast and improve text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        
        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(padding / 2))

            // Logo at top

            Box(modifier = Modifier) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.theralive_logo),
                        contentDescription = "Theralieve Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(logoHeight),
                        contentScale = ContentScale.Fit
                    )

                    // Promotional text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = buildAnnotatedString {
                                append("Welcome to ")
                                withStyle(
                                    style = SpanStyle(
                                        color = TheraColorTokens.Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(uiState.locationName)
                                }
                                append(" – We’re Glad You’re Here!")
                            },
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = headlineSize,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TheraColorTokens.TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        // "What Every Body Wants" with "Body" in blue
                        Text(
                            text = buildAnnotatedString {
                                append("What Every ")
                                withStyle(
                                    style = SpanStyle(
                                        color = TheraColorTokens.Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Body")
                                }
                                append(" Wants")
                            },
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = headlineSize,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TheraColorTokens.TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        // Tagline
                        Text(
                            text = stringResource(id = R.string.welcome_tagline),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = taglineSize
                            ),
                            color = TheraColorTokens.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Three large kiosk-style buttons in horizontal layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                TheraPrimaryButton(
                    label = stringResource(id = R.string.button_non_member),
                    onClick = onNonMemberClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight)
                )

                TheraPrimaryButton(
                    label = stringResource(id = R.string.button_membership),
                    onClick = onMembershipClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight)
                )

                TheraSecondaryButton(
                    label = stringResource(id = R.string.button_existing_member),
                    onClick = {
                        onExistingMemberClick()                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight)
                )
            }

            Spacer(modifier = Modifier.height(padding / 2))
        }
    }
}

@Composable
fun AutoImageSlider(
    modifier: Modifier = Modifier,
    sliderImages: List<Int>
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto-rotate every 4 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % sliderImages.size
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentIndex,
            animationSpec = tween(1200)   // smooth fade
        ) { index ->
            Image(
                painter = painterResource(sliderImages[index]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}



@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun WelcomeScreenPreview() {
    TheraJetTabTheme {
        WelcomeScreen(
            uiState = WelcomeUiState(),
            onNonMemberClick = {},
            onMembershipClick = {},
            onExistingMemberClick = {}
        )
    }
}

