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