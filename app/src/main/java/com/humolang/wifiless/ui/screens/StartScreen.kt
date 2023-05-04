package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.StartViewModel

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    StartScreen()
}

@Composable
fun StartScreen(
    startViewModel: StartViewModel =
        viewModel(factory = StartViewModel.Factory)
) {
    val startUiState by startViewModel
        .startUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "latest rssi = ${startUiState.latestRssi}")
        Text(text = "deque size = ${startUiState.rssiValues.size}")
        RssiGraph(
            rssiValues = startUiState.rssiValues,
            dequeCapacity = startViewModel.dequeCapacity
        )
    }
}

@Composable
fun RssiGraph(
    rssiValues: ArrayDeque<Int>,
    dequeCapacity: Int,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier
        .border(
            2.dp,
            Color.Blue,
            RoundedCornerShape(8.dp)
        )
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
            .padding(8.dp),
            onDraw = {
                val graph = Path()

                val y = if (rssiValues.isNotEmpty()) {
                    size.height * (rssiValues.first().toFloat() / 100)
                } else {
                    0f
                }

                graph.moveTo(0f, y)

                for (index in rssiValues.indices) {
                    if (index == 0) {
                        continue
                    }

                    graph.lineTo(
                        size.width * (index.toFloat() / dequeCapacity),
                        size.height * (rssiValues[index].toFloat() / 100)
                    )
                }

                drawPath(graph, Color.Red, style = Stroke(width = 6f))
            }
        )
    }
}