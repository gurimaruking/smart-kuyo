package com.smartkuyo.app.ui.bell

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.smartkuyo.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object BellSoundManager {
    private var soundPool: SoundPool? = null
    private var bellSoundId: Int = 0
    private var isLoaded = false

    fun init(context: Context) {
        if (soundPool != null) return
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
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
                bellSoundId = pool.load(context, R.raw.orin, 1)
            }
    }

    fun play() {
        if (isLoaded && bellSoundId != 0) {
            soundPool?.play(bellSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
        bellSoundId = 0
    }
}

@Composable
fun BellButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 300f),
        label = "bell_scale"
    )

    Canvas(
        modifier = modifier
            .size(64.dp)
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onTap()
                scope.launch {
                    isPressed = true
                    BellSoundManager.play()
                    delay(500)
                    isPressed = false
                }
            }
    ) {
        drawOrin(size)
    }
}

private fun DrawScope.drawOrin(canvasSize: Size) {
    val cx = canvasSize.width / 2f
    val w = canvasSize.width
    val h = canvasSize.height

    // ── 黒漆の輪台（リングスタンド） ──
    val ringCy = h * 0.85f
    val ringRx = w * 0.44f
    val ringRy = h * 0.08f
    // 輪台影
    drawOval(
        color = Color(0xFF050505),
        topLeft = Offset(cx - ringRx, ringCy - ringRy * 0.2f),
        size = Size(ringRx * 2f, ringRy * 1.5f)
    )
    // 輪台本体
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF2A2A2A), Color(0xFF0A0A0A), Color(0xFF1A1A1A)),
            startY = ringCy - ringRy,
            endY = ringCy + ringRy * 0.8f
        ),
        topLeft = Offset(cx - ringRx, ringCy - ringRy),
        size = Size(ringRx * 2f, ringRy * 1.5f)
    )
    // 輪台の光沢
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3A3A3A).copy(alpha = 0.6f), Color.Transparent),
            startY = ringCy - ringRy * 0.8f,
            endY = ringCy
        ),
        topLeft = Offset(cx - ringRx * 0.85f, ringCy - ringRy * 0.8f),
        size = Size(ringRx * 1.7f, ringRy * 0.8f)
    )

    // ── 座布団（紫＋金の錦クッション） ──
    val cushionCy = h * 0.70f
    val cushionRx = w * 0.42f
    val cushionRy = h * 0.13f

    // クッション影
    drawOval(
        color = Color(0xFF1A0A2E),
        topLeft = Offset(cx - cushionRx, cushionCy - cushionRy * 0.2f),
        size = Size(cushionRx * 2f, cushionRy * 2f)
    )
    // クッション本体（紫）
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF4A2080), Color(0xFF3A1860), Color(0xFF2A1040)),
            startY = cushionCy - cushionRy,
            endY = cushionCy + cushionRy
        ),
        topLeft = Offset(cx - cushionRx, cushionCy - cushionRy),
        size = Size(cushionRx * 2f, cushionRy * 1.8f)
    )
    // クッション上の金の菱形模様
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFD4AF37).copy(alpha = 0.3f), Color.Transparent),
            startY = cushionCy - cushionRy * 0.7f,
            endY = cushionCy + cushionRy * 0.3f
        ),
        topLeft = Offset(cx - cushionRx * 0.75f, cushionCy - cushionRy * 0.7f),
        size = Size(cushionRx * 1.5f, cushionRy * 1.0f)
    )
    // クッション光沢
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF6B3FA0).copy(alpha = 0.4f), Color.Transparent),
            startY = cushionCy - cushionRy * 0.8f,
            endY = cushionCy
        ),
        topLeft = Offset(cx - cushionRx * 0.8f, cushionCy - cushionRy * 0.8f),
        size = Size(cushionRx * 1.6f, cushionRy * 1.0f)
    )

    // ── おりん本体（お椀型：上が広い口、下が狭い底） ──
    val bellBottom = cushionCy - cushionRy * 0.25f
    val bellTop = h * 0.20f
    val bellWidth = w * 0.58f
    val bellHeight = bellBottom - bellTop

    val bellPath = Path().apply {
        moveTo(cx - bellWidth * 0.48f, bellTop)
        cubicTo(
            cx - bellWidth * 0.50f, bellTop + bellHeight * 0.4f,
            cx - bellWidth * 0.35f, bellTop + bellHeight * 0.8f,
            cx - bellWidth * 0.18f, bellBottom
        )
        lineTo(cx + bellWidth * 0.18f, bellBottom)
        cubicTo(
            cx + bellWidth * 0.35f, bellTop + bellHeight * 0.8f,
            cx + bellWidth * 0.50f, bellTop + bellHeight * 0.4f,
            cx + bellWidth * 0.48f, bellTop
        )
        close()
    }

    drawPath(
        path = bellPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF8B7355),
                Color(0xFFD4AF37),
                Color(0xFFE8C860),
                Color(0xFFD4AF37),
                Color(0xFF8B6914)
            ),
            startX = cx - bellWidth * 0.5f,
            endX = cx + bellWidth * 0.5f
        )
    )

    // 口の縁
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE8C860), Color(0xFFD4AF37)),
            startY = bellTop - h * 0.02f,
            endY = bellTop + h * 0.035f
        ),
        topLeft = Offset(cx - bellWidth * 0.50f, bellTop - h * 0.015f),
        size = Size(bellWidth * 1.0f, h * 0.035f)
    )
    drawOval(
        color = Color(0xFFB8960C).copy(alpha = 0.5f),
        topLeft = Offset(cx - bellWidth * 0.50f, bellTop - h * 0.01f),
        size = Size(bellWidth * 1.0f, h * 0.025f),
        style = Stroke(width = 1f)
    )

    // 光沢ハイライト
    drawPath(
        path = Path().apply {
            moveTo(cx - bellWidth * 0.35f, bellTop + bellHeight * 0.1f)
            cubicTo(
                cx - bellWidth * 0.38f, bellTop + bellHeight * 0.4f,
                cx - bellWidth * 0.30f, bellTop + bellHeight * 0.75f,
                cx - bellWidth * 0.15f, bellBottom - bellHeight * 0.05f
            )
            lineTo(cx - bellWidth * 0.10f, bellBottom - bellHeight * 0.05f)
            cubicTo(
                cx - bellWidth * 0.22f, bellTop + bellHeight * 0.7f,
                cx - bellWidth * 0.28f, bellTop + bellHeight * 0.35f,
                cx - bellWidth * 0.25f, bellTop + bellHeight * 0.1f
            )
            close()
        },
        color = Color.White.copy(alpha = 0.2f)
    )

    // ── 撥（ばち）- 黒い木製 ──
    val bachiStartX = cx + bellWidth * 0.35f
    val bachiStartY = bellTop + bellHeight * 0.2f
    val bachiEndX = cx + w * 0.42f
    val bachiEndY = h * 0.92f

    // 撥の柄（黒い木）
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF1A1A1A), Color(0xFF3A2A1A), Color(0xFF2A1A0A), Color(0xFF1A1A1A)),
            start = Offset(bachiStartX, bachiStartY),
            end = Offset(bachiEndX, bachiEndY)
        ),
        start = Offset(bachiStartX, bachiStartY),
        end = Offset(bachiEndX, bachiEndY),
        strokeWidth = w * 0.03f,
        cap = StrokeCap.Round
    )
    // 柄の光沢
    drawLine(
        color = Color(0xFF4A3A2A).copy(alpha = 0.4f),
        start = Offset(bachiStartX - w * 0.005f, bachiStartY),
        end = Offset(bachiEndX - w * 0.005f, bachiEndY),
        strokeWidth = w * 0.008f,
        cap = StrokeCap.Round
    )

    // 撥の先端（丸い打ち部分）
    val tipR = w * 0.042f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF3A2A1A), Color(0xFF2A1A0A), Color(0xFF1A1008)),
            center = Offset(bachiStartX, bachiStartY),
            radius = tipR
        ),
        radius = tipR,
        center = Offset(bachiStartX, bachiStartY)
    )
    // 先端の光沢
    drawCircle(
        color = Color(0xFF5A4A3A).copy(alpha = 0.3f),
        radius = tipR * 0.5f,
        center = Offset(bachiStartX - tipR * 0.2f, bachiStartY - tipR * 0.2f)
    )
}
