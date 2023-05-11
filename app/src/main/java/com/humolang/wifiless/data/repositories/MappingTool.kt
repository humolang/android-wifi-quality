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
            if (previousTimestamp.value == 0L)
                previousTimestamp.value = acceleration.timestamp
            val time = (acceleration.timestamp -
                    previousTimestamp.value) * 0.000000001
            previousTimestamp.value = acceleration.timestamp

            distanceCounter.value = Distance(
                x = (acceleration.x * time * time)
                        + distanceCounter.value.x,
                y = (acceleration.y * time * time)
                        + distanceCounter.value.y,
                z = (acceleration.z * time * time)
                        + distanceCounter.value.z,
                time = time + distanceCounter.value.time
            )
            distanceCounter.value
        }
        .combine(magneticCallback.magnetic) { distance, magnetic ->
            val point = MappingPoint(
                x = distance.x.toFloat(),
                y = distance.y.toFloat(),
                z = distance.z.toFloat(),
                magnetix = magnetic.x.toFloat(),
                magnetiy = magnetic.y.toFloat(),
                magnetiz = magnetic.z.toFloat(),
            )

            mappingPoints.value.add(point)
            mappingPoints.value.toList()
        }

    val points: Flow<List<MappingPoint>>
        get() = _points
}