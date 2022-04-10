package com.waterfly.vendor.practice

import android.location.Location

interface WfLocationListener {
    fun onLocationChangeCallApi(location: Location)
}