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

package com.humolang.wifiless.data.datasources.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.Date

@Entity(tableName = "heats")
data class Heat(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "heat_id")
    val id: Long = 0L,

    val name: String = "New Plan",
    val columns: Int = 3,
    val rows: Int = 3,

    @ColumnInfo(name = "creation_timestamp")
    val creationTimestamp: Long = 0L,

    @ColumnInfo(name = "modification_timestamp")
    val modificationTimestamp: Long = 0L
) {

    val creationDate: String
        get() = format(creationTimestamp)

    val modificationDate: String
        get() = format(modificationTimestamp)

    private fun format(timestamp: Long): String {
        val date = Date(timestamp)
        val formatted = DateFormat
            .getDateInstance()
            .format(date)

        return formatted
    }
}