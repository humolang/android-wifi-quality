package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import com.humolang.wifiless.data.datasources.model.BlockType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class PlanningTool(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao,
    private val blockDao: BlockDao
) {

    private var _heat = emptyFlow<Heat>()
    val heat: Flow<Heat>
        get() = _heat

    private var _blocks =
        emptyFlow<Map<Column, List<Block>>>()
    val blocks: Flow<Map<Column, List<Block>>>
        get() = _blocks

    suspend fun initialHeat(): Long {
        val timestamp = System.currentTimeMillis()
        val heat = Heat(
            creationTimestamp = timestamp,
            modificationTimestamp = timestamp
        )
        val heatId = insertHeat(heat)

        val columnList = mutableListOf<Column>()
        for (index in 0 until heat.columns) {
            val column = Column(
                heatId = heatId,
                x = index
            )

            columnList.add(column)
        }
        val columnIds = insertColumns(columnList)

        val blockList = mutableListOf<Block>()
        for (columnId in columnIds) {
            for (row in 0 until heat.rows) {
                val block = Block(
                    columnId = columnId,
                    y = row
                )

                blockList.add(block)
            }
        }
        insertBlocks(blockList)

        return heatId
    }

    private suspend fun insertHeat(
        heat: Heat
    ): Long {
        val id = heatDao.insert(heat).first()
        return id
    }

    private suspend fun insertColumns(
        columns: List<Column>
    ): List<Long> {
        val listIds = columnDao.insert(columns)
        return listIds
    }

    private suspend fun updateColumns(
        columns: List<Column>
    ): Int {
        val columnsUpdated = columnDao
            .update(columns)

        return columnsUpdated
    }

    private suspend fun insertBlocks(
        blocks: List<Block>
    ): List<Long> {
        val blockIds = blockDao.insert(blocks)
        return blockIds
    }

    private suspend fun updateBlocks(
        blocks: List<Block>
    ): Int {
        val blocksUpdated = blockDao
            .update(blocks)

        return blocksUpdated
    }

    fun loadHeat(heatId: Long) {
        _heat = heatDao.loadObservableHeat(heatId)
    }

    fun loadBlocks(heatId: Long) {
        _blocks = columnDao.loadObservableBlocks(heatId)
    }

    suspend fun updateBlockType(
        block: Block,
        type: BlockType
    ) {
        val updated = block.copy(
            type = type
        )

        blockDao.update(updated)
    }

    suspend fun updateHeatName(
        heat: Heat,
        name: String
    ) {
        val updated = heat.copy(
            name = name
        )

        heatDao.update(updated)
    }

    suspend fun insertTopRow(heatId: Long) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val updatedHeat = oldHeat.copy(
            rows = oldHeat.rows + 1
        )
        heatDao.update(updatedHeat)

        val blocks = columnDao.loadBlocks(updatedHeat.id)
        val updatedBlocksList = mutableListOf<Block>()
        val blocksList = mutableListOf<Block>()

        for (column in blocks) {
            for (block in column.value) {
                val updated = block.copy(
                    y = block.y + 1
                )

                updatedBlocksList.add(updated)
            }

            val block = Block(
                columnId = column.key.id,
                y = 0
            )

            blocksList.add(block)
        }
        updateBlocks(updatedBlocksList)
        insertBlocks(blocksList)
    }

    suspend fun insertBottomRow(heatId: Long) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val updatedHeat = oldHeat.copy(
            rows = oldHeat.rows + 1
        )
        heatDao.update(updatedHeat)

        val columns = columnDao.loadColumns(heatId)
        val blocksList = mutableListOf<Block>()
        for (column in columns) {
            val block = Block(
                columnId = column.id,
                y = updatedHeat.rows - 1
            )

            blocksList.add(block)
        }
        insertBlocks(blocksList)
    }

    suspend fun insertRightColumn(heatId: Long) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val updatedHeat = oldHeat.copy(
            columns = oldHeat.columns + 1
        )
        heatDao.update(updatedHeat)

        val column = Column(
            heatId = updatedHeat.id,
            x = updatedHeat.columns - 1
        )
        val columnId = columnDao.insert(column).first()

        val blocksList = mutableListOf<Block>()
        for (row in 0 until updatedHeat.rows) {
            val block = Block(
                columnId = columnId,
                y = row
            )

            blocksList.add(block)
        }
        insertBlocks(blocksList)
    }

    suspend fun insertLeftColumn(heatId: Long) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val updatedHeat = oldHeat.copy(
            columns = oldHeat.columns + 1
        )
        heatDao.update(updatedHeat)

        val newColumn = Column(
            heatId = updatedHeat.id,
            x = 0
        )
        val columnId = columnDao.insert(newColumn).first()

        val blocks = columnDao.loadBlocks(updatedHeat.id)
        val updatedColumnsList = mutableListOf<Column>()

        for (column in blocks.keys) {
            val updatedColumn = column.copy(
                x = column.x + 1
            )

            updatedColumnsList.add(updatedColumn)
        }
        updateColumns(updatedColumnsList)

        val blocksList = mutableListOf<Block>()
        for (y in 0 until updatedHeat.rows) {
            val block = Block(
                columnId = columnId,
                y = y
            )

            blocksList.add(block)
        }
        insertBlocks(blocksList)
    }
}