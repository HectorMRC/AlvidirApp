package com.carnice.morales.hector.alvidiriel.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carnice.morales.hector.alvidiriel.R;

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
    @SuppressLint("WrongConstant")
    public void customizedToast(LayoutInflater inflater, View view, String message, Drawable image){
        if(currentToast != null) currentToast.cancel();
        custom = (LinearLayout) inflater.inflate(R.layout.customized_toast, (ViewGroup) view.findViewById(R.id.toastHolder));

        TextView toastText = custom.findViewById(R.id.custom_text);
        ImageView toastPic = custom.findViewById(R.id.custom_img);

        if(message != null) toastText.setText(message);
        if(image != null) toastPic.setImageDrawable(image);

        currentToast = new Toast(context);
        currentToast.setDuration(LENGTH_LONG);
        currentToast.setGravity(Gravity.CENTER, 0, 0);
        currentToast.setView(custom);

        currentToast.show();
    }
}
