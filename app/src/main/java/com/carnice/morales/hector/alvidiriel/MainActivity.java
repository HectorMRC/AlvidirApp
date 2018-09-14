package com.carnice.morales.hector.alvidiriel;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.Fragments.ListViewFragment;
import com.carnice.morales.hector.alvidiriel.Fragments.OptionsFragment;
import com.carnice.morales.hector.alvidiriel.Fragments.UpdaterFragment;
import com.carnice.morales.hector.alvidiriel.Utils.ColorsManager;
import com.carnice.morales.hector.alvidiriel.Utils.ContentChecker;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.FilesManager;
import com.carnice.morales.hector.alvidiriel.Utils.NetworkManager;
import com.carnice.morales.hector.alvidiriel.Utils.Statements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                               View.OnLongClickListener,
                                                               PopupMenu.OnMenuItemClickListener,
                                                               SwipeRefreshLayout.OnRefreshListener,
                                                               TextWatcher{

    //DECLARACIÓ D'OBJECTES:
    private DBManager dbManager;
    private Statements statement;

    //Control de tasques corrent:
    public static SwipeRefreshLayout Refresher;

    ListViewFragment listViewFragment;
    UpdaterFragment updaterFragment;
    OptionsFragment optionsFragment;

    LinearLayout NothingFound;
    android.widget.FrameLayout FrameLayout;

    ImageButton SearchButton, FilterButton;
    Button SortFirst, SortSecond, AddFromEmpty;
    FloatingActionButton AddNewItem;

    TextView ActivityTitle, Status;
    EditText ListSearch;

    PopupMenu FilterMenu;

    //OVERRIDES:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iniAllObjects();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        this.onRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ic_search:
                swapListSearcherVisibility();
                break;

            case R.id.ic_filter:
                showFilterMenu(view);
                break;

            case R.id.sort_first:
            case R.id.sort_second:
                sortListView(view);
                break;

            case R.id.afegir_from_empty:
            case R.id.ic_add:
                swapFrameLayoutVisibility(true);
                break;

            case R.id.list_search:
                showKeyboard();

            default: break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        swapFrameLayoutVisibility(false);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId() != R.id.select_match) menuItem.setChecked(!menuItem.isChecked());
        else menuItem.setTitle(Objects.equals(menuItem.getTitle(), getText(R.string.comença))? R.string.conté :
                Objects.equals(menuItem.getTitle(), getText(R.string.conté))? R.string.estricte : R.string.comença);

        if(!ListSearch.getText().toString().isEmpty()){ //En cas de tenir ple el ListSearch:
            setWhereStatement();    //Actualització de la clausula where.
            refreshListView();      //Actualització de la listView segons el where.
            refreshCounter();       //Actualització de la compta d'elements trobats.
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(removeOddSpaces(s.toString().replace(',',' ')).isEmpty())
            statement.resetStatus();
        else setWhereStatement();
        refreshListView();
        refreshCounter();
    }

    @Override
    public void onBackPressed() {
        if(optionsFragment != null && optionsFragment.onBackPressed()); //El fragment se n'encarrega
        else if(FrameLayout.getVisibility() == View.VISIBLE) swapFrameLayoutVisibility(true);
        else if(listViewFragment.onBackPressed()); //El fragment se n'encarrega.
        else if(ListSearch.getVisibility() == View.VISIBLE) swapListSearcherVisibility();
        else /*if(BusyTask.isEmpty())*/ super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        while(listViewFragment.onBackPressed()); //Tanca del tot qualsevol item seleccionat.
        if(optionsFragment != null && FrameLayout.getVisibility() != View.GONE)
            swapFrameLayoutVisibility(true);

        if(ListSearch.getVisibility() == View.VISIBLE) refreshCounter();
        else Status.setText(new NetworkManager().getStatus(this));

        refreshListView();
        AddNewItem.show();
        Refresher.setRefreshing(false);
    }

    //FUNCIONS PRIVADES:
    /*pre: cert*/
    /*post: s'han inicialitzat tots els objectes de l'activity.*/
    private void iniAllObjects(){
        dbManager = new DBManager(this);
        statement = new Statements();
        //BusyTask = new HashSet<>();

        FrameLayout = findViewById(R.id.frame_layout);
        NothingFound = findViewById(R.id.nothing_found);
        Refresher = findViewById(R.id.SwipeRefresh);
        Refresher.setOnRefreshListener(this);

        SearchButton = findViewById(R.id.ic_search);
        SearchButton.setOnClickListener(this);
        FilterButton = findViewById(R.id.ic_filter);
        FilterButton.setOnClickListener(this);
        SortFirst = findViewById(R.id.sort_first);
        SortFirst.setOnClickListener(this);
        SortSecond = findViewById(R.id.sort_second);
        SortSecond.setOnClickListener(this);
        AddFromEmpty = findViewById(R.id.afegir_from_empty);
        AddFromEmpty.setOnClickListener(this);
        AddNewItem = findViewById(R.id.ic_add);
        AddNewItem.setOnClickListener(this);
        AddNewItem.setOnLongClickListener(this);

        ActivityTitle = findViewById(R.id.app_title);
        Status = findViewById(R.id.status);
        Status.setText(new NetworkManager().getStatus(this));
        ListSearch = findViewById(R.id.list_search);
        ListSearch.setOnClickListener(this);
        ListSearch.addTextChangedListener(this);

        iniListViewFragment();
        setFrameLayoutContent(true);

        showFilterMenu(FilterButton);
        sortListView(findViewById(R.id.sort_first));
    }

    /*pre: cert*/
    /*post: s'ha inicialitzat la instancia del ListViewFragment.*/
    private void iniListViewFragment(){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        listViewFragment = (ListViewFragment) fm.findFragmentByTag("fragment_ListView");
        if(listViewFragment == null){
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            listViewFragment = new ListViewFragment();
            ft.add(R.id.fragment_ListView, listViewFragment, "fragment_ListView");
            ft.commit();
        }
    }

    /*pre: cert*/
    /*post: s'ha retornat true si la View solicitada ja està montada; altrament retorna false
    *       i en monta la vista.*/
    private boolean alreadyCreatedFrameView(boolean updater){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        updaterFragment = (UpdaterFragment) fm.findFragmentByTag("fragment_updater");
        optionsFragment = (OptionsFragment) fm.findFragmentByTag("fragment_options");

        if(updater? updaterFragment == null : optionsFragment == null){
            FrameLayout.removeAllViews(); //Neteja la vista corrent
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            if(updater) updaterFragment = new UpdaterFragment();
            else optionsFragment = new OptionsFragment();
            ft.add(R.id.frame_layout, updater? updaterFragment : optionsFragment,
                    updater? "fragment_updater" : "fragment_options");
            ft.commit();
            return false;
        }

        return true;
    }

    /*pre: cert*/
    /*post: s'ha inicialitzat la instancia del UpdaterFragment si, i només si, updater és true;
     *      altrament s'ha inicialitzat la de OptionsFragment.*/
    private void setFrameLayoutContent(boolean updater){
        if(alreadyCreatedFrameView(updater)){
            updaterFragment.reset(false);
            if(FrameLayout.getVisibility() == View.VISIBLE) {
                FrameLayout.removeAllViews();
                FrameLayout.addView(updaterFragment.getView());
            }
            else if(!updater){
                FrameLayout.removeAllViews();
                FrameLayout.addView(optionsFragment.getView());
            }
        }
    }

    /*pre: cert*/
    /*post: s'ha obert el teclat digital si, i només si, no estava ja obert.*/
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(this.getCurrentFocus(), 0);
    }

    /*pre: cert*/
    /*post: s'ha retornat un string amb el mateix contingut pero sense espais innecessaris.*/
    private String removeOddSpaces(String string){
        //Neteja espais inicials:
        while(!string.isEmpty() && string.charAt(0) == ' ')
            if(string.length() > 1) string = string.substring(1);
            else string = "";
        //Neteja espais finals:
        while(!string.isEmpty() && string.charAt(string.length()-1) == ' ')
            string = string.substring(0, string.length()-1);

        return string;
    }

    /*pre: cert*/
    /*post: s'han establert els valors del statement.*/
    private void setWhereStatement(){
        String[] key = dbManager.getKey();
        statement.selection =
                (FilterMenu.getMenu().findItem(R.id.select_català).isChecked()? key[0].concat(" LIKE ? ").concat(
                 FilterMenu.getMenu().findItem(R.id.select_alvidir).isChecked()? " OR " : "") : "").concat(
                 FilterMenu.getMenu().findItem(R.id.select_alvidir).isChecked()? key[1].concat(" LIKE ? ") : "");

        StringTokenizer tokenForInstances = new StringTokenizer(ListSearch.getText().toString(), ",");
        StringTokenizer tokenForArguments = new StringTokenizer(statement.selection, "?");

        if(tokenForArguments.countTokens() > 0) {
            statement.args = new String[tokenForInstances.countTokens()][tokenForArguments.countTokens() - 1];
            for (int j = 0; j < statement.args.length; j++) {
                String current = tokenForInstances.nextToken();
                for (int i = 0; i < statement.args[j].length; i++)
                    statement.args[j][i] =
                            (Objects.equals(FilterMenu.getMenu().findItem(R.id.select_match).getTitle(), getText(R.string.conté)) ? "%" : "")
                                    .concat(removeOddSpaces(current))
                                    .concat(Objects.equals(FilterMenu.getMenu().findItem(R.id.select_match).getTitle(), getText(R.string.estricte))
                                            || current.charAt(current.length()-1) == ' '? "" : "%");
            }
        }
    }

    /*pre: cert*/
    /*post: el counter senyala quants items té la llista. Si els items son 0 o el ListSearch
    *       està en desús marca la conectivitat del dispositiu.*/
    private void refreshCounter(){
        Status.setText(ListSearch.getVisibility() == View.VISIBLE? listViewFragment.getCount() == 0?
                        getText(R.string.sense_resultats) :
                        "Trobats ".concat(String.valueOf(listViewFragment.getCount())).concat(" elements") :
                        new NetworkManager().getStatus(this));
    }

    //FUNCIONS AUXILIARS:
    /*pre: cert*/
    /*post: s'ha retornat true, si i només si, el frame layout és visible; altrament false.*/
    public boolean isFrameLayoutVisible(){
        return FrameLayout.getVisibility() == View.VISIBLE;
    }

    /*pre: cert*/
    /*post: s'han materialitzat el fragment d'agregació.*/
    public void swapFrameLayoutVisibility(boolean updater){
        setFrameLayoutContent(updater);
        FrameLayout.startAnimation(AnimationUtils.loadAnimation(this,
                FrameLayout.getVisibility() == View.GONE? R.anim.updater_open : R.anim.updater_close));
        FrameLayout.setVisibility(FrameLayout.getVisibility() == View.GONE? View.VISIBLE : View.GONE);

        setAddButtonVisibility(0, FrameLayout.getVisibility() == View.VISIBLE? 1 : -1);
        if(NothingFound.getVisibility() == View.VISIBLE && !ListSearch.getText().toString().isEmpty())
            updaterFragment.setWordText(ListSearch.getText().toString());
    }

    /*pre: cert*/
    /*post: si ListSearch és visible, el tanca; altremnt l'obre.*/
    public void swapListSearcherVisibility(){
        ListSearch.setVisibility((ListSearch.getVisibility() == View.GONE)? View.VISIBLE : View.GONE);
        ActivityTitle.setVisibility((ListSearch.getVisibility() == View.GONE)? View.VISIBLE : View.GONE);
        SearchButton.setImageDrawable((ListSearch.getVisibility() == View.GONE)? getDrawable(R.drawable.ic_list_search) : getDrawable(R.drawable.ic_clear));
        ListSearch.setText("");

        if(ListSearch.getVisibility() == View.VISIBLE){
            ListSearch.requestFocus();
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showSoftInput(ListSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /*pre: cert*/
    /*post: s'ha obert el menu del filtre de cerca.*/
    public void showFilterMenu(View view){
        if(FilterMenu == null) {
            FilterMenu = new PopupMenu(this, view);
            FilterMenu.setOnMenuItemClickListener(this);

            MenuInflater inflater = FilterMenu.getMenuInflater();
            inflater.inflate(R.menu.filter_menu, FilterMenu.getMenu());
        }

        else FilterMenu.show();
    }

    /*pre: cert*/
    /*post: s'ha ocultat el button AddNewItem si, i només si, scrollDown; altrament es mostra.*/
    public void setAddButtonVisibility(int oldItem, int newItem){
        if(oldItem > newItem && FrameLayout.getVisibility() == View.GONE) AddNewItem.show();
        else if(oldItem < newItem) AddNewItem.hide();
    }

    /*pre: cert*/
    /*post: s'ha ordenat la ListView segons convingui*/
    public void sortListView(View view){
        String[] columns = dbManager.getKey();
        statement.sortBy = ((view.getId() == R.id.sort_first)? columns[0] : columns[1]).concat(
                Objects.equals(((Button)view).getText(), getText(R.string.a_z))? " DESC": " ASC");

        ((Button)view).setText(Objects.equals(((Button)view).getText(), getText(R.string.a_z))? getText(R.string.z_a): getText(R.string.a_z));
        refreshListView();
    }

    /*pre: cert*/
    /*post: s'ha actualitzat el contingut de la ListView.*/
    public void refreshListView() {
        ColorsManager colors = new ColorsManager();
        ArrayList<ContentValues> tuples = new ArrayList<ContentValues>();
        ContentChecker content = new ContentChecker(dbManager.getKey());

        if(statement.args == null)
            tuples.addAll(dbManager.getAllTuples(statement.selection, null, null, null, statement.sortBy, colors.randColor()));
        else for(String[] arg: statement.args) { //Gracies al ContentChecker s'evita la repetició d'elemetns
            ArrayList<ContentValues> consulta = dbManager.getAllTuples(statement.selection, arg, null, null, statement.sortBy, colors.getColor());
            for(ContentValues values: consulta) if(content.check(values)) tuples.add(values);
        }

        listViewFragment.refresh(this, tuples, dbManager.getKey(), statement.args != null && statement.args.length > 1);

        findViewById(R.id.nothing_found).setVisibility((tuples.isEmpty())? View.VISIBLE : View.GONE);
        //setAddButtonVisibility(0, tuples.isEmpty() || BusyTask.contains("Updater")? 1 : -1);
        NothingFound.setVisibility(tuples.isEmpty()? View.VISIBLE : View.GONE);
    }
}
