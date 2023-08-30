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

import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow

class HeatsRepository(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao
) {

    private val _heats = heatDao.loadHeats()
    val heats: Flow<List<Heat>>
        get() = _heats

    suspend fun deleteHeat(heat: Heat) {
        heatDao.delete(heat)
    }

    fun loadBlocks(
        heatId: Long
    ): Flow<Map<Column, List<Block>>> {

        return columnDao
            .loadObservableBlocks(heatId)
    }
}