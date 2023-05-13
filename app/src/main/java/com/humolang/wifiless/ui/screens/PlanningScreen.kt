package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.model.MappingBlock
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel

@Composable
fun PlanningScreen(
    onCancelClicked: () -> Unit,
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    val planningUiState by planningViewModel
        .planningUiState.collectAsStateWithLifecycle()

    if (!planningUiState.parametersEntered) {
        RoomParameters(
            onCancelClicked = { onCancelClicked() },
            onNextClicked = { length, width ->
                planningViewModel.saveParameters(length, width)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    } else {
        PlanningField(
            columns = planningUiState.columns,
            rows = planningUiState.rows,
            blocks = planningUiState.blocks,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomParameters(
    onCancelClicked: () -> Unit,
    onNextClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.room_parameters))

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
                onClick = { onNextClicked(length, width) },
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
    blocks: Map<Pair<Int, Int>, MappingBlock>,
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
        mutableStateOf(rows.toFloat() / columns)
    }

    Column(
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
        for (column in 0 until columns) {

            Row(modifier = Modifier.weight(1F)) {
                for (row in 0 until rows) {

                    val key = Pair(column, row)
                    Block(
                        block = blocks[key] ?: MappingBlock(),
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
    block: MappingBlock,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.onBackground
    val rectColor = MaterialTheme.colorScheme.tertiary

    Canvas(
        modifier = modifier
            .border(
                2.dp,
                borderColor,
                RoundedCornerShape(4.dp)
            ),
        onDraw = {
            drawRect(
                color = rectColor
            )
        }
    )
}