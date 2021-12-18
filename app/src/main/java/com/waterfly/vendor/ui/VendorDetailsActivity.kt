package com.waterfly.vendor.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
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

class VendorDetailsActivity : AppCompatActivity() {
    lateinit var vendorViewModel: VendorDetailsViewModel
    lateinit var dataStoreManager: DataStoreManager
    private var token:String? = null
    private var vendorId:String? = null

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
        val vendorName = editVendorName.text.toString()
        val plantName = editPlantName.text.toString()
        val plantPhone = editPlantPhone.text.toString()
        val plantAdd = editPlantAddress.text.toString()
        hideKeyboard(view)
        validateInput(vendorName, plantName, plantPhone, plantAdd)
    }

    private fun validateInput(
        vendorName: String, plantName: String,
        plantPhone: String, plantAdd: String
    ) {

        if (vendorName.isEmpty() || vendorName.length < 3) {
            progress.errorSnack(getString(R.string.enter_valid_vendor_name), Snackbar.LENGTH_LONG)
        } else if (plantName.isEmpty() || plantName.length < 3) {
            progress.errorSnack(getString(R.string.enter_valid_plant_name), Snackbar.LENGTH_LONG)
        } else if (plantPhone.isEmpty() || plantPhone.length < 10) {
            progress.errorSnack(getString(R.string.enter_valid_mobile_no), Snackbar.LENGTH_LONG)
        } else if (plantAdd.isEmpty() || plantAdd.length < 3) {
            progress.errorSnack(getString(R.string.enter_valid_address), Snackbar.LENGTH_LONG)
        } else {
            val body = RequestBodies.VendorDetailBody(token,vendorId,
                vendorName, plantName, plantPhone, plantAdd)

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
           startActivity(Intent(this,HomeActivity::class.java))
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