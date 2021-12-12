package com.waterfly.vendor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.waterfly.vendor.R
import com.waterfly.vendor.app.MyApplication
import com.waterfly.vendor.model.RequestOTPResponse
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
    AndroidViewModel(app) {

    private val _vendorStatusResponse = MutableLiveData<Event<Resource<VendorStatusResponse>>>()
    val vendorLiveStatusResponse: LiveData<Event<Resource<VendorStatusResponse>>> = _vendorStatusResponse


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

    private fun handleVendorStatusResponse(response: Response<VendorStatusResponse>): Event<Resource<VendorStatusResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }


}