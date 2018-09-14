package com.carnice.morales.hector.alvidiriel;

import android.content.ContentValues;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.Fragments.RecyclerFragment;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.Transcriber;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener,
                                                               View.OnLongClickListener{

    //DECLARACIÓ D'OBJECTES:
    ImageButton TurnBack, ShowOptions;
    Button RefrButton, RootButton;

    TextView ItemType, ItemWord, ItemTran, ItemFon;

    RecyclerFragment recyclerFragment;
    LinearLayout ItemFlag;

    ViewPager SlideViewPager;
    LinearLayout DoterLayout;

    PopupMenu OptionsMenu;

    //OVERRIDES:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        iniAllObjects();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.onItem_back:
                super.onBackPressed();
                break;

            default: break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    //FUNCIONS PRIVADES:
    /*pre: cert*/
    /*post: s'han inicialitzat tots els objectes de l'activity.*/
    private void iniAllObjects(){
        TurnBack = findViewById(R.id.onItem_back);
        TurnBack.setOnClickListener(this);
        ShowOptions = findViewById(R.id.onItem_options);
        ShowOptions.setOnClickListener(this);

        RefrButton = findViewById(R.id.onItem_refr);
        RefrButton.setOnClickListener(this);
        RootButton = findViewById(R.id.onItem_root);
        RootButton.setOnClickListener(this);

        ItemType = findViewById(R.id.onItem_type);
        ItemWord = findViewById(R.id.onItem_word);
        ItemTran = findViewById(R.id.onItem_tran);
        ItemFon = findViewById(R.id.onItem_fon);

        ItemFlag = findViewById(R.id.onItem_flag);

        //iniRecyclerViewFragment();
        setObjectsContent();
    }

    /*pre: cert*/
    /*post: s'ha inicialitzat la instancia del RecyclerFragment.*/
    private void iniRecyclerViewFragment(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        recyclerFragment = (RecyclerFragment) fm.findFragmentByTag("fragment_OnItem");
        if(recyclerFragment == null){
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            recyclerFragment = new RecyclerFragment();
            ft.add(R.id.fragment_ListView, recyclerFragment, "fragment_OnItem");
            ft.commit();
        }
    }

    /*pre: cert*/
    /*post: s'ha afegit la informació del Item als elements corresponents.*/
    private void setObjectsContent(){
        DBManager dbManager = new DBManager(this);
        String[] keys = dbManager.getMainColumns();

        ItemWord.setText(getIntent().getExtras().getString("ThisWord"));
        ItemFon.setText(getIntent().getExtras().getString("ThisTran"));
        ItemTran.setText(new Transcriber().transcribe(ItemFon.getText().toString()));

        ContentValues tuple = dbManager.getTuple(ItemWord.getText().toString(), ItemFon.getText().toString());
        ItemType.setText(tuple.getAsString(keys[0]));

        //Defineix la quantitat d'Items que apunten al item corrent.
        int countRefr = dbManager.getAllReferences(ItemWord.getText().toString(), ItemFon.getText().toString()).size();
        RefrButton.setText(countRefr == 0? getString(R.string.no_sinonims) :
                           "Té ".concat(String.valueOf(countRefr)).concat(" sinonims"));

        //Activa o no el botó Root en cas de que l'Item referencii a algun altre item.
        tuple = dbManager.getRoot(ItemWord.getText().toString(), ItemFon.getText().toString());
        RootButton.setVisibility(tuple.getAsString(keys[1]).equals(ItemWord.getText().toString()) &&
                                 tuple.getAsString(keys[2]).equals(ItemFon.getText().toString()) ?
                                 View.GONE : View.VISIBLE);

        ItemFlag.setBackgroundColor(getIntent().getExtras().getInt("ThisFlag"));
    }
}
