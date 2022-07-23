package com.waterfly.vendor.app

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // should not init app in this process.
            return
        }
        LeakCanary.install(this)*/
    }

}