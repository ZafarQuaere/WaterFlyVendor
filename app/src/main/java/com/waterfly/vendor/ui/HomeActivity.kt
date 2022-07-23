package com.waterfly.vendor.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION.SDK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.bgtask.LocationService
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.ui.interfaces.DialogOkCancelListener
import com.waterfly.vendor.ui.interfaces.DialogSingleButtonListener
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

class HomeActivity : AppCompatActivity()/*, OnMapReadyCallback*/ {

    lateinit var homeViewModel: HomeViewModel
    lateinit var dataStoreManager: DataStoreManager
    private var token:String? = null
    private var vendorId:String? = null
    lateinit var mMap: GoogleMap
    private var REQUEST_LOCATION_PERMISSION = 123
    private var REQUEST_BG_LOCATION_PERMISSION = 124
    lateinit var mLocation: Location
    var mOldLocation: Location? = null
    private var vendorLiveStatus = false
    private val TAG = HomeActivity::class.simpleName

    // flag for GPS status
    var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initMap()
        initUI()
        dataStoreManager = DataStoreManager(this)
        observeData()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.md_blue_500)))
    }

    private fun startLocationService() {
//        val intent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))
    }

    public fun stopLocationService() {
        LocationService.isServiceStarted = false
        LogUtils.error("$TAG LocationService Stopped ### ")
        stopService(Intent(this, LocationService::class.java))
    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)*/
//        locationPermission()
        if (!checkPermission()) {
            requestPermission()
        } else {
            getUserLocation()
        }
        if(!checkBgLocationPermission()) {
            requestBgLocationPermission()
        }
    }

    private fun checkBgLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ) {
            val result = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            result == PackageManager.PERMISSION_GRANTED
        } else
            true
    }

    private fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestBgLocationPermission() {
        val backPermList = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        DialogUtil.showDialogSingleActionButton(
            this,
            getString(R.string.dlg_bg_permission_positive_btn),
            getString(R.string.dlg_bg_permission_msg)
        ) { requestPermissions(backPermList, 2) }
    }

    private fun getUserLocation() {
        val myLocation = VendorLocationListener()
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
        /*val locationThread = LocationThread()
        locationThread.start()*/
    }

    private fun observeData() {
        dataStoreManager.getToken.asLiveData().observe(this@HomeActivity, Observer {
            token = it
            token?.let { it1 -> homeViewModel.updateStoredData(it1) }
            LogUtils.DEBUG("getToken :time: ${System.currentTimeMillis()}  : $vendorId : $token")
        })
        dataStoreManager.getVendorId.asLiveData().observe(this@HomeActivity, Observer {
            vendorId = it
            vendorId?.let { it1 -> homeViewModel.updateStoredData(it1) }
            LogUtils.DEBUG("getVendorId :time: ${System.currentTimeMillis()}  : $vendorId : $token")
        })

        dataStoreManager.getVendorName.asLiveData().observe(this, Observer {
            LogUtils.DEBUG("Vendor name: $it")
            textVendorName.text = "Welcome $it"
        })
        homeViewModel.dataStoreValue.observe(this, Observer {
            println("dataStoreValue :time: ${System.currentTimeMillis()}  : $vendorId : $token")
            getVendorLiveStatus()
        })

    }

    private  fun getVendorLiveStatus() {
        val body = RequestBodies.GetVendorStatusBody(token,vendorId)
        println("getVendorLiveStatus :time: ${System.currentTimeMillis()}  \n $vendorId $token")
        homeViewModel.getVendorLiveStatus(body)
        homeViewModel.checkVendorLiveStatusResponse.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                            //Update UI
                            if(response.data.status == 1) {
                                vendorLiveStatus = response.data.message?.get(0) == "Online"
                                updateSwitch()
                            } else {
                                LogUtils.showToast(this, response.data.message?.get(0))
                            }
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

    private fun initUI() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        Glide.with(this).load(R.drawable.offline).into(imgBg)
        cardOffline.setOnClickListener {
            if (isGPSEnabled) {
                DialogUtil.showDialogDoubleButton(this,getString(R.string.cancel),getString(R.string.ok),getString(R.string.are_you_ready_for_service_msg),object: DialogOkCancelListener{
                    override fun onOkClick() {
                        updateVendorLiveStatus(true)
                    }
                    override fun onCancelClick() {
                        //Implementation will be done if required
                    }
                })
            } else {
                Utils.showGPSAlert(this@HomeActivity)
            }
        }

        cardOnline.setOnClickListener {
            DialogUtil.showDialogDoubleButton(this,
                getString(R.string.cancel),
                getString(R.string.ok),
                getString(R.string.you_will_be_offline_and_user_will_not_be_able_to_connect_you),
                object : DialogOkCancelListener {
                    override fun onOkClick() {
                        updateVendorLiveStatus(false)
                    }
                    override fun onCancelClick() {
                        //Implementation will be done if required
                    }
                })
        }

        imgBtnShare.setOnClickListener {
            Utils.shareApplication(this@HomeActivity)
        }
    }


    public fun updateVendorLiveStatus(isOnline: Boolean) {
        val action = if (isOnline) "set_online" else "set_offline"
        vendorLiveStatus = isOnline
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
                                updateSwitch()
                            } else if (response.data.status == 0 && response.data.message?.get(0)?.contains("already Online") != false){
                                updateSwitch()
                            }
                            else {
//                                LogUtils.showToast(this, response.data.message?.get(0))
                                LogUtils.DEBUG(response.data.message?.get(0).toString())
                             }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            progress.errorSnack(message, Snackbar.LENGTH_LONG)
                            LogUtils.error(message)
                        }
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
        })
    }

    private fun updateSwitch() {
        if (vendorLiveStatus) {
            startLocationService()
            Glide.with(this).load(R.drawable.online).into(imgBg)
//            imgBg.visibility = View.GONE
            cardOffline.visibility = View.GONE
            cardOnline.visibility = View.VISIBLE
        } else {
            stopLocationService()
            Glide.with(this).load(R.drawable.offline).into(imgBg)
//            imgBg.visibility = View.VISIBLE
            cardOffline.visibility = View.VISIBLE
            cardOnline.visibility = View.GONE
        }
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

     fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isIndoorEnabled = true
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18F),5000,null)
    }


    inner class VendorLocationListener : LocationListenerCompat {
        init {
            mLocation = Location("Start")
            mLocation.longitude = 0.0
            mLocation.longitude = 0.0
        }

        override fun onLocationChanged(location: Location) {
            mLocation = location
            LogUtils.error("$TAG Location Change:VendorLocationListener ### ${location.latitude}  ${location.longitude}")
            if (vendorLiveStatus)
                callLiveLocationApi(location)
        }

        override fun onProviderDisabled(provider: String) {
            //GPS is disable ask him to enable GPS
            isGPSEnabled = false
            if (vendorLiveStatus){
                Utils.showGPSAlert(this@HomeActivity)
            }
        }

        override fun onProviderEnabled(provider: String) {
            isGPSEnabled = true
            val myLocation = VendorLocationListener()
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_BW_UPDATES, Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocation)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
            LogUtils.DEBUG("$TAG  provider: $provider status: $status")
            super.onStatusChanged(provider, status, extras)
        }
    }

     fun callLiveLocationApi(location: Location?) {
//        val lat = location.latitude.toDouble()
//       val lat = String.format("%.6f", location.latitude).toDouble()
//       val long = String.format("%.6f", location.longitude).toDouble()
         if (vendorLiveStatus) {
             val body = RequestBodies.VendorLiveLocationBody(token, vendorId, location?.latitude.toString(), location?.longitude.toString())
             homeViewModel.setVendorLiveLocation(body)
             homeViewModel.vendorLiveLocationResponse.observe(this, Observer { event ->
                 event.getContentIfNotHandled()?.let { response ->
                     when (response) {
                         is Resource.Success -> {
                             hideProgressBar()
                             response.data?.let {
                                 //Print Output
                                 LogUtils.error("$TAG Location Change Response ###: ${it.status} ${it.message?.get(0)}")
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
    }


    inner class LocationThread : Thread() {
        init {
            mOldLocation = Location("Start")
            mOldLocation?.longitude=0.0
            mOldLocation?.longitude=0.0
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
//                    Thread.sleep(1000)
                } catch (ex: Exception) { }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    boolean_permission = true
                    getUserLocation()
                } else {
                    LogUtils.showToast(this,"Please Allow the permission")
                }
            }
            REQUEST_BG_LOCATION_PERMISSION -> {
                //TODO update this for user background permission.
            }
        }
    }

    fun navigateToBg(view: android.view.View) {
//        startActivity(Intent(this,BGDemo::class.java))
    }

    override fun onDetachedFromWindow() {
        stopLocationService()
        updateVendorLiveStatus(false)
        LogUtils.error("$TAG onDetachedFromWindow Called :: ")
        super.onDetachedFromWindow()
    }

    override fun onDestroy() {
        stopLocationService()
        updateVendorLiveStatus(false)
        LogUtils.error("$TAG onDestroy Called :: ")
        super.onDestroy()
    }
}


