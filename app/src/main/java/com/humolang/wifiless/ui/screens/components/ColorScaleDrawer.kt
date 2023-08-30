/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.humolang.wifiless.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humolang.wifiless.R

@Preview(showBackground = true)
@Composable
private fun RssiColorScalePreview() {
    RssiHorizontalScale(
        minRssi = -127,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp)
    )
}

@Composable
fun RssiHorizontalScale(
    minRssi: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val startColor = MaterialTheme.colorScheme
            .tertiaryContainer.copy(
                //alpha = 0f,
                red = 1f,
                green = 0f,
                blue = 0f
            )
        val endColor = MaterialTheme.colorScheme
            .tertiaryContainer.copy(
                //alpha = 1f,
                red = 0f,
                green = 1f,
                blue = 0f
            )

        val startLabel = stringResource(
            id = R.string.rssi_dbm,
            minRssi
        )
        val endLabel = stringResource(
            id = R.string.rssi_dbm,
            0
        )

        HorizontalColorScale(
            startLabel = startLabel,
            endLabel = endLabel,
            startColor = startColor,
            endColor = endColor,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun HorizontalColorScale(
    startLabel: String,
    endLabel: String,
    startColor: Color,
    endColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val horizontalGradient = Brush
            .horizontalGradient(
                colors = listOf(
                    startColor,
                    endColor
                )
            )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            drawLine(
                brush = horizontalGradient,
                start = Offset(
                    0f,
                    size.height / 2
                ),
                end = Offset(
                    size.width,
                    size.height / 2
                ),
                strokeWidth = size.height
            )
        }

        val textStyle = MaterialTheme.typography
            .labelSmall

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = startLabel,
                style = textStyle
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = endLabel,
                style = textStyle
            )
        }
    }
}