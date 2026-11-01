package com.smartkuyo.app.domain.repository

import com.smartkuyo.app.domain.model.DisplayMode
import com.smartkuyo.app.domain.model.TabletStyle
import com.smartkuyo.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateActiveDeceasedId(id: Long)
    suspend fun updateDisplayMode(mode: DisplayMode)
    suspend fun updateTabletStyle(style: TabletStyle)
    suspend fun updateAutoSwitch(enabled: Boolean)
    suspend fun updateIhaiDisplayMinutes(minutes: Int)
    suspend fun updateIeiDisplayMinutes(minutes: Int)
    suspend fun updateSlideshowInterval(seconds: Int)
    suspend fun updateNightMode(enabled: Boolean)
    suspend fun updateNightModeStart(time: LocalTime)
    suspend fun updateNightModeEnd(time: LocalTime)
    suspend fun updateSetupComplete(complete: Boolean)
    suspend fun updateKeepScreenOn(keepOn: Boolean)
    suspend fun updateUseMonochrome(monochrome: Boolean)
    suspend fun updateFullScreenDisplay(enabled: Boolean)
    suspend fun clearAll()
}
