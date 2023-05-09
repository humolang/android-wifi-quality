package com.humolang.wifiless.data.datasources

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import com.humolang.wifiless.data.model.Magnetic
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class MagneticCallback(
    sensorManager: SensorManager,
    private val magneticSensor: Sensor?
) {

    val hasMagnetic: Boolean
        get() = magneticSensor != null

    private val _magnetic = callbackFlow {
        if (hasMagnetic) {
            val magneticCallback = object : SensorEventCallback() {

                override fun onSensorChanged(event: SensorEvent?) {
                    super.onSensorChanged(event)

                    val magneticValue = if (event?.values != null) {
                        Magnetic(
                            x = event.values[0].toDouble(),
                            y = event.values[1].toDouble(),
                            z = event.values[2].toDouble(),
                            timestamp = event.timestamp
                        )
                    } else {
                        Magnetic()
                    }

                    trySendBlocking(magneticValue)
                        .onFailure { throwable ->
                            close(throwable)
                        }
                }
            }

            sensorManager.registerListener(
                magneticCallback,
                magneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL
                // SensorManager.SENSOR_DELAY_FASTEST
            )

            awaitClose {
                sensorManager.unregisterListener(
                    magneticCallback,
                    magneticSensor
                )
            }
        }
    }.buffer(Channel.CONFLATED)

    val magnetic: Flow<Magnetic>
        get() = _magnetic
}