package com.waterfly.vendor.practice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.IBinder
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleCoroutineScope
import com.waterfly.vendor.R
import com.waterfly.vendor.util.Constants
import com.waterfly.vendor.util.LogUtils
import kotlinx.coroutines.launch
import java.util.*


class BgLocationService: Service(), LocationListener{


    var isGPSEnable = false
    var isNetworkEnable = false
    var latitude = 0.0
    var longitude = 0.0
    lateinit var locationManager: LocationManager
    lateinit var location: Location

    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"

//    var str_receiver = "servicetutorial.service.receiver"
    lateinit var intent: Intent

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        LogUtils.error("onLocationChanged latitude ### ${location.latitude} ${location.longitude}");
//        mLocListener.onLocationChangeCallApi(location)
    }

    override fun onCreate() {
        super.onCreate()
        location = Location("Start")
        location.longitude = 0.0
        location.longitude = 0.0
        getlocation()
        serviceForNotification()

    }

    private fun serviceForNotification() {
//        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }

    }

    @SuppressLint("MissingPermission")
    private fun getlocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // getting GPS status
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnable && !isNetworkEnable){

        }else {
            if (isNetworkEnable) {
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.MIN_TIME_BW_UPDATES, Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this)
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!;
                LogUtils.error("isNetworkEnable latitude ### ${location.latitude}  ${location.longitude}");
                latitude = location.latitude
                longitude = location.longitude
                update(location)
            }

            if (isGPSEnable) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_BW_UPDATES, Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, this)
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                LogUtils.error("isGPSEnable latitude ### ${location.latitude}  ${location.longitude}");
                latitude = location.latitude
                longitude = location.longitude
                update(location)
            }
        }
    }



    private fun update(location: Location){
//        mLocListener.onLocationChangeCallApi(location)
        /*intent.putExtra("latitude","${location.latitude}")
        intent.putExtra("longitude","${location.longitude}")
        sendBroadcast(intent)*/
    }
}