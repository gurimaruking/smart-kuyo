package com.example.smartkuyo.domain.model

import java.time.LocalTime

data class UserPreferences(
    val activeDeceasedId: Long? = null,
    val displayMode: DisplayMode = DisplayMode.IHAI,
    val tabletStyle: TabletStyle = TabletStyle.TRADITIONAL_BLACK,
    val autoSwitchEnabled: Boolean = false,
    val ihaiDisplayMinutes: Int = 30,
    val ieiDisplayMinutes: Int = 30,
    val slideshowIntervalSeconds: Int = 10,
    val nightModeEnabled: Boolean = false,
    val nightModeStart: LocalTime = LocalTime.of(22, 0),
    val nightModeEnd: LocalTime = LocalTime.of(6, 0),
    val isSetupComplete: Boolean = false,
    val keepScreenOn: Boolean = true,
    val useMonochrome: Boolean = false,
    val fullScreenDisplay: Boolean = false
)
