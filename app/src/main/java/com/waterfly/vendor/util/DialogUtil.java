package com.waterfly.vendor.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.waterfly.vendor.BuildConfig;
import com.waterfly.vendor.R;
import com.waterfly.vendor.ui.interfaces.DialogOkCancelListener;
import com.waterfly.vendor.ui.interfaces.DialogSingleButtonListener;

public class DialogUtil {

    public static final String TAG = "WaterFly";

    public static void showToast(Context context, String message) {
        if (context != null) {
            final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void DEBUG(String sb) {
        //To print the log on debug mode only
        if (BuildConfig.DEBUG) {
            if (sb.length() > 4000) {
                int chunkCount = sb.length() / 4000;
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.d(TAG, " >> " + sb.substring(4000 * i));
                    } else {
                        Log.d(TAG, " >> " + sb.substring(4000 * i, max));
                    }
                }
            } else {
                Log.d(TAG, " >> " + sb.toString());
            }
        }
    }

    /**
     * @param message
     */
    public static void ERROR(String message) {
        Log.e(TAG, " >> " + message);
    }

    public static void showSnackBar(Context context, ViewGroup layout, String msg) {
        Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }

    public static void showErrorDialog(Context ctx, String btnText, String message) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_single_button);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
        TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
        textMessage.setText(message);
        dialogButton.setText(btnText);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDialogDoubleButton(Context ctx, String btnCancelTxt, String btnOkTxt, String message, final DialogOkCancelListener listener) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_double_button);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
        textMessage.setText(message);
        btnCancel.setText(btnCancelTxt);
        btnOk.setText(btnOkTxt);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onOkClick();
                }
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static void showDialogSingleActionButton(Context ctx, String btnOkTxt, String message, final DialogSingleButtonListener listener) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_single_button);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
        textMessage.setText(message);

        btnOk.setText(btnOkTxt);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.okClick();
            }
        });
        dialog.show();
    }
}
