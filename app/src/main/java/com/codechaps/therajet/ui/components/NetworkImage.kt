package com.codechaps.therajet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.codechaps.therajet.R
import com.codechaps.therajet.ui.utils.throttledClickable

@Composable
fun NetworkImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Format URL: if it doesn't start with http, prepend base URL
    val fullImageUrl = if (imageUrl?.startsWith("http") == true) {
        imageUrl
    } else {
        "https://theralieve.co/storage//app/public/$imageUrl"
//        "https://apptechhubs.com//theralieve/storage//app/public/$imageUrl"
    }

    SubcomposeAsyncImage(
        model = fullImageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text("Image failed to load")
                }
            }

            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}


@Composable
fun ProfileNetworkImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Format URL: if it doesn't start with http, prepend base URL
    val fullImageUrl = if (imageUrl?.startsWith("http") == true) {
        imageUrl
    } else {
        "https://theralieve.co/storage//app/public/$imageUrl"
//        "https://apptechhubs.com//theralieve/storage//app/public/$imageUrl"
    }

    SubcomposeAsyncImage(
        model = fullImageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .size(56.dp) // large for kiosk touch
                        .clip(CircleShape)
                        .background(Color.White)
                        .throttledClickable {
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Back",
                        tint = Color(0xFF1A73E8), // Theralieve Blue
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}
