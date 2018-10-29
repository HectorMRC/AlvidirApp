package com.carnice.morales.hector.alvidiriel.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.Interfaces.Linker;
import com.carnice.morales.hector.alvidiriel.Interfaces.Observer;
import com.carnice.morales.hector.alvidiriel.Interfaces.Subject;
import com.carnice.morales.hector.alvidiriel.MainActivity;
import com.carnice.morales.hector.alvidiriel.R;
import com.carnice.morales.hector.alvidiriel.Utils.ListViewAdapter;
import com.carnice.morales.hector.alvidiriel.Utils.DBManager;
import com.carnice.morales.hector.alvidiriel.Utils.TextManager;
import com.carnice.morales.hector.alvidiriel.Utils.Toaster;

import java.util.ArrayList;
import java.util.HashSet;

public class UpdaterFragment extends Fragment implements View.OnClickListener,
                                                         PopupMenu.OnMenuItemClickListener,
                                                         TextWatcher,
                                                         Observer,
                                                         Linker<Pair<String, String>, String>{
    //DECLARACIÓ D'OBJECTES:
    private Subject Subject;
    private Linker<Pair<String, String>, String> ItemSelectedGiver;
    private boolean erasing = false; //Indica si s'està esborrant o no l'InfoText

    Button Afegir, Ignorar, Arrel;
    ImageButton SwapContent, CategoryExpand;

    EditText WordText, TranText, InfoText;
    TextView Categories;

    android.support.v7.widget.AppCompatCheckBox Equival, EquWord, EquTran;
    LinearLayout EquSelectors;

    PopupMenu CategoryMenu;

    //OVERRIDES:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        (Subject = ListViewAdapter.getCurrentInstance()).setObserver(this);
        ItemSelectedGiver = ListViewFragment.getCurrentInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_updater, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Afegir = getView().findViewById(R.id.afegir);
        Afegir.setOnClickListener(this);
        Ignorar = getView().findViewById(R.id.ignorar);
        Ignorar.setOnClickListener(this);
        Arrel = getView().findViewById(R.id.goto_root);
        Arrel.setOnClickListener(this);
        SwapContent = getView().findViewById(R.id.swap);
        SwapContent.setOnClickListener(this);
        CategoryExpand = getView().findViewById(R.id.expand);
        CategoryExpand.setOnClickListener(this);

        WordText = getView().findViewById(R.id.text_word);
        WordText.addTextChangedListener(this);
        TranText = getView().findViewById(R.id.text_tran);
        TranText.addTextChangedListener(this);
        InfoText = getView().findViewById(R.id.text_info);
        InfoText.addTextChangedListener(this);
        Categories = getView().findViewById(R.id.categories);
        Categories.setOnClickListener(this);

        Equival = getView().findViewById(R.id.equival);
        Equival.setOnClickListener(this);
        EquWord = getView().findViewById(R.id.equ_first);
        EquWord.setOnClickListener(this);
        EquTran = getView().findViewById(R.id.equ_second);
        EquTran.setOnClickListener(this);

        EquSelectors = getView().findViewById(R.id.equ_selector);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.swap:
                swapContent();
                break;

            case R.id.equival:
                swapEquivalVisivility();
                break;

            case R.id.equ_first:
                EquTran.setChecked(false);
                break;

            case R.id.equ_second:
                EquWord.setChecked(false);
                break;

            case R.id.expand:
            case R.id.categories:
                showCategoryMenu();
                break;

            case R.id.afegir:
                afegirButtonPressed();
                break;

            case R.id.goto_root:
                gotoRoot();
                break;

            default:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.setChecked(!item.isChecked());
        setCategoryTitle();

        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        erasing = count < after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(WordText.getText() == s)
            EquWord.setText(getText(R.string.equ_ind).toString().concat(s.toString()));

        if(TranText.getText() == s)
            EquTran.setText(getText(R.string.equ_ind).toString().concat(s.toString()));

        boolean inTitle = false;
        if(InfoText.getText() == s) for (int i = 0; i < s.length(); i++) switch (s.charAt(i)) {
                case '"':
                    s = s.replace(i, i + 1, "\u0027");
                    break;

                case '#':  //Autocompletador de # al tag: #new_page
                    if (!inTitle && erasing) //Controla estats del text alïens al tag
                        if (i + 1 == s.length() || s.charAt(i + 1) != getString(R.string.divider).charAt(1))
                            s = s.replace(i, i + 1, new TextManager().tag(getString(R.string.divider)));
                    break;

                case '@':
                    inTitle = true;
                    break;

                case '\n':
                    inTitle = false;
                    break;

                default:
                    break;
            }

        syncronizeForEquivalence();
    }

    @Override
    public void onActionListener(Pair<String, String> item, String action) {
        if(action.equals(String.valueOf(R.id.rigth_button))){
            DBManager dbManager = new DBManager(getContext());
            dbManager.deleteItem(item.first, item.second);
            ((MainActivity) getActivity()).refreshListView();
        }

        else setItemValuesForAction(item, action.equals(String.valueOf(R.id.left_button)));
    }

    @Override
    public Pair<String, String> returnRequiredData(String requestCode) {
        return ItemSelectedGiver.returnRequiredData(null);
    }

    @Override
    public void setRequiredData(Pair<String, String> content, String requestCode) {
        ItemSelectedGiver.setRequiredData(content,requestCode);
    }

    @Override
    public void setLinker(Linker linker, String requestCode) {

    }

    //FUNCIONS INHERENTS AL FRAGMENT:
    public UpdaterFragment() {
        // Required empty public constructor
    }

    public static UpdaterFragment newInstance(String param1, String param2) {
        return  new UpdaterFragment();
    }

    //FUNCIONS AUXILIARS:
    /*pre: cert*/
    /*post: s'han intercanviat els mots WordText i TranText*/
    private void swapContent(){
        Editable content = WordText.getText();
        WordText.setText(TranText.getText());
        TranText.setText(content);

    }

    /*pre: cert*/
    /*post: si el booleà purge és cert, l'Item selected s'ha fet null. Altrament és conserva.*/
    private void purgeItemSelected(boolean purge){
        if(purge) ItemSelectedGiver.setRequiredData(null, null);
    }

    /*pre: item no és buit*/
    /*post: s'han configurat els components del layout segons l'item entrat*/
    private void setItemValuesForAction(Pair<String, String> item, boolean toRefer){
        DBManager dbManager = new DBManager(getContext());

        String where = dbManager.getKey()[0] + " LIKE ? AND " + dbManager.getKey()[1] + " LIKE ? ";
        String[] args = {item.first, item.second};
        ArrayList<ContentValues> tupla = dbManager.getAllTuples(where, args, null, null, null, 0);

        if(!((MainActivity) getActivity()).isFrameLayoutVisible())
            ((MainActivity) getActivity()).swapFrameLayoutVisibility(true);

        String[] column = dbManager.getMainColumns();
        Afegir.setText(toRefer? R.string.afegir : R.string.actualitza);

        Categories.setText(tupla.get(0).getAsString(column[0]));
        WordText.setText(tupla.get(0).getAsInteger(column[4]) == 1 && !toRefer?
                         tupla.get(0).getAsString(column[3]) : tupla.get(0).getAsString(column[1]));

        TranText.setText(tupla.get(0).getAsInteger(column[4]) == 2 && !toRefer?
                         tupla.get(0).getAsString(column[3]) : tupla.get(0).getAsString(column[2]));

        InfoText.setText(!toRefer? tupla.get(0).getAsInteger(column[4]) == 0 ?
                         tupla.get(0).getAsString(column[3]) :
                         tupla.get(0).getAsString(column[tupla.get(0).getAsInteger(column[4])]) :
                         "");

        InfoText.setText(new TextManager(getContext()).format(InfoText.getText().toString(), true)); //Transforma els caracters especials en l'etiqueta llegible
        Equival.setChecked(toRefer || tupla.get(0).getAsInteger(column[4]) > 0);
        EquWord.setChecked(tupla.get(0).getAsInteger(column[4]) == 1 && !toRefer);
        EquTran.setChecked(tupla.get(0).getAsInteger(column[4]) == 2 && !toRefer);
        EquSelectors.setVisibility(toRefer || tupla.get(0).getAsInteger(column[4]) > 0? View.VISIBLE : View.GONE);

        purgeItemSelected(toRefer);
        syncronizeForEquivalence();
    }

    /*pre: cert*/
    /*post: s'ha establer la visivilitat del Check Equ. segons l'estat del check Equival*/
    private void swapEquivalVisivility() {
        EquSelectors.setVisibility(Equival.isChecked()? View.VISIBLE : View.GONE);
        if(EquSelectors.getVisibility() == View.GONE){
            EquWord.setChecked(false);
            EquTran.setChecked(false);
        }

        WordText.setHint(getText(Equival.isChecked()? R.string.hint_for_equ_word : R.string.hint_for_word));
        TranText.setHint(getText(Equival.isChecked()? R.string.hint_for_equ_tran : R.string.hint_for_tran));
        InfoText.setHint(getText(Equival.isChecked()? R.string.hint_for_equ_info : R.string.hint_for_info));
        syncronizeForEquivalence();
    }

    /*pre: cert*/
    /*post: s'ha obert el menu de categories.*/
    private void showCategoryMenu() {
        if(CategoryMenu == null) {
            CategoryMenu = new PopupMenu(getActivity(), CategoryExpand);
            CategoryMenu.setOnMenuItemClickListener(this);

            MenuInflater inflater = CategoryMenu.getMenuInflater();
            inflater.inflate(R.menu.category_menu, CategoryMenu.getMenu());
        }

        if(Equival.isChecked()){
            Toaster toaster = new Toaster(getContext());
            toaster.standardToast(getText(R.string.err_modify_category).toString());
        }
        else CategoryMenu.show();
        setFromCategoryTitleToMenu();
    }

    /*pre: cert*/
    /*post: el text del botó arrel té color si, i només si està habilitat; altrament no.*/
    private void enableArrelButton(boolean enable){
        Arrel.setTextColor(ResourcesCompat.getColor(getResources(),
                           enable? R.color.colorAccent : R.color.transparent,
                          null));
    }

    /*pre: cert*/
    /*post: s'han seleccionat tots els check tal que el seu titol és present en el titol
     *      del botó del menu de categories.*/
    private void setFromCategoryTitleToMenu(){
        for(int itemIndex = 0; itemIndex < CategoryMenu.getMenu().size(); itemIndex++)
            CategoryMenu.getMenu().getItem(itemIndex).setChecked(
                    Categories.getText().toString().contains(
                            CategoryMenu.getMenu().getItem(itemIndex).getTitle().toString().substring(0, 3)));
    }

    /*pre: cert*/
    /*post: s'han modificat el titol del botó del menu segons els checks seleccionats al menú.*/
    private void setCategoryTitle(){
        String selected = "";
        for(int itemIndex = 0; itemIndex < CategoryMenu.getMenu().size(); itemIndex++) {
            if (CategoryMenu.getMenu().getItem(itemIndex).isChecked()) {
                if(!selected.isEmpty()) selected = selected.concat(" / ");
                selected = selected.concat(CategoryMenu.getMenu().getItem(itemIndex).getTitle().toString().substring(0, 3));
            }
        }

        Categories.setText(selected.isEmpty()? getText(R.string.tipus) : selected);
    }

    /*pre: cert*/
    /*post: en cas de complir les condicions d'integritat, el item s'afegeix o s'actualitza;
    *       altrament salta un missatge d'error.*/
    private void afegirButtonPressed(){
        Toaster toaster = new Toaster(getContext());
        if(Afegir.getCurrentTextColor() == ResourcesCompat.getColor(getResources(), R.color.solidGray, null))
            //Cas d'error: es preten referenciar a una definició inexistent.
            toaster.standardToast(getText(R.string.err_equ_not_match).toString());

        else if(WordText.getText().toString().isEmpty() ||
                TranText.getText().toString().isEmpty() ||
                Categories.getText().toString().isEmpty() ||
                (Equival.isChecked() &&
                        (!(EquWord.isChecked() || EquTran.isChecked()) ||
                           InfoText.getText().toString().isEmpty()))){

            //Cas d'error: no poden ser buits, ja que la base de dades ho requereix axi.
            toaster.standardToast(getText(R.string.err_empty_elements).toString());
        }

        else insertOrUpdateItem();
    }

    /*pre: dbManager no és null*/
    /*post: s'ha inserit l'element definit*/
    private boolean updateItem(DBManager dbManager){
        return dbManager.updateItem(returnRequiredData(null).first,
                                    returnRequiredData(null).second,

                                    Categories.getText().toString(),
                                    EquWord.isChecked()? InfoText.getText().toString() : WordText.getText().toString(),
                                    EquTran.isChecked()? InfoText.getText().toString() : TranText.getText().toString(),
                                    Equival.isChecked()? EquWord.isChecked()? WordText.getText().toString() :
                                            TranText.getText().toString() :
                                            new TextManager(getContext()).format(InfoText.getText().toString(), false).toString(),
                                    Equival.isChecked()? EquWord.isChecked()? 1 : 2 : 0);
    }

    /*pre: dbManager no és null*/
    /*post: s'ha actualitzat l'element definit*/
    private boolean insertItem(DBManager dbManager){
        return dbManager.insertItem(Categories.getText().toString(),
                                    EquWord.isChecked()? InfoText.getText().toString() : WordText.getText().toString(),
                                    EquTran.isChecked()? InfoText.getText().toString() : TranText.getText().toString(),
                                    Equival.isChecked()? EquWord.isChecked()? WordText.getText().toString() :
                                            TranText.getText().toString() :
                                            new TextManager(getContext()).format(InfoText.getText().toString(), false).toString(),
                                    Equival.isChecked()? EquWord.isChecked()? 1 : 2 : 0);
    }

    /*pre: cert*/
    /*post: s'ha inserit o actualitzat l'item definit.*/
    private void insertOrUpdateItem(){
        Toaster toaster = new Toaster(getContext());
        DBManager dbManager = new DBManager(getContext());
        if((Afegir.getText() == getText(R.string.actualitza) && updateItem(dbManager)) ||
           (Afegir.getText() == getText(R.string.afegir) && insertItem(dbManager))){

            ((MainActivity) getActivity()).swapFrameLayoutVisibility(true);
            ((MainActivity) getActivity()).refreshListView();
            toaster.standardToast(getText(R.string.commit_insert).toString());
            reset(true);
        }

        else toaster.standardToast(getText(R.string.reboke_action).toString());
    }

    /*pre: cert*/
    /*post: si inhib és false, el botó és de color blau; altrament de color gris.*/
    private void setCommitButtonColor(boolean inhib){
        Afegir.setTextColor(ResourcesCompat.getColor(getResources(),
                            inhib? R.color.solidGray : R.color.aquaBlue, null));
    }

    /*pre: cert*/
    /*post: s'han afegit les categories de la definició, i habilitat el botó d'agregació si,
     *      i només si, la definició present existeix a la base de dades.*/
    private void syncronizeForEquivalence(){
        setCommitButtonColor(Equival.isChecked());

        DBManager dbManager = new DBManager(getContext());
        if(Equival.isChecked() &&
           !(WordText.getText().toString().isEmpty() || TranText.getText().toString().isEmpty()) &&
           dbManager.checkIfExists(WordText.getText().toString(), TranText.getText().toString())){

            enableArrelButton(Afegir.getText() == getText(R.string.actualitza));
            String where = dbManager.getKey()[0].concat(" LIKE ? AND ").concat(dbManager.getKey()[1]).concat(" LIKE ? ");
            String[] args = new String[]{WordText.getText().toString(), TranText.getText().toString()};
            ArrayList<ContentValues> tupla = dbManager.getAllTuples(where, args, null, null, null, 0);

            String[] column = dbManager.getMainColumns();
            Categories.setText(tupla.get(0).getAsString(column[0]));

            setCommitButtonColor(returnRequiredData(null) != null &&
                                (returnRequiredData(null).first.equals(WordText.getText().toString()) &&
                                 returnRequiredData(null).second.equals(TranText.getText().toString()) ||
                                 dbManager.checkWay(returnRequiredData(null), new Pair<>(WordText.getText().toString(), TranText.getText().toString()))));

        }
        else if(Equival.isChecked()) Categories.setText(R.string.tipus);
    }

    /*pre: cert*/
    /*post: en cas de la definició agregada sigui una equivalencia, s'accedeix a la definició arrel.*/
    private void gotoRoot(){
        if(Arrel.getCurrentTextColor() == ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)){
            DBManager dbManager = new DBManager(getContext());
            if(dbManager.checkIfExists(WordText.getText().toString(), TranText.getText().toString())) {
                String[] key = dbManager.getKey();
                ContentValues root = dbManager.getRoot(WordText.getText().toString(), TranText.getText().toString());
                setRequiredData(new Pair<>(root.getAsString(key[0]), root.getAsString(key[1])),null);

                WordText.setText(root.getAsString(dbManager.getMainColumns()[1]));
                TranText.setText(root.getAsString(dbManager.getMainColumns()[2]));
                InfoText.setText(root.getAsString(dbManager.getMainColumns()[3]));

                //Com és una arrel, podem deduir que no es equivalent a res: és pur
                EquSelectors.setVisibility(View.GONE);
                Equival.setChecked(false); EquWord.setChecked(false); EquTran.setChecked(false);
                Arrel.setTextColor(ResourcesCompat.getColor(getResources(), R.color.transparent, null));

                //De vegades, al accedir a la root el botó d'actualitzar no és posava blau;
                //així que resincronitzem:
                syncronizeForEquivalence();
            }

            else new Toaster(getContext()).standardToast(getText(R.string.err_root_not_found).toString());
        }
    }

    /*pre: wordText no és null.*/
    /*post: WordText ara té el valor de wordText.*/
    public void setWordText(String wordText){
        WordText.setText(wordText);
    }

    /*pre: cert*/
    /*post: s'han restaurat tots els elements del fragment al seu valor per defecte.*/
    public void reset(boolean purge){
        EquSelectors.setVisibility(View.GONE);
        Equival.setChecked(false); EquWord.setChecked(false); EquTran.setChecked(false);
        WordText.setText(""); TranText.setText(""); InfoText.setText(""); Categories.setText("");
        WordText.setHint(R.string.hint_for_word);
        TranText.setHint(R.string.hint_for_tran);
        InfoText.setHint(R.string.hint_for_info);
        Afegir.setText(R.string.afegir);

        purgeItemSelected(purge);
    }
}
