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

package com.humolang.wifiless.ui.viewmodels

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.datasources.SYSTEM_THEME
import com.humolang.wifiless.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _dynamicColor = MutableStateFlow(true)
    val dynamicColor: StateFlow<Boolean>
        get() = _dynamicColor.asStateFlow()

    private val _theme = MutableStateFlow(SYSTEM_THEME)
    val theme: StateFlow<Int>
        get() = _theme.asStateFlow()

    init {
        viewModelScope.launch {
            launch { collectTheme() }

            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

                launch { collectDynamicColor() }
            }
        }
    }

    private suspend fun collectDynamicColor() {
        settingsRepository.dynamicColor.collect { dynamicColor ->
            _dynamicColor.value = dynamicColor
        }
    }

    private suspend fun collectTheme() {
        settingsRepository.theme.collect { theme ->
            _theme.value = theme
        }
    }

    fun updateTheme(newTheme: Int) {
        viewModelScope.launch {
            settingsRepository.setTheme(newTheme)
        }
    }

    fun updateDynamicColor(newDynamicColor: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColor(
                dynamicColor = newDynamicColor
            )
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = (this[APPLICATION_KEY]
                        as WiFilessApplication).settingsRepository

                SettingsViewModel(
                    settingsRepository = settingsRepository
                )
            }
        }
    }
}