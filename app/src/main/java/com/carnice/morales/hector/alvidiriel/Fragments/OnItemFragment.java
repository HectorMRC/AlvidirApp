package com.carnice.morales.hector.alvidiriel.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carnice.morales.hector.alvidiriel.R;

public class OnItemFragment extends Fragment {

    private static OnItemFragment CurrentInstance;

    //OVERRIDES:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_item, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //FUNCIONS INHERITES AL FRAGMENT:
    public OnItemFragment() {
        // Required empty public constructor
    }

    public static OnItemFragment newInstance() {
        return new OnItemFragment();
    }

    public static OnItemFragment getCurrentInstance(){
        if(CurrentInstance == null) CurrentInstance = new OnItemFragment();
        return CurrentInstance;
    }

    //FUNCIONS AUXILIARS:
}
