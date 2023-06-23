package com.humolang.wifiless

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.humolang.wifiless.data.datasources.DATA_STORE_NAME

val Context.dataStore by preferencesDataStore(
    name = DATA_STORE_NAME
)