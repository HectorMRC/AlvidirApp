package com.carnice.morales.hector.alvidiriel;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.Fragments.OptionsFragment;
import com.carnice.morales.hector.alvidiriel.Interfaces.Observer;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.SliderAdapter;
import com.carnice.morales.hector.alvidiriel.Utils.TextManager;

import java.util.HashSet;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener,
                                                               View.OnLongClickListener,
                                                               ViewPager.OnPageChangeListener,
                                                               PopupMenu.OnMenuItemClickListener,
                                                               Observer{

    //DECLARACIÓ D'OBJECTES:
    private HashSet<ContentValues> References;
    private int CurrentPage;

    ImageButton TurnBack, ShowOptions;
    Button RefrButton, RootButton, BackPageButton, AddPageButton;
    PopupMenu optionsMenu;

    TextView ItemType, ItemWord, ItemTran, ItemFon;
    LinearLayout ItemFlag;

    LinearLayout DotLayout;
    TextView[] Dots;

    ViewPager SlideViewPager;
    SliderAdapter sliderAdapter;

    //OVERRIDES:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        iniAllObjects();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.onItem_back:
                super.onBackPressed();
                break;

            case R.id.onItem_add:
                setActionForReturn("a:".concat(String.valueOf(CurrentPage)));
                break;

            case R.id.onItem_backpage:
                SlideViewPager.setCurrentItem(0, true);
                CurrentPage = 0;
                break;

            case R.id.onItem_options:
                showOptionsMenu(v);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        BackPageButton.setTextColor(getColor(position == 0? R.color.nonOpaqueWhite : R.color.softWhiteX2));
        CurrentPage = position;
    }

    @Override
    public void onPageSelected(int position) {
        addDotIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String actionData = "";
        switch (item.getItemId()){
            case R.id.action_delete:
                actionData = "d:";
                break;

            case R.id.action_edit:
                actionData = "e:";
                break;

            case R.id.action_refer:
                actionData = "r:";
                break;

            default:
                break;
        }

        setActionForReturn(actionData);
        return false;
    }

    @Override
    public void onActionListener(Pair<String, String> item, String action) {

    }

    //FUNCIONS PRIVADES:
    /*pre: cert*/
    /*post: s'han inicialitzat tots els objectes de l'activity.*/
    private void iniAllObjects(){
        References = new HashSet<>();

        TurnBack = findViewById(R.id.onItem_back);
        TurnBack.setOnClickListener(this);
        ShowOptions = findViewById(R.id.onItem_options);
        ShowOptions.setOnClickListener(this);

        RefrButton = findViewById(R.id.onItem_refr);
        RefrButton.setOnClickListener(this);
        RootButton = findViewById(R.id.onItem_root);
        RootButton.setOnClickListener(this);
        BackPageButton = findViewById(R.id.onItem_backpage);
        BackPageButton.setOnClickListener(this);
        AddPageButton = findViewById(R.id.onItem_add);
        AddPageButton.setOnClickListener(this);

        ItemType = findViewById(R.id.onItem_type);
        ItemWord = findViewById(R.id.onItem_word);
        ItemTran = findViewById(R.id.onItem_tran);
        ItemFon = findViewById(R.id.onItem_fon);
        ItemFlag = findViewById(R.id.onItem_flag);

        //iniRecyclerViewFragment();
        setObjectsContent();

        DotLayout = findViewById(R.id.onItem_dot);

        SlideViewPager = findViewById(R.id.onItem_ViewPager);
        SlideViewPager.addOnPageChangeListener(this);
        sliderAdapter = new SliderAdapter(this, getIntent().getExtras().getString("ThisInfo"));
        SlideViewPager.setAdapter(sliderAdapter);

        addDotIndicator(0);
    }

    /*pre: cert*/
    /*post: s'ha afegit la informació del Item als elements corresponents.*/
    private void setObjectsContent(){
        DBManager dbManager = new DBManager(this);
        String[] keys = dbManager.getMainColumns();

        ItemWord.setText(getIntent().getExtras().getString("ThisWord"));
        ItemFon.setText(getIntent().getExtras().getString("ThisTran"));
        ItemTran.setText(new TextManager().transcribe(ItemFon.getText().toString()));

        ContentValues tuple = dbManager.getTuple(ItemWord.getText().toString(), ItemFon.getText().toString());
        ItemType.setText(tuple.getAsString(keys[0]));

        //Activa o no el botó Root en cas de que l'Item referencii a algun altre item.
        tuple = dbManager.getRoot(ItemWord.getText().toString(), ItemFon.getText().toString());
        RootButton.setVisibility(tuple.getAsString(keys[1]).equals(ItemWord.getText().toString()) &&
                                 tuple.getAsString(keys[2]).equals(ItemFon.getText().toString()) ?
                                 View.GONE : View.VISIBLE);

        //Defineix la quantitat d'Items que apunten a l'arrel del item corrent (Doncs son sinonim).
        dbManager.getAllReferences(tuple.getAsString(keys[1]), tuple.getAsString(keys[2]), References);
        RefrButton.setText(References.size() == 0? getString(R.string.no_sinonims) :
                "Té ".concat(String.valueOf(References.size())).concat(" sinonims"));

        ItemFlag.setBackgroundColor(getIntent().getExtras().getInt("ThisFlag"));
    }

    /*pre: cert*/
    /*post: finalitza l'activity duent a terme l'acció seleccionada.*/
    private void setActionForReturn(String data){
        Intent action = new Intent();
        action.setData(Uri.parse(data));

        setResult(RESULT_OK, action);
        finish();
    }

    /*pre: cert*/
    /*post: s'han generat tants punts com pagines d'info hi hagi.*/
    public void addDotIndicator(int position){
        Dots = new TextView[sliderAdapter.getCount()];
        DotLayout.removeAllViews();

        for(int i = 0; i < sliderAdapter.getCount(); i++){
            Dots[i] = new TextView(this);
            Dots[i].setText(Html.fromHtml("&#8226;"));
            Dots[i].setTextColor(getResources().getColor(i == position?
                                                         R.color.white : R.color.nonOpaqueWhite,
                                                        null));
            Dots[i].setTextSize(35);

            DotLayout.addView(Dots[i]);
        }
    }

    /*pre: cert*/
    /*post: obre el menú d'opcions.*/
    public void showOptionsMenu(View view){
        if(optionsMenu == null) {
            optionsMenu = new PopupMenu(this, view);
            optionsMenu.setOnMenuItemClickListener(this);

            MenuInflater inflater = optionsMenu.getMenuInflater();
            inflater.inflate(R.menu.onitem_menu, optionsMenu.getMenu());
        }

        optionsMenu.show();
    }

}
