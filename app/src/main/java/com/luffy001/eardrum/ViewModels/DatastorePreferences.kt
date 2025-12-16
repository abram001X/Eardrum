package com.luffy001.eardrum.ViewModels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.luffy001.eardrum.MyApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "layout_references")

class DatastorePreferences() {
    private val dataStore = MyApplication.instance.dataStore

    companion object{
        val LIST_ORDER_KEY = stringPreferencesKey("date")
    }
    // leer preferencia
    val listOrder : Flow<String> = dataStore.data.map { preferences -> preferences[LIST_ORDER_KEY]  ?: "abc"}

    // reescribir preferencia
    suspend fun saveListOrder(order: String){
        dataStore.edit { preferences -> preferences[LIST_ORDER_KEY] = order }
    }
}
