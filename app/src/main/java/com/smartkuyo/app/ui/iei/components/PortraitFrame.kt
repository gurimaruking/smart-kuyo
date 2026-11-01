package com.smartkuyo.app.ui.iei.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun PortraitFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Canvas背景（額縁描画）
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPortraitFrame()
        }

        // 写真コンテンツ（額縁の窓エリアに配置）
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val borderW = maxWidth * 0.05f
            val matThick = maxWidth * 0.03f
            val frameStartX = maxWidth * 0.06f
            val frameStartY = maxHeight * 0.06f
            val frameEndY = maxHeight * 0.82f

            val hPad = frameStartX + borderW + matThick
            val topPad = frameStartY + borderW + matThick
            val bottomPad = (maxHeight - frameEndY) + borderW + matThick

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = hPad,
                        end = hPad,
                        top = topPad,
                        bottom = bottomPad
                    ),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }
}

private fun DrawScope.drawPortraitFrame() {
    val w = size.width
    val h = size.height

    // === 額縁の寸法 ===
    val frameL = w * 0.06f
    val frameR = w * 0.94f
    val frameT = h * 0.06f
    val frameB = h * 0.82f
    val frameW = frameR - frameL
    val frameH = frameB - frameT

    val borderW = w * 0.05f
    val matThick = w * 0.03f

    // === 黒い外枠 ===
    drawRoundRect(
        color = Color(0xFF0A0A0A),
        topLeft = Offset(frameL, frameT),
        size = Size(frameW, frameH),
        cornerRadius = CornerRadius(6f)
    )
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A), Color(0xFF0F0F0F)),
            startY = frameT,
            endY = frameB
        ),
        topLeft = Offset(frameL + 3f, frameT + 3f),
        size = Size(frameW - 6f, frameH - 6f),
        cornerRadius = CornerRadius(4f)
    )
    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(frameL + borderW - 2f, frameT + borderW - 2f),
        size = Size(frameW - borderW * 2 + 4f, frameH - borderW * 2 + 4f),
        cornerRadius = CornerRadius(2f),
        style = Stroke(width = 1f)
    )

    // === 白/クリーム色のマット ===
    val matL = frameL + borderW
    val matR = frameR - borderW
    val matT = frameT + borderW
    val matB = frameB - borderW

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF0EDE4), Color(0xFFE8E4DB), Color(0xFFEDE8DF)),
            startY = matT,
            endY = matB
        ),
        topLeft = Offset(matL, matT),
        size = Size(matR - matL, matB - matT)
    )

    // === 金の細い枠線 ===
    val photoL = matL + matThick
    val photoR = matR - matThick
    val photoT = matT + matThick
    val photoB = matB - matThick

    drawRect(
        color = Color(0xFFD4AF37),
        topLeft = Offset(photoL - 2f, photoT - 2f),
        size = Size(photoR - photoL + 4f, photoB - photoT + 4f),
        style = Stroke(width = 1.5f)
    )

    // === 写真エリア背景 ===
    drawRect(
        color = Color.Black,
        topLeft = Offset(photoL, photoT),
        size = Size(photoR - photoL, photoB - photoT)
    )

}
