package com.waterfly.vendor.network

object RequestBodies {

    data class RequestOTPBody(val action:String = "request_otp", val phone:String)
    data class ValidateOTPBody(val action:String = "validate_otp", val phone:String, val otp: String)
    data class VendorDetailBody(val jwt_token:String?, val vendor_id:String?, val vendor_name: String,
                                val plant_name:String, val plant_phone:String, val plant_address: String)

    data class VendorStatusBody(val jwt_token:String? , val action:String, val vendor_id: String?,
                                val vendor_latitude:String?, val vendor_longitude:String?)
}