package com.smartkuyo.app.ui.iei

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartkuyo.app.ui.iei.components.PhotoSlideshow
import com.smartkuyo.app.ui.iei.components.PortraitFrame

@Composable
fun IeiScreen(
    modifier: Modifier = Modifier,
    viewModel: IeiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.photos.isNotEmpty()) {
            PortraitFrame {
                PhotoSlideshow(
                    photos = uiState.photos,
                    intervalMs = uiState.slideshowIntervalMs,
                    useMonochrome = uiState.useMonochrome
                )
            }
        }
        // 故人名は詳細オーバーレイ（中央長押し）で表示
    }
}
