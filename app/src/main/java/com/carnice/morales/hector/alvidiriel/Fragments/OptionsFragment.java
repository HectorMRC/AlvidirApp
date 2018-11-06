package com.carnice.morales.hector.alvidiriel.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carnice.morales.hector.alvidiriel.Interfaces.Linker;
import com.carnice.morales.hector.alvidiriel.MainActivity;
import com.carnice.morales.hector.alvidiriel.R;
import com.carnice.morales.hector.alvidiriel.Utils.ListViewAdapter;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.FilesManager;
import com.carnice.morales.hector.alvidiriel.Utils.Toaster;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class OptionsFragment extends Fragment implements View.OnClickListener, Linker<String, String>{

    //DECLARACIÓ D'OBJECTES:
    Button LeftButton, CenterButton, RigthButton;
    LinearLayout LeftSeparator, RigthSeparator;
    LinearLayout Background;

    private Linker<String, String> Getter;
    boolean RigthButtonSelected = false;

    //OVERRIDES:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (this.Getter = ListViewAdapter.getCurrentInstance()).setLinker(this, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LeftButton = getView().findViewById(R.id.left_button);
        LeftButton.setOnClickListener(this);
        CenterButton = getView().findViewById(R.id.center_button);
        CenterButton.setOnClickListener(this);
        RigthButton = getView().findViewById(R.id.rigth_button);
        RigthButton.setOnClickListener(this);
        Background = getView().findViewById(R.id.options_background);

        LeftButton.setText(getActivity() == null? R.string.referenciar : R.string.carregar);
        CenterButton.setText(getActivity() == null? R.string.editar : R.string.salva);
        Background.setBackgroundColor(getActivity() == null? 0x00000000 : 0x99000000);


        LeftSeparator = getView().findViewById(R.id.left_separator);
        RigthSeparator = getView().findViewById(R.id.rigth_separator);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_button:
            case R.id.center_button:
                onActionSelected(String.valueOf(v.getId()));
                break;

            case R.id.rigth_button:
                if(!RigthButtonSelected) rigthButtonControler();
                else onActionSelected(String.valueOf(v.getId()));
                break;

            default: break;
        }
    }

    @Override
    public String returnRequiredData(String requestCode) {
        return null;
    }

    @Override
    public void setRequiredData(String content, String requestCode) {
        Getter.setRequiredData(content, null);
    }

    @Override
    public void setLinker(Linker linker, String requestCode) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toaster toaster = new Toaster(getContext());

        if(resultCode == RESULT_OK && data != null) try{
            FilesManager filesManager = new FilesManager(getContext(), data);
            if(requestCode == 0) filesManager.getDataFileContent();
            else filesManager.saveDataBaseContent();

        } catch (IOException e){
            Log.i("FILES MANAGER IOException", e.toString());
            toaster.customizedToast(getLayoutInflater(), getView(), getString(R.string.reboke_action), getContext().getDrawable(R.drawable.ic_error_vector));
        }

        toaster.customizedToast(getLayoutInflater(), getView(), getString(R.string.commit_advanced), getContext().getDrawable(R.drawable.ic_storage_vector));
    }

    public boolean onBackPressed(){
        if(!RigthButtonSelected) return false;
        else rigthButtonControler();
        return true;
    }

    //FUNCIONS INHERENTS AL FRAGMENT:
    public OptionsFragment() {
        // Required empty public constructor
    }

    public static OptionsFragment newInstance() {
        return new OptionsFragment();
    }

    public void callFromCustomAdapter(){
        this.Getter = ListViewAdapter.getCurrentInstance();
    }

    //FUNCIONS AUXILIARS:
    /*pre: cert*/
    /*post: s'ha iniciat el buidat de tot el contingut de la base de dades.*/
    private void rigthButtonPressed(){
        Toaster toaster = new Toaster(getContext());
        DBManager dbManager = new DBManager(getContext());
        if(!dbManager.clearContent()) toaster.customizedToast(getLayoutInflater(), getView(), getString(R.string.reboke_action), getContext().getDrawable(R.drawable.ic_error_vector));
        else ((MainActivity) getActivity()).onRefresh();
    }

    /*pre: action és la id d'un dels botons d'aquest fragment*/
    /*post: s'ha canalitzat l'acció segons si ha sigut cridada per l'activiti o per
    *       una classe alternativa*/
    private void onActionSelected(String action){
        if(getActivity() == null) setRequiredData(action, null);
        else switch (Integer.valueOf(action)){

            case R.id.rigth_button:
                rigthButtonControler();
                rigthButtonPressed();
                break;

            default:
                iniActivityForResult(action.equals(String.valueOf(R.id.left_button))? 0 : 1);
                break;
        }
    }

    /*pre: cert*/
    /*post: s'ha obert el navegador d'archius per seleccionar o crear un arxiu.*/
    private void iniActivityForResult(int requestCode){
        Intent intentTask = new Intent(requestCode == 0? Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_CREATE_DOCUMENT);
        intentTask.addCategory(Intent.CATEGORY_OPENABLE);
        intentTask.setType("*/*");

        startActivityForResult(intentTask, requestCode);
    }

    /*pre: cert*/
    /*post: s'ha seleccionat o deseleccionat el RigthButton segons l'estat anterior del mateix.*/
    private void rigthButtonControler(){
        RigthButtonSelected = !RigthButtonSelected;
        LeftButton.setVisibility(RigthButtonSelected? View.GONE : View.VISIBLE);
        CenterButton.setVisibility(RigthButtonSelected? View.GONE : View.VISIBLE);
        LeftSeparator.setVisibility(RigthButtonSelected? View.GONE : View.VISIBLE);
        RigthSeparator.setVisibility(RigthButtonSelected? View.GONE : View.VISIBLE);
    }

    /*pre: cert*/
    /*post: s'ha canviat el focus d'aquesta classe.*/
    public void setFocus(View view){
        LeftButton = view.findViewById(R.id.left_button);
        LeftButton.setOnClickListener(this);
        CenterButton = view.findViewById(R.id.center_button);
        CenterButton.setOnClickListener(this);
        RigthButton = view.findViewById(R.id.rigth_button);
        RigthButton.setOnClickListener(this);

        LeftSeparator = view.findViewById(R.id.left_separator);
        RigthSeparator = view.findViewById(R.id.rigth_separator);
        RigthButtonSelected = false;
    }

    /*pre: cert*/
    /*post: s'ha retornat true si, i només si, s'ha activat el botó d'eliminar.*/
    public Boolean getRigthButtonSelected() {
        return RigthButtonSelected;
    }

    /*pre: cert*/
    /*post: s'ha canviat l'estat del botó d'eliminació.*/
    public void setRigthButtonSelected(Boolean selected) {
        if(RigthButtonSelected != selected) rigthButtonControler();
    }
}
