/*
package com.waterfly.vendor.bgtask

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.waterfly.vendor.util.LogUtils

class GpsTracker(val mContext: MapsActivity) : Service(), LocationListener {

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false

    var location: Location? = null
    var latitude = 0.0 as Double
    var longitude = 0.0 as Double

    // The minimum distance to change Updates in meters
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters


    // The minimum time between updates in milliseconds
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
            ).toLong()

    // Declaring a Location Manager
    lateinit var locationManager: LocationManager

    init {
        getVendorLocation()
    }

     fun getVendorLocation(): Location? {
        locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

        // getting GPS Status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        //getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
            // No location provider is enabled
        } else {
            canGetLocation = true

            if (isNetworkEnabled) {
//check the network permission

                //check the network permission
                //check the network permission

                //check the network permission
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
                    requestPermissions((mContext as Activity)!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION), 101)
                }

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,this)
                LogUtils.DEBUG("Network Check ")

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    latitude = location?.latitude!!
                    longitude = location?.longitude!!
                }
            }

            // if GPS Enabled get lat/long using GPS Services

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            (mContext as Activity),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            101
                        )
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toDouble(), this)
                    Log.d("GPS Enabled", "GPS Enabled")
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            latitude = location?.latitude!!
                            longitude = location?.longitude!!
                        }
                    }
                }
            }
        }

        return location
    }


    */
/**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     *//*

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this@GpsTracker)
        }
    }

    */
/**
     * Function to get latitude
     *//*

    @JvmName("getLatitude1")
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location?.latitude?.toDouble()!!
        }

        // return latitude
        return latitude
    }

    */
/**
     * Function to get longitude
     *//*

    @JvmName("getLongitude1")
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location?.longitude?.toDouble()!!
        }

        // return longitude
        return longitude
    }

    */
/**
     * Function to check GPS/wifi enabled
     * @return boolean
     *//*

    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    */
/**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     *//*

    fun showSettingsAlert() {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
            DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        alertDialog.show()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location) {
        mContext.updateLocation(p0)
    }
}*/
