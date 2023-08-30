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

package com.humolang.wifiless.data.datasources.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.humolang.wifiless.data.datasources.db.entities.Block

@Dao
interface BlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg blocks: Block): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blocks: List<Block>): List<Long>

    @Update
    suspend fun update(vararg blocks: Block): Int

    @Update
    suspend fun update(blocks: List<Block>): Int

    @Delete
    suspend fun delete(vararg blocks: Block): Int

    @Delete
    suspend fun delete(blocks: List<Block>): Int

    @Query("select blocks.block_id, blocks.column_id, blocks.y, blocks.type, blocks.rssi " +
            "from blocks " +
            "join columns on columns.column_id = blocks.column_id " +
            "where heat_id = :heatId and y >= :y")
    suspend fun loadLowerBlocks(heatId: Long, y: Int): List<Block>
}