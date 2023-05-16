package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class PlanningTool(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao,
    private val blockDao: BlockDao
) {

    private var _blocks: Flow<Map<Column, List<Block>>> = emptyFlow()
    val blocks: Flow<Map<Column, List<Block>>>
        get() = _blocks

    suspend fun insertHeat(
        name: String,
        length: String,
        width: String
    ): Long {
        val timestamp = System.currentTimeMillis()
        val heat = Heat(
            name = name,
            length = length.toIntOrNull() ?: 1,
            width = width.toIntOrNull() ?: 1,
            creationTimestamp = timestamp,
            modificationTimestamp = timestamp
        )

        val id = heatDao.insert(heat)
            .first()

        return id
    }

    suspend fun deleteHeat(heat: Heat) {
        heatDao.delete(heat)
    }

    suspend fun loadHeatById(id: Int): Heat =
        heatDao.loadHeatById(id)

    suspend fun insertColumn(
        heatId: Int,
        x: Int
    ): Long {
        val column = Column(
            heatId = heatId,
            x = x
        )

        val id = columnDao.insert(column)
            .first()

        return id
    }

    suspend fun insertBlock(
        columnId: Int,
        y: Int
    ): Long {
        val block = Block(
            columnId = columnId,
            y = y
        )

        val id = blockDao.insert(block)
            .first()

        return id
    }

    fun loadBlocks(heatId: Int) {
        _blocks = columnDao.loadBlocks(heatId)
    }
}