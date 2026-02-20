package com.example.smartkuyo.ui.bell

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartkuyo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * おりんの音を管理するシングルトン
 * Compositionのライフサイクルに依存しないので、
 * オーバーレイが消えても音は途切れない
 */
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
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 300f),
        label = "bell_scale"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_orin),
        contentDescription = "おりんを鳴らす",
        modifier = modifier
            .size(72.dp)
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
    )
}
