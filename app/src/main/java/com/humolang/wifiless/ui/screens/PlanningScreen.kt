package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel
import kotlin.math.abs

@Composable
fun PlanningScreen(
    onCancelClicked: () -> Unit,
    navigateToMapping: (Int) -> Unit,
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    val planningUiState by planningViewModel
        .planningUiState.collectAsStateWithLifecycle()

    if (planningUiState.heat == null) {
        RoomParameters(
            onCancelClicked = { onCancelClicked() },
            onNextClicked = { name, length, width ->
                planningViewModel.initialHeat(
                    name,
                    length,
                    width
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    } else {
        val heat = planningUiState.heat
        val blocks = planningUiState.blocks

        if (heat != null) {
            PlanningField(
                columns = heat.length,
                rows = heat.width,
                blocks = blocks,
                onBackClicked = { planningViewModel.resetHeat() },
                onNextClicked = { navigateToMapping(heat.id) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomParameters(
    onCancelClicked: () -> Unit,
    onNextClicked: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.room_parameters))

        var name by remember { mutableStateOf("") }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(text = stringResource(
                    id = R.string.plan_name)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        var length by remember { mutableStateOf("") }
        OutlinedTextField(
            value = length,
            onValueChange = { length = it },
            label = {
                Text(text = stringResource(
                    id = R.string.length)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        var width by remember { mutableStateOf("") }
        OutlinedTextField(
            value = width,
            onValueChange = { width = it },
            label = {
                Text(text = stringResource(
                    id = R.string.width)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            OutlinedButton(
                onClick = { onCancelClicked() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Button(
                onClick = { onNextClicked(name, length, width) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }
    }
}

@Composable
private fun PlanningField(
    columns: Int,
    rows: Int,
    blocks: Map<Column, List<Block>>,
    onBackClicked: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        RoomPlan(
            columns = columns,
            rows = rows,
            blocks = blocks
        )

        Row(modifier = Modifier.padding(top = 16.dp)) {
            OutlinedButton(
                onClick = { onBackClicked() }
            ) {
                Text(text = stringResource(id = R.string.back))
            }
            Button(
                onClick = { onNextClicked() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }
    }
}

@Composable
private fun RoomPlan(
    columns: Int,
    rows: Int,
    blocks: Map<Column, List<Block>>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    val ratioValue by remember {
        mutableStateOf(columns.toFloat() / rows)
    }

    Row(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
            .aspectRatio(ratioValue)
    ) {
        for (column in blocks) {

            Column(modifier = Modifier.weight(1F)) {
                for (block in column.value) {

                    Block(
                        block = block,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Block(
    block: Block,
    modifier: Modifier = Modifier
) {
    val tertiaryBorder = MaterialTheme.colorScheme.tertiary
    val tertiaryRectangle = MaterialTheme.colorScheme.tertiaryContainer

    val hasRssi = abs(block.rssi) in 0..100
    val rssiGreen = abs(block.rssi.toFloat()) / 100

    val borderColor = Color(
        tertiaryBorder.red,
        if (hasRssi) rssiGreen else tertiaryBorder.green,
        tertiaryBorder.blue,
        tertiaryBorder.alpha,
        tertiaryBorder.colorSpace
    )

    val rectangleColor = Color(
        tertiaryRectangle.red,
        if (hasRssi) rssiGreen else tertiaryRectangle.green,
        tertiaryRectangle.blue,
        tertiaryRectangle.alpha,
        tertiaryRectangle.colorSpace
    )

    val selectedColor = MaterialTheme.colorScheme.primaryContainer
    var selected by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(4.dp)
            )
            .clickable {
                selected = !selected
            },
        onDraw = {
            drawRoundRect(
                color = if (selected)
                    selectedColor
                else rectangleColor,
                cornerRadius = CornerRadius(
                    4.dp.toPx(),
                    4.dp.toPx()
                )
            )
        }
    )
}