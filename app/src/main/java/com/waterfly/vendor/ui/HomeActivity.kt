package com.waterfly.vendor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.toggle.LabeledSwitch
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.HomeViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_home.*
//TODO 1 Ask location permission
//TODO 2 Check GPS Enable
//TODO 3 Share live location with api even in background
//TODO 4 show enable disable option
//TODO 5 update UI on the basis of offline/online feature

class HomeActivity : AppCompatActivity() {

    lateinit var homeViewModel: HomeViewModel
    lateinit var dataStoreManager: DataStoreManager
    private var token:String? = null
    private var vendorId:String? = null
    lateinit var labeledSwitch: LabeledSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        dataStoreManager = DataStoreManager(this)
        observeData()
        initUI()
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

        labeledSwitch = findViewById(R.id.toggle1)
        labeledSwitch.setOnToggledListener { toggleableView, isOn ->
            vendorLiveStatus(isOn)
        }

       /* textOffline.setOnClickListener {
            vendorLiveStatus(false)
        }
        textOnline.setOnClickListener {
            vendorLiveStatus(true)
        }*/
    }

    private fun vendorLiveStatus(isOnline: Boolean) {
        val action = if (isOnline) "set_online" else "set_offline"

        val body = RequestBodies.VendorStatusBody(token,action,vendorId,"37.5561","-122.069021666666")
        homeViewModel.setVendorLiveStatus(body)

        homeViewModel.vendorLiveStatusResponse.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                           //update UI
                            updateSwitch(isOnline)
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

    private fun updateSwitch(online: Boolean) {
        if (labeledSwitch.isOn) {
            imgBg.setBackgroundResource(R.drawable.online_home_bg2)
//            labeledSwitch.labelOn = Constants.TAG_ONLINE
//            labeledSwitch.setOn(true)
        } else {
            imgBg.setBackgroundResource(R.drawable.offline2)
//            labeledSwitch.setOn(false)
//            labeledSwitch.labelOff = Constants.TAG_OFFLINE
        }
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

}