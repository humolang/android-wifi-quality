package com.humolang.wifiless.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.humolang.wifiless.data.datasources.DYNAMIC_COLOR_NAME
import com.humolang.wifiless.data.datasources.SYSTEM_THEME
import com.humolang.wifiless.data.datasources.THEME_NAME
import com.humolang.wifiless.dataStore
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val context: Context
) {

    private val dynamicColorKey =
        booleanPreferencesKey(DYNAMIC_COLOR_NAME)
    val dynamicColor = context.dataStore.data
        .map { settings ->
            settings[dynamicColorKey] ?: true
        }

    private val themeKey = intPreferencesKey(THEME_NAME)
    val theme = context.dataStore.data
        .map { settings ->
            settings[themeKey] ?: SYSTEM_THEME
        }

    suspend fun setDynamicColor(dynamicColor: Boolean) {
        context.dataStore.edit { settings ->
            settings[dynamicColorKey] = dynamicColor
        }
    }

    suspend fun setTheme(theme: Int) {
        context.dataStore.edit { settings ->
            settings[themeKey] = theme
        }
    }
}