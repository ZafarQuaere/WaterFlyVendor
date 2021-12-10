package com.waterfly.vendor.repository

import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.network.RetrofitInstance

class AppRepository {

    suspend fun updateVendorDetail(body: RequestBodies.VendorDetailBody) =
        RetrofitInstance.picsumApi.updateVendorDetail(body)

    suspend fun requestOTP(body: RequestBodies.RequestOTPBody) =
        RetrofitInstance.loginApi.requestOTP(body)

    suspend fun validateUser(body: RequestBodies.ValidateOTPBody) =
        RetrofitInstance.loginApi.validateUser(body)
}