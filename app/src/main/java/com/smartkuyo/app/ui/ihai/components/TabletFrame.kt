package com.smartkuyo.app.ui.ihai.components

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.smartkuyo.app.domain.model.TabletStyle
import com.smartkuyo.app.ui.theme.GoldDark
import com.smartkuyo.app.ui.theme.GoldLight
import com.smartkuyo.app.ui.theme.GoldMuted
import com.smartkuyo.app.ui.theme.LacquerBlack
import com.smartkuyo.app.ui.theme.LacquerDarkBrown
import com.smartkuyo.app.ui.theme.WoodBrown
import com.smartkuyo.app.ui.theme.WoodLight

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
            innerBg = Color(0xFF050505),
            accentColor = GoldLight
        )
        TabletStyle.TRADITIONAL_GOLD -> TabletColors(
            frameGradient = listOf(GoldMuted, GoldLight, GoldMuted),
            borderColor = GoldLight,
            innerBg = LacquerBlack,
            accentColor = GoldLight
        )
        TabletStyle.WOOD_GRAIN -> TabletColors(
            frameGradient = listOf(WoodBrown, WoodLight, WoodBrown),
            borderColor = GoldMuted,
            innerBg = Color(0xFF1A0F05),
            accentColor = GoldMuted
        )
        TabletStyle.MODERN_WHITE -> TabletColors(
            frameGradient = listOf(Color(0xFFE8E8E8), Color(0xFFF5F5F5), Color(0xFFE8E8E8)),
            borderColor = Color(0xFFCCCCCC),
            innerBg = Color(0xFF1C1C1E),
            accentColor = Color(0xFFAAAAAA)
        )
        TabletStyle.SIMPLE -> TabletColors(
            frameGradient = listOf(Color(0xFF2C2C2E), Color(0xFF3A3A3C), Color(0xFF2C2C2E)),
            borderColor = GoldMuted,
            innerBg = Color(0xFF1C1C1E),
            accentColor = GoldMuted
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRealisticTablet(colors)
        }
        // コンテンツは名板の内側エリアに配置
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val topPad = maxHeight * 0.17f
            val bottomPad = maxHeight * 0.36f
            val hPad = maxWidth * 0.25f
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

private data class TabletColors(
    val frameGradient: List<Color>,
    val borderColor: Color,
    val innerBg: Color,
    val accentColor: Color
)

private fun DrawScope.drawRealisticTablet(colors: TabletColors) {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    // === 位牌の寸法比率 (実物に近い) ===
    val tabletLeft = w * 0.20f
    val tabletRight = w * 0.80f
    val tabletWidth = tabletRight - tabletLeft

    // ── 宝珠 (頂部の玉) ──
    val houjuCy = h * 0.065f
    val houjuR = w * 0.045f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.accentColor.copy(alpha = 0.8f), colors.frameGradient[1], colors.frameGradient[0]),
            center = Offset(cx, houjuCy),
            radius = houjuR
        ),
        radius = houjuR,
        center = Offset(cx, houjuCy)
    )
    drawCircle(
        color = colors.borderColor.copy(alpha = 0.6f),
        radius = houjuR,
        center = Offset(cx, houjuCy),
        style = Stroke(width = 1.5f)
    )

    // 宝珠の台座（小さな台）
    val houjuBaseTop = houjuCy + houjuR * 0.8f
    val houjuBaseBottom = houjuCy + houjuR * 1.6f
    val houjuBaseHalfW = houjuR * 1.2f
    drawPath(
        path = Path().apply {
            moveTo(cx - houjuBaseHalfW, houjuBaseBottom)
            lineTo(cx - houjuR * 0.5f, houjuBaseTop)
            lineTo(cx + houjuR * 0.5f, houjuBaseTop)
            lineTo(cx + houjuBaseHalfW, houjuBaseBottom)
            close()
        },
        brush = Brush.verticalGradient(colors.frameGradient, startY = houjuBaseTop, endY = houjuBaseBottom)
    )

    // ── 笠 (屋根) ──
    val roofTop = h * 0.10f
    val roofBottom = h * 0.145f
    val roofOverhang = w * 0.03f
    drawPath(
        path = Path().apply {
            moveTo(cx, roofTop)
            lineTo(tabletRight + roofOverhang, roofBottom)
            lineTo(tabletLeft - roofOverhang, roofBottom)
            close()
        },
        brush = Brush.verticalGradient(colors.frameGradient, startY = roofTop, endY = roofBottom)
    )
    drawPath(
        path = Path().apply {
            moveTo(cx, roofTop)
            lineTo(tabletRight + roofOverhang, roofBottom)
            lineTo(tabletLeft - roofOverhang, roofBottom)
            close()
        },
        color = colors.borderColor.copy(alpha = 0.5f),
        style = Stroke(width = 1.5f)
    )

    // ── 名板 (メインの板) ──
    val plateTop = roofBottom
    val plateBottom = h * 0.68f

    // 名板外枠
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = colors.frameGradient,
            startY = plateTop,
            endY = plateBottom
        ),
        topLeft = Offset(tabletLeft, plateTop),
        size = Size(tabletWidth, plateBottom - plateTop),
        cornerRadius = CornerRadius(6f)
    )
    drawRoundRect(
        color = colors.borderColor.copy(alpha = 0.4f),
        topLeft = Offset(tabletLeft, plateTop),
        size = Size(tabletWidth, plateBottom - plateTop),
        cornerRadius = CornerRadius(6f),
        style = Stroke(width = 1.5f)
    )

    // 名板内側（黒い面 - テキスト領域）
    val innerMargin = tabletWidth * 0.06f
    val innerLeft = tabletLeft + innerMargin
    val innerRight = tabletRight - innerMargin
    val innerTop = plateTop + innerMargin
    val innerBottom = plateBottom - innerMargin
    drawRoundRect(
        color = colors.innerBg,
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerRight - innerLeft, innerBottom - innerTop),
        cornerRadius = CornerRadius(3f)
    )
    // 内側の細い金縁
    drawRoundRect(
        color = colors.borderColor.copy(alpha = 0.25f),
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerRight - innerLeft, innerBottom - innerTop),
        cornerRadius = CornerRadius(3f),
        style = Stroke(width = 1f)
    )

    // ── 蓮台 (名板と台座の間の装飾) ──
    val rendaiTop = plateBottom + h * 0.008f
    val rendaiBottom = plateBottom + h * 0.035f
    val rendaiLeft = tabletLeft + tabletWidth * 0.05f
    val rendaiRight = tabletRight - tabletWidth * 0.05f

    // 蓮弁の上辺（波状）
    drawPath(
        path = Path().apply {
            moveTo(rendaiLeft, rendaiBottom)
            lineTo(rendaiLeft, rendaiTop + (rendaiBottom - rendaiTop) * 0.4f)
            // 蓮弁の波
            val segments = 7
            val segW = (rendaiRight - rendaiLeft) / segments
            for (i in 0 until segments) {
                val sx = rendaiLeft + segW * i
                val ex = sx + segW
                val midX = (sx + ex) / 2
                cubicTo(
                    sx + segW * 0.2f, rendaiTop,
                    midX, rendaiTop,
                    ex, rendaiTop + (rendaiBottom - rendaiTop) * 0.4f
                )
            }
            lineTo(rendaiRight, rendaiBottom)
            close()
        },
        brush = Brush.verticalGradient(colors.frameGradient, startY = rendaiTop, endY = rendaiBottom)
    )

    // ── 上台座 ──
    val base1Top = rendaiBottom + h * 0.005f
    val base1Bottom = base1Top + h * 0.04f
    val base1Left = tabletLeft - w * 0.01f
    val base1Right = tabletRight + w * 0.01f
    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = base1Top, endY = base1Bottom),
        topLeft = Offset(base1Left, base1Top),
        size = Size(base1Right - base1Left, base1Bottom - base1Top),
        cornerRadius = CornerRadius(3f)
    )
    drawRoundRect(
        color = colors.borderColor.copy(alpha = 0.3f),
        topLeft = Offset(base1Left, base1Top),
        size = Size(base1Right - base1Left, base1Bottom - base1Top),
        cornerRadius = CornerRadius(3f),
        style = Stroke(width = 1f)
    )

    // ── 中台座（くびれ） ──
    val waistTop = base1Bottom + h * 0.003f
    val waistBottom = waistTop + h * 0.05f
    val waistNarrowL = tabletLeft + tabletWidth * 0.12f
    val waistNarrowR = tabletRight - tabletWidth * 0.12f
    drawPath(
        path = Path().apply {
            moveTo(base1Left + w * 0.01f, waistTop)
            lineTo(base1Right - w * 0.01f, waistTop)
            lineTo(waistNarrowR, waistTop + (waistBottom - waistTop) * 0.5f)
            lineTo(base1Right + w * 0.01f, waistBottom)
            lineTo(base1Left - w * 0.01f, waistBottom)
            lineTo(waistNarrowL, waistTop + (waistBottom - waistTop) * 0.5f)
            close()
        },
        brush = Brush.verticalGradient(colors.frameGradient, startY = waistTop, endY = waistBottom)
    )

    // ── 下台座（広い台） ──
    val base2Top = waistBottom + h * 0.003f
    val base2Bottom = base2Top + h * 0.045f
    val base2Left = tabletLeft - w * 0.04f
    val base2Right = tabletRight + w * 0.04f
    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = base2Top, endY = base2Bottom),
        topLeft = Offset(base2Left, base2Top),
        size = Size(base2Right - base2Left, base2Bottom - base2Top),
        cornerRadius = CornerRadius(4f)
    )
    drawRoundRect(
        color = colors.borderColor.copy(alpha = 0.3f),
        topLeft = Offset(base2Left, base2Top),
        size = Size(base2Right - base2Left, base2Bottom - base2Top),
        cornerRadius = CornerRadius(4f),
        style = Stroke(width = 1f)
    )

    // ── 最下段の台（足） ──
    val footTop = base2Bottom + h * 0.005f
    val footBottom = footTop + h * 0.025f
    val footLeft = base2Left - w * 0.02f
    val footRight = base2Right + w * 0.02f
    drawRoundRect(
        brush = Brush.verticalGradient(colors.frameGradient, startY = footTop, endY = footBottom),
        topLeft = Offset(footLeft, footTop),
        size = Size(footRight - footLeft, footBottom - footTop),
        cornerRadius = CornerRadius(5f)
    )
    drawRoundRect(
        color = colors.borderColor.copy(alpha = 0.3f),
        topLeft = Offset(footLeft, footTop),
        size = Size(footRight - footLeft, footBottom - footTop),
        cornerRadius = CornerRadius(5f),
        style = Stroke(width = 1f)
    )

    // 足の下の細いライン
    val lineY = footBottom + h * 0.003f
    drawLine(
        color = colors.borderColor.copy(alpha = 0.15f),
        start = Offset(footLeft + w * 0.02f, lineY),
        end = Offset(footRight - w * 0.02f, lineY),
        strokeWidth = 1f,
        cap = StrokeCap.Round
    )
}
