package com.humolang.wifiless.data.model

class MappingSpace {

    private var _length = 1
    val length: Int
        get() = _length

    private var _width = 1
    val width: Int
        get() = _width

    private val _blocks =
        hashMapOf<Pair<Int, Int>, MappingBlock>()
    val blocks: Map<Pair<Int, Int>, MappingBlock>
        get() = _blocks

    fun initial(length: Int, width: Int) {
        initialWithEmptyBlocks(length, width)
    }

    private fun initialWithEmptyBlocks(length: Int, width: Int) {
        _length = length
        _width = width

        for (column in 0 until length) {
            for (row in 0 until width) {
                val key = Pair(column, row)
                _blocks[key] = MappingBlock()
            }
        }
    }
}