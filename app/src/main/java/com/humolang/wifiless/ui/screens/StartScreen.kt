package com.humolang.wifiless.ui.screens

import android.net.wifi.WifiInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.StartViewModel

@Composable
fun StartScreen(
    startViewModel: StartViewModel =
        viewModel(factory = StartViewModel.Factory)
) {
    val startUiState by startViewModel
        .startUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
    ) {
        RssiGraph(
            latestRssi = startUiState.latestRssi,
            rssiValues = startUiState.rssiValues,
            dequeCapacity = startViewModel.dequeCapacity,
            modifier = Modifier.padding(top = 16.dp)
        )
        SpeedGraph(
            latestSpeed = startUiState.latestSpeed,
            speedValues = startUiState.speedValues,
            dequeCapacity = startViewModel.dequeCapacity,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "IP Address: ${startUiState.ipAddress}",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun RssiGraph(
    latestRssi: Int,
    rssiValues: ArrayDeque<Int>,
    dequeCapacity: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "$latestRssi dBm")
        AutoUpdateGraph(
            points = rssiValues,
            dequeCapacity = dequeCapacity,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SpeedGraph(
    latestSpeed: Int,
    speedValues: ArrayDeque<Int>,
    dequeCapacity: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "$latestSpeed ${WifiInfo.LINK_SPEED_UNITS}")
        AutoUpdateGraph(
            points = speedValues,
            dequeCapacity = dequeCapacity,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AutoUpdateGraph(
    points: ArrayDeque<Int>,
    dequeCapacity: Int,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier
        .border(
            2.dp,
            MaterialTheme.colorScheme.onBackground,
            RoundedCornerShape(8.dp)
        )
    ) {
        val graphColor = MaterialTheme.colorScheme.onBackground

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .padding(8.dp),
            onDraw = {
                val graph = Path()

                val y = if (points.isNotEmpty()) {
                    size.height * (points.first().toFloat() / 100)
                } else {
                    0f
                }

                graph.moveTo(0f, y)

                for (index in points.indices) {
                    if (index == 0) {
                        continue
                    }

                    graph.lineTo(
                        size.width * (index.toFloat() / dequeCapacity),
                        size.height * (points[index].toFloat() / 100)
                    )
                }

                drawPath(
                    path = graph,
                    color = graphColor,
                    style = Stroke(width = 6f)
                )
            }
        )
    }
}