package com.example.smartkuyo.domain.usecase

import com.example.smartkuyo.domain.repository.PreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

class IsNightModeActiveUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Boolean> =
        preferencesRepository.userPreferences.flatMapLatest { prefs ->
            if (!prefs.nightModeEnabled) {
                flow { emit(false) }
            } else {
                flow {
                    while (true) {
                        val now = LocalTime.now()
                        val isActive = isTimeInRange(now, prefs.nightModeStart, prefs.nightModeEnd)
                        emit(isActive)
                        delay(60_000L)
                    }
                }
            }
        }

    companion object {
        fun isTimeInRange(now: LocalTime, start: LocalTime, end: LocalTime): Boolean {
            return if (start <= end) {
                now in start..end
            } else {
                // 夜間が日付をまたぐ場合（例: 22:00 ~ 06:00）
                now >= start || now <= end
            }
        }
    }
}
