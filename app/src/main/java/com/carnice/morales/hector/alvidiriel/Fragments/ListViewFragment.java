package com.carnice.morales.hector.alvidiriel.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.carnice.morales.hector.alvidiriel.Interfaces.Linker;
import com.carnice.morales.hector.alvidiriel.MainActivity;
import com.carnice.morales.hector.alvidiriel.R;
import com.carnice.morales.hector.alvidiriel.Utils.CustomAdapter;
import com.carnice.morales.hector.alvidiriel.Utils.CustomObject;
import com.carnice.morales.hector.alvidiriel.ViewActivity;

import java.util.ArrayList;

public class ListViewFragment extends ListFragment implements AdapterView.OnItemClickListener,
                                                              AdapterView.OnItemLongClickListener,
                                                              AbsListView.OnScrollListener,
                                                              Linker<Pair<String, String>, String>{
    private CustomAdapter customAdapter;
    private int currentItemOnScroll;
    private static ListViewFragment CurrentInstance;

    //OVERRIDES:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(customAdapter);
        CurrentInstance = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        getListView().setOnScrollListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        ((MainActivity) getActivity()).setAddButtonVisibility(currentItemOnScroll, i);

        int current = (getListView() == null || getListView().getChildCount() == 0)?
                       0 : getListView().getChildAt(0).getTop();

        MainActivity.Refresher.setEnabled(i == 0 && current >= 0);
        currentItemOnScroll = i;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), ViewActivity.class);
        getContext().startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        customAdapter.setSelectedItem(new Pair<>(customAdapter.getItem(i).getWord(), customAdapter.getItem(i).getTran()), "0");
        getListView().smoothScrollToPosition(i);
        return true;
    }

    @Override
    public Pair<String, String> returnRequiredData(String requestCode) {
        return customAdapter.getSelectedItem();
    }

    @Override
    public void setRequiredData(Pair<String, String> content, String requestCode) {
        customAdapter.setSelectedItem(content, requestCode);
    }

    @Override
    public void setLinker(Linker linker, String requestCode) {

    }

    @Override
    public void removeLinker(Linker linker, String requestCode) {

    }

    public boolean onBackPressed(){
        if(customAdapter.getSelectedItem() == null) return false;

        else if(customAdapter.getDeleteButtonSelected())
            customAdapter.setSelectedItem(customAdapter.getSelectedItem(), "0");

        else if(!customAdapter.getDeleteButtonSelected())
            customAdapter.setSelectedItem(null, null);

        return true;
    }

    //FUNCIONS INHERENTS AL FRAGMENT:
    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    public static ListViewFragment getCurrentInstance(){
        if(CurrentInstance == null) CurrentInstance = new ListViewFragment();
        return CurrentInstance;
    }

    //FUNCIONS AUXILIARS:
    /*pre: l'atribut tuples no és buit.*/
    /*post: el contingut de la listView s'ha actualitzat pel de l'atribut.*/
    public void refresh(Context context, ArrayList<ContentValues> tuples, String[] key, boolean withFlag){
        ArrayList<CustomObject> customArray = new ArrayList<>();
        for(ContentValues value: tuples) {
            CustomObject element = new CustomObject(value.getAsString(key[0]), value.getAsString(key[1]), value.getAsInteger(key[2]));
            customArray.add(element);
        }

        onAttach(getContext());
        if(customAdapter == null) customAdapter = new CustomAdapter(context, customArray, withFlag);
        else customAdapter.replaceAll(customArray, withFlag);
        customAdapter.notifyDataSetChanged();
    }

    /*pre: cert*/
    /*post: s'ha retornat el nombre d'items que te la llista.*/
    public int getCount(){
        return customAdapter.getCount();
    }
}
