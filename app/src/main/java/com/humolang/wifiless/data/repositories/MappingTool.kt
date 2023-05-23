package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao
import com.humolang.wifiless.data.datasources.db.entities.Block
import com.humolang.wifiless.data.datasources.db.entities.Column
import com.humolang.wifiless.data.datasources.db.entities.Heat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MappingTool(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao,
    private val blockDao: BlockDao,
    private val rssiValue: RssiValue
) {

    private var _heat = emptyFlow<Heat>()
    val heat: Flow<Heat>
        get() = _heat

    private var _blocks =
        emptyFlow<Map<Column, List<Block>>>()
    val blocks: Flow<Map<Column, List<Block>>>
        get() = _blocks

    fun loadHeat(heatId: Long) {
        _heat = heatDao
            .loadObservableHeat(heatId)
    }

    fun loadBlocks(heatId: Long) {
        _blocks = columnDao
            .loadObservableBlocks(heatId)
    }

    suspend fun checkRssi(block: Block) {
        val rssi = rssiValue.rssi
        val updatedBlock = block.copy(
            rssi = rssi
        )

        blockDao.update(updatedBlock)
    }
}