package com.smartkuyo.app.ui.ihai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkuyo.app.ui.theme.GoldLight

@Composable
fun PosthumousNameText(
    posthumousName: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 36.sp
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val charCount = posthumousName.length

        // 文字数に応じてフォントサイズを自動調整
        val adjustedFontSize = remember(charCount, maxHeightPx, fontSize) {
            if (charCount <= 0) return@remember fontSize
            val requestedPx = with(density) { fontSize.toPx() }
            // 各文字の高さ: フォントサイズ * 1.4 (CJK文字) + 間隔
            val spacingPx = with(density) { 2.dp.toPx() }
            val charHeightPx = requestedPx * 1.4f + spacingPx
            val totalNeeded = charCount * charHeightPx
            val usableHeight = maxHeightPx * 0.95f
            if (totalNeeded <= usableHeight) {
                fontSize
            } else {
                val availablePerChar = usableHeight / charCount - spacingPx
                val adjustedPx = (availablePerChar / 1.4f).coerceAtLeast(10f)
                with(density) { adjustedPx.toSp() }
            }
        }

        // 文字数に応じた間隔
        val spacing = when {
            charCount > 12 -> 1.dp
            charCount > 8 -> 2.dp
            charCount > 5 -> 4.dp
            else -> 6.dp
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically)
        ) {
            posthumousName.forEach { char ->
                Text(
                    text = char.toString(),
                    color = GoldLight,
                    fontSize = adjustedFontSize,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
