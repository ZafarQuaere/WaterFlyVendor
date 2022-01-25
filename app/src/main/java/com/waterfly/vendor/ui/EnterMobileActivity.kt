package com.waterfly.vendor.ui

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.waterfly.vendor.R
import com.waterfly.vendor.bgtask.BGDemo
import com.waterfly.vendor.bgtask.PokemonMapsActivity
import com.waterfly.vendor.model.ValidateOTPResponse
import com.waterfly.vendor.model.ValidateUserData
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.LoginViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_enter_mobile.*
import kotlinx.coroutines.launch


class EnterMobileActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel
    lateinit var mobileNo : String
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this@EnterMobileActivity)
//        moveToActivity()
        dataStoreManager.isUserLogin.asLiveData().observe(this, Observer {
            if (it){
                val intent = Intent(this@EnterMobileActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                setContentView(R.layout.activity_enter_mobile)
                init()
            }
        })
    }

    private fun moveToActivity() {
        startActivity(Intent(this@EnterMobileActivity, BGDemo::class.java))
        finish()
    }

    private fun init() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

    }

    fun onSendOTPClick(view: View) {
        mobileNo = edit_mobile_no.text.toString()
        if (mobileNo.isNotEmpty() && mobileNo.length == 10) {
            val body = RequestBodies.RequestOTPBody(phone = mobileNo)
            loginViewModel.requestOTP(body)
            loginViewModel.requestOTPResponse.observe(this, Observer { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let {
                                if (response.data.status == 1) {
                                    updateUIForValidateOTP()
                                } else {
                                    response.message?.get(0)
                                        ?.let { message ->
                                            progress.showSnack(message.toString(), Snackbar.LENGTH_LONG)
                                        }
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
        } else {
            hideKeyboard(view)
            progress.errorSnack(getString(R.string.enter_valid_mobile_no), Snackbar.LENGTH_LONG)
        }
    }

    private fun updateUIForValidateOTP() {
        tilOTP.visibility = View.VISIBLE
        btnSendOTP.visibility = View.GONE
        btnValidateOTP.visibility = View.VISIBLE
        edit_mobile_no.isEnabled = false
    }

    fun onValidateClick(view: View) {
        val otp = editEnterOTP.text.toString()
        if (otp.isNotEmpty() && otp.length == 6){
            val body = RequestBodies.ValidateOTPBody(phone = mobileNo, otp = otp)
            loginViewModel.validateOTP(body)
            loginViewModel.validateOTPResponse.observe(this, Observer { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let {
                                navigateToHome(it)
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
        } else {
            hideKeyboard(view)
            progress.errorSnack(getString(R.string.enter_correct_otp), Snackbar.LENGTH_LONG)
        }
    }

    private fun navigateToHome(data: ValidateOTPResponse) {
        if (data.data?.isNotEmpty() == true && data.status == 1) {
            val vendorData: ValidateUserData? = data.data[0]
            if (vendorData?.details_completed == "1") {
                lifecycleScope.launch {
                    storeDataAndNavigateToHome(vendorData)
                }
            } else {
                lifecycleScope.launch{
                    if (vendorData != null) {
                        navigateToDetailsScreen(vendorData)
                    }
                }
            }
        } else {
            data.message?.get(0)?.let { progress.errorSnack(it) }
        }
    }

    private suspend fun navigateToDetailsScreen(vendorData: ValidateUserData) {
        dataStoreManager.storeToken(vendorData.JWT_Token)
        dataStoreManager.storeVendorId(vendorData.id)
        startActivity(Intent(this@EnterMobileActivity,VendorDetailsActivity::class.java))
    }

    private suspend fun storeDataAndNavigateToHome(vendorData: ValidateUserData) {
        dataStoreManager.storeToken(vendorData.JWT_Token)
        dataStoreManager.storeVendorId(vendorData.id)
        dataStoreManager.storeVendorData(vendorData)
        dataStoreManager.setUserLogin(true)
        startActivity(Intent(this@EnterMobileActivity,HomeActivity::class.java))
        finish()
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

}