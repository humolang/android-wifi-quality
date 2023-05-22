package com.humolang.wifiless.data.datasources.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.humolang.wifiless.data.datasources.DATABASE_NAME
import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat

@Database(
    entities = [
        Heat::class,
        Column::class,
        Block::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MappingDatabase : RoomDatabase() {

    abstract fun heatDao(): HeatDao
    abstract fun columnDao(): ColumnDao
    abstract fun blockDao(): BlockDao

    companion object {

        @Volatile
        private var instance: MappingDatabase? = null

        fun getDatabase(context: Context): MappingDatabase {
            return instance ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    MappingDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()

                instance = database
                database
            }
        }
    }
}