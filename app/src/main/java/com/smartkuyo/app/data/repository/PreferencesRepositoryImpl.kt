package com.smartkuyo.app.data.repository

import com.smartkuyo.app.data.local.datastore.UserPreferencesDataStore
import com.smartkuyo.app.domain.model.DisplayMode
import com.smartkuyo.app.domain.model.TabletStyle
import com.smartkuyo.app.domain.model.UserPreferences
import com.smartkuyo.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : PreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.userPreferences

    override suspend fun updateActiveDeceasedId(id: Long) =
        dataStore.updateActiveDeceasedId(id)

    override suspend fun updateDisplayMode(mode: DisplayMode) =
        dataStore.updateDisplayMode(mode)

    override suspend fun updateTabletStyle(style: TabletStyle) =
        dataStore.updateTabletStyle(style)

    override suspend fun updateAutoSwitch(enabled: Boolean) =
        dataStore.updateAutoSwitch(enabled)

    override suspend fun updateIhaiDisplayMinutes(minutes: Int) =
        dataStore.updateIhaiDisplayMinutes(minutes)

    override suspend fun updateIeiDisplayMinutes(minutes: Int) =
        dataStore.updateIeiDisplayMinutes(minutes)

    override suspend fun updateSlideshowInterval(seconds: Int) =
        dataStore.updateSlideshowInterval(seconds)

    override suspend fun updateNightMode(enabled: Boolean) =
        dataStore.updateNightMode(enabled)

    override suspend fun updateNightModeStart(time: LocalTime) =
        dataStore.updateNightModeStart(time)

    override suspend fun updateNightModeEnd(time: LocalTime) =
        dataStore.updateNightModeEnd(time)

    override suspend fun updateSetupComplete(complete: Boolean) =
        dataStore.updateSetupComplete(complete)

    override suspend fun updateKeepScreenOn(keepOn: Boolean) =
        dataStore.updateKeepScreenOn(keepOn)

    override suspend fun updateUseMonochrome(monochrome: Boolean) =
        dataStore.updateUseMonochrome(monochrome)

    override suspend fun updateFullScreenDisplay(enabled: Boolean) =
        dataStore.updateFullScreenDisplay(enabled)

    override suspend fun clearAll() =
        dataStore.clearAll()
}
