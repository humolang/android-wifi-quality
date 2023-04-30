package com.humolang.wifiless.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.humolang.wifiless.WiFilessApplication
import com.humolang.wifiless.data.RssiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class StartViewModel(
    private val _dequeCapacity: Int = 120,
    private val rssiRepository: RssiRepository
) : ViewModel() {

    private val _latestRssi = MutableStateFlow(0)
    val latestRssi = _latestRssi.asStateFlow()

    private val _rssiDeque = ArrayDeque<Int>(_dequeCapacity)

    private val _rssiValues = MutableStateFlow(_rssiDeque.toList())
    val rssiValues: StateFlow<List<Int>>
        get() = _rssiValues.asStateFlow()

    val dequeCapacity: Int
        get() = _dequeCapacity

    init {
        viewModelScope.launch(Dispatchers.IO) {
            rssiRepository.latestRssi.collect { rssi ->
                _latestRssi.value = rssi

                _rssiDeque.add(abs(rssi))
                if (_rssiDeque.size > dequeCapacity) {
                    _rssiDeque.removeFirst()
                }

                _rssiValues.value = _rssiDeque.toList()
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val rssiRepository = (this[APPLICATION_KEY]
                        as WiFilessApplication).rssiRepository

                StartViewModel(
                    rssiRepository = rssiRepository
                )
            }
        }
    }
}