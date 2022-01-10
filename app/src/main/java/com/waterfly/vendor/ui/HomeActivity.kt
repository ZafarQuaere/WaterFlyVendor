package com.waterfly.vendor.ui

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.HomeViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.on_off_toggle.*

//TODO 1 Ask location permission
//TODO 2 Check GPS Enable
//TODO 3 Share live location with api even in background
//TODO 4 show enable disable option
//TODO 5 update UI on the basis of offline/online feature

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var homeViewModel: HomeViewModel
    lateinit var dataStoreManager: DataStoreManager
    private var token:String? = null
    private var vendorId:String? = null
    lateinit var mMap: GoogleMap
    private var ACCESSLOCATION = 123
    lateinit var mLocation: Location
    var mOldLocation: Location? = null
    private var vendorLiveStatus = false

    // flag for GPS status
    var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initMap()
        dataStoreManager = DataStoreManager(this)
        observeData()
        initUI()
    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationPermission()
    }

    private fun locationPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    private fun getUserLocation() {
//        Toast.makeText(this,"User location access on", Toast.LENGTH_LONG).show()
        val myLocation= VendorLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGPSEnabled){
            Utils.showSettingsAlert(this@HomeActivity)
            //requesting location update through Network provider
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.MIN_TIME_BW_UPDATES, Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocation)
        } else {
            //requesting location update through GPS provider
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_BW_UPDATES, Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocation)
        }
        val locationThread = LocationThread()
        locationThread.start()
    }

    private fun observeData() {
        dataStoreManager.getToken.asLiveData().observe(this, Observer {
            token = it
            token?.let { token -> LogUtils.DEBUG(token) }
        })
        dataStoreManager.getVendorId.asLiveData().observe(this, Observer {
            vendorId = it
            vendorId?.let { vendorId ->LogUtils.DEBUG(vendorId) }
        })
        dataStoreManager.getVendorData().asLiveData().observe(this, Observer {
            LogUtils.DEBUG("Vendor name: ${it.vendor_name}")
            textVendorName.text = "Welcome ${it.vendor_name}"
        })
    }

    private fun initUI() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        lytOffline.setOnClickListener {
            lytOffline.visibility = View.GONE
            lytOnline.visibility = View.VISIBLE
            vendorLiveStatus(true)
        }

        lytOnline.setOnClickListener {
            lytOffline.visibility = View.VISIBLE
            lytOnline.visibility = View.GONE
            vendorLiveStatus(false)
        }

    }

    private fun vendorLiveStatus(isOnline: Boolean) {
        val action = if (isOnline) "set_online" else "set_offline"

        val body = RequestBodies.VendorStatusBody(token,action,vendorId,mLocation.latitude.toString(),mLocation.longitude.toString())
        homeViewModel.setVendorLiveStatus(body)

        homeViewModel.vendorLiveStatusResponse.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            //Update UI
                            if(response.data.status == 1) {
                                updateSwitch(vendorLiveStatus)
                            }
                            vendorLiveStatus = isOnline
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        vendorLiveStatus = isOnline
                        response.message?.let { message ->
                            progress.errorSnack(message, Snackbar.LENGTH_LONG)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        })
    }

    private fun updateSwitch(online: Boolean) {
        if (online) {
            imgBg.setBackgroundResource(R.drawable.online_home_bg2)
        } else {
            imgBg.setBackgroundResource(R.drawable.offline2)
        }
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(12F)
        mMap.isIndoorEnabled = true
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
    }

    inner class VendorLocationListener : LocationListener {
        init {
            mLocation = Location("Start")
            mLocation.longitude = 0.0
            mLocation.longitude = 0.0
        }
        override fun onLocationChanged(location: Location) {
            mLocation = location
            Log.e("Location Change: >>> ","${location.latitude}  ${location.longitude}")
            if (vendorLiveStatus)
                callLiveLocationApi(location)
        }
    }

    private fun callLiveLocationApi(location: Location) {
        val body = RequestBodies.VendorLiveLocationBody(token,vendorId,location.latitude.toString(),location.longitude.toString())
        homeViewModel.setVendorLiveLocation(body)

        homeViewModel.vendorLiveLocationResponse.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            //Print Output
                            LogUtils.error("Location Change Response: ${it.status } ${it.message?.get(0)}")
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            progress.errorSnack(message, Snackbar.LENGTH_LONG)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        })
    }


    inner class LocationThread : Thread() {
        init {
            mOldLocation= Location("Start")
            mOldLocation!!.longitude=0.0
            mOldLocation!!.longitude=0.0
        }

        override fun run(){
            while (true){
                try {
                    if(mOldLocation!!.distanceTo(mLocation)==0f){
                        continue
                    }
                    mOldLocation=mLocation
                    runOnUiThread {
                        mMap.clear()
                        // show me
                        val myLocation = LatLng(mLocation.latitude, mLocation.longitude)
                        mMap.addMarker(MarkerOptions()
                            .position(myLocation)
                            .title("Me")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14f))
                    }
                    Thread.sleep(1000)
                }catch (ex:Exception){}

            }
        }
    }
}


