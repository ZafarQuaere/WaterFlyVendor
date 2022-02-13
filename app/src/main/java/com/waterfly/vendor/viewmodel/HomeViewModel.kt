package com.waterfly.vendor.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.waterfly.vendor.R
import com.waterfly.vendor.app.MyApplication
import com.waterfly.vendor.model.CheckVendorStatusResponse
import com.waterfly.vendor.model.VendorLiveLocationResponse
import com.waterfly.vendor.model.VendorStatusResponse
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.Event
import com.waterfly.vendor.util.Resource
import com.waterfly.vendor.util.Utils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeViewModel(app: Application, private val appRepository: AppRepository) :
    BaseViewModel(app) {

    private val _dataStoreValue = MutableLiveData(Pair("",""))
    val dataStoreValue : LiveData<Pair<String,String>> = _dataStoreValue

    private val _vendorStatusResponse = MutableLiveData<Event<Resource<VendorStatusResponse>>>()
    val vendorLiveStatusResponse: LiveData<Event<Resource<VendorStatusResponse>>> = _vendorStatusResponse

    private val vendorLocationResponse = MutableLiveData<Event<Resource<VendorLiveLocationResponse>>>()
    val vendorLiveLocationResponse: LiveData<Event<Resource<VendorLiveLocationResponse>>> = vendorLocationResponse

    private val _checkVendorStatusResponse = MutableLiveData<Event<Resource<CheckVendorStatusResponse>>>()
    val checkVendorLiveStatusResponse: LiveData<Event<Resource<CheckVendorStatusResponse>>> = _checkVendorStatusResponse


    fun getVendorLiveStatus(body: RequestBodies.GetVendorStatusBody) = viewModelScope.launch {
        checkVendorLiveStatus(body)
    }

    fun setVendorLiveStatus(body: RequestBodies.VendorStatusBody) = viewModelScope.launch {
        setVendorStatus(body)
    }

    private suspend fun setVendorStatus(body: RequestBodies.VendorStatusBody) {
        _vendorStatusResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.setVendorStatus(body)
                _vendorStatusResponse.postValue(handleVendorStatusResponse(response))
            } else {
                _vendorStatusResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection
                ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _vendorStatusResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure
                    ))))
                }
                else -> {
                    _vendorStatusResponse.postValue(
                        Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }

    private suspend fun checkVendorLiveStatus(body: RequestBodies.GetVendorStatusBody) {
        _checkVendorStatusResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.getVendorLiveStatus(body)
                _checkVendorStatusResponse.postValue(handleGetVendorLiveStatusResponse(response))
            } else {
                _checkVendorStatusResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection
                            ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _checkVendorStatusResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }
                else -> {
                    _checkVendorStatusResponse.postValue(
                        Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }

    private fun handleGetVendorLiveStatusResponse(response: Response<CheckVendorStatusResponse>): Event<Resource<CheckVendorStatusResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    private fun handleVendorStatusResponse(response: Response<VendorStatusResponse>): Event<Resource<VendorStatusResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    fun setVendorLiveLocation(body: RequestBodies.VendorLiveLocationBody) = viewModelScope.launch{
        updateVendorLiveLocation(body)
    }

    private suspend fun updateVendorLiveLocation(body: RequestBodies.VendorLiveLocationBody) {
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.setVendorLocationStatus(body)
                vendorLocationResponse.postValue(handleVendorLiveLocationResponse(response))
            } else {
                vendorLocationResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                    R.string.no_internet_connection
                ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    vendorLocationResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                        R.string.network_failure
                    ))))
                }
                else -> {
                    vendorLocationResponse.postValue(
                        Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }

    private fun handleVendorLiveLocationResponse(response: Response<VendorLiveLocationResponse>): Event<Resource<VendorLiveLocationResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    /**
    This method is used to observe both of the stored data #token and #vendorId
    and then trigger the liveData to call getLiveStatus method
     */
    fun updateStoredData(data: String) {
        val pair = _dataStoreValue.value
            if (TextUtils.isDigitsOnly(data)) {
                val secondPair = pair?.second as String
                val pairInObservable = Pair(data, secondPair)
                _dataStoreValue.value = pairInObservable
            } else {
                val firstPair = pair?.first as String
                val pairInObservable = Pair(firstPair, data)
                _dataStoreValue.value = pairInObservable
            }
    }
}