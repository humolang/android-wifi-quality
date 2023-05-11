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

            Text(text = "distance", modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "x = ${mappingUiState.points.lastOrNull()?.x} m",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "y = ${mappingUiState.points.lastOrNull()?.y} m",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "z = ${mappingUiState.points.lastOrNull()?.z} m",
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

            Text(text = "magnetic", modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "x = ${mappingUiState.points.lastOrNull()?.magnetix} μT",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "y = ${mappingUiState.points.lastOrNull()?.magnetiy} μT",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "z = ${mappingUiState.points.lastOrNull()?.magnetiz} μT",
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(text = "orientation", modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "azimuth = ${mappingUiState.orientation.azimuth}",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "pitch = ${mappingUiState.orientation.pitch}",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "roll = ${mappingUiState.orientation.roll}",
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}