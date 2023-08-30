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
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow

@Dao
interface HeatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg heats: Heat): List<Long>

    @Update
    suspend fun update(vararg heats: Heat): Int

    @Delete
    suspend fun delete(vararg heats: Heat): Int

    @Query("select * from heats " +
            "where heat_id = :id")
    suspend fun loadHeatById(id: Long): Heat

    @Query("select * from heats " +
            "where heat_id = :id")
    fun loadObservableHeat(id: Long): Flow<Heat>

    @Query("select * from heats " +
            "order by modification_timestamp desc")
    fun loadHeats(): Flow<List<Heat>>
}