package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.MappingPoint
import com.humolang.wifiless.data.model.Orientation

data class MappingUiState(
    val hasAccelerometer: Boolean,
    val hasMagnetic: Boolean,
    val points: List<MappingPoint> = emptyList(),
    val orientation: Orientation = Orientation()
)
