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

    @ColumnInfo(name = "column_id")
    val columnId: Long,

    val y: Int,
    val type: BlockType = BlockType.FREE,
    val rssi: Int = Int.MIN_VALUE
) {

    val imageId: Int
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
                    .twotone_app_icon_24
            }

            return image
        }
}
