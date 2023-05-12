package com.humolang.wifiless.data.datasources

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import com.humolang.wifiless.data.model.Gyroscope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GyroscopeCallback(
    sensorManager: SensorManager,
    private val gyroscopeSensor: Sensor?
) {

    val hasGyroscope: Boolean
        get() = gyroscopeSensor != null

    private val _gyroscope = callbackFlow {
        if (hasGyroscope) {
            val gyroscopeCallback = object : SensorEventCallback() {

                override fun onSensorChanged(event: SensorEvent?) {
                    super.onSensorChanged(event)

                    val gyroscopeValue = if (event?.values != null) {
                        Gyroscope(
                            x = event.values[0].toDouble(),
                            y = event.values[1].toDouble(),
                            z = event.values[2].toDouble()
                        )
                    } else {
                        Gyroscope()
                    }

                    trySendBlocking(gyroscopeValue)
                        .onFailure { throwable ->
                            close(throwable)
                        }
                }
            }

            sensorManager.registerListener(
                gyroscopeCallback,
                gyroscopeSensor,
                // SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            awaitClose {
                sensorManager.unregisterListener(
                    gyroscopeCallback,
                    gyroscopeSensor
                )
            }
        }
    }

    val gyroscope: Flow<Gyroscope>
        get() = _gyroscope
}