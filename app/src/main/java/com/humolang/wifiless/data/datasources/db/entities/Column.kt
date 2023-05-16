package com.humolang.wifiless.data.datasources.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "columns",
    foreignKeys = [ForeignKey(
        entity = Heat::class,
        parentColumns = arrayOf("heat_id"),
        childColumns = arrayOf("heat_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Column(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "column_id")
    val id: Int = 0,

    @ColumnInfo(name = "heat_id")
    val heatId: Int,

    val x: Int
)
