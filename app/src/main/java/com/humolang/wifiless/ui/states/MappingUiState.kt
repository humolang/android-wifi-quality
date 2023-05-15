package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column

data class MappingUiState(
    val blocks: Map<Column, List<Block>>
)
