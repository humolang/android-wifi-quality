package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.AccelerometerCallback
import com.humolang.wifiless.data.datasources.MagneticCallback
import com.humolang.wifiless.data.model.Acceleration
import com.humolang.wifiless.data.model.Distance
import com.humolang.wifiless.data.model.Magnetic
import com.humolang.wifiless.data.model.Velocity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

class MappingTool(
    private val accelerometerCallback: AccelerometerCallback,
    private val magneticCallback: MagneticCallback
) {

    val hasAccelerometer: Boolean
        get() = accelerometerCallback.hasAccelerometer

    val hasMagnetic: Boolean
        get() = magneticCallback.hasMagnetic

    private var calibration = Acceleration()
    private var previousTimestamp = 0L
    private var distanceCounter = Distance()

    private val _calibrated = MutableStateFlow(false)
    val calibrated: StateFlow<Boolean>
        get() = _calibrated

    private val _acceleration = accelerometerCallback.acceleration
        .map { withoutCalibration ->
            if (previousTimestamp == 0L) {
                previousTimestamp = withoutCalibration.timestamp
            }

            val calibrated = Acceleration(
                x = withoutCalibration.x - calibration.x,
                y = withoutCalibration.y - calibration.y,
                z = withoutCalibration.z - calibration.z,
                time = (withoutCalibration.timestamp - previousTimestamp)
                        * 0.000000001, // from nanoseconds to seconds
                timestamp = withoutCalibration.timestamp
            )

            previousTimestamp = withoutCalibration.timestamp

            calibrated
        }
    val acceleration: Flow<Acceleration>
        get() = _acceleration

    private val _velocity = _acceleration
        .map { acceleration ->
            Velocity(
                x = abs(acceleration.x) * acceleration.time,
                y = abs(acceleration.y) * acceleration.time,
                z = abs(acceleration.z) * acceleration.time,
                time = acceleration.time
            )
        }
    val velocity: Flow<Velocity>
        get() = _velocity

    private val _distance = _velocity
        .map { velocity ->
            distanceCounter = Distance(
                x = (velocity.x * velocity.time) + distanceCounter.x,
                y = (velocity.y * velocity.time) + distanceCounter.y,
                z = (velocity.z * velocity.time) + distanceCounter.z,
                time = velocity.time + distanceCounter.time
            )

            distanceCounter
        }
    val distance: Flow<Distance>
        get() = _distance

    private val _magnetic = magneticCallback.magnetic
    val magnetic: Flow<Magnetic>
        get() = _magnetic

    suspend fun calibrateAccelerometer() {
        var counter = 0
        var calibrationValue = Acceleration()

        withTimeoutOrNull(5000L) {
            accelerometerCallback.acceleration.collect { acceleration ->
                calibrationValue = Acceleration(
                    x = calibrationValue.x + acceleration.x,
                    y = calibrationValue.y + acceleration.y,
                    z = calibrationValue.z + acceleration.z
                )
                counter++
            }
        }

        calibration = Acceleration(
            x = calibrationValue.x / counter,
            y = calibrationValue.y / counter,
            z = calibrationValue.z / counter
        )

        _calibrated.value = true
    }
}