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
}