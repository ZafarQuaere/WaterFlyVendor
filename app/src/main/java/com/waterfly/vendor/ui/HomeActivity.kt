package com.waterfly.vendor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.DataStoreManager
import com.waterfly.vendor.util.LogUtils
import com.waterfly.vendor.util.Resource
import com.waterfly.vendor.util.errorSnack
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
    }

    private fun initUI() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        textOffline.setOnClickListener {
            vendorLiveStatus(false)
        }
        textOnline.setOnClickListener {
            vendorLiveStatus(true)
        }
    }

    private fun vendorLiveStatus(isOnline: Boolean) {
        val action = if (isOnline) "set_online" else "set_offline"

        val body = RequestBodies.VendorStatusBody(token,action,vendorId,"","")
        homeViewModel.setVendorLiveStatus(body)

        homeViewModel.vendorLiveStatusResponse.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let {
                           //update UI

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

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

}