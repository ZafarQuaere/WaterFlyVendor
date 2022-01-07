package com.waterfly.vendor.model

data class RequestOTPResponse(val status: Int, val message: List<String>?, val data: List<Any?>?)

data class ValidateOTPResponse(val status: Int, val message: List<String>?, val data: List<ValidateUserData?>?)
data class ValidateUserData(val id: String,val phone:String,val vendor_name: String,val plant_name: String,
                            val plant_phone: String,val plant_address:String,val details_completed: String,
                            val JWT_Token: String)

data class VendorDetailResponse(val status: Int, val message: List<String>?, val data: List<Any?>?)

data class VendorStatusResponse(val status: Int, val message: List<String>?, val data: List<Any?>?)

data class VendorLiveLocationResponse(val status: Int, val message: List<String>?, val data: List<Any?>?)