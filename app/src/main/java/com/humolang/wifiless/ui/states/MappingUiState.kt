package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.Distance
import com.humolang.wifiless.data.model.MappingPoint

data class MappingUiState(
    val hasAccelerometer: Boolean,
    val hasMagnetic: Boolean,
    val points: List<MappingPoint> = emptyList(),
    val distance: Distance = Distance()
)
