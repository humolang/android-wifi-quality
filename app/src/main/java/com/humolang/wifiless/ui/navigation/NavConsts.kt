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

package com.humolang.wifiless.ui.navigation

const val HEAT_ID_ARG = "heatId"

const val START_SCREEN_STRING = "start"
const val SETTINGS_SCREEN_STRING = "settings"
const val PLANNING_SCREEN_STRING = "planning"
const val MAPPING_SCREEN_STRING = "mapping"
const val HEATS_SCREEN_STRING = "heats"
const val ABOUT_SCREEN_STRING = "about"

const val PLANNING_SCREEN_WITH_ARG = "$PLANNING_SCREEN_STRING/{$HEAT_ID_ARG}"
const val MAPPING_SCREEN_WITH_ARG = "$MAPPING_SCREEN_STRING/{$HEAT_ID_ARG}"