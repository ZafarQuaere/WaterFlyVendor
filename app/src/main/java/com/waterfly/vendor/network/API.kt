package com.waterfly.vendor.network


import com.waterfly.vendor.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface API {

    @POST("Waterfly/api/vendors/update_vendor_details.php")
    suspend fun updateVendorDetail(@Body body: RequestBodies.VendorDetailBody): Response<VendorDetailResponse>

    @POST("Waterfly/api/vendors/login.php")
    suspend fun requestOTP(@Body body: RequestBodies.RequestOTPBody): Response<RequestOTPResponse>

    @POST("Waterfly/api/vendors/login.php")
    suspend fun validateUser(@Body body: RequestBodies.ValidateOTPBody): Response<ValidateOTPResponse>

    @POST("Waterfly/api/vendors/set_vendor_live_status.php")
    suspend fun vendorLiveStatus(@Body body: RequestBodies.VendorStatusBody): Response<VendorStatusResponse>

    @POST("Waterfly/api/vendors/update_vendor_location.php")
    suspend fun vendorLiveLocation(@Body body: RequestBodies.VendorLiveLocationBody): Response<VendorLiveLocationResponse>

    @POST("Waterfly/api/vendors/get_vendor_live_status.php")
    suspend fun getVendorLiveStatus(@Body body: RequestBodies.GetVendorStatusBody): Response<CheckVendorStatusResponse>

}