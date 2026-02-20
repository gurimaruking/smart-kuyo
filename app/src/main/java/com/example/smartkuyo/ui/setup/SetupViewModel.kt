package com.example.smartkuyo.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.model.Denomination
import com.example.smartkuyo.domain.model.Religion
import com.example.smartkuyo.domain.repository.PreferencesRepository
import com.example.smartkuyo.domain.usecase.AddPhotoUseCase
import com.example.smartkuyo.domain.usecase.SaveDeceasedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SetupUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    // Step 1: 宗教
    val selectedReligion: Religion? = null,
    val selectedDenomination: Denomination? = null,
    // Step 2: 故人情報
    val familyName: String = "",
    val givenName: String = "",
    val posthumousName: String = "",
    val dateOfDeath: LocalDate = LocalDate.now(),
    val dateOfBirth: LocalDate? = null,
    val ageAtDeath: String = "",
    // Step 3: 写真
    val photoUris: List<String> = emptyList(),
    // Step 4: 表示設定
    val autoSwitch: Boolean = false,
    val nightMode: Boolean = false,
    val useMonochrome: Boolean = false,
    // 状態
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canProceed: Boolean
        get() = when (currentStep) {
            0 -> selectedDenomination != null
            1 -> familyName.isNotBlank() && givenName.isNotBlank()
            2 -> true // 写真は任意
            3 -> true
            else -> false
        }
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val saveDeceasedUseCase: SaveDeceasedUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun selectReligion(religion: Religion) {
        _uiState.update {
            it.copy(
                selectedReligion = religion,
                selectedDenomination = null
            )
        }
    }

    fun selectDenomination(denomination: Denomination) {
        _uiState.update { it.copy(selectedDenomination = denomination) }
    }

    fun updateFamilyName(name: String) {
        _uiState.update { it.copy(familyName = name) }
    }

    fun updateGivenName(name: String) {
        _uiState.update { it.copy(givenName = name) }
    }

    fun updatePosthumousName(name: String) {
        _uiState.update { it.copy(posthumousName = name) }
    }

    fun updateDateOfDeath(date: LocalDate) {
        _uiState.update { it.copy(dateOfDeath = date) }
    }

    fun updateAgeAtDeath(age: String) {
        _uiState.update { it.copy(ageAtDeath = age) }
    }

    fun addPhotoUri(uri: String) {
        _uiState.update { it.copy(photoUris = it.photoUris + uri) }
    }

    fun removePhotoUri(uri: String) {
        _uiState.update { it.copy(photoUris = it.photoUris - uri) }
    }

    fun updateAutoSwitch(enabled: Boolean) {
        _uiState.update { it.copy(autoSwitch = enabled) }
    }

    fun updateNightMode(enabled: Boolean) {
        _uiState.update { it.copy(nightMode = enabled) }
    }

    fun updateUseMonochrome(enabled: Boolean) {
        _uiState.update { it.copy(useMonochrome = enabled) }
    }

    fun nextStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(it.totalSteps - 1)) }
    }

    fun previousStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }
    }

    fun completeSetup(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val state = _uiState.value
                val deceased = Deceased(
                    familyName = state.familyName,
                    givenName = state.givenName,
                    posthumousName = state.posthumousName.ifBlank { null },
                    denomination = state.selectedDenomination ?: Denomination.NONE,
                    dateOfDeath = state.dateOfDeath,
                    dateOfBirth = state.dateOfBirth,
                    ageAtDeath = state.ageAtDeath.toIntOrNull()
                )
                val deceasedId = saveDeceasedUseCase(deceased)

                state.photoUris.forEachIndexed { index, uri ->
                    addPhotoUseCase(deceasedId, uri, index)
                }

                preferencesRepository.updateAutoSwitch(state.autoSwitch)
                preferencesRepository.updateNightMode(state.nightMode)
                preferencesRepository.updateUseMonochrome(state.useMonochrome)
                preferencesRepository.updateSetupComplete(true)

                _uiState.update { it.copy(isLoading = false) }
                onComplete()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
