package com.humolang.wifiless.ui.navigation

const val HEAT_ID_ARG = "heatId"

const val START_SCREEN_STRING = "start"
const val SETTINGS_SCREEN_STRING = "settings"
const val PLANNING_SCREEN_STRING = "planning"
const val MAPPING_SCREEN_STRING = "mapping"
const val HEATS_SCREEN_STRING = "heats"

const val PLANNING_SCREEN_WITH_ARG = "$PLANNING_SCREEN_STRING/{$HEAT_ID_ARG}"
const val MAPPING_SCREEN_WITH_ARG = "$MAPPING_SCREEN_STRING/{$HEAT_ID_ARG}"