package com.smartkuyo.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkuyo.app.data.local.db.SmartKuyoDatabase
import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.model.Denomination
import com.smartkuyo.app.domain.model.Photo
import com.smartkuyo.app.domain.model.TabletStyle
import com.smartkuyo.app.domain.model.UserPreferences
import com.smartkuyo.app.domain.repository.DeceasedRepository
import com.smartkuyo.app.domain.repository.PhotoRepository
import com.smartkuyo.app.domain.repository.PreferencesRepository
import com.smartkuyo.app.domain.usecase.AddPhotoUseCase
import com.smartkuyo.app.domain.usecase.GetDeceasedUseCase
import com.smartkuyo.app.domain.usecase.GetPhotosUseCase
import com.smartkuyo.app.domain.usecase.GetUserPreferencesUseCase
import com.smartkuyo.app.domain.usecase.SaveDeceasedUseCase
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
