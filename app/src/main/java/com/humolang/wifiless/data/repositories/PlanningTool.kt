package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.model.MappingBlock
import com.humolang.wifiless.data.model.MappingSpace

class PlanningTool(
    private val mappingSpace: MappingSpace
) {

    val length: Int
        get() = mappingSpace.length

    val width: Int
        get() = mappingSpace.width

    val blocks: Map<Pair<Int, Int>, MappingBlock>
        get() = mappingSpace.blocks

    fun saveParameters(length: Int, width: Int) {
        mappingSpace.initial(length, width)
    }
}