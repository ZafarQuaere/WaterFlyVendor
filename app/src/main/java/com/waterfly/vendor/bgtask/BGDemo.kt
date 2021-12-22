package com.waterfly.vendor.bgtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.waterfly.vendor.R

class BGDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bgdemo)

    }

    fun workerApiCall(){
        val demoRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(demoRequest)
    }

    fun callWorker(view: View) {
        workerApiCall()
    }
}