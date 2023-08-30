/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    suspend fun initialHeatmap(): Long {
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

    private suspend fun insertColumn(
        column: Column
    ): Long {
        val id = columnDao.insert(column).first()
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

    private suspend fun deleteColumns(
        columns: List<Column>
    ): Int {
        val columnsDeleted = columnDao
            .delete(columns)

        return columnsDeleted
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

    private suspend fun deleteBlocks(
        blocks: List<Block>
    ): Int {
        val blocksDeleted = blockDao
            .delete(blocks)

        return blocksDeleted
    }

    suspend fun updateBlockType(
        heat: Heat,
        block: Block,
        type: BlockType
    ) {
        val timestamp = System.currentTimeMillis()
        val updatedHeat = heat.copy(
            modificationTimestamp = timestamp
        )

        heatDao.update(updatedHeat)

        val updated = block.copy(
            type = type
        )

        blockDao.update(updated)
    }

    suspend fun updateHeatName(
        heat: Heat,
        name: String
    ) {
        val timestamp = System.currentTimeMillis()
        val updated = heat.copy(
            name = name,
            modificationTimestamp = timestamp
        )

        heatDao.update(updated)
    }

    suspend fun insertRow(heatId: Long, y: Int) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val timestamp = System.currentTimeMillis()
        val updatedHeat = oldHeat.copy(
            rows = oldHeat.rows + 1,
            modificationTimestamp = timestamp
        )
        heatDao.update(updatedHeat)

        if (y != oldHeat.rows) {
            updateLowerBlocks(heatId, y)
        }

        insertLowerBlocks(heatId, y)
    }

    private suspend fun insertLowerBlocks(heatId: Long, y: Int) {
        val columns = columnDao.loadColumns(heatId)
        val insertedBlocksList = mutableListOf<Block>()

        for (column in columns) {
            val block = Block(
                columnId = column.id,
                y = y
            )

            insertedBlocksList.add(block)
        }
        insertBlocks(insertedBlocksList)
    }

    private suspend fun updateLowerBlocks(heatId: Long, y: Int) {
        val blocks = blockDao.loadLowerBlocks(heatId, y)
        val updatedBlocksList = mutableListOf<Block>()

        for (block in blocks) {
            val updatedBlock = block.copy(
                y = block.y + 1
            )

            updatedBlocksList.add(updatedBlock)
        }
        updateBlocks(updatedBlocksList)
    }

    suspend fun insertColumn(heatId: Long, x: Int) {
        val oldHeat = heatDao.loadHeatById(heatId)
        val timestamp = System.currentTimeMillis()
        val updatedHeat = oldHeat.copy(
            columns = oldHeat.columns + 1,
            modificationTimestamp = timestamp
        )
        heatDao.update(updatedHeat)

        if (x != oldHeat.columns) {
            updateRighterColumns(heatId, x)
        }

        insertRighterBlocks(heatId, x, updatedHeat.rows)
    }

    private suspend fun insertRighterBlocks(
        heatId: Long,
        x: Int,
        rows: Int
    ) {
        val insertedColumn = Column(
            heatId = heatId,
            x = x
        )
        val columnId = insertColumn(insertedColumn)

        val insertedBlocksList = mutableListOf<Block>()
        for (y in 0 until rows) {
            val insertedBlock = Block(
                columnId = columnId,
                y = y
            )

            insertedBlocksList.add(insertedBlock)
        }
        insertBlocks(insertedBlocksList)
    }

    private suspend fun updateRighterColumns(heatId: Long, x: Int) {
        val columns = columnDao.loadRighterColumns(heatId, x)
        val updatedColumnsList = mutableListOf<Column>()

        for (column in columns) {
            val updatedColumn = column.copy(
                x = column.x + 1
            )

            updatedColumnsList.add(updatedColumn)
        }
        updateColumns(updatedColumnsList)
    }

    suspend fun deleteRow(heatId: Long, y: Int) {
        val oldHeat = heatDao.loadHeatById(heatId)

        if (oldHeat.rows > 1) {
            val timestamp = System.currentTimeMillis()
            val updatedHeat = oldHeat.copy(
                rows = oldHeat.rows - 1,
                modificationTimestamp = timestamp
            )
            heatDao.update(updatedHeat)

            val blocks = blockDao.loadLowerBlocks(heatId, y)
            val updatedBlocksList = mutableListOf<Block>()
            val deletedBlocksList = mutableListOf<Block>()

            for (block in blocks) {
                if (block.y == y) {
                    deletedBlocksList.add(block)
                } else {
                    val updatedBlock = block.copy(
                        y = block.y - 1
                    )

                    updatedBlocksList.add(updatedBlock)
                }
            }

            deleteBlocks(deletedBlocksList)
            updateBlocks(updatedBlocksList)
        }
    }

    suspend fun deleteColumn(heatId: Long, x: Int) {
        val oldHeat = heatDao.loadHeatById(heatId)

        if (oldHeat.columns > 1) {
            val timestamp = System.currentTimeMillis()
            val updatedHeat = oldHeat.copy(
                columns = oldHeat.columns - 1,
                modificationTimestamp = timestamp
            )
            heatDao.update(updatedHeat)

            val columns = columnDao.loadRighterColumns(heatId, x)
            val updatedColumnsList = mutableListOf<Column>()
            val deletedColumnsList=  mutableListOf<Column>()

            for (column in columns) {
                if (column.x == x) {
                    deletedColumnsList.add(column)
                } else {
                    val updatedColumn = column.copy(
                        x = column.x - 1
                    )

                    updatedColumnsList.add(updatedColumn)
                }
            }

            deleteColumns(deletedColumnsList)
            updateColumns(updatedColumnsList)
        }
    }

    fun loadHeat(heatId: Long) {
        _heat = heatDao
            .loadObservableHeat(heatId)
    }

    fun loadBlocks(heatId: Long) {
        _blocks = columnDao
            .loadObservableBlocks(heatId)
    }
}