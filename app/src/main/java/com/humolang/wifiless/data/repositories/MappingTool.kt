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

import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MappingTool(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao,
    private val blockDao: BlockDao,
    private val rssiValue: RssiValue
) {

    private var _heat = emptyFlow<Heat>()
    val heat: Flow<Heat>
        get() = _heat

    private var _blocks =
        emptyFlow<Map<Column, List<Block>>>()
    val blocks: Flow<Map<Column, List<Block>>>
        get() = _blocks

    val minRssi: Int
        get() = rssiValue.minRssi

    fun loadHeat(heatId: Long) {
        _heat = heatDao
            .loadObservableHeat(heatId)
    }

    fun loadBlocks(heatId: Long) {
        _blocks = columnDao
            .loadObservableBlocks(heatId)
    }

    suspend fun checkRssi(heat: Heat, block: Block) {
        val timestamp = System.currentTimeMillis()
        val updated = heat.copy(
            modificationTimestamp = timestamp
        )

        heatDao.update(updated)

        val rssi = rssiValue.rssi
        val updatedBlock = block.copy(
            rssi = rssi
        )

        blockDao.update(updatedBlock)
    }
}