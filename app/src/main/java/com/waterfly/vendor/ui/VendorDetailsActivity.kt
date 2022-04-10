package com.waterfly.vendor.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.model.VendorDetailResponse
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.VendorDetailsViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_vendor_details.*
import kotlinx.android.synthetic.main.activity_vendor_details.progress
import kotlinx.coroutines.launch
import com.google.android.exoplayer2.upstream.HttpDataSource


class VendorDetailsActivity : AppCompatActivity() {
    lateinit var vendorViewModel: VendorDetailsViewModel
    lateinit var dataStoreManager: DataStoreManager
    private var token:String? = null
    private var vendorId:String? = null
    private var vendorName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_details)
        dataStoreManager = DataStoreManager(this)
        observeData()
        initViewModel()
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
    private fun initViewModel() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        vendorViewModel = ViewModelProvider(this, factory)[VendorDetailsViewModel::class.java]
    }

    fun addDetails(view: View) {
        vendorName = editVendorName.text.toString()
        val plantName = editPlantName.text.toString()
        val pincode = editVPincode.text.toString()
        val vendorAddress = editVendorAddress.text.toString()
        hideKeyboard(view)
        validateInput(vendorName, plantName, pincode, vendorAddress)
    }

    private fun validateInput(
        vendorName: String?, plantName: String,
        pincode: String, address: String
    ) {

        if (vendorName?.isEmpty() == true || vendorName?.length!! < 3) {
            progress.errorSnack(getString(R.string.enter_valid_vendor_name), Snackbar.LENGTH_LONG)
        } else if (address.isEmpty() || address.length < 3) {
            progress.errorSnack(getString(R.string.enter_valid_address), Snackbar.LENGTH_LONG)
        } else if (pincode.isEmpty() || pincode.length < 6) {
            progress.errorSnack(getString(R.string.enter_valid_area_pincode), Snackbar.LENGTH_LONG)
        } else if (plantName.isEmpty() || plantName.length < 3) {
            progress.errorSnack(getString(R.string.enter_valid_plant_name), Snackbar.LENGTH_LONG)
        } else {
            val body = RequestBodies.VendorDetailBody(token,vendorId,
                vendorName, "$address $pincode", plantName)

            vendorViewModel.updateVendor(body)
            observeVendor()
        }
    }

    private fun observeVendor() {
        vendorViewModel.vendorDetails.observe(this, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        navigateToHome(response.data)
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
        })
    }

    private fun navigateToHome(data: VendorDetailResponse) {
       if (data.status == 1){
           lifecycleScope.launch {
               dataStoreManager.setUserLogin(true)
               vendorName?.let { dataStoreManager.storeVendorName(it) }
           }
           startActivity(Intent(this,HomeActivity::class.java))
           finish()
       } else {
           data.message?.get(0)?.let { progress.errorSnack(it.toString(), Snackbar.LENGTH_LONG) }
       }
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

}