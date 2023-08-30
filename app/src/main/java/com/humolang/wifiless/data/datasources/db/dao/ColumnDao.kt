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
import com.humolang.wifiless.data.datasources.db.entities.Column
import kotlinx.coroutines.flow.Flow

@Dao
interface ColumnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg columns: Column): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(columns: List<Column>): List<Long>

    @Update
    suspend fun update(vararg columns: Column): Int

    @Update
    suspend fun update(columns: List<Column>): Int

    @Delete
    suspend fun delete(vararg columns: Column): Int

    @Delete
    suspend fun delete(columns: List<Column>): Int

    @Query("select * from columns " +
            "join blocks on columns.column_id = blocks.column_id " +
            "where heat_id = :heatId " +
            "order by columns.x, blocks.y asc")
    fun loadObservableBlocks(heatId: Long): Flow<Map<Column, List<Block>>>

    @Query("select * from columns " +
            "join blocks on columns.column_id = blocks.column_id " +
            "where heat_id = :heatId " +
            "order by columns.x, blocks.y asc")
    suspend fun loadBlocks(heatId: Long): Map<Column, List<Block>>

    @Query("select * from columns " +
            "where heat_id = :heatId " +
            "order by x asc")
    suspend fun loadColumns(heatId: Long): List<Column>

    @Query("select * from columns " +
            "where heat_id = :heatId and x >= :x")
    suspend fun loadRighterColumns(heatId: Long, x: Int): List<Column>
}