package com.waterfly.vendor.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waterfly.vendor.R
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.waterfly.vendor.ui.EnterMobileActivity
import com.waterfly.vendor.util.DataStoreManager

class SplashActivity : AppCompatActivity() {

    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)
        dataStoreManager.isUserLogin.asLiveData().observe(this, Observer {
            if (it){
                val intent = Intent(this, EnterMobileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                setContentView(R.layout.activity_splash)
               initUI();
            }
        })

    }

    private fun initUI() {
        findViewById<View>(R.id.txtSkip).setOnClickListener { v: View? ->
            startActivity(Intent(this@SplashActivity, EnterMobileActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.txtDownloadApp).setOnClickListener { v: View? ->
            /*Utils.openWaterFlyApp(SplashActivity.this);*/startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.waterfly.users&hl=en")
            )
        )
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }
}