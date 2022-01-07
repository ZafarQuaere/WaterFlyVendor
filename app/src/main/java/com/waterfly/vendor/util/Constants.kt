package com.waterfly.vendor.util

object Constants{
        const val BASE_URL = "http://econnecto.com/"
        const val TAG = "WaterFlyVendor"
        const val TAG_ONLINE = "ONLINE"
        const val TAG_OFFLINE = "OFFLINE"

        // The minimum time between updates in milliseconds
         const val MIN_TIME_BW_UPDATES = (1000 * 10 * 1 ).toLong()// 10 second

        // The minimum distance to change Updates in meters
         const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters

}