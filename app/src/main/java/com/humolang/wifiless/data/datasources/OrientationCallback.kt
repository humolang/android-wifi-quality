package com.humolang.wifiless.data.datasources

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import com.humolang.wifiless.data.model.Orientation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combineTransform

class OrientationCallback(
    sensorManager: SensorManager,
    private val accelerometerSensor: Sensor?,
    private val magneticSensor: Sensor?
) {

    private val hasAccelerometer: Boolean
        get() = accelerometerSensor != null

    private val hasMagnetic: Boolean
        get() = magneticSensor != null

    private val gravity = callbackFlow {
        val accelerometerCallback = object : SensorEventCallback() {

            override fun onSensorChanged(event: SensorEvent?) {
                super.onSensorChanged(event)

                val accelerationValue = event?.values
                    ?: FloatArray(3)

                trySendBlocking(accelerationValue)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }
        }

        sensorManager.registerListener(
            accelerometerCallback,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        awaitClose {
            sensorManager.unregisterListener(
                accelerometerCallback,
                accelerometerSensor
            )
        }
    }

    private val geomagnetic = callbackFlow {
        val magneticCallback = object : SensorEventCallback() {


            override fun onSensorChanged(event: SensorEvent?) {
                super.onSensorChanged(event)

                val geomagneticValue = event?.values
                    ?: FloatArray(3)

                trySendBlocking(geomagneticValue)
                    .onFailure { throwable ->
                        close(throwable)
                    }
            }
        }

        sensorManager.registerListener(
            magneticCallback,
            magneticSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        awaitClose {
            sensorManager.unregisterListener(
                magneticCallback,
                magneticSensor
            )
        }
    }

    private val _orientation = combineTransform(
        gravity,
        geomagnetic
    ) { gravity, geomagnetic ->
        if (hasAccelerometer
            && hasMagnetic) {

            val rotation = FloatArray(9)
            val inclination = FloatArray(9)

            val gotMatrix = SensorManager.getRotationMatrix(
                rotation,
                inclination,
                gravity,
                geomagnetic
            )

            val orientationValue = if (gotMatrix) {
                val orientationArray = FloatArray(3)
                SensorManager.getOrientation(rotation, orientationArray)

                Orientation(
                    azimuth = orientationArray[0],
                    pitch = orientationArray[1],
                    roll = orientationArray[2]
                )
            } else {
                Orientation()
            }

            emit(orientationValue)
        }
    }

    val orientation: Flow<Orientation>
        get() = _orientation
}