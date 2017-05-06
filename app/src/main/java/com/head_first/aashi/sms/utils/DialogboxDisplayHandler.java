package com.head_first.aashi.sms.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;

import com.head_first.aashi.sms.R;

/**
 * Created by Aashish Indorewala on 06-May-17.
 */

public class DialogBoxDisplayHandler {
    private static ProgressDialog progressDialog;
    private static String message;
    private static Activity activity;

    //---------------------------------------------------
    //Progress Dialog Box methods
    public static void showIndefiniteProgressDialog(final Activity activity){
        if(activity != null){
            DialogBoxDisplayHandler.activity = activity;
            DialogBoxDisplayHandler.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(DialogBoxDisplayHandler.activity);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setContentView(R.layout.dialog_indeterminate_progress_bar);
                }
            });
        }
    }

    public static void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
