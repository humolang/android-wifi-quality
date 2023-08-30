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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Preview(showBackground = true)
@Composable
private fun PositiveGraphDrawerPreview() {
    val pointsCapacity = 60
    val points = ArrayDeque<Int>(pointsCapacity)

    repeat(pointsCapacity) {
        val point = Random
            .nextInt(0, 145)

        points.add(point)
    }

    GraphDrawer(
        points = points,
        pointsCapacity = pointsCapacity,
        horizontalLimit = 60,
        verticalLimit = 145,
        isPositive = true,
        labelX = "label, x",
        labelY = "label, y",
        modifier = Modifier
            .aspectRatio(3 / 2f)
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun NegativeGraphDrawerPreview() {
    val pointsCapacity = 60
    val points = ArrayDeque<Int>(pointsCapacity)

    repeat(pointsCapacity) {
        val point = Random
            .nextInt(-127, 0)

        points.add(point)
    }

    GraphDrawer(
        points = points,
        pointsCapacity = pointsCapacity,
        horizontalLimit = 60,
        verticalLimit = 128,
        isPositive = false,
        labelX = "label, x",
        labelY = "label, y",
        modifier = Modifier
            .aspectRatio(3 / 2f)
            .padding(16.dp)
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun GraphDrawer(
    points: ArrayDeque<Int>,
    pointsCapacity: Int,
    horizontalLimit: Int,
    verticalLimit: Int,
    isPositive: Boolean,
    labelX: String,
    labelY: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme
        .colorScheme
        .tertiaryContainer

    val backgroundShape = MaterialTheme
        .shapes
        .small

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = backgroundShape
            )
    ) {
        val lineColor = MaterialTheme
            .colorScheme
            .onTertiaryContainer
            .copy(alpha = 0.4f)

        val graphColor = MaterialTheme
            .colorScheme
            .tertiary

        val labelMeasurer = rememberTextMeasurer()
        val labelStyle = MaterialTheme
            .typography
            .labelSmall.copy(
                color = MaterialTheme
                    .colorScheme
                    .onTertiaryContainer
            )

        Canvas(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .drawWithCache {
                    val graphWidthPx = 2.dp.toPx()
                    val graph = createGraph(
                        points = points,
                        pointsCapacity = pointsCapacity,
                        verticalLimit = verticalLimit,
                        isPositive = isPositive,
                        canvasSize = size
                    )

                    onDrawBehind {
                        drawPath(
                            path = graph,
                            color = graphColor,
                            style = Stroke(
                                width = graphWidthPx,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
        ) {
            val lineWidthPx = 1.dp.toPx()
            val labelXOffsetPx = 2.dp.toPx()

            drawRect(
                color = lineColor,
                style = Stroke(
                    width = lineWidthPx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawHorizontalsWithLabels(
                verticalLimit = verticalLimit,
                isPositive = isPositive,
                lineWidthPx = lineWidthPx,
                lineColor = lineColor,
                labelMeasurer = labelMeasurer,
                labelXOffsetPx = labelXOffsetPx,
                labelStyle = labelStyle
            )

            drawVerticalsWithLabels(
                horizontalLimit = horizontalLimit,
                isPositive = isPositive,
                lineWidthPx = lineWidthPx,
                lineColor = lineColor,
                labelMeasurer = labelMeasurer,
                labelXOffsetPx = labelXOffsetPx,
                labelStyle = labelStyle
            )

            drawSingleLabels(
                isPositive = isPositive,
                labelX = labelX,
                labelY = labelY,
                labelMeasurer = labelMeasurer,
                labelXOffsetPx = labelXOffsetPx,
                labelStyle = labelStyle
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawHorizontalsWithLabels(
    verticalLimit: Int,
    isPositive: Boolean,
    lineWidthPx: Float,
    lineColor: Color,
    labelMeasurer: TextMeasurer,
    labelXOffsetPx: Float,
    labelStyle: TextStyle
) {
    val horizontals = if (size.width > size.height) {
        size.width / size.height
    } else {
        size.height / size.width
    }.toInt() * 3

    val horizontalsInterval = size.height / (horizontals + 1)
    val valueInterval = verticalLimit / (horizontals + 1)

    repeat(horizontals) { number ->
        val y = horizontalsInterval * (number + 1)

        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = lineWidthPx
        )

        val text = if (isPositive) {
            valueInterval * (horizontals - number)
        } else {
            valueInterval * (number + 1)
        }.toString()

        drawText(
            textMeasurer = labelMeasurer,
            text = text,
            topLeft = Offset(0f + labelXOffsetPx, y),
            style = labelStyle
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawVerticalsWithLabels(
    horizontalLimit: Int,
    isPositive: Boolean,
    lineWidthPx: Float,
    lineColor: Color,
    labelMeasurer: TextMeasurer,
    labelXOffsetPx: Float,
    labelStyle: TextStyle
) {
    val verticals = if (size.width > size.height) {
        size.width / size.height
    } else {
        size.height / size.width
    }.toInt() * 4

    val verticalsInterval = size.width / (verticals + 1)
    val timeInterval = horizontalLimit / (verticals + 1)

    repeat(verticals) { number ->
        val x = verticalsInterval * (number + 1)

        drawLine(
            color = lineColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = lineWidthPx
        )

        val text = (timeInterval * (number + 1))
            .toString()

        val y = if (isPositive) {
            val labelHeight = labelMeasurer
                .measure(text, labelStyle)
                .size.height

            size.height - labelHeight
        } else {
            0f
        }

        drawText(
            textMeasurer = labelMeasurer,
            text = text,
            topLeft = Offset(
                x = x + labelXOffsetPx,
                y = y
            ),
            style = labelStyle
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawSingleLabels(
    isPositive: Boolean,
    labelX: String,
    labelY: String,
    labelMeasurer: TextMeasurer,
    labelXOffsetPx: Float,
    labelStyle: TextStyle
) {
    val labelZero = "0"

    drawText(
        textMeasurer = labelMeasurer,
        text = labelZero,
        topLeft = Offset(
            x = 0f + labelXOffsetPx,
            y = if (isPositive) {
                val labelZeroHeight = labelMeasurer
                    .measure(labelZero, labelStyle)
                    .size.height

                size.height - labelZeroHeight
            }
            else 0f
        ),
        style = labelStyle
    )

    val labelXWidth = labelMeasurer
        .measure(labelX, labelStyle)
        .size.width

    drawText(
        textMeasurer = labelMeasurer,
        text = labelX,
        topLeft = Offset(
            x = size.width - labelXWidth - labelXOffsetPx,
            y = if (isPositive) {
                val labelXHeight = labelMeasurer
                    .measure(labelX, labelStyle)
                    .size.height

                size.height - labelXHeight
            }
            else 0f
        ),
        style = labelStyle
    )

    drawText(
        textMeasurer = labelMeasurer,
        text = labelY,
        topLeft = Offset(
            x = 0f + labelXOffsetPx,
            y = if (isPositive)
                0f
            else {
                val labelYHeight = labelMeasurer
                    .measure(labelY, labelStyle)
                    .size.height

                size.height - labelYHeight
            }
        ),
        style = labelStyle
    )
}

private fun createGraph(
    points: ArrayDeque<Int>,
    pointsCapacity: Int,
    verticalLimit: Int,
    isPositive: Boolean,
    canvasSize: Size
): Path {
    val graph = Path()

    points.forEachIndexed { index, point ->
        val x = canvasSize.width *
                ((index + 1).toFloat() / pointsCapacity)

        val y = if (isPositive) {
            canvasSize.height *
                    ((verticalLimit - point).toFloat() / verticalLimit)
        } else {
            canvasSize.height *
                    (point.toFloat() / verticalLimit)
        }

        if (index == 0) {
            graph.moveTo(0f, y)
        } else {
            graph.lineTo(x, y)
        }
    }

    return graph
}