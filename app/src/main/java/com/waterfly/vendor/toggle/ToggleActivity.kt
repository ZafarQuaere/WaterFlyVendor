package com.waterfly.vendor.toggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waterfly.vendor.R
import com.waterfly.vendor.util.Constants

class ToggleActivity : AppCompatActivity() {
    lateinit var labeledSwitch: LabeledSwitch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toggle)
        labeledSwitch = findViewById(R.id.toggle1)

        labeledSwitch.setOnToggledListener { toggleableView ,isOn ->
            if (isOn){
                labeledSwitch.labelOn = Constants.TAG_ONLINE
                labeledSwitch.setOn(true)
            } else {
                labeledSwitch.labelOff = Constants.TAG_OFFLINE
                labeledSwitch.setOn(false)
            }
        }
    }
}