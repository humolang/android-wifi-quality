package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.model.MappingBlock

data class MappingUiState(
    val blocks: List<MappingBlock> = emptyList()
)
