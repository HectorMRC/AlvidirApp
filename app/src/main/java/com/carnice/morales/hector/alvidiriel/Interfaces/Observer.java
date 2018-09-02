package com.carnice.morales.hector.alvidiriel.Interfaces;


import android.support.v4.util.Pair;

public interface Observer {
    void onActionListener(Pair<String, String> item, String action);
}
