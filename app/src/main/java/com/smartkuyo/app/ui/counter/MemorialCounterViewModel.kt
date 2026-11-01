package com.smartkuyo.app.ui.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkuyo.app.domain.model.Deceased
import com.smartkuyo.app.domain.model.MemorialDate
import com.smartkuyo.app.domain.model.MemorialDateCalculator
import com.smartkuyo.app.domain.usecase.GetDeceasedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MemorialCounterUiState(
    val deceased: Deceased? = null,
    val daysSinceDeath: Long = 0,
    val nextMemorial: MemorialDate? = null,
    val upcomingMemorials: List<MemorialDate> = emptyList()
)

@HiltViewModel
class MemorialCounterViewModel @Inject constructor(
    getDeceasedUseCase: GetDeceasedUseCase
) : ViewModel() {

    val uiState: StateFlow<MemorialCounterUiState> = getDeceasedUseCase()
        .map { deceased ->
            if (deceased == null) {
                MemorialCounterUiState()
            } else {
                val allDates = MemorialDateCalculator.calculateAll(deceased.dateOfDeath)
                val futureDates = allDates.filter { it.daysFromNow >= 0 }
                MemorialCounterUiState(
                    deceased = deceased,
                    daysSinceDeath = MemorialDateCalculator.getDaysSinceDeath(deceased.dateOfDeath),
                    nextMemorial = futureDates.minByOrNull { it.daysFromNow },
                    upcomingMemorials = futureDates.take(10)
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MemorialCounterUiState()
        )
}
