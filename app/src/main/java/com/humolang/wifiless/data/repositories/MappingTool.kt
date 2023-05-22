package com.humolang.wifiless.data.repositories

import com.humolang.wifiless.data.datasources.RssiValue
import com.humolang.wifiless.data.datasources.db.dao.BlockDao
import com.humolang.wifiless.data.datasources.db.dao.ColumnDao
import com.humolang.wifiless.data.datasources.db.dao.HeatDao

class MappingTool(
    private val heatDao: HeatDao,
    private val columnDao: ColumnDao,
    private val blockDao: BlockDao,
    private val rssiValue: RssiValue
) {

//    private var _blocks: Flow<Map<Column, List<Block>>> = emptyFlow()
//    val blocks: Flow<Map<Column, List<Block>>>
//        get() = _blocks
//
//    suspend fun loadHeatById(id: Int): Heat =
//        heatDao.loadHeatById(id)
//
//    fun loadBlocks(heatId: Int) {
//        _blocks = columnDao.loadBlocks(heatId)
//    }
//
//    suspend fun checkRssi(block: Block) {
//        val rssi = rssiValue.rssi
//        val updatedBlock = block.copy(
//            rssi = rssi
//        )
//
//        blockDao.update(updatedBlock)
//    }
}