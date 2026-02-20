package com.example.smartkuyo.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smartkuyo.domain.model.DisplayMode
import com.example.smartkuyo.domain.model.TabletStyle
import com.example.smartkuyo.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val ACTIVE_DECEASED_ID = longPreferencesKey("active_deceased_id")
        val DISPLAY_MODE = stringPreferencesKey("display_mode")
        val TABLET_STYLE = stringPreferencesKey("tablet_style")
        val AUTO_SWITCH_ENABLED = booleanPreferencesKey("auto_switch_enabled")
        val IHAI_DISPLAY_MINUTES = intPreferencesKey("ihai_display_minutes")
        val IEI_DISPLAY_MINUTES = intPreferencesKey("iei_display_minutes")
        val SLIDESHOW_INTERVAL_SECONDS = intPreferencesKey("slideshow_interval_seconds")
        val NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")
        val NIGHT_MODE_START_HOUR = intPreferencesKey("night_mode_start_hour")
        val NIGHT_MODE_START_MINUTE = intPreferencesKey("night_mode_start_minute")
        val NIGHT_MODE_END_HOUR = intPreferencesKey("night_mode_end_hour")
        val NIGHT_MODE_END_MINUTE = intPreferencesKey("night_mode_end_minute")
        val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val USE_MONOCHROME = booleanPreferencesKey("use_monochrome")
        val FULL_SCREEN_DISPLAY = booleanPreferencesKey("full_screen_display")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            activeDeceasedId = prefs[ACTIVE_DECEASED_ID],
            displayMode = prefs[DISPLAY_MODE]?.let {
                runCatching { DisplayMode.valueOf(it) }.getOrDefault(DisplayMode.IHAI)
            } ?: DisplayMode.IHAI,
            tabletStyle = prefs[TABLET_STYLE]?.let {
                runCatching { TabletStyle.valueOf(it) }.getOrDefault(TabletStyle.TRADITIONAL_BLACK)
            } ?: TabletStyle.TRADITIONAL_BLACK,
            autoSwitchEnabled = prefs[AUTO_SWITCH_ENABLED] ?: false,
            ihaiDisplayMinutes = prefs[IHAI_DISPLAY_MINUTES] ?: 30,
            ieiDisplayMinutes = prefs[IEI_DISPLAY_MINUTES] ?: 30,
            slideshowIntervalSeconds = prefs[SLIDESHOW_INTERVAL_SECONDS] ?: 10,
            nightModeEnabled = prefs[NIGHT_MODE_ENABLED] ?: false,
            nightModeStart = LocalTime.of(
                prefs[NIGHT_MODE_START_HOUR] ?: 22,
                prefs[NIGHT_MODE_START_MINUTE] ?: 0
            ),
            nightModeEnd = LocalTime.of(
                prefs[NIGHT_MODE_END_HOUR] ?: 6,
                prefs[NIGHT_MODE_END_MINUTE] ?: 0
            ),
            isSetupComplete = prefs[IS_SETUP_COMPLETE] ?: false,
            keepScreenOn = prefs[KEEP_SCREEN_ON] ?: true,
            useMonochrome = prefs[USE_MONOCHROME] ?: false,
            fullScreenDisplay = prefs[FULL_SCREEN_DISPLAY] ?: false
        )
    }

    suspend fun updateActiveDeceasedId(id: Long) {
        dataStore.edit { it[ACTIVE_DECEASED_ID] = id }
    }

    suspend fun updateDisplayMode(mode: DisplayMode) {
        dataStore.edit { it[DISPLAY_MODE] = mode.name }
    }

    suspend fun updateTabletStyle(style: TabletStyle) {
        dataStore.edit { it[TABLET_STYLE] = style.name }
    }

    suspend fun updateAutoSwitch(enabled: Boolean) {
        dataStore.edit { it[AUTO_SWITCH_ENABLED] = enabled }
    }

    suspend fun updateIhaiDisplayMinutes(minutes: Int) {
        dataStore.edit { it[IHAI_DISPLAY_MINUTES] = minutes }
    }

    suspend fun updateIeiDisplayMinutes(minutes: Int) {
        dataStore.edit { it[IEI_DISPLAY_MINUTES] = minutes }
    }

    suspend fun updateSlideshowInterval(seconds: Int) {
        dataStore.edit { it[SLIDESHOW_INTERVAL_SECONDS] = seconds }
    }

    suspend fun updateNightMode(enabled: Boolean) {
        dataStore.edit { it[NIGHT_MODE_ENABLED] = enabled }
    }

    suspend fun updateNightModeStart(time: LocalTime) {
        dataStore.edit {
            it[NIGHT_MODE_START_HOUR] = time.hour
            it[NIGHT_MODE_START_MINUTE] = time.minute
        }
    }

    suspend fun updateNightModeEnd(time: LocalTime) {
        dataStore.edit {
            it[NIGHT_MODE_END_HOUR] = time.hour
            it[NIGHT_MODE_END_MINUTE] = time.minute
        }
    }

    suspend fun updateSetupComplete(complete: Boolean) {
        dataStore.edit { it[IS_SETUP_COMPLETE] = complete }
    }

    suspend fun updateKeepScreenOn(keepOn: Boolean) {
        dataStore.edit { it[KEEP_SCREEN_ON] = keepOn }
    }

    suspend fun updateUseMonochrome(monochrome: Boolean) {
        dataStore.edit { it[USE_MONOCHROME] = monochrome }
    }

    suspend fun updateFullScreenDisplay(enabled: Boolean) {
        dataStore.edit { it[FULL_SCREEN_DISPLAY] = enabled }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
