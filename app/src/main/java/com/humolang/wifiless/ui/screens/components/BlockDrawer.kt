package com.humolang.wifiless.ui.screens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.model.BlockType
import kotlin.math.abs

@Preview(showBackground = true)
@Composable
private fun FreeBlockPreview() {
    val block = Block(
        columnId = 0,
        y = 0,
        type = BlockType.FREE,
        rssi = 0
    )

    RssiBlock(
        block = block,
        onBlockClicked = {  },
        onBlockLongClicked = {  },
        modifier = Modifier
            .aspectRatio(1 / 1f)
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun WallBlockPreview() {
    val block = Block(
        columnId = 0,
        y = 0,
        type = BlockType.WALL
    )

    RssiBlock(
        block = block,
        onBlockClicked = {  },
        onBlockLongClicked = {  },
        modifier = Modifier
            .aspectRatio(1 / 1f)
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun IconBlockPreview() {
    val block = Block(
        columnId = 0,
        y = 0,
        type = BlockType.ARMCHAIR,
        rssi = 100
    )

    RssiBlock(
        block = block,
        onBlockClicked = {  },
        onBlockLongClicked = {  },
        modifier = Modifier
            .aspectRatio(1 / 1f)
            .padding(16.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RssiBlock(
    block: Block,
    modifier: Modifier = Modifier,
    onBlockClicked: (Block) -> Unit = {  },
    onBlockLongClicked: (Block) -> Unit = {  },
) {
    Box(modifier = modifier) {
        val borderColor = MaterialTheme
            .colorScheme
            .onTertiaryContainer
        val tertiaryContainer = MaterialTheme
            .colorScheme
            .tertiaryContainer

        val hasRssi = abs(block.rssi) in 0..100
        val rssiColor = abs(block.rssi.toFloat()) / 100

        val rectangleColor = if (hasRssi) {
            tertiaryContainer.copy(
                //alpha = 1 - rssiColor,
                red = rssiColor,
                blue = rssiColor
            )
        } else {
            tertiaryContainer
        }

        BlockDrawer(
            block = block,
            borderColor = borderColor,
            rectangleColor = rectangleColor,
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = { onBlockClicked(block) },
                    onLongClick = { onBlockLongClicked(block) }
                )
        )
    }
}

@Composable
private fun BlockDrawer(
    block: Block,
    borderColor: Color,
    rectangleColor: Color,
    modifier: Modifier = Modifier,
    lineWidth: Dp = 2.dp,
    cornerRadius: Dp = 0.dp,
) {
    Box(
        modifier = modifier
            .drawWithCache {
                val corners = CornerRadius(
                    x = cornerRadius.toPx(),
                    y = cornerRadius.toPx()
                )

                onDrawBehind {
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = corners,
                        style = Stroke(lineWidth.toPx())
                    )
                    drawRoundRect(
                        color = rectangleColor,
                        cornerRadius = corners
                    )
                }
            }
    ) {
        when (block.type) {
            BlockType.WALL -> {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            onDrawBehind {
                                drawWall(
                                    lineWidth = lineWidth.toPx(),
                                    lineColor = borderColor,
                                    canvasSize = size
                                )
                            }
                        }
                )
            }

            else -> {
                if (block.type != BlockType.FREE) {
                    Icon(
                        painter = painterResource(id = block.imageId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWall(
    lineWidth: Float,
    lineColor: Color,
    canvasSize: Size
) {
    val times = 6

    val intervalX = canvasSize.width / times
    val intervalY = canvasSize.height / times

    repeat(times) { number ->
        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f,
                y = canvasSize.height - (intervalY * number)
            ),
            end = Offset(
                x = canvasSize.width - (intervalX * number),
                y = 0f
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f + (intervalX * number),
                y = canvasSize.height
            ),
            end = Offset(
                x = canvasSize.width,
                y = 0f + (intervalY * number)
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f + (intervalY * number),
                y = 0f
            ),
            end = Offset(
                x = canvasSize.width,
                y = canvasSize.height - (intervalX * number)
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = lineColor,
            start = Offset(
                x = 0f,
                y = 0f + (intervalY * number)
            ),
            end = Offset(
                x = canvasSize.width - (intervalX * number),
                y = canvasSize.height
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
}