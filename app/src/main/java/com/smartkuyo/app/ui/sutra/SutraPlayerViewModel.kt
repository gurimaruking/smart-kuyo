package com.smartkuyo.app.ui.sutra

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkuyo.app.R
import com.smartkuyo.app.domain.model.Denomination
import com.smartkuyo.app.domain.model.SutraType
import com.smartkuyo.app.domain.usecase.GetDeceasedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SutraPlayerUiState(
    val availableSutras: List<SutraType> = emptyList(),
    val selectedSutra: SutraType? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val denomination: Denomination? = null
)

@HiltViewModel
class SutraPlayerViewModel @Inject constructor(
    getDeceasedUseCase: GetDeceasedUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    _playerState.update {
                        it.copy(currentPosition = mp.currentPosition, duration = mp.duration)
                    }
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    private val _playerState = MutableStateFlow(SutraPlayerUiState())

    val uiState: StateFlow<SutraPlayerUiState> = getDeceasedUseCase()
        .map { deceased ->
            val denomination = deceased?.denomination ?: Denomination.NONE
            val sutras = SutraType.getForDenomination(denomination)
            _playerState.update {
                it.copy(
                    availableSutras = sutras,
                    denomination = denomination
                )
            }
            _playerState.value
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SutraPlayerUiState()
        )

    val playerState: StateFlow<SutraPlayerUiState> = _playerState.asStateFlow()

    fun selectSutra(sutra: SutraType) {
        if (_playerState.value.selectedSutra != sutra) {
            stop()
        }
        _playerState.update { it.copy(selectedSutra = sutra) }
    }

    fun togglePlayback() {
        val state = _playerState.value
        if (state.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    private fun play() {
        val sutra = _playerState.value.selectedSutra ?: return
        if (mediaPlayer == null) {
            try {
                val resId = getSutraResourceId(sutra)
                if (resId != 0) {
                    mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                        setOnCompletionListener {
                            handler.removeCallbacks(progressRunnable)
                            _playerState.update {
                                it.copy(isPlaying = false, currentPosition = 0)
                            }
                        }
                        start()
                        _playerState.update {
                            it.copy(isPlaying = true, duration = duration)
                        }
                    }
                    handler.post(progressRunnable)
                    return
                }
            } catch (_: Exception) { }
        } else {
            mediaPlayer?.start()
            handler.post(progressRunnable)
        }
        _playerState.update { it.copy(isPlaying = true) }
    }

    private fun pause() {
        mediaPlayer?.pause()
        handler.removeCallbacks(progressRunnable)
        _playerState.update { it.copy(isPlaying = false) }
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        _playerState.update { it.copy(isPlaying = false, currentPosition = 0, duration = 0) }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playerState.update { it.copy(currentPosition = positionMs) }
    }

    private fun getSutraResourceId(sutra: SutraType): Int {
        return when (sutra) {
            SutraType.HANNYA_SHINGYO -> R.raw.hannya_shingyo
            SutraType.KOMYO_SHINGON -> R.raw.sonshyo_darani
            SutraType.MITSUGONINHOTSUROZANGEMON -> R.raw.mitsugoninhotsurozangemon
            SutraType.SHUSHOGI -> R.raw.shushogi_1_3
            SutraType.SHUSHOGI_PART2 -> R.raw.shushogi_4_5
            SutraType.DAIHI_SHU -> R.raw.daihi
            SutraType.JODOJI_SHINGYO -> R.raw.jodoji_shingyo
            SutraType.AVE_MARIA -> R.raw.ave_maria
            SutraType.AMAZING_GRACE -> R.raw.amazing_grace
            else -> 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
