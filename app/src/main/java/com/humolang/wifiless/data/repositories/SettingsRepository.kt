/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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