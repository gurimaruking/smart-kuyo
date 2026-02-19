package com.example.smartkuyo.ui.ihai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartkuyo.domain.model.Deceased
import com.example.smartkuyo.domain.model.TabletStyle
import com.example.smartkuyo.domain.usecase.GetDeceasedUseCase
import com.example.smartkuyo.domain.usecase.GetUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class IhaiUiState(
    val deceased: Deceased? = null,
    val tabletStyle: TabletStyle = TabletStyle.TRADITIONAL_BLACK
)

@HiltViewModel
class IhaiViewModel @Inject constructor(
    getDeceasedUseCase: GetDeceasedUseCase,
    getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    val uiState: StateFlow<IhaiUiState> = combine(
        getDeceasedUseCase(),
        getUserPreferencesUseCase()
    ) { deceased, prefs ->
        IhaiUiState(
            deceased = deceased,
            tabletStyle = prefs.tabletStyle
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IhaiUiState()
    )
}
