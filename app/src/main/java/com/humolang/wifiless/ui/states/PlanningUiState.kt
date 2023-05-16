package com.humolang.wifiless.ui.states

import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat

data class PlanningUiState(
    val heat: Heat? = null,
    val blocks: Map<Column, List<Block>>
)
