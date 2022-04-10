package com.waterfly.vendor.bgtask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.waterfly.vendor.R
import com.waterfly.vendor.ui.HomeActivity
import com.waterfly.vendor.util.LogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"

    override fun onCreate() {
        super.onCreate()
        LogUtils.error("$TAG LocationService started ### ")
        isServiceStarted = true
        val notifIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,notifIntent,0)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setContentTitle(applicationContext.getText(R.string.app_name))
                .setContentText(applicationContext.getText(R.string.bg_notif_message))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent)

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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
       /* HomeActivity().startListeningUserLocation(this,object: BgLocationListener{
            override fun onLocationChanged(location: Location) {
                TODO("Not yet implemented")
            }
        })*/
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (isServiceStarted) {
                        println("$TAG onLocationChanged ### ${location?.latitude} ${location?.longitude}");
                        HomeActivity().callLiveLocationApi(location)
                    }
                }
            })
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.error("$TAG LocationService onDestroy ### ")
        isServiceStarted = false
    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}