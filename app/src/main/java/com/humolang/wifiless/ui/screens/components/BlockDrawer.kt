package com.humolang.wifiless.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
            .drawBehind {
                val corners = CornerRadius(
                    x = cornerRadius.toPx(),
                    y = cornerRadius.toPx()
                )

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
    ) {
        when (block.type) {
            BlockType.WALL -> {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawWall(
                        lineWidth = lineWidth.toPx(),
                        lineColor = borderColor,
                        canvasSize = size
                    )
                }
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

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun Block(
//    heat: Heat,
//    column: Column,
//    block: Block,
//    onBlockTypeClicked: (Block, BlockType) -> Unit,
//    onInsertRowClicked: (Long, Int) -> Unit,
//    onInsertColumnClicked: (Long, Int) -> Unit,
//    onDeleteRowClicked: (Long, Int) -> Unit,
//    onDeleteColumnClicked: (Long, Int) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val tertiaryBorder = MaterialTheme.colorScheme.onTertiaryContainer
//    val tertiaryRectangle = MaterialTheme.colorScheme.tertiaryContainer
//
//    val hasRssi = abs(block.rssi) in 0..100
//    val rssiGreen = abs(block.rssi.toFloat()) / 100
//
//    val borderColor = Color(
//        tertiaryBorder.red,
//        if (hasRssi) rssiGreen else tertiaryBorder.green,
//        tertiaryBorder.blue,
//        tertiaryBorder.alpha,
//        tertiaryBorder.colorSpace
//    )
//
//    val rectangleColor = Color(
//        tertiaryRectangle.red,
//        if (hasRssi) rssiGreen else tertiaryRectangle.green,
//        tertiaryRectangle.blue,
//        tertiaryRectangle.alpha,
//        tertiaryRectangle.colorSpace
//    )
//
//    var typeMenuExpanded by remember {
//        mutableStateOf(false)
//    }
//
//    var editMenuExpanded by remember {
//        mutableStateOf(false)
//    }
//
//    when (block.type) {
//
//        BlockType.WALL -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawWallBlock(
//                        rectangleColor = rectangleColor,
//                        lineColor = borderColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        ),
//                        size = size
//                    )
//                },
//                modifier = modifier
//                    .combinedClickable(
//                        onClick = {
//                            typeMenuExpanded = true
//                        },
//                        onLongClick = {
//                            editMenuExpanded = true
//                        }
//                    )
//            )
//        }
//
//        BlockType.FREE -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawFreeBlock(
//                        rectangleColor = rectangleColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        )
//                    )
//                },
//                modifier = modifier
//                    .combinedClickable(
//                        onClick = {
//                            typeMenuExpanded = true
//                        },
//                        onLongClick = {
//                            editMenuExpanded = true
//                        }
//                    )
//            )
//        }
//
//        else -> {
//            Icon(
//                painter = painterResource(id = block.imageId),
//                contentDescription = null,
//                modifier = modifier
//                    .border(
//                        2.dp,
//                        borderColor,
//                        RoundedCornerShape(4.dp)
//                    )
//                    .padding(2.dp)
//                    .drawBehind {
//                        drawFreeBlock(
//                            rectangleColor = rectangleColor,
//                            cornerRadius = CornerRadius(
//                                4.dp.toPx(),
//                                4.dp.toPx()
//                            )
//                        )
//                    }
//                    .combinedClickable(
//                        onClick = {
//                            typeMenuExpanded = true
//                        },
//                        onLongClick = {
//                            editMenuExpanded = true
//                        }
//                    )
//            )
//        }
//    }
//
//    BlockTypeMenu(
//        expanded = typeMenuExpanded,
//        onDismissRequest = { typeMenuExpanded = false },
//        block = block,
//        onBlockTypeClicked = onBlockTypeClicked
//    )
//
//    EditPlanMenu(
//        expanded = editMenuExpanded,
//        onDismissRequest = { editMenuExpanded = false },
//        heat = heat,
//        column = column,
//        block = block,
//        onInsertRowClicked = onInsertRowClicked,
//        onInsertColumnClicked = onInsertColumnClicked,
//        onDeleteRowClicked = onDeleteRowClicked,
//        onDeleteColumnClicked = onDeleteColumnClicked
//    )
//}
//
//@Composable
//private fun BlockDrawer(
//    borderColor: Color,
//    drawBlock: DrawScope.() -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Canvas(
//        modifier = modifier
//            .border(
//                2.dp,
//                borderColor,
//                RoundedCornerShape(4.dp)
//            )
//            .padding(2.dp)
//    ) {
//        drawBlock()
//    }
//}
//
//private fun DrawScope.drawFreeBlock(
//    rectangleColor: Color,
//    cornerRadius: CornerRadius
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//}
//
//private fun DrawScope.drawWallBlock(
//    rectangleColor: Color,
//    lineColor: Color,
//    cornerRadius: CornerRadius,
//    size: Size
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//
//    val lineWidth = 2.dp.toPx()
//
//    val times = 6
//
//    val intervalX = size.width / times
//    val intervalY = size.height / times
//
//    repeat(times) { number ->
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = size.height - (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = 0f
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalX * number),
//                y = size.height
//            ),
//            end = Offset(
//                x = size.width,
//                y = 0f + (intervalY * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalY * number),
//                y = 0f
//            ),
//            end = Offset(
//                x = size.width,
//                y = size.height - (intervalX * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = 0f + (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = size.height
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//@Composable
//private fun Block(
//    block: Block,
//    onBlockClicked: (Block) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val hasRssi = abs(block.rssi) in 0..100
//    val rssiColor = abs(block.rssi.toFloat()) / 100
//
//    val borderColor = MaterialTheme.colorScheme
//        .onTertiaryContainer
//
//    val tertiaryRectangle = MaterialTheme.colorScheme
//        .tertiaryContainer
//    val rectangleColor = tertiaryRectangle.copy(
//        red = if (hasRssi)
//            rssiColor
//        else tertiaryRectangle.red,
//
//        green = if (hasRssi)
//            rssiColor
//        else tertiaryRectangle.green,
//    )
//
//    when (block.type) {
//
//        BlockType.WALL -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawWallBlock(
//                        rectangleColor = rectangleColor,
//                        lineColor = borderColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        ),
//                        size = size
//                    )
//                },
//                modifier = modifier
//                    .clickable {
//                        onBlockClicked(block)
//                    }
//            )
//        }
//
//        BlockType.FREE -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawFreeBlock(
//                        rectangleColor = rectangleColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        )
//                    )
//                },
//                modifier = modifier
//                    .clickable {
//                        onBlockClicked(block)
//                    }
//            )
//        }
//
//        else -> {
//            Icon(
//                painter = painterResource(id = block.imageId),
//                contentDescription = null,
//                modifier = modifier
//                    .border(
//                        2.dp,
//                        borderColor,
//                        RoundedCornerShape(4.dp)
//                    )
//                    .padding(2.dp)
//                    .drawBehind {
//                        drawFreeBlock(
//                            rectangleColor = rectangleColor,
//                            cornerRadius = CornerRadius(
//                                4.dp.toPx(),
//                                4.dp.toPx()
//                            )
//                        )
//                    }
//                    .clickable {
//                        onBlockClicked(block)
//                    }
//            )
//        }
//    }
//}
//
//@Composable
//private fun BlockDrawer(
//    borderColor: Color,
//    drawBlock: DrawScope.() -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Canvas(
//        modifier = modifier
//            .border(
//                2.dp,
//                borderColor,
//                RoundedCornerShape(6.dp)
//            )
//            .padding(2.dp)
//    ) {
//        drawBlock()
//    }
//}
//
//private fun DrawScope.drawFreeBlock(
//    rectangleColor: Color,
//    cornerRadius: CornerRadius
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//}
//
//private fun DrawScope.drawWallBlock(
//    rectangleColor: Color,
//    lineColor: Color,
//    cornerRadius: CornerRadius,
//    size: Size
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//
//    val lineWidth = 2.dp.toPx()
//
//    val times = 6
//
//    val intervalX = size.width / times
//    val intervalY = size.height / times
//
//    repeat(times) { number ->
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = size.height - (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = 0f
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalX * number),
//                y = size.height
//            ),
//            end = Offset(
//                x = size.width,
//                y = 0f + (intervalY * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalY * number),
//                y = 0f
//            ),
//            end = Offset(
//                x = size.width,
//                y = size.height - (intervalX * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = 0f + (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = size.height
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//@Composable
//private fun Block(
//    block: Block,
//    modifier: Modifier = Modifier
//) {
//    val tertiaryBorder = MaterialTheme.colorScheme.onTertiaryContainer
//    val tertiaryRectangle = MaterialTheme.colorScheme.tertiaryContainer
//
//    val hasRssi = abs(block.rssi) in 0..100
//    val rssiGreen = abs(block.rssi.toFloat()) / 100
//
//    val borderColor = Color(
//        tertiaryBorder.red,
//        if (hasRssi) rssiGreen else tertiaryBorder.green,
//        tertiaryBorder.blue,
//        tertiaryBorder.alpha,
//        tertiaryBorder.colorSpace
//    )
//
//    val rectangleColor = Color(
//        tertiaryRectangle.red,
//        if (hasRssi) rssiGreen else tertiaryRectangle.green,
//        tertiaryRectangle.blue,
//        tertiaryRectangle.alpha,
//        tertiaryRectangle.colorSpace
//    )
//
//    when (block.type) {
//
//        BlockType.WALL -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawWallBlock(
//                        rectangleColor = rectangleColor,
//                        lineColor = borderColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        ),
//                        size = size
//                    )
//                },
//                modifier = modifier
//            )
//        }
//
//        BlockType.FREE -> {
//            BlockDrawer(
//                borderColor = borderColor,
//                drawBlock = {
//                    drawFreeBlock(
//                        rectangleColor = rectangleColor,
//                        cornerRadius = CornerRadius(
//                            4.dp.toPx(),
//                            4.dp.toPx()
//                        )
//                    )
//                },
//                modifier = modifier
//            )
//        }
//
//        else -> {
//            Icon(
//                painter = painterResource(id = block.imageId),
//                contentDescription = null,
//                modifier = modifier
//                    .border(
//                        2.dp,
//                        borderColor,
//                        RoundedCornerShape(4.dp)
//                    )
//                    .padding(2.dp)
//                    .drawBehind {
//                        drawFreeBlock(
//                            rectangleColor = rectangleColor,
//                            cornerRadius = CornerRadius(
//                                4.dp.toPx(),
//                                4.dp.toPx()
//                            )
//                        )
//                    }
//            )
//        }
//    }
//}
//
//@Composable
//private fun BlockDrawer(
//    borderColor: Color,
//    drawBlock: DrawScope.() -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Canvas(
//        modifier = modifier
//            .border(
//                2.dp,
//                borderColor,
//                RoundedCornerShape(4.dp)
//            )
//            .padding(2.dp)
//    ) {
//        drawBlock()
//    }
//}
//
//private fun DrawScope.drawFreeBlock(
//    rectangleColor: Color,
//    cornerRadius: CornerRadius
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//}
//
//private fun DrawScope.drawWallBlock(
//    rectangleColor: Color,
//    lineColor: Color,
//    cornerRadius: CornerRadius,
//    size: Size
//) {
//    drawRoundRect(
//        color = rectangleColor,
//        cornerRadius = cornerRadius
//    )
//
//    val lineWidth = 2.dp.toPx()
//
//    val times = 6
//
//    val intervalX = size.width / times
//    val intervalY = size.height / times
//
//    repeat(times) { number ->
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = size.height - (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = 0f
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalX * number),
//                y = size.height
//            ),
//            end = Offset(
//                x = size.width,
//                y = 0f + (intervalY * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f + (intervalY * number),
//                y = 0f
//            ),
//            end = Offset(
//                x = size.width,
//                y = size.height - (intervalX * number)
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//
//        drawLine(
//            color = lineColor,
//            start = Offset(
//                x = 0f,
//                y = 0f + (intervalY * number)
//            ),
//            end = Offset(
//                x = size.width - (intervalX * number),
//                y = size.height
//            ),
//            strokeWidth = lineWidth,
//            cap = StrokeCap.Round
//        )
//    }
//}