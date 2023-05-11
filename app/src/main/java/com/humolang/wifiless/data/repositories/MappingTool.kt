package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.AccelerometerCallback
import com.humolang.wifiless.data.datasources.MagneticCallback
import com.humolang.wifiless.data.model.Distance
import com.humolang.wifiless.data.model.MappingPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MappingTool(
    private val accelerometerCallback: AccelerometerCallback,
    private val magneticCallback: MagneticCallback
) {

    val hasAccelerometer: Boolean
        get() = accelerometerCallback.hasAccelerometer

    val hasMagnetic: Boolean
        get() = magneticCallback.hasMagnetic

    private val previousTimestamp = MutableStateFlow(0L)
    private val distanceCounter = MutableStateFlow(Distance())
    private val mappingPoints = MutableStateFlow(
        mutableListOf<MappingPoint>()
    )

    private val _points = accelerometerCallback.acceleration
        .map { acceleration ->
            if (previousTimestamp.value == 0L) {
                previousTimestamp.value = acceleration.timestamp
            }

            val withTime = acceleration.copy(
                time = (acceleration.timestamp - previousTimestamp.value)
                        * 0.000000001
            )

            previousTimestamp.value = acceleration.timestamp

            withTime
        }
        .map { acceleration ->
            distanceCounter.value = Distance(
                x = (acceleration.x * acceleration.time * acceleration.time)
                        + distanceCounter.value.x,
                y = (acceleration.y * acceleration.time * acceleration.time)
                        + distanceCounter.value.y,
                z = (acceleration.z * acceleration.time * acceleration.time)
                        + distanceCounter.value.z,
                time = acceleration.time + distanceCounter.value.time
            )
            distanceCounter.value
        }
        .combine(magneticCallback.magnetic) { distance, magnetic ->
            val point = MappingPoint(
                x = distance.x.toFloat(),
                y = distance.y.toFloat()
            )

            mappingPoints.value.add(point)
            mappingPoints.value.toList()
        }

    val points: Flow<List<MappingPoint>>
        get() = _points
}