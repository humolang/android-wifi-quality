package com.humolang.wifiless.data.datasources.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.humolang.wifiless.data.BlockType

@Entity(
    tableName = "blocks",
    foreignKeys = [ForeignKey(
        entity = Column::class,
        parentColumns = arrayOf("column_id"),
        childColumns = arrayOf("column_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Block(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "block_id")
    val id: Int,

    @ColumnInfo(name = "column_id")
    val columnId: Int,

    val y: Int,

    @ColumnInfo(name = "block_type")
    val blockType: BlockType = BlockType.FREE,

    val rssi: Int = Int.MIN_VALUE
)
