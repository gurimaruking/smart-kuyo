package com.example.smartkuyo.ui.iei

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartkuyo.ui.iei.components.PhotoSlideshow
import com.example.smartkuyo.ui.iei.components.PortraitFrame
import com.example.smartkuyo.ui.theme.CreamWhite

@Composable
fun IeiScreen(
    modifier: Modifier = Modifier,
    viewModel: IeiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val deceased = uiState.deceased

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

        // 故人名を下部に表示
        if (deceased != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = deceased.fullName,
                    color = CreamWhite,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
                if (!deceased.posthumousName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deceased.posthumousName,
                        color = CreamWhite.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
