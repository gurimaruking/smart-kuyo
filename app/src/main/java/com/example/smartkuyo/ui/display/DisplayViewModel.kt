package com.example.smartkuyo.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartkuyo.domain.model.DisplayMode
import com.example.smartkuyo.domain.model.UserPreferences
import com.example.smartkuyo.domain.repository.PreferencesRepository
import com.example.smartkuyo.domain.usecase.GetUserPreferencesUseCase
import com.example.smartkuyo.domain.usecase.IsNightModeActiveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisplayUiState(
    val currentMode: DisplayMode = DisplayMode.IHAI,
    val isNightMode: Boolean = false,
    val keepScreenOn: Boolean = true,
    val fullScreenDisplay: Boolean = false
)

@HiltViewModel
class DisplayViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val isNightModeActiveUseCase: IsNightModeActiveUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val autoSwitchMode = MutableStateFlow(DisplayMode.IHAI)
    private var autoSwitchJob: Job? = null

    val preferences: StateFlow<UserPreferences> = getUserPreferencesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val uiState: StateFlow<DisplayUiState> = combine(
        preferences,
        isNightModeActiveUseCase(),
        autoSwitchMode
    ) { prefs, isNight, autoMode ->
        val currentMode = when {
            isNight -> DisplayMode.OFF
            prefs.autoSwitchEnabled -> autoMode
            else -> prefs.displayMode
        }
        DisplayUiState(
            currentMode = currentMode,
            isNightMode = isNight,
            keepScreenOn = prefs.keepScreenOn,
            fullScreenDisplay = prefs.fullScreenDisplay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DisplayUiState()
    )

    init {
        viewModelScope.launch {
            preferences.collect { prefs ->
                if (prefs.autoSwitchEnabled) {
                    startAutoSwitch(prefs.ihaiDisplayMinutes, prefs.ieiDisplayMinutes)
                } else {
                    stopAutoSwitch()
                }
            }
        }
    }

    private fun startAutoSwitch(ihaiMinutes: Int, ieiMinutes: Int) {
        autoSwitchJob?.cancel()
        autoSwitchJob = viewModelScope.launch {
            while (true) {
                autoSwitchMode.value = DisplayMode.IHAI
                delay(ihaiMinutes * 60_000L)
                autoSwitchMode.value = DisplayMode.IEI
                delay(ieiMinutes * 60_000L)
            }
        }
    }

    private fun stopAutoSwitch() {
        autoSwitchJob?.cancel()
        autoSwitchJob = null
    }

    fun switchMode(mode: DisplayMode) {
        viewModelScope.launch {
            preferencesRepository.updateDisplayMode(mode)
        }
    }

    fun toggleMode() {
        val current = uiState.value.currentMode
        val next = when (current) {
            DisplayMode.IHAI -> DisplayMode.IEI
            DisplayMode.IEI -> DisplayMode.IHAI
            DisplayMode.OFF -> return // 夜間モードでは切り替えない
        }
        // 自動切り替え中でも手動で切り替え可能に
        autoSwitchMode.value = next
        viewModelScope.launch {
            preferencesRepository.updateDisplayMode(next)
        }
    }
}
