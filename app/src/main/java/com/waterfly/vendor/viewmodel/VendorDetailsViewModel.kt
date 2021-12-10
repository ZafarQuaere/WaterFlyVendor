package com.waterfly.vendor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.waterfly.vendor.R
import com.waterfly.vendor.app.MyApplication
import com.waterfly.vendor.model.VendorDetailResponse
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.Resource
import com.waterfly.vendor.util.Utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException


class VendorDetailsViewModel(app: Application, private val appRepository: AppRepository) : AndroidViewModel(app) {

    val vendorDetails: MutableLiveData<Resource<VendorDetailResponse>> = MutableLiveData()

    fun updateVendor(body: RequestBodies.VendorDetailBody) = viewModelScope.launch {
        updateVendorDetails(body)
    }


    private suspend fun updateVendorDetails(body: RequestBodies.VendorDetailBody) {
        vendorDetails.postValue(Resource.Loading())
        try {
            if (hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.updateVendorDetail(body)
                vendorDetails.postValue(handleVendorResponse(response))
            } else {
                vendorDetails.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> vendorDetails.postValue(Resource.Error(getApplication<MyApplication>().getString
                    (R.string.network_failure)))
                else -> vendorDetails.postValue(Resource.Error(getApplication<MyApplication>().getString(
                            R.string.conversion_error)))
            }
        }
    }

    private fun handleVendorResponse(response: Response<VendorDetailResponse>): Resource<VendorDetailResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
