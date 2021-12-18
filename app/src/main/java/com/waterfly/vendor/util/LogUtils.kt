package com.waterfly.vendor.util

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.waterfly.vendor.BuildConfig
import com.waterfly.vendor.util.Constants.TAG

object LogUtils {

    fun error(msg: String){
        Log.e("$TAG  >> ",msg)
    }

    fun showToast(context: Context?, message: String?) {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    fun DEBUG(sb: String) {
        //To print the log on debug mode only
        if (BuildConfig.DEBUG) {
            if (sb.length > 4000) {
                val chunkCount = sb.length / 4000
                for (i in 0..chunkCount) {
                    val max = 4000 * (i + 1)
                    if (max >= sb.length) {
                        Log.d("$TAG  >> ",  sb.substring(4000 * i))
                    } else {
                        Log.d("$TAG  >> ", sb.substring(4000 * i, max))
                    }
                }
            } else {
                Log.d("$TAG  >> ",sb)
            }
        }
    }
}