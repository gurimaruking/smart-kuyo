package com.smartkuyo.app.ui.candle

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smartkuyo.app.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * 着火音（シュボッ）を管理するシングルトン
 */
object LighterSoundManager {
    private var soundPool: SoundPool? = null
    private var lighterSoundId: Int = 0
    private var isLoaded = false

    fun init(context: Context) {
        if (soundPool != null) return
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
            .also { pool ->
                pool.setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) isLoaded = true
                }
                lighterSoundId = pool.load(context, R.raw.lighter, 1)
            }
    }

    fun play() {
        if (isLoaded && lighterSoundId != 0) {
            soundPool?.play(lighterSoundId, 0.8f, 0.8f, 1, 0, 1f)
        }
    }
}

/**
 * ろうそくの状態
 * STAND_ONLY: 燭台のみ表示
 * CANDLE_UNLIT: ろうそくが載っている（火なし）
 * CANDLE_LIT: ろうそくに火がついている
 */
enum class CandleState {
    STAND_ONLY,
    CANDLE_UNLIT,
    CANDLE_LIT
}

/**
 * タップ式デジタルろうそく（左右に1本ずつ）
 * - 燭台は常時表示
 * - タップで ろうそく表示 → 着火 → 消火 → ろうそく非表示 のサイクル
 */
@Composable
fun CandleEffect(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val context = LocalContext.current
    LighterSoundManager.init(context)

    var leftState by remember { mutableStateOf(CandleState.STAND_ONLY) }
    var rightState by remember { mutableStateOf(CandleState.STAND_ONLY) }

    val infiniteTransition = rememberInfiniteTransition(label = "candle")

    val time1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_time1"
    )
    val time2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_time2"
    )
    val time3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_time3"
    )

    // フェードアニメーション
    val leftCandleAlpha by animateFloatAsState(
        targetValue = if (leftState != CandleState.STAND_ONLY) 1f else 0f,
        animationSpec = tween(500),
        label = "left_candle_alpha"
    )
    val rightCandleAlpha by animateFloatAsState(
        targetValue = if (rightState != CandleState.STAND_ONLY) 1f else 0f,
        animationSpec = tween(500),
        label = "right_candle_alpha"
    )
    val leftFlameAlpha by animateFloatAsState(
        targetValue = if (leftState == CandleState.CANDLE_LIT) 1f else 0f,
        animationSpec = tween(400),
        label = "left_flame_alpha"
    )
    val rightFlameAlpha by animateFloatAsState(
        targetValue = if (rightState == CandleState.CANDLE_LIT) 1f else 0f,
        animationSpec = tween(400),
        label = "right_flame_alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // 左ろうそくタップエリア
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(100.dp)
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    val prev = leftState
                    leftState = when (leftState) {
                        CandleState.STAND_ONLY -> CandleState.CANDLE_UNLIT
                        CandleState.CANDLE_UNLIT -> CandleState.CANDLE_LIT
                        CandleState.CANDLE_LIT -> CandleState.STAND_ONLY
                    }
                    if (prev == CandleState.CANDLE_UNLIT && leftState == CandleState.CANDLE_LIT) {
                        LighterSoundManager.play()
                    }
                }
        )

        // 右ろうそくタップエリア
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(100.dp)
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    val prev = rightState
                    rightState = when (rightState) {
                        CandleState.STAND_ONLY -> CandleState.CANDLE_UNLIT
                        CandleState.CANDLE_UNLIT -> CandleState.CANDLE_LIT
                        CandleState.CANDLE_LIT -> CandleState.STAND_ONLY
                    }
                    if (prev == CandleState.CANDLE_UNLIT && rightState == CandleState.CANDLE_LIT) {
                        LighterSoundManager.play()
                    }
                }
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val candleWidth = w * 0.04f
            val candleHeight = h * 0.12f
            val flameHeight = h * 0.06f

            val leftX = w * 0.12f
            val rightX = w * 0.88f
            val candleBottomY = h * 0.82f

            // 燭台の受け皿トップY（ろうそくの底の位置）を計算
            // 各パーツ比率合算: base0.06+gap0.01 + ring0.05+gap0.01 + ball10.10+gap0.005
            // + neck0.06 + ball20.09+gap0.005 + neck20.05 + ball30.07+gap0.005
            // + neck30.04 + saucer0.06 = 0.615 (受け皿トップ)
            val totalH = candleWidth * 7f
            val candleBaseY = candleBottomY - totalH * 0.615f

            // 左: ろうそく本体を先に描画（奥）→ 燭台を後で描画（手前）
            if (leftCandleAlpha > 0.01f) {
                drawCandleBody(leftX, candleBaseY, candleWidth, candleHeight, leftCandleAlpha)
                if (leftFlameAlpha > 0.01f) {
                    drawFlame(leftX, candleBaseY - candleHeight, candleWidth, flameHeight, time1, time2, time3, leftFlameAlpha)
                }
            }
            drawCandleStand(leftX, candleBottomY, candleWidth)

            // 右: ろうそく本体を先に描画（奥）→ 燭台を後で描画（手前）
            if (rightCandleAlpha > 0.01f) {
                drawCandleBody(rightX, candleBaseY, candleWidth, candleHeight, rightCandleAlpha)
                if (rightFlameAlpha > 0.01f) {
                    drawFlame(rightX, candleBaseY - candleHeight, candleWidth, flameHeight, time1, time2, time3, rightFlameAlpha)
                }
            }
            drawCandleStand(rightX, candleBottomY, candleWidth)
        }
    }
}

/**
 * 金色の仏壇用燭台を描画（段重ねデザイン）
 */
private fun DrawScope.drawCandleStand(centerX: Float, bottomY: Float, candleWidth: Float) {
    val gold = Color(0xFFD4AF37)
    val goldLight = Color(0xFFE8D060)
    val goldDark = Color(0xFF8B6914)
    val goldShadow = Color(0xFF6B5210)

    val baseW = candleWidth * 4.0f
    val totalH = candleWidth * 7f
    var currentY = bottomY

    // ── 台座（一番下の広い円盤） ──
    val baseH = totalH * 0.06f
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(goldLight, gold, goldDark),
            startY = currentY - baseH,
            endY = currentY
        ),
        topLeft = Offset(centerX - baseW / 2, currentY - baseH),
        size = Size(baseW, baseH)
    )
    drawOval(
        color = goldShadow.copy(alpha = 0.3f),
        topLeft = Offset(centerX - baseW / 2, currentY - baseH * 0.3f),
        size = Size(baseW, baseH * 0.4f)
    )
    currentY -= baseH + totalH * 0.01f

    // ── 第1段リング ──
    val ring1W = baseW * 0.82f
    val ring1H = totalH * 0.05f
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(goldLight, gold, goldDark),
            startY = currentY - ring1H,
            endY = currentY
        ),
        topLeft = Offset(centerX - ring1W / 2, currentY - ring1H),
        size = Size(ring1W, ring1H)
    )
    currentY -= ring1H + totalH * 0.01f

    // ── 第2段球（大きめ） ──
    val ball1W = baseW * 0.55f
    val ball1H = totalH * 0.10f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(goldLight, gold, goldDark),
            center = Offset(centerX - ball1W * 0.15f, currentY - ball1H * 0.4f),
            radius = ball1W * 0.6f
        ),
        topLeft = Offset(centerX - ball1W / 2, currentY - ball1H),
        size = Size(ball1W, ball1H)
    )
    currentY -= ball1H + totalH * 0.005f

    // ── 細い首 ──
    val neckW = baseW * 0.18f
    val neckH = totalH * 0.06f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(goldDark, gold, goldLight, gold, goldDark),
            startX = centerX - neckW / 2,
            endX = centerX + neckW / 2
        ),
        topLeft = Offset(centerX - neckW / 2, currentY - neckH),
        size = Size(neckW, neckH)
    )
    currentY -= neckH

    // ── 第3段球（中） ──
    val ball2W = baseW * 0.45f
    val ball2H = totalH * 0.09f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(goldLight, gold, goldDark),
            center = Offset(centerX - ball2W * 0.15f, currentY - ball2H * 0.4f),
            radius = ball2W * 0.6f
        ),
        topLeft = Offset(centerX - ball2W / 2, currentY - ball2H),
        size = Size(ball2W, ball2H)
    )
    currentY -= ball2H + totalH * 0.005f

    // ── 細い首2 ──
    val neck2H = totalH * 0.05f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(goldDark, gold, goldLight, gold, goldDark),
            startX = centerX - neckW / 2,
            endX = centerX + neckW / 2
        ),
        topLeft = Offset(centerX - neckW / 2, currentY - neck2H),
        size = Size(neckW, neck2H)
    )
    currentY -= neck2H

    // ── 第4段球（小） ──
    val ball3W = baseW * 0.35f
    val ball3H = totalH * 0.07f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(goldLight, gold, goldDark),
            center = Offset(centerX - ball3W * 0.15f, currentY - ball3H * 0.4f),
            radius = ball3W * 0.5f
        ),
        topLeft = Offset(centerX - ball3W / 2, currentY - ball3H),
        size = Size(ball3W, ball3H)
    )
    currentY -= ball3H + totalH * 0.005f

    // ── 上部の首 ──
    val neck3H = totalH * 0.04f
    val neck3W = baseW * 0.15f
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(goldDark, gold, goldLight, gold, goldDark),
            startX = centerX - neck3W / 2,
            endX = centerX + neck3W / 2
        ),
        topLeft = Offset(centerX - neck3W / 2, currentY - neck3H),
        size = Size(neck3W, neck3H)
    )
    currentY -= neck3H

    // ── 受け皿（お椀型） ──
    val saucerW = baseW * 0.70f
    val saucerH = totalH * 0.06f
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(goldLight, gold, goldDark),
            startY = currentY - saucerH,
            endY = currentY
        ),
        topLeft = Offset(centerX - saucerW / 2, currentY - saucerH),
        size = Size(saucerW, saucerH)
    )
    // 受け皿の上面
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(goldLight, gold),
            center = Offset(centerX, currentY - saucerH),
            radius = saucerW * 0.4f
        ),
        topLeft = Offset(centerX - saucerW * 0.4f, currentY - saucerH - saucerH * 0.2f),
        size = Size(saucerW * 0.8f, saucerH * 0.5f)
    )
    currentY -= saucerH

    // ── 芯立て（中央のピン） ──
    val pinH = totalH * 0.04f
    drawLine(
        color = gold,
        start = Offset(centerX, currentY),
        end = Offset(centerX, currentY - pinH),
        strokeWidth = 2.5f
    )
}

/**
 * ろうそく本体を描画
 */
private fun DrawScope.drawCandleBody(
    centerX: Float,
    bottomY: Float,
    candleWidth: Float,
    candleHeight: Float,
    alpha: Float
) {
    val topY = bottomY - candleHeight

    // ろうそく本体
    val candleBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF8DC).copy(alpha = alpha),
            Color(0xFFFFE4B5).copy(alpha = alpha),
            Color(0xFFFFDAB9).copy(alpha = alpha),
            Color(0xFFDEB887).copy(alpha = alpha),
        ),
        startY = topY,
        endY = bottomY
    )
    drawRect(
        brush = candleBrush,
        topLeft = Offset(centerX - candleWidth / 2, topY),
        size = Size(candleWidth, candleHeight)
    )

    // ハイライト
    drawRect(
        color = Color.White.copy(alpha = 0.15f * alpha),
        topLeft = Offset(centerX - candleWidth / 2, topY),
        size = Size(candleWidth * 0.3f, candleHeight)
    )

    // 蝋が溶けた表現
    drawOval(
        color = Color(0xFFFFF8DC).copy(alpha = alpha),
        topLeft = Offset(centerX - candleWidth / 2 - 2f, topY - 3f),
        size = Size(candleWidth + 4f, 8f)
    )

    // 芯
    drawLine(
        color = Color(0xFF2F2F2F).copy(alpha = alpha),
        start = Offset(centerX, topY),
        end = Offset(centerX, topY - 8f),
        strokeWidth = 2f
    )
}

/**
 * 炎を描画
 */
private fun DrawScope.drawFlame(
    centerX: Float,
    candleTopY: Float,
    candleWidth: Float,
    flameHeight: Float,
    time1: Float,
    time2: Float,
    time3: Float,
    alpha: Float
) {
    val flameBaseY = candleTopY - 8f
    val flameTipY = flameBaseY - flameHeight

    val sway1 = sin(time1) * candleWidth * 0.15f
    val sway2 = sin(time2 * 1.7f) * candleWidth * 0.08f
    val sway3 = cos(time3 * 0.7f) * candleWidth * 0.05f
    val totalSway = sway1 + sway2 + sway3

    val flameScale = 1f + sin(time2 * 2f) * 0.08f + cos(time3) * 0.05f

    // グロー
    val glowRadius = flameHeight * 0.8f * flameScale
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF8C00).copy(alpha = 0.15f * alpha),
                Color(0xFFFF4500).copy(alpha = 0.05f * alpha),
                Color.Transparent
            ),
            center = Offset(centerX + totalSway * 0.5f, flameBaseY - flameHeight * 0.4f),
            radius = glowRadius * 1.5f
        ),
        radius = glowRadius * 1.5f,
        center = Offset(centerX + totalSway * 0.5f, flameBaseY - flameHeight * 0.4f)
    )

    // 外炎
    val outerFlamePath = Path().apply {
        moveTo(centerX - candleWidth * 0.4f, flameBaseY)
        cubicTo(
            centerX - candleWidth * 0.5f * flameScale,
            flameBaseY - flameHeight * 0.3f,
            centerX - candleWidth * 0.3f * flameScale + totalSway * 0.3f,
            flameBaseY - flameHeight * 0.6f,
            centerX + totalSway,
            flameTipY * flameScale + flameBaseY * (1f - flameScale)
        )
        cubicTo(
            centerX + candleWidth * 0.3f * flameScale + totalSway * 0.3f,
            flameBaseY - flameHeight * 0.6f,
            centerX + candleWidth * 0.5f * flameScale,
            flameBaseY - flameHeight * 0.3f,
            centerX + candleWidth * 0.4f,
            flameBaseY
        )
        close()
    }

    drawPath(
        path = outerFlamePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF4500).copy(alpha = 0.7f * alpha),
                Color(0xFFFF8C00).copy(alpha = 0.85f * alpha),
                Color(0xFFFFD700).copy(alpha = 0.9f * alpha),
            ),
            startY = flameTipY,
            endY = flameBaseY
        )
    )

    // 内炎
    val innerFlameHeight = flameHeight * 0.65f * flameScale
    val innerFlamePath = Path().apply {
        moveTo(centerX - candleWidth * 0.2f, flameBaseY)
        cubicTo(
            centerX - candleWidth * 0.25f * flameScale,
            flameBaseY - innerFlameHeight * 0.35f,
            centerX - candleWidth * 0.1f + totalSway * 0.5f,
            flameBaseY - innerFlameHeight * 0.7f,
            centerX + totalSway * 0.8f,
            flameBaseY - innerFlameHeight
        )
        cubicTo(
            centerX + candleWidth * 0.1f + totalSway * 0.5f,
            flameBaseY - innerFlameHeight * 0.7f,
            centerX + candleWidth * 0.25f * flameScale,
            flameBaseY - innerFlameHeight * 0.35f,
            centerX + candleWidth * 0.2f,
            flameBaseY
        )
        close()
    }

    drawPath(
        path = innerFlamePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFF00).copy(alpha = 0.8f * alpha),
                Color(0xFFFFFFF0).copy(alpha = 0.95f * alpha),
                Color(0xFFFFFFE0).copy(alpha = 0.9f * alpha),
            ),
            startY = flameBaseY - innerFlameHeight,
            endY = flameBaseY
        )
    )

    // 青い炎
    val blueFlameHeight = flameHeight * 0.2f
    val blueFlamePath = Path().apply {
        moveTo(centerX - candleWidth * 0.08f, flameBaseY)
        quadraticTo(
            centerX + totalSway * 0.2f,
            flameBaseY - blueFlameHeight,
            centerX + candleWidth * 0.08f,
            flameBaseY
        )
        close()
    }
    drawPath(
        path = blueFlamePath,
        color = Color(0xFF4169E1).copy(alpha = (0.5f + sin(time1) * 0.15f) * alpha)
    )

    // 照り返し
    val reflectionAlpha = (0.1f + sin(time2) * 0.05f) * alpha
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFD700).copy(alpha = reflectionAlpha),
                Color.Transparent
            ),
            startY = candleTopY,
            endY = candleTopY + 40f
        ),
        topLeft = Offset(centerX - candleWidth / 2, candleTopY),
        size = Size(candleWidth, 40f)
    )
}
