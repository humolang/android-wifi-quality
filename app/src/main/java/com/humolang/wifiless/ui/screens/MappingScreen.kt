package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.MappingViewModel
import kotlin.math.abs

@Composable
fun MappingScreen(
    mappingViewModel: MappingViewModel =
        viewModel(factory = MappingViewModel.Factory)
) {
    val mappingUiState by mappingViewModel
        .mappingUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "mapping screen")
        if (mappingUiState.hasAccelerometer
            && mappingUiState.hasMagnetic) {

            Text(text = "mapping point", modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "x = ${mappingUiState.points.lastOrNull()?.x}",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "y = ${mappingUiState.points.lastOrNull()?.y}",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "distance = ${mappingUiState.points.lastOrNull()?.distance} m",
                modifier = Modifier.padding(top = 4.dp)
            )

            val pathColor = MaterialTheme.colorScheme.onBackground

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .padding(top = 16.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onBackground,
                        RoundedCornerShape(8.dp)
                    ),
                onDraw = {
                    val path = Path()

                    path.moveTo(
                        x = size.width / 2,
                        y = size.height / 2
                    )

                    var maxHorizontal = Float.MIN_VALUE
                    var maxVertical = Float.MIN_VALUE

                    var minHorizontal = Float.MAX_VALUE
                    var minVertical = Float.MAX_VALUE

                    for (point in mappingUiState.points) {
                        if (maxHorizontal < point.x)
                            maxHorizontal = point.x
                        if (maxVertical < point.y)
                            maxVertical = point.y
                        if (minHorizontal > point.x)
                            minHorizontal = point.x
                        if (minVertical > point.y)
                            minVertical = point.y
                    }

                    if (maxHorizontal < 0)
                        maxHorizontal = abs(maxHorizontal)
                    if (maxVertical < 0)
                        maxVertical = abs(maxVertical)
                    if (minHorizontal < 0)
                        minHorizontal = abs(minHorizontal)
                    if (minVertical < 0)
                        minVertical = abs(minVertical)

                    val horizontalRange = minHorizontal + maxHorizontal
                    val verticalRange = minVertical + maxVertical

                    for (point in mappingUiState.points) {
                        path.lineTo(
                            size.width / 2 + (point.x / horizontalRange) * (size.width / 2),
                            size.height / 2 + (point.y / verticalRange) * (size.height / 2)
                        )
                    }

                    drawPath(
                        path = path,
                        color = pathColor,
                        style = Stroke(width = 6f)
                    )
                }
            )
        }
    }
}