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

import com.carnice.morales.hector.alvidiriel.Interfaces.Linker;
import com.carnice.morales.hector.alvidiriel.MainActivity;
import com.carnice.morales.hector.alvidiriel.R;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.ListViewAdapter;
import com.carnice.morales.hector.alvidiriel.Utils.ListViewObject;
import com.carnice.morales.hector.alvidiriel.ViewActivity;

import java.util.ArrayList;
import java.util.HashSet;

import static android.app.Activity.RESULT_OK;

public class ListViewFragment extends ListFragment implements AdapterView.OnItemClickListener,
                                                              AdapterView.OnItemLongClickListener,
                                                              AbsListView.OnScrollListener,
                                                              Linker<Pair<String, String>, String>{
    private ListViewAdapter listViewAdapter;
    private int currentItemOnScroll;
    private static ListViewFragment CurrentInstance;

    //OVERRIDES:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(listViewAdapter);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            
        }
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

        DBManager dbManager = new DBManager(getContext());
        String[] keys = dbManager.getMainColumns();
        ContentValues tupla = dbManager.getRoot(listViewAdapter.getItem(i).getWord(),
                                                listViewAdapter.getItem(i).getTran());

        intent.putExtra("ThisWord", listViewAdapter.getItem(i).getWord());
        intent.putExtra("ThisTran", listViewAdapter.getItem(i).getTran());
        intent.putExtra("ThisFlag", listViewAdapter.getItem(i).getFlag());
        intent.putExtra("ThisInfo", tupla.getAsString(keys[3]));

        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        listViewAdapter.setSelectedItem(new Pair<>(listViewAdapter.getItem(i).getWord(), listViewAdapter.getItem(i).getTran()), "0");
        getListView().smoothScrollToPosition(i);
        return true;
    }

    @Override
    public Pair<String, String> returnRequiredData(String requestCode) {
        return listViewAdapter.getSelectedItem();
    }

    @Override
    public void setRequiredData(Pair<String, String> content, String requestCode) {
        listViewAdapter.setSelectedItem(content, requestCode);
    }

    @Override
    public void setLinker(Linker linker, String requestCode) {

    }

    public boolean onBackPressed(){
        if(listViewAdapter.getSelectedItem() == null) return false;

        else if(listViewAdapter.getDeleteButtonSelected())
            listViewAdapter.setSelectedItem(listViewAdapter.getSelectedItem(), "0");

        else if(!listViewAdapter.getDeleteButtonSelected())
            listViewAdapter.setSelectedItem(null, null);

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
        ArrayList<ListViewObject> customArray = new ArrayList<>();
        for(ContentValues value: tuples) {
            ListViewObject element = new ListViewObject(value.getAsString(key[0]), value.getAsString(key[1]),  value.getAsInteger(key[2]));
            customArray.add(element);
        }

        onAttach(getContext());
        if(listViewAdapter == null) listViewAdapter = new ListViewAdapter(context, customArray, withFlag);
        else listViewAdapter.replaceAll(customArray, withFlag);
        listViewAdapter.notifyDataSetChanged();
    }

    /*pre: cert*/
    /*post: s'ha retornat el nombre d'items que te la llista.*/
    public int getCount(){
        return listViewAdapter.getCount();
    }
}
