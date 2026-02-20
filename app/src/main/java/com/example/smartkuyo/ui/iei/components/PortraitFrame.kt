package com.example.smartkuyo.ui.iei.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.smartkuyo.ui.theme.GoldDark
import com.example.smartkuyo.ui.theme.GoldLight
import com.example.smartkuyo.ui.theme.GoldMuted

@Composable
fun PortraitFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GoldMuted, GoldLight, GoldDark, GoldLight, GoldMuted)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GoldDark, GoldMuted)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .clip(RoundedCornerShape(6.dp)),
        content = content
    )
}
