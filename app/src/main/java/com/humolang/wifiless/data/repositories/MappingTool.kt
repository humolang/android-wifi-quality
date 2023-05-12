package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.AccelerometerCallback
import com.humolang.wifiless.data.datasources.MagneticCallback
import com.humolang.wifiless.data.datasources.OrientationCallback
import com.humolang.wifiless.data.model.Distance
import com.humolang.wifiless.data.model.MappingPoint
import com.humolang.wifiless.data.model.Velocity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.PI
import kotlin.math.sin

class MappingTool(
    private val accelerometerCallback: AccelerometerCallback,
    private val magneticCallback: MagneticCallback,
    private val orientationCallback: OrientationCallback
) {

    val hasAccelerometer: Boolean
        get() = accelerometerCallback.hasAccelerometer

    val hasMagnetic: Boolean
        get() = magneticCallback.hasMagnetic

    private val previousTimestamp = MutableStateFlow(0L)
    private val velocityCounter = MutableStateFlow(Velocity())
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

            velocityCounter.value = Velocity(
                x = velocityCounter.value.x + integral(acceleration.x, time),
                y = velocityCounter.value.y + integral(acceleration.y, time),
                z = velocityCounter.value.z + integral(acceleration.z, time),
                time = time
            )

            velocityCounter.value
        }
        .map { velocity ->
            distanceCounter.value = Distance(
                x = distanceCounter.value.x + integral(velocity.x, velocity.time),
                y = distanceCounter.value.y + integral(velocity.y, velocity.time),
                z = distanceCounter.value.z + integral(velocity.z, velocity.time),
                time = velocity.time
            )

            distanceCounter.value
        }
        .combine(orientationCallback.orientation) { distance, orientation ->
            val alphaAngle = orientation.azimuth
            val betaAngle = PI / 2.0
            val gammaAngle = PI - (alphaAngle + betaAngle)

            val bSide = distance.y
            val aSide = (bSide * sin(alphaAngle)) / sin(betaAngle)
            val cSide = (aSide * sin(gammaAngle)) / sin(alphaAngle)

            val point = MappingPoint(
                x = aSide.toFloat(),
                y = cSide.toFloat(),
                distance = bSide
            )

            mappingPoints.value.add(point)
            mappingPoints.value.toList()
        }

    val points: Flow<List<MappingPoint>>
        get() = _points

    private fun integral(value: Double, time: Double): Double {
        val t = arrayListOf<Double>()

        val amount = 10
        val step = time / amount

        var counter = 0.0

        t.add(counter)
        for (i in 0 until amount) {
            counter += step
            t.add(counter)
        }

//        return trapezoidFormula(x) { value }
        return simpsonsFormula(t, step) { value }
    }

    private fun trapezoidFormula(
        x: ArrayList<Double>,
        f: (Double) -> Double
    ): Double {
        var result = 0.0

        for (k in 0 until x.size - 1) {
            val h = x[k + 1] - x[k]
            result += (h / 2) * (f(x[k + 1]) + f(x[k]))
        }

        return result
    }

    private fun simpsonsFormula(
        x: ArrayList<Double>,
        h: Double,
        f: (Double) -> Double
    ): Double {
        var result = 0.0
        val h6 = h / 6
        val h2 = h / 2

        for (k in 0 until x.lastIndex) {
            result += h6 * (f(x[k + 1]) + 4 * f(x[k] + h2) + f(x[k]))
        }

        return result
    }
}