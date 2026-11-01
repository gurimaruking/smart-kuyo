package com.smartkuyo.app.ui.iei

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.model.Photo
import com.smartkuyo.app.domain.usecase.GetDeceasedUseCase
import com.smartkuyo.app.domain.usecase.GetPhotosUseCase
import com.smartkuyo.app.domain.usecase.GetUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class IeiUiState(
    val deceased: Deceased? = null,
    val photos: List<Photo> = emptyList(),
    val slideshowIntervalMs: Long = 10_000L,
    val useMonochrome: Boolean = false
)

@HiltViewModel
class IeiViewModel @Inject constructor(
    getDeceasedUseCase: GetDeceasedUseCase,
    getPhotosUseCase: GetPhotosUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    val uiState: StateFlow<IeiUiState> = combine(
        getDeceasedUseCase(),
        getPhotosUseCase(),
        getUserPreferencesUseCase()
    ) { deceased, photos, prefs ->
        IeiUiState(
            deceased = deceased,
            photos = photos,
            slideshowIntervalMs = prefs.slideshowIntervalSeconds * 1000L,
            useMonochrome = prefs.useMonochrome
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IeiUiState()
    )
}
