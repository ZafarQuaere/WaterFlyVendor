package com.waterfly.vendor.bgtask

import android.location.Location

interface WfLocationListener {
    fun onLocationChangeCallApi(location: Location)
}