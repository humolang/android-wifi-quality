package com.humolang.wifiless.data.datasources.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heats")
data class Heat(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "heat_id")
    val id: Long = 0,

    val name: String = "New Plan",
    val columns: Int = 3,
    val rows: Int = 3,

    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long = 0L,

    @ColumnInfo(name = "modification_timestamp")
    val modificationTimestamp: Long = 0L
)
