package com.waterfly.vendor.practice

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.waterfly.vendor.R
import kotlinx.android.synthetic.main.on_off_toggle.*

class PokemonMapsActivity : FragmentActivity(), OnMapReadyCallback  {

    //WORK WITH USER LOCATION
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initUI()
        checkPermmison()
    }

    private fun initUI() {

        lytOffline.setOnClickListener {
            lytOffline.visibility = View.GONE
            lytOnline.visibility = View.VISIBLE
        }

        lytOnline.setOnClickListener {
            lytOffline.visibility = View.VISIBLE
            lytOnline.visibility = View.GONE
        }

    }

    var ACCESSLOCATION=123
    fun checkPermmison(){

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.
                    checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }

        getUserLocation()
    }

    fun getUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        val myLocation = MylocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 8f, myLocation)

        val mythread = myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            ACCESSLOCATION->{

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    var location: Location? = null

    //Get user location

    inner class MylocationListener:LocationListener{
        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }

        override fun onLocationChanged(p0: Location) {
            location=p0
            Log.e("Location Change: >>> ","${p0?.latitude}  ${p0?.longitude}")
        }


    }


    var oldLocation:Location?=null
    inner class myThread:Thread{

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.longitude=0.0
        }

        override fun run(){

            while (true){

                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation=location


                    runOnUiThread {


                        mMap!!.clear()

                        // show me
                    val sydney = LatLng(location!!.latitude, location!!.longitude)
                    mMap!!.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet(" here is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_48)))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                    }

                    Thread.sleep(1000)

                }catch (ex:Exception){}


            }

        }

    }


}
