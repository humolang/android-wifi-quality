package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow

class HeatsRepository(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao
) {

    private val _heats = heatDao.loadHeats()
    val heats: Flow<List<Heat>>
        get() = _heats

    suspend fun deleteHeat(heat: Heat) {
        heatDao.delete(heat)
    }

    fun loadBlocks(
        heatId: Long
    ): Flow<Map<Column, List<Block>>> {

        return columnDao
            .loadObservableBlocks(heatId)
    }
}