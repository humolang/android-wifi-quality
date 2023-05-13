package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.MappingBlock

data class PlanningUiState(
    val blocks: List<MappingBlock> = emptyList()
)
