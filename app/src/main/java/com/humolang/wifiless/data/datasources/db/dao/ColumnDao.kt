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

    @Update
    suspend fun update(vararg columns: Column): Int

    @Delete
    suspend fun delete(vararg columns: Column): Int

    @Query("select * from columns " +
            "join blocks on columns.column_id = blocks.column_id " +
            "where heat_id = :heatId " +
            "order by columns.x, blocks.y asc")
    fun loadBlocks(heatId: Int): Flow<Map<Column, List<Block>>>
}