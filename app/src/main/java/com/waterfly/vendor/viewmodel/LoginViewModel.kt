package com.waterfly.vendor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.waterfly.vendor.R
import com.waterfly.vendor.app.MyApplication
import com.waterfly.vendor.model.RequestOTPResponse
import com.waterfly.vendor.model.ValidateOTPResponse
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.Event
import com.waterfly.vendor.util.Resource
import com.waterfly.vendor.util.Utils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class LoginViewModel(app: Application, private val appRepository: AppRepository) :
    AndroidViewModel(app) {

    private val _otpResponse = MutableLiveData<Event<Resource<RequestOTPResponse>>>()
    val requestOTPResponse: LiveData<Event<Resource<RequestOTPResponse>>> = _otpResponse

    private val _validateOtpResponse = MutableLiveData<Event<Resource<ValidateOTPResponse>>>()
    val validateOTPResponse: LiveData<Event<Resource<ValidateOTPResponse>>> = _validateOtpResponse


    fun requestOTP(body: RequestBodies.RequestOTPBody) = viewModelScope.launch {
        sendOTP(body)
    }

    fun validateOTP(body: RequestBodies.ValidateOTPBody) = viewModelScope.launch {
        validateUser(body)
    }

    private suspend fun validateUser(body: RequestBodies.ValidateOTPBody) {
        _validateOtpResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.validateUser(body)
                _validateOtpResponse.postValue(handleUserResponse(response))
            } else {
                _validateOtpResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _validateOtpResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString
                        (R.string.network_failure))))
                }
                else -> {
                    _validateOtpResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.conversion_error))))
                }
            }
        }
    }

    private fun handleUserResponse(response: Response<ValidateOTPResponse>): Event<Resource<ValidateOTPResponse>>? {
        if (response.isSuccessful){
            response.body()?.let { resultResponse -> return Event(Resource.Success(resultResponse)) }
        }
        return Event(Resource.Error(response.message()))
    }


    private suspend fun sendOTP(body: RequestBodies.RequestOTPBody) {
        _otpResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.requestOTP(body)
                _otpResponse.postValue(handleOTPResponse(response))
            } else {
                _otpResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                R.string.no_internet_connection
                            ))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _otpResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(
                                    R.string.network_failure
                                ))))
                }
                else -> {
                    _otpResponse.postValue(
                        Event(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error))))
                }
            }
        }
    }

    private fun handleOTPResponse(response: Response<RequestOTPResponse>): Event<Resource<RequestOTPResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }
}