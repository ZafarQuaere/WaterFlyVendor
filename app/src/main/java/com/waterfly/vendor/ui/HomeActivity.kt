package com.waterfly.vendor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initUI()
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
        val body = RequestBodies.VendorStatusBody(action = action)
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