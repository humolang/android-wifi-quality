package com.humolang.wifiless.data.datasources

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import com.humolang.wifiless.data.model.Acceleration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class AccelerometerCallback(
    sensorManager: SensorManager,
    private val accelerometerSensor: Sensor?
) {

    val hasAccelerometer: Boolean
        get() = accelerometerSensor != null

    private val _acceleration = callbackFlow {
        if (hasAccelerometer) {
            val accelerometerCallback = object : SensorEventCallback() {

                override fun onSensorChanged(event: SensorEvent?) {
                    super.onSensorChanged(event)

                    val accelerationValue = if (event?.values != null) {
                        Acceleration(
                            x = event.values[0].toDouble(),
                            y = event.values[1].toDouble(),
                            z = event.values[2].toDouble(),
                            timestamp = event.timestamp
                        )
                    } else {
                        Acceleration()
                    }

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
                // SensorManager.SENSOR_DELAY_FASTEST
            )

            awaitClose {
                sensorManager.unregisterListener(
                    accelerometerCallback,
                    accelerometerSensor
                )
            }
        }
    }.buffer(Channel.CONFLATED)

    val acceleration: Flow<Acceleration>
        get() = _acceleration
}