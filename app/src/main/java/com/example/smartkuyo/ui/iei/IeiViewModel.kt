package com.example.smartkuyo.ui.iei

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.model.Photo
import com.example.smartkuyo.domain.usecase.GetDeceasedUseCase
import com.example.smartkuyo.domain.usecase.GetPhotosUseCase
import com.example.smartkuyo.domain.usecase.GetUserPreferencesUseCase
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
