package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.MappingViewModel

@Composable
fun MappingScreen(
    mappingViewModel: MappingViewModel =
        viewModel(factory = MappingViewModel.Factory)
) {
    val mappingUiState by mappingViewModel
        .mappingUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "mapping screen")
        if (mappingUiState.hasAccelerometer) {
            Button(
                onClick = {
                    mappingViewModel.calibrateAccelerometer()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = "calibrate accelerometer")
            }

            if (mappingUiState.calibrated) {
                Text(
                    text = "linear acceleration",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(text = "x = ${mappingUiState.acceleration.x} m/s2")
                    Text(text = "y = ${mappingUiState.acceleration.y} m/s2")
                    Text(text = "z = ${mappingUiState.acceleration.z} m/s2")
                    Text(text = "time = ${mappingUiState.acceleration.time} s")
                    Text(text = "timestamp = ${mappingUiState.acceleration.timestamp} ns")
                }

                Text(
                    text = "velocity",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(text = "x = ${mappingUiState.velocity.x} m/s")
                    Text(text = "y = ${mappingUiState.velocity.y} m/s")
                    Text(text = "z = ${mappingUiState.velocity.z} m/s")
                    Text(text = "time = ${mappingUiState.velocity.time} s")
                }

                Text(
                    text = "distance",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(text = "x = ${mappingUiState.distance.x} m")
                    Text(text = "y = ${mappingUiState.distance.y} m")
                    Text(text = "z = ${mappingUiState.distance.z} m")
                    Text(text = "time = ${mappingUiState.distance.time} s")
                }
            }
        }

        if (mappingUiState.hasMagnetic) {
            Text(
                text = "geomagnetic field",
                modifier = Modifier.padding(top = 16.dp)
            )
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(text = "x = ${mappingUiState.magnetic.x} μT")
                Text(text = "y = ${mappingUiState.magnetic.y} μT")
                Text(text = "z = ${mappingUiState.magnetic.z} μT")
                Text(text = "time = ${mappingUiState.magnetic.timestamp} ns")
            }
        }

        if (mappingUiState.hasAccelerometer
            && mappingUiState.hasMagnetic) {

            val pathColor = MaterialTheme.colorScheme.onBackground

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
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

                    for (point in mappingUiState.points) {
                        path.lineTo(
                            size.width / 2 + point.x,
                            size.height / 2 + point.y
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