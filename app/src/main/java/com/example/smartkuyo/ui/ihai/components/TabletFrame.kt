package com.example.smartkuyo.ui.ihai.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.smartkuyo.domain.model.TabletStyle
import com.example.smartkuyo.ui.theme.GoldDark
import com.example.smartkuyo.ui.theme.GoldLight
import com.example.smartkuyo.ui.theme.GoldMuted
import com.example.smartkuyo.ui.theme.LacquerBlack
import com.example.smartkuyo.ui.theme.LacquerDarkBrown
import com.example.smartkuyo.ui.theme.WoodBrown
import com.example.smartkuyo.ui.theme.WoodLight

@Composable
fun TabletFrame(
    style: TabletStyle,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = when (style) {
        TabletStyle.TRADITIONAL_BLACK -> TabletColors(
            frameGradient = listOf(LacquerBlack, LacquerDarkBrown, LacquerBlack),
            borderColor = GoldDark,
            innerBg = Color(0xFF0A0A0A)
        )
        TabletStyle.TRADITIONAL_GOLD -> TabletColors(
            frameGradient = listOf(GoldMuted, GoldLight, GoldMuted),
            borderColor = GoldLight,
            innerBg = LacquerBlack
        )
        TabletStyle.WOOD_GRAIN -> TabletColors(
            frameGradient = listOf(WoodBrown, WoodLight, WoodBrown),
            borderColor = GoldMuted,
            innerBg = Color(0xFF1A0F05)
        )
        TabletStyle.MODERN_WHITE -> TabletColors(
            frameGradient = listOf(Color(0xFFE8E8E8), Color(0xFFF5F5F5), Color(0xFFE8E8E8)),
            borderColor = Color(0xFFCCCCCC),
            innerBg = Color(0xFF1C1C1E)
        )
        TabletStyle.SIMPLE -> TabletColors(
            frameGradient = listOf(Color(0xFF2C2C2E), Color(0xFF3A3A3C), Color(0xFF2C2C2E)),
            borderColor = GoldMuted,
            innerBg = Color(0xFF1C1C1E)
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTabletShape(colors)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 80.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

private data class TabletColors(
    val frameGradient: List<Color>,
    val borderColor: Color,
    val innerBg: Color
)

private fun DrawScope.drawTabletShape(colors: TabletColors) {
    val width = size.width
    val height = size.height
    val centerX = width / 2

    // 宝珠（頂部の装飾）
    val houjuRadius = width * 0.06f
    val houjuCenterY = height * 0.08f
    drawCircle(
        brush = Brush.radialGradient(
            colors = colors.frameGradient,
            center = Offset(centerX, houjuCenterY),
            radius = houjuRadius
        ),
        radius = houjuRadius,
        center = Offset(centerX, houjuCenterY)
    )
    drawCircle(
        color = colors.borderColor,
        radius = houjuRadius,
        center = Offset(centerX, houjuCenterY),
        style = Stroke(width = 2f)
    )

    // 笠（屋根部分）
    val roofTop = height * 0.11f
    val roofBottom = height * 0.16f
    val roofPath = Path().apply {
        moveTo(centerX, roofTop)
        lineTo(width * 0.72f, roofBottom)
        lineTo(width * 0.28f, roofBottom)
        close()
    }
    drawPath(
        path = roofPath,
        brush = Brush.verticalGradient(colors.frameGradient, startY = roofTop, endY = roofBottom)
    )
    drawPath(path = roofPath, color = colors.borderColor, style = Stroke(width = 2f))

    // 名板（メインの板部分）
    val plateLeft = width * 0.18f
    val plateRight = width * 0.82f
    val plateTop = roofBottom
    val plateBottom = height * 0.75f
    val plateRect = Rect(plateLeft, plateTop, plateRight, plateBottom)

    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = plateTop, endY = plateBottom),
        topLeft = Offset(plateLeft, plateTop),
        size = Size(plateRight - plateLeft, plateBottom - plateTop),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = colors.borderColor,
        topLeft = Offset(plateLeft, plateTop),
        size = Size(plateRight - plateLeft, plateBottom - plateTop),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2f)
    )

    // 内側の背景
    val innerMargin = width * 0.04f
    drawRoundRect(
        color = colors.innerBg,
        topLeft = Offset(plateLeft + innerMargin, plateTop + innerMargin),
        size = Size(
            plateRight - plateLeft - innerMargin * 2,
            plateBottom - plateTop - innerMargin * 2
        ),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // 台座（下部）
    val baseTop = plateBottom + height * 0.02f
    val baseBottom = height * 0.88f
    val baseLeft = width * 0.15f
    val baseRight = width * 0.85f

    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = baseTop, endY = baseBottom),
        topLeft = Offset(baseLeft, baseTop),
        size = Size(baseRight - baseLeft, baseBottom - baseTop),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = colors.borderColor,
        topLeft = Offset(baseLeft, baseTop),
        size = Size(baseRight - baseLeft, baseBottom - baseTop),
        cornerRadius = CornerRadius(4f, 4f),
        style = Stroke(width = 2f)
    )

    // 最下段の台座
    val footTop = baseBottom + height * 0.01f
    val footBottom = height * 0.93f
    val footLeft = width * 0.12f
    val footRight = width * 0.88f

    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = footTop, endY = footBottom),
        topLeft = Offset(footLeft, footTop),
        size = Size(footRight - footLeft, footBottom - footTop),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = colors.borderColor,
        topLeft = Offset(footLeft, footTop),
        size = Size(footRight - footLeft, footBottom - footTop),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 2f)
    )
}
