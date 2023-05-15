package com.humolang.wifiless.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.ui.viewmodels.PlanningViewModel

@Composable
fun PlanningScreen(
    planningViewModel: PlanningViewModel =
        viewModel(factory = PlanningViewModel.Factory)
) {
    val planningUiState by planningViewModel
        .planningUiState.collectAsStateWithLifecycle()
}

//@Composable
//fun PlanningScreen(
//    onCancelClicked: () -> Unit,
//    navigateToMapping: () -> Unit,
//    planningViewModel: PlanningViewModel =
//        viewModel(factory = PlanningViewModel.Factory)
//) {
//    val planningUiState by planningViewModel
//        .planningUiState.collectAsStateWithLifecycle()
//
//    if (!planningUiState.parametersEntered) {
//        RoomParameters(
//            onCancelClicked = { onCancelClicked() },
//            onNextClicked = { length, width ->
//                planningViewModel
//                    .saveParameters(length, width)
//            },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize()
//        )
//    } else {
//        PlanningField(
//            columns = planningUiState.columns,
//            rows = planningUiState.rows,
//            blocks = planningUiState.blocks,
//            onBackClicked = { planningViewModel.removeParameters() },
//            onSaveClicked = { planningViewModel.savePlan() },
//            onNextClicked = { navigateToMapping() },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize()
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun RoomParameters(
//    onCancelClicked: () -> Unit,
//    onNextClicked: (String, String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier
//    ) {
//        Text(text = stringResource(id = R.string.room_parameters))
//
//        var length by remember { mutableStateOf("") }
//        OutlinedTextField(
//            value = length,
//            onValueChange = { length = it },
//            label = {
//                Text(text = stringResource(
//                    id = R.string.length)
//                )
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number
//            ),
//            modifier = Modifier.padding(top = 8.dp)
//        )
//
//        var width by remember { mutableStateOf("") }
//        OutlinedTextField(
//            value = width,
//            onValueChange = { width = it },
//            label = {
//                Text(text = stringResource(
//                    id = R.string.width)
//                )
//            },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number
//            ),
//            modifier = Modifier.padding(top = 8.dp)
//        )
//
//        Row(modifier = Modifier.padding(top = 16.dp)) {
//            OutlinedButton(
//                onClick = { onCancelClicked() }
//            ) {
//                Text(text = stringResource(id = R.string.cancel))
//            }
//            Button(
//                onClick = { onNextClicked(length, width) },
//                modifier = Modifier.padding(start = 8.dp)
//            ) {
//                Text(text = stringResource(id = R.string.next))
//            }
//        }
//    }
//}
//
//@Composable
//private fun PlanningField(
//    columns: Int,
//    rows: Int,
//    blocks: Map<Pair<Int, Int>, MappingBlock>,
//    onBackClicked: () -> Unit,
//    onSaveClicked: () -> Unit,
//    onNextClicked: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//        modifier = modifier
//    ) {
//        RoomPlan(
//            columns = columns,
//            rows = rows,
//            blocks = blocks
//        )
//
//        Row(modifier = Modifier.padding(top = 16.dp)) {
//            OutlinedButton(
//                onClick = { onBackClicked() }
//            ) {
//                Text(text = stringResource(id = R.string.back))
//            }
//            Button(
//                onClick = { onSaveClicked() },
//                modifier = Modifier.padding(start = 8.dp)
//            ) {
//                Text(text = stringResource(id = R.string.save))
//            }
//            Button(
//                onClick = { onNextClicked() },
//                modifier = Modifier.padding(start = 8.dp)
//            ) {
//                Text(text = stringResource(id = R.string.next))
//            }
//        }
//    }
//}
//
//@Composable
//private fun RoomPlan(
//    columns: Int,
//    rows: Int,
//    blocks: Map<Pair<Int, Int>, MappingBlock>,
//    modifier: Modifier = Modifier
//) {
//    var scale by remember { mutableStateOf(1f) }
//    var rotation by remember { mutableStateOf(0f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
//        scale *= zoomChange
//        rotation += rotationChange
//        offset += offsetChange
//    }
//
//    val ratioValue by remember {
//        mutableStateOf(rows.toFloat() / columns)
//    }
//
//    Column(
//        modifier = modifier
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//                rotationZ = rotation,
//                translationX = offset.x,
//                translationY = offset.y
//            )
//            .transformable(state = state)
//            .aspectRatio(ratioValue)
//    ) {
//        for (column in 0 until columns) {
//
//            Row(modifier = Modifier.weight(1F)) {
//                for (row in 0 until rows) {
//
//                    val key = Pair(column, row)
//                    Block(
//                        block = blocks[key] ?: MappingBlock(),
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .weight(1F)
//                            .padding(1.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun Block(
//    block: MappingBlock,
//    modifier: Modifier = Modifier
//) {
//    val tertiaryBorder = MaterialTheme.colorScheme.tertiary
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
//    val selectedColor = MaterialTheme.colorScheme.primaryContainer
//    var selected by remember { mutableStateOf(false) }
//
//    Canvas(
//        modifier = modifier
//            .border(
//                2.dp,
//                borderColor,
//                RoundedCornerShape(4.dp)
//            )
//            .clickable {
//                selected = !selected
//            },
//        onDraw = {
//            drawRect(
//                color = if (selected)
//                    selectedColor
//                else rectangleColor
//            )
//        }
//    )
//}