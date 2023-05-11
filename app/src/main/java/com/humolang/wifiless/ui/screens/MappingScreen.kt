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
                text = "x = ${mappingUiState.distance.x} m",
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "y = ${mappingUiState.distance.y} m",
                modifier = Modifier.padding(top = 4.dp)
            )

            val pathColor = MaterialTheme.colorScheme.onBackground

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(512.dp)
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