package com.example.smartkuyo.ui.candle

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
import com.example.smartkuyo.R
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

            val candleWidth = w * 0.05f
            val candleHeight = h * 0.25f
            val flameHeight = h * 0.10f

            val leftX = w * 0.10f
            val rightX = w * 0.90f
            val candleBottomY = h * 0.80f

            // 左の燭台＋ろうそく
            drawCandleStand(leftX, candleBottomY, candleWidth)
            if (leftCandleAlpha > 0.01f) {
                drawCandleBody(leftX, candleBottomY, candleWidth, candleHeight, leftCandleAlpha)
                if (leftFlameAlpha > 0.01f) {
                    drawFlame(leftX, candleBottomY - candleHeight, candleWidth, flameHeight, time1, time2, time3, leftFlameAlpha)
                }
            }

            // 右の燭台＋ろうそく
            drawCandleStand(rightX, candleBottomY, candleWidth)
            if (rightCandleAlpha > 0.01f) {
                drawCandleBody(rightX, candleBottomY, candleWidth, candleHeight, rightCandleAlpha)
                if (rightFlameAlpha > 0.01f) {
                    drawFlame(rightX, candleBottomY - candleHeight, candleWidth, flameHeight, time1, time2, time3, rightFlameAlpha)
                }
            }
        }
    }
}

/**
 * 燭台を描画（常時表示）
 */
private fun DrawScope.drawCandleStand(centerX: Float, bottomY: Float, candleWidth: Float) {
    val baseWidth = candleWidth * 2.5f
    val baseHeight = candleWidth * 0.6f

    // 台座下部
    drawRoundRect(
        color = Color(0xFF8B7355),
        topLeft = Offset(centerX - baseWidth / 2, bottomY),
        size = Size(baseWidth, baseHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    // 台座装飾ライン
    drawRoundRect(
        color = Color(0xFFB8960C),
        topLeft = Offset(centerX - baseWidth / 2 + 2f, bottomY + 1f),
        size = Size(baseWidth - 4f, 2f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f, 1f)
    )
    // 受け皿
    val saucerWidth = candleWidth * 1.8f
    drawOval(
        color = Color(0xFF8B7355),
        topLeft = Offset(centerX - saucerWidth / 2, bottomY - baseHeight * 0.3f),
        size = Size(saucerWidth, baseHeight * 0.5f)
    )
    drawOval(
        color = Color(0xFFB8960C).copy(alpha = 0.5f),
        topLeft = Offset(centerX - saucerWidth / 2 + 2f, bottomY - baseHeight * 0.3f + 1f),
        size = Size(saucerWidth - 4f, baseHeight * 0.3f)
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
