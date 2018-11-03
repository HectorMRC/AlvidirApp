package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Toaster extends Toast{

    private static Toast currentToast;
    private Context context;
    private LinearLayout custom;

    public Toaster(Context context) {
        super(context);
        this.context = context;
    }

    /*pre: cert*/
    /*post: s'ha llençat un toast estanderd*/
    public void standardToast(String message){
        if(currentToast != null) currentToast.cancel();
        currentToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        currentToast.show();
    }

    /*pre: cert*/
    /*post: s'ha llençat un toast personalitzat amb la imatge image i el missatge message*/
    public void customizedToast(String message, Image image){
        if(currentToast != null) currentToast.cancel();
    }
}
