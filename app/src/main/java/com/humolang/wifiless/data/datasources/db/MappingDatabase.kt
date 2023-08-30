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
    version = 1,
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