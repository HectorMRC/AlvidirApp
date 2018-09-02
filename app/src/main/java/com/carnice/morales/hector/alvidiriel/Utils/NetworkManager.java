package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    /*pre: l'atribut context és l'adient */
    /*post: s'ha retornat un string on s'indica el tipus de connexió del dispositiu.
            Contemplant offline, wifi, mobile i errors.*/
    public String getStatus(Context context) {
        return isOnline(context)? (isConnectedWifi(context)? "wifi" :
                (isConnectedMobile(context)? "mobile" : "error" ) ) : "offline";
    }

    /*pre: idem a l'anterior*/
    /*post: s'ha retornat true si i només si el dispositiu té connexió a internet.*/
    private static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    /*pre: idem a l'anterior*/
    /*post: s'han retornat true si i només si el dispositiu té connexió de tipus wifi.*/
    private static boolean isConnectedWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /*pre: idem a l'anterior*/
    /*post: s'han retornat true si i només si el dispositiu té connexió de tipus movil.*/
    private static boolean isConnectedMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
