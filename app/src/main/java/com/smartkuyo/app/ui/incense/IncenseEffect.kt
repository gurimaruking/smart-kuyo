package com.smartkuyo.app.ui.incense

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * 線香の状態
 */
enum class IncenseState {
    BURNER_ONLY,   // 香炉のみ
    BURNING,       // 線香が燃えている（煙あり）
}

/**
 * 煙パーティクルの定義
 */
data class SmokeParticle(
    val startXOffset: Float,
    val riseSpeed: Float,
    val driftAmplitude: Float,
    val driftFrequency: Float,
    val phaseOffset: Float,
    val baseSize: Float,
    val growthRate: Float,
    val baseAlpha: Float,
    val fadeRate: Float,
    val layer: Int
)

/**
 * タップ式デジタル線香
 * - 香炉は常時表示
 * - タップで線香点火、自然に燃え尽きる（約25分）
 * - 燃焼中にタップで消火
 */
@Composable
fun IncenseEffect(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    var state by remember { mutableStateOf(IncenseState.BURNER_ONLY) }
    // 燃焼進行度（0.0 = 燃え始め, 1.0 = 燃え尽きた）
    var burnProgress by remember { mutableFloatStateOf(0f) }

    // 燃焼タイマー（約25分 = 1500秒）
    LaunchedEffect(state) {
        if (state == IncenseState.BURNING) {
            burnProgress = 0f
            val totalDurationMs = 25 * 60 * 1000L // 25分
            val stepMs = 200L
            val steps = totalDurationMs / stepMs
            for (i in 0..steps) {
                if (state != IncenseState.BURNING) break
                burnProgress = i.toFloat() / steps.toFloat()
                delay(stepMs)
            }
            if (state == IncenseState.BURNING) {
                state = IncenseState.BURNER_ONLY
            }
        }
    }

    val particles = remember {
        val list = mutableListOf<SmokeParticle>()
        // レイヤー0: 芯の煙（濃い、細い）
        for (i in 0 until 25) {
            list.add(
                SmokeParticle(
                    startXOffset = (i % 5 - 2) * 0.004f,
                    riseSpeed = 0.12f + i * 0.010f,
                    driftAmplitude = 0.006f + (i * 0.0015f),
                    driftFrequency = 1.5f + (i % 4) * 0.3f,
                    phaseOffset = i * 0.28f,
                    baseSize = 6f + (i * 0.8f),
                    growthRate = 2.8f,
                    baseAlpha = 0.65f - (i * 0.015f),
                    fadeRate = 0.80f,
                    layer = 0
                )
            )
        }
        // レイヤー1: 中間の煙（ある程度濃い）
        for (i in 0 until 20) {
            list.add(
                SmokeParticle(
                    startXOffset = (i % 3 - 1) * 0.008f,
                    riseSpeed = 0.18f + i * 0.015f,
                    driftAmplitude = 0.012f + (i * 0.003f),
                    driftFrequency = 1.0f + (i % 3) * 0.4f,
                    phaseOffset = i * 0.40f + 1.5f,
                    baseSize = 8f + (i * 1.5f),
                    growthRate = 3.8f,
                    baseAlpha = 0.45f - (i * 0.012f),
                    fadeRate = 0.85f,
                    layer = 1
                )
            )
        }
        // レイヤー2: 外側の煙（広がる、ふんわり）
        for (i in 0 until 15) {
            list.add(
                SmokeParticle(
                    startXOffset = (i % 5 - 2) * 0.012f,
                    riseSpeed = 0.22f + i * 0.025f,
                    driftAmplitude = 0.020f + (i * 0.005f),
                    driftFrequency = 0.6f + (i % 3) * 0.2f,
                    phaseOffset = i * 0.55f + 3.0f,
                    baseSize = 12f + (i * 2.5f),
                    growthRate = 5.5f,
                    baseAlpha = 0.25f - (i * 0.010f),
                    fadeRate = 0.90f,
                    layer = 2
                )
            )
        }
        list.toList()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "incense")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smoke_time"
    )
    val time2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smoke_sway"
    )
    val time3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smoke_micro"
    )
    val burnGlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "burn_glow"
    )

    val smokeAlpha by animateFloatAsState(
        targetValue = if (state == IncenseState.BURNING) 1f else 0f,
        animationSpec = tween(800),
        label = "smoke_alpha"
    )

    // タップエリア（画面下部中央）
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.3f)
                .height(200.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    state = when (state) {
                        IncenseState.BURNER_ONLY -> IncenseState.BURNING
                        IncenseState.BURNING -> IncenseState.BURNER_ONLY
                    }
                }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width * 0.5f
            val incenseBottomY = size.height * 0.87f

            // === 線香（燃焼中のみ）→ 香炉より先に描画（奥のレイヤー） ===
            if (smokeAlpha > 0.01f) {
                // 線香の長さは燃焼進行に伴い短くなる
                val burnerH = size.width * 0.065f
                val fullLength = size.height * 0.12f
                val remainingLength = fullLength * (1f - burnProgress)
                val incenseTopY = incenseBottomY - burnerH * 0.6f - remainingLength

                // 線香の棒
                val stickEndY = incenseBottomY - burnerH * 0.5f
                drawLine(
                    color = Color(0xFF6B4226).copy(alpha = smokeAlpha),
                    start = Offset(centerX, incenseTopY),
                    end = Offset(centerX, stickEndY),
                    strokeWidth = 3.5f
                )
                drawLine(
                    color = Color(0xFF8B6914).copy(alpha = 0.4f * smokeAlpha),
                    start = Offset(centerX - 0.8f, incenseTopY),
                    end = Offset(centerX - 0.8f, stickEndY),
                    strokeWidth = 1f
                )

                // 燃焼先端
                val glowIntensity = 0.6f + sin(burnGlow) * 0.25f + cos(burnGlow * 2.3f) * 0.15f

                // 灰の部分
                drawLine(
                    color = Color(0xFF808080).copy(alpha = smokeAlpha),
                    start = Offset(centerX, incenseTopY),
                    end = Offset(centerX, incenseTopY + 6f),
                    strokeWidth = 3f
                )

                // 赤い燃焼グロー
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF4500).copy(alpha = 0.3f * glowIntensity * smokeAlpha),
                            Color(0xFFFF6600).copy(alpha = 0.1f * glowIntensity * smokeAlpha),
                            Color.Transparent
                        ),
                        center = Offset(centerX, incenseTopY),
                        radius = 20f
                    ),
                    radius = 20f,
                    center = Offset(centerX, incenseTopY)
                )
                drawCircle(
                    color = Color(0xFFFF4500).copy(alpha = glowIntensity * smokeAlpha),
                    radius = 3.5f,
                    center = Offset(centerX, incenseTopY)
                )
                drawCircle(
                    color = Color(0xFFFFE4B5).copy(alpha = glowIntensity * 0.6f * smokeAlpha),
                    radius = 1.5f,
                    center = Offset(centerX, incenseTopY)
                )

                // 煙パーティクル
                for (layer in 2 downTo 0) {
                    particles.filter { it.layer == layer }.forEach { particle ->
                        drawSmokeParticle(
                            particle = particle,
                            time = time,
                            time2 = time2,
                            time3 = time3,
                            originX = centerX,
                            originY = incenseTopY - 2f,
                            screenHeight = size.height,
                            screenWidth = size.width,
                            alpha = smokeAlpha
                        )
                    }
                }
            }

            // === 香炉（常時表示、線香より手前のレイヤー） ===
            drawIncenseBurner(centerX, incenseBottomY, size.width)
        }
    }
}

/**
 * 青磁の香炉を描画（蓮の花模様付き）
 */
private fun DrawScope.drawIncenseBurner(centerX: Float, bottomY: Float, screenWidth: Float) {
    val celadon = Color(0xFFACE1AF)
    val celadonDark = Color(0xFF7CB47F)
    val celadonLight = Color(0xFFCEF0D0)
    val goldAccent = Color(0xFFD4AF37)
    val burnerW = screenWidth * 0.12f
    val burnerH = screenWidth * 0.065f
    val bodyTop = bottomY - burnerH

    // ── 本体（お椀型、底がすぼまり上が広い） ──
    val bodyPath = Path().apply {
        moveTo(centerX - burnerW * 0.50f, bodyTop + burnerH * 0.15f)
        cubicTo(
            centerX - burnerW * 0.55f, bodyTop + burnerH * 0.5f,
            centerX - burnerW * 0.35f, bodyTop + burnerH * 0.85f,
            centerX - burnerW * 0.20f, bottomY
        )
        lineTo(centerX + burnerW * 0.20f, bottomY)
        cubicTo(
            centerX + burnerW * 0.35f, bodyTop + burnerH * 0.85f,
            centerX + burnerW * 0.55f, bodyTop + burnerH * 0.5f,
            centerX + burnerW * 0.50f, bodyTop + burnerH * 0.15f
        )
        close()
    }
    drawPath(
        path = bodyPath,
        brush = Brush.horizontalGradient(
            colors = listOf(celadonDark, celadon, celadonLight, celadon, celadonDark),
            startX = centerX - burnerW * 0.5f,
            endX = centerX + burnerW * 0.5f
        )
    )

    // ── 蓮の花模様（簡略化） ──
    val lotusY = bodyTop + burnerH * 0.5f
    val petalW = burnerW * 0.06f
    val petalH = burnerH * 0.30f
    for (i in -3..3) {
        val px = centerX + i * petalW * 1.4f
        drawOval(
            color = goldAccent.copy(alpha = 0.35f),
            topLeft = Offset(px - petalW / 2, lotusY - petalH / 2),
            size = Size(petalW, petalH)
        )
    }

    // ── 口の縁（金色のリム） ──
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(goldAccent, Color(0xFFB8960C)),
            startY = bodyTop,
            endY = bodyTop + burnerH * 0.2f
        ),
        topLeft = Offset(centerX - burnerW * 0.52f, bodyTop),
        size = Size(burnerW * 1.04f, burnerH * 0.2f)
    )
    // リム上面
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(celadonLight, celadon),
            center = Offset(centerX, bodyTop + burnerH * 0.05f),
            radius = burnerW * 0.4f
        ),
        topLeft = Offset(centerX - burnerW * 0.42f, bodyTop - burnerH * 0.03f),
        size = Size(burnerW * 0.84f, burnerH * 0.15f)
    )
}

/**
 * 個々の煙パーティクルを描画
 */
private fun DrawScope.drawSmokeParticle(
    particle: SmokeParticle,
    time: Float,
    time2: Float,
    time3: Float,
    originX: Float,
    originY: Float,
    screenHeight: Float,
    screenWidth: Float,
    alpha: Float
) {
    val progress = (time + particle.phaseOffset) % 1f
    val riseDistance = progress * screenHeight * particle.riseSpeed
    val y = originY - riseDistance

    val drift1 = sin(progress * 6.2832f * particle.driftFrequency + particle.phaseOffset) *
        screenWidth * particle.driftAmplitude
    val drift2 = cos(time2 * particle.driftFrequency * 0.7f + particle.phaseOffset * 2f) *
        screenWidth * particle.driftAmplitude * 0.5f
    val drift3 = sin(time3 * particle.driftFrequency * 1.3f + particle.phaseOffset * 0.5f) *
        screenWidth * particle.driftAmplitude * 0.3f
    val driftMultiplier = 1f + progress * 2f
    val totalDrift = (drift1 + drift2 + drift3) * driftMultiplier

    val x = originX + screenWidth * particle.startXOffset + totalDrift
    val currentSize = particle.baseSize * (1f + progress * particle.growthRate)
    val fadeProgress = progress.pow(particle.fadeRate)
    val particleAlpha = particle.baseAlpha * (1f - fadeProgress) * alpha

    if (y < -currentSize || particleAlpha < 0.005f) return

    val smokeColor = when (particle.layer) {
        0 -> {
            val grayAmount = progress * 0.12f
            Color(
                red = 0.92f - grayAmount,
                green = 0.92f - grayAmount,
                blue = 0.95f - grayAmount * 0.5f,
                alpha = particleAlpha.coerceIn(0f, 1f)
            )
        }
        1 -> Color(0.88f, 0.88f, 0.91f, particleAlpha.coerceIn(0f, 1f))
        else -> Color(0.82f, 0.82f, 0.86f, particleAlpha.coerceIn(0f, 1f))
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                smokeColor,
                smokeColor.copy(alpha = smokeColor.alpha * 0.65f),
                smokeColor.copy(alpha = 0f)
            ),
            center = Offset(x, y),
            radius = currentSize
        ),
        radius = currentSize,
        center = Offset(x, y)
    )

    if (particle.layer == 0 && progress < 0.6f) {
        val coreAlpha = particleAlpha * 0.5f * (1f - progress * 1.67f)
        if (coreAlpha > 0.01f) {
            drawCircle(
                color = Color.White.copy(alpha = coreAlpha.coerceIn(0f, 1f)),
                radius = currentSize * 0.45f,
                center = Offset(x, y)
            )
        }
    }
}
