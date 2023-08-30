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
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.model.BlockType

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
    val id: Long = 0,

    @ColumnInfo(
        name = "column_id",
        index = true
    )
    val columnId: Long,

    val y: Int,
    val type: BlockType = BlockType.FREE,
    val rssi: Int = Int.MIN_VALUE
) {

    val drawableId: Int
        get() {
            val image = when (type) {

                BlockType.ARMCHAIR -> R.drawable
                    .twotone_chair_24

                BlockType.CHAIR -> R.drawable
                    .twotone_chair_alt_24

                BlockType.COMPUTER -> R.drawable
                    .twotone_computer_24

                BlockType.DOOR -> R.drawable
                    .twotone_door_front_24

                BlockType.ROUTER -> R.drawable
                    .twotone_router_24

                BlockType.TABLE -> R.drawable
                    .twotone_table_restaurant_24

                BlockType.TV -> R.drawable
                    .twotone_tv_24

                BlockType.WINDOW -> R.drawable
                    .twotone_window_24

                else -> R.drawable
                    .twotone_air_24
            }

            return image
        }
}
