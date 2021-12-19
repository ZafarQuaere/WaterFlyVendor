package com.waterfly.vendor.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.waterfly.vendor.model.ValidateUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val VENDOR_DATASTORE = "datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = VENDOR_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val JWT_TOKEN = stringPreferencesKey("JWT_TOKEN")
        val VENDOR_ID = stringPreferencesKey("VENDOR_ID")
        val phone = stringPreferencesKey("phone")
        val vendor_name = stringPreferencesKey("vendor_name")
        val plant_name = stringPreferencesKey("plant_name")
        val plant_phone = stringPreferencesKey("plant_phone")
        val plant_address = stringPreferencesKey("plant_address")
        val details_completed = stringPreferencesKey("details_completed")
        val isLogin = booleanPreferencesKey("isLogin")
    }


    suspend fun storeVendorData(data: ValidateUserData) {
        context.dataStore.edit {
            it[JWT_TOKEN] = data.JWT_Token
            it[VENDOR_ID] = data.id
            it[phone] = data.phone
            it[vendor_name] = data.vendor_name
            it[plant_name] = data.plant_name
            it[plant_phone] = data.plant_phone
            it[plant_address] = data.plant_address
            it[details_completed] = data.details_completed
        }
    }

    suspend fun getVendorData() = context.dataStore.data.map {
        ValidateUserData(
            JWT_Token = it[JWT_TOKEN] ?: "",
            id = it[VENDOR_ID] ?: "",
            phone = it[phone] ?: "",
            vendor_name = it[vendor_name] ?: "",
            plant_name = it[plant_name] ?: "",
            plant_phone = it[plant_phone] ?: "",
            plant_address = it[plant_address] ?: "",
            details_completed = it[details_completed] ?: ""
        )
    }

    suspend fun storeVendorId(id: String) {
        context.dataStore.edit {
            it[VENDOR_ID] = id
        }
    }

    suspend fun storeToken(token: String) {
        context.dataStore.edit {
            it[JWT_TOKEN] = token
        }
    }

    suspend fun setUserLogin( login: Boolean){
        context.dataStore.edit {
            it[isLogin] = login
        }
    }

    val isUserLogin:Flow<Boolean>
        get() = context.dataStore.data.map { preferences ->
            preferences[isLogin] == true
        }

    val getToken: Flow<String?>
        get() =
            context.dataStore.data.map { preferences ->
                preferences[JWT_TOKEN]
            }

    val getVendorId: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[VENDOR_ID]
        }
}