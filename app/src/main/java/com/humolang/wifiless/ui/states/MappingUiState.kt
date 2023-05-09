package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.Acceleration
import com.humolang.wifiless.data.model.Distance
import com.humolang.wifiless.data.model.Magnetic
import com.humolang.wifiless.data.model.MappingPoint
import com.humolang.wifiless.data.model.Velocity

data class MappingUiState(
    val hasAccelerometer: Boolean,
    val calibrated: Boolean = false,
    val acceleration: Acceleration = Acceleration(),
    val velocity: Velocity = Velocity(),
    val distance: Distance = Distance(),
    val hasMagnetic: Boolean,
    val magnetic: Magnetic = Magnetic(),
    val points: List<MappingPoint> = emptyList()
)
