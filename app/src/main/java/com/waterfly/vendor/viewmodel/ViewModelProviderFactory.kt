package com.waterfly.vendor.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waterfly.vendor.repository.AppRepository
import java.lang.IllegalArgumentException

class ViewModelProviderFactory(val app: Application, val appRepository: AppRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app, appRepository) as T
        }
        if (modelClass.isAssignableFrom(VendorDetailsViewModel::class.java)) {
            return VendorDetailsViewModel(app, appRepository) as T
        }
        throw IllegalArgumentException("Unknown Class Name")
    }
}