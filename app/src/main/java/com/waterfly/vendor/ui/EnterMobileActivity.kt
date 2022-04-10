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
import com.waterfly.vendor.practice.BGDemo
import com.waterfly.vendor.network.RequestBodies
import com.waterfly.vendor.repository.AppRepository
import com.waterfly.vendor.util.*
import com.waterfly.vendor.viewmodel.LoginViewModel
import com.waterfly.vendor.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_enter_mobile.*


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
                                    val intent = Intent(this@EnterMobileActivity, EnterOTPActivity::class.java)
                                    intent.putExtra("mobileno",mobileNo)
                                    startActivity(intent)
                                    finish()
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

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

    fun navigateToOTP(view: View) {
        mobileNo = edit_mobile_no.text.toString()
        val intent = Intent(this@EnterMobileActivity, EnterOTPActivity::class.java)
        intent.putExtra("mobileno",mobileNo)
        startActivity(intent)
        finish()
    }

}