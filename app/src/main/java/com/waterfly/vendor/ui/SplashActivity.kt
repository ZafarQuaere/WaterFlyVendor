package com.waterfly.vendor.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.waterfly.vendor.R;
import com.waterfly.vendor.util.Utils;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.txtSkip).setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this,EnterMobileActivity.class));
            finish();
        });
        findViewById(R.id.txtDownloadApp).setOnClickListener(v-> {
            /*Utils.openWaterFlyApp(SplashActivity.this);*/
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.waterfly.users&hl=en")));
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}