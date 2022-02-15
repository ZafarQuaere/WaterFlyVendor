package com.waterfly.vendor.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import com.waterfly.vendor.app.MyApplication
import com.waterfly.vendor.ui.HomeActivity
import androidx.core.content.ContextCompat.startActivity
import com.waterfly.vendor.BuildConfig


object Utils {
    fun hasInternetConnection(application: MyApplication): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun showSettingsAlert(mContext: Activity) {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle("Location Setting")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Please enable GPS to use this App!")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
            showGPSAlert(mContext)
        }
        alertDialog.show()
    }

    fun showGPSAlert(mContext: Activity) {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle("Location Setting")

        // Setting Dialog Message
        alertDialog.setMessage("For better experience of this application you need to enable the GPS!")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        // on pressing cancel button
        alertDialog.setNegativeButton("Close App") { dialog, which ->
            (mContext as HomeActivity).stopLocationService()
            (mContext as HomeActivity).vendorLiveStatus(false)
            dialog.cancel()
            mContext.finish()
        }
        alertDialog.setCancelable(false);
        alertDialog.show()
    }

    fun shareApplication(activity: HomeActivity) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        activity.startActivity(sendIntent)
    }

}