package com.smartkuyo.app.ui.iei.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.smartkuyo.app.domain.model.Photo
import kotlinx.coroutines.delay

@Composable
fun PhotoSlideshow(
    photos: List<Photo>,
    intervalMs: Long,
    useMonochrome: Boolean,
    modifier: Modifier = Modifier
) {
    if (photos.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(photos.size, intervalMs) {
        if (photos.size > 1) {
            while (true) {
                delay(intervalMs)
                currentIndex = (currentIndex + 1) % photos.size
            }
        }
    }

    val monochromeFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }

    Crossfade(
        targetState = currentIndex,
        animationSpec = tween(1500),
        label = "slideshow",
        modifier = modifier
    ) { index ->
        val safeIndex = index.coerceIn(0, photos.lastIndex)
        AsyncImage(
            model = photos[safeIndex].uri,
            contentDescription = "遺影",
            contentScale = ContentScale.Fit,
            colorFilter = if (useMonochrome) monochromeFilter else null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
