package com.waterfly.vendor.practice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.waterfly.vendor.R
import com.waterfly.vendor.util.LogUtils

class BGDemo : AppCompatActivity(), WfLocationListener,OnMapReadyCallback {

    lateinit var mMap: GoogleMap

    private var REQUEST_PERMISSIONS = 123
    private var BG_REQUEST_PERMISSIONS = 124
    var boolean_permission : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bgdemo)
        bgService()
        locationPermission()
        backGroundPermission()
        initMap()
    }

    private fun bgService() {

    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.bgMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun backGroundPermission() {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            return
        AlertDialog.Builder(this)
            .setTitle("R.string.background_location_permission_title")
            .setMessage("R.string.background_location_permission_message")
            .setPositiveButton("yes") { _,_ ->
                // this request will take user to Application's Setting page
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BG_REQUEST_PERMISSIONS)
            }
            .setNegativeButton("no") { dialog,_ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun locationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {

                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
                }
            } else {
                boolean_permission = true
            }
        }
        getUserLocation()
    }

    private fun Context.checkSinglePermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getUserLocation() {
        if (boolean_permission) {
//            val intent = Intent(applicationContext, BgLocationService::class.java)
//            startService(intent)
            ContextCompat.startForegroundService(this, Intent(this, BgLocationService::class.java))
        } else {
            LogUtils.showToast(this, "Please enable GPS")

        }
    }

    fun workerApiCall(){
        val demoRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(demoRequest)
    }

    fun callWorker(view: View) {
        workerApiCall()
    }

    override fun onLocationChangeCallApi(location: Location) {
        updateLocationOnMap(location)
    }

    private fun updateLocationOnMap(location: Location) {
        if (mMap != null) {
            mMap.clear()
            val myLocation = LatLng(location.latitude, location.longitude)
            mMap.addMarker(
                MarkerOptions()
                .position(myLocation)
                .title("Me")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14f))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap.setMinZoomPreference(12F)
        mMap.isIndoorEnabled = true
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        val mLocation = Location("start map")
        mLocation.latitude = 28.66051
        mLocation.longitude = 77.20356
        updateLocationOnMap(mLocation)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true
                } else {
                    LogUtils.showToast(this,"Please Allow the permission")

                }
            }
        }
    }
}