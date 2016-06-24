package com.example.gabriel.iapp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.example.gabriel.iapp.R;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class Tools {

    public static boolean isOnline(ConnectivityManager cm) {


        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())

            return true;
        else
            return false;
    }

    public static String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static int selectSpinnerItemByValue(ArrayList ar, String value) {

        int index=0;
        for (int i = 0; i < ar.size(); i++) {
            if (((SpinnerItems_Clase)ar.get(i)).getName().equals(value)) {
                index=i;
            }
        }

        return index;
    }







}