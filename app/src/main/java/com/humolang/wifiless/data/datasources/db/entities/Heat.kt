package com.humolang.wifiless.data.datasources.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heats")
data class Heat(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "heat_id")
    val id: Int,

    val name: String,
    val length: Int,
    val width: Int,

    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long,

    @ColumnInfo(name = "modification_timestamp")
    val modificationTimestamp: Long
)
