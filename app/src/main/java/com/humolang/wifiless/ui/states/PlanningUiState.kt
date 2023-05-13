package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.MappingBlock

data class PlanningUiState(
    val parametersEntered: Boolean = false,
    val columns: Int = 1,
    val rows: Int = 1,
    val blocks: Map<Pair<Int, Int>, MappingBlock> = emptyMap()
)
