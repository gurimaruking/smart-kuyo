package com.example.smartkuyo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartkuyo.data.local.db.SmartKuyoDatabase
import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.model.Denomination
import com.example.smartkuyo.domain.model.Photo
import com.example.smartkuyo.domain.model.TabletStyle
import com.example.smartkuyo.domain.model.UserPreferences
import com.example.smartkuyo.domain.repository.DeceasedRepository
import com.example.smartkuyo.domain.repository.PhotoRepository
import com.example.smartkuyo.domain.repository.PreferencesRepository
import com.example.smartkuyo.domain.usecase.AddPhotoUseCase
import com.example.smartkuyo.domain.usecase.GetDeceasedUseCase
import com.example.smartkuyo.domain.usecase.GetPhotosUseCase
import com.example.smartkuyo.domain.usecase.GetUserPreferencesUseCase
import com.example.smartkuyo.domain.usecase.SaveDeceasedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class SettingsUiState(
    val deceased: Deceased? = null,
    val photos: List<Photo> = emptyList(),
    val preferences: UserPreferences = UserPreferences()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getDeceasedUseCase: GetDeceasedUseCase,
    getPhotosUseCase: GetPhotosUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val saveDeceasedUseCase: SaveDeceasedUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val photoRepository: PhotoRepository,
    private val preferencesRepository: PreferencesRepository,
    private val database: SmartKuyoDatabase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        getDeceasedUseCase(),
        getPhotosUseCase(),
        getUserPreferencesUseCase()
    ) { deceased, photos, prefs ->
        SettingsUiState(deceased = deceased, photos = photos, preferences = prefs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateDeceased(deceased: Deceased) {
        viewModelScope.launch { saveDeceasedUseCase(deceased) }
    }

    fun addPhoto(deceasedId: Long, uri: String, order: Int) {
        viewModelScope.launch { addPhotoUseCase(deceasedId, uri, order) }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch { photoRepository.deletePhoto(photo) }
    }

    fun updateTabletStyle(style: TabletStyle) {
        viewModelScope.launch { preferencesRepository.updateTabletStyle(style) }
    }

    fun updateAutoSwitch(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateAutoSwitch(enabled) }
    }

    fun updateIhaiMinutes(minutes: Int) {
        viewModelScope.launch { preferencesRepository.updateIhaiDisplayMinutes(minutes) }
    }

    fun updateIeiMinutes(minutes: Int) {
        viewModelScope.launch { preferencesRepository.updateIeiDisplayMinutes(minutes) }
    }

    fun updateSlideshowInterval(seconds: Int) {
        viewModelScope.launch { preferencesRepository.updateSlideshowInterval(seconds) }
    }

    fun updateNightMode(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateNightMode(enabled) }
    }

    fun updateNightModeStart(time: LocalTime) {
        viewModelScope.launch { preferencesRepository.updateNightModeStart(time) }
    }

    fun updateNightModeEnd(time: LocalTime) {
        viewModelScope.launch { preferencesRepository.updateNightModeEnd(time) }
    }

    fun updateKeepScreenOn(keepOn: Boolean) {
        viewModelScope.launch { preferencesRepository.updateKeepScreenOn(keepOn) }
    }

    fun updateUseMonochrome(monochrome: Boolean) {
        viewModelScope.launch { preferencesRepository.updateUseMonochrome(monochrome) }
    }

    fun updateFullScreenDisplay(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateFullScreenDisplay(enabled) }
    }

    fun resetApp(onResetComplete: () -> Unit) {
        viewModelScope.launch {
            database.deceasedDao().deleteAll()
            database.photoDao().deleteAll()
            preferencesRepository.clearAll()
            onResetComplete()
        }
    }
}
