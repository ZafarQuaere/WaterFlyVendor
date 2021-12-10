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
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.VendorDetailsViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_vendor_details.*
import kotlinx.android.synthetic.main.activity_vendor_details.progress

class VendorDetailsActivity : AppCompatActivity() {
    lateinit var vendorViewModel: VendorDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_details)
        initViewModel()
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
            val body = RequestBodies.VendorDetailBody(
                vendor_name = vendorName, plant_name = plantName,
                plant_phone = plantPhone, plant_address = plantAdd)
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
                        //TODO move to home screen
                        progress.showSnack("U have updated data", Snackbar.LENGTH_SHORT)
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

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }
}