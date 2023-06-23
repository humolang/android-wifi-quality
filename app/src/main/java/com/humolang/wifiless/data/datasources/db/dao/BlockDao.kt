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