package com.theralieve.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat


fun formatPriceTo2Decimal(price:Any?):String{
   return DecimalFormat("0.##").format(price)
}

fun Modifier.trackUserInteraction(
    onUserInteraction: () -> Unit
): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent()
            onUserInteraction()
        }
    }
}



suspend fun loadImageFromNetwork(url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val urlToOpen = if (url.startsWith("http")) URL(url)
            else URL("https://theralieve.co/storage//app/public/$url")
//            else URL("https://apptechhubs.com//theralieve/storage//app/public/$url")
            val connection = urlToOpen.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
