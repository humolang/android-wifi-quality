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

package com.humolang.wifiless.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.ui.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreen(
    popBackStack: () -> Unit,
    settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.Factory)
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            SettingsTopBar(
                popBackStack = popBackStack
            )
        },
        content = { innerPadding ->
            SettingsContent(
                settingsViewModel = settingsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(
                    id = R.string.settings
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = popBackStack
            ) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.back
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme
                .colorScheme
                .surfaceColorAtElevation(3.dp)
        )
    )
}

@Composable
private fun SettingsContent(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        ThemeSettings(
            themeState = settingsViewModel.theme,
            dynamicColorState = settingsViewModel
                .dynamicColor,

            updateTheme = { theme ->
                settingsViewModel.updateTheme(theme)
            },
            updateDynamicColor = { dynamicColor ->
                settingsViewModel
                    .updateDynamicColor(dynamicColor)
            },

            modifier = Modifier
        )
    }
}

@Composable
private fun ThemeSettings(
    themeState: StateFlow<Int>,
    dynamicColorState: StateFlow<Boolean>,
    updateTheme: (Int) -> Unit,
    updateDynamicColor: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.theme_settings
                ),
                style = MaterialTheme
                    .typography
                    .titleLarge
            )

            ThemeMenu(
                themeState = themeState,
                onItemSelected = updateTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

                ColorSchemeSwitch(
                    dynamicColorState = dynamicColorState,
                    onCheckedChange = updateDynamicColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeMenu(
    themeState: StateFlow<Int>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme by themeState
        .collectAsStateWithLifecycle()

    val label = stringResource(
        id = R.string.theme
    )
    val options = listOf(
        Pair(
            painterResource(id = R.drawable.twotone_auto_mode_24),
            stringResource(id = R.string.system_theme)
        ),
        Pair(
            painterResource(id = R.drawable.twotone_light_mode_24),
            stringResource(id = R.string.light_theme)
        ),
        Pair(
            painterResource(id = R.drawable.twotone_dark_mode_24),
            stringResource(id = R.string.dark_theme)
        )
    )

    ExposedMenu(
        label = label,
        options = options,
        initialIndex = theme,
        onItemSelected = onItemSelected,
        modifier = modifier
    )
}

@Composable
private fun ColorSchemeSwitch(
    dynamicColorState: StateFlow<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val label = stringResource(
            id = R.string.dynamic_colors
        )
        Text(
            text = label,
            style = MaterialTheme
                .typography
                .bodyLarge
        )
        
        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        val dynamicColor by dynamicColorState
            .collectAsStateWithLifecycle()
        Switch(
            checked = dynamicColor,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            },
            thumbContent = {
                if (dynamicColor) {
                    Icon(
                        painterResource(
                            id = R.drawable.twotone_brush_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(
                            SwitchDefaults.IconSize
                        ),
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedMenu(
    label: String,
    options: List<Pair<Painter, String>>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            readOnly = true,
            value = options[initialIndex].second,
            onValueChange = {  },
            label = { Text(text = label) },
            leadingIcon = {
                Icon(
                    painter = options[initialIndex].first,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults
                    .TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults
                .textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            for (index in options.indices) {
                DropdownMenuItem(
                    text = { Text(text = options[index].second) },
                    onClick = {
                        onItemSelected(index)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            painter = options[index].first,
                            contentDescription = null
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults
                        .ItemContentPadding,
                )
            }
        }
    }
}