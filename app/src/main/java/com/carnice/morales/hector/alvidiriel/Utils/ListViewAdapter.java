package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.carnice.morales.hector.alvidiriel.Fragments.OptionsFragment;
import com.carnice.morales.hector.alvidiriel.Interfaces.Linker;
import com.carnice.morales.hector.alvidiriel.Interfaces.Observer;
import com.carnice.morales.hector.alvidiriel.Interfaces.Subject;
import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter implements Subject,
                                                          Linker<String, String> {

    private LayoutInflater inflater;
    private ArrayList<ListViewObject> AllItems;
    private Linker<String, String> Linked;
    private boolean withFlag, showType;

    private static ArrayList<Observer> Observers;
    private static ListViewAdapter CurrentInstance;
    private static OptionsFragment OptControler;

    private Pair<String, String> SelectedItem;
    private boolean showOptions = false;

    public ListViewAdapter(){
        Observers = new ArrayList<>();
    }

    public ListViewAdapter(Context context, ArrayList<ListViewObject> AllItems, boolean withFlag){
        inflater = LayoutInflater.from(context);

        Observers = new ArrayList<>();
        OptControler = new OptionsFragment();

        this.AllItems = AllItems;
        this.withFlag = withFlag;

        CurrentInstance = this;
        OptControler.callFromCustomAdapter();
    }

    @Override
    public int getCount() {
        return AllItems.size();
    }

    @Override
    public ListViewObject getItem(int i) {
        return AllItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.list_view_adapter, viewGroup, false);

            holder = new ListViewHolder();
            holder.thisWord = view.findViewById(R.id.item_word);
            holder.thisTran = view.findViewById(R.id.item_tran);
            holder.thisFont = view.findViewById(R.id.item_font);
            holder.thisFlag = view.findViewById(R.id.item_flag);

            holder.background = view.findViewById(R.id.item_background);
            holder.frameLayout = view.findViewById(R.id.item_options);
            view.setTag(holder);

        } else holder = (ListViewHolder) view.getTag();

        holder.thisWord.setText(getItem(i).getWord());
        holder.thisFont.setText(getItem(i).getTran());
        holder.thisTran.setText(new TextManager().transcribe(getItem(i).getTran()));

        holder.thisFlag.setVisibility(withFlag || show(holder, SelectedItem) ? View.VISIBLE : View.GONE);
        holder.thisFlag.setBackgroundColor(getItem(i).getFlag());

        holder.background.setBackgroundColor(showOptions && show(holder, SelectedItem)? 0x55000000 : 0x00000000);
        holder.frameLayout.setVisibility(showOptions && show(holder, SelectedItem)? View.VISIBLE : View.GONE);
        if(showOptions && show(holder, SelectedItem))
            OptControler.setFocus(view.findViewById(R.id.item_options));

        return view;
    }

    @Override
    public void setObserver(Observer observer) {
        if(!Observers.contains(observer)) Observers.add(observer);
    }

    @Override
    public void notifyObservers(String action) {
        for(Observer observer: Observers)
            observer.onActionListener(SelectedItem, action);

        showOptions = false;
        this.notifyDataSetChanged();
    }

    @Override
    public String returnRequiredData(String requestCode) {
        return null;
    }

    @Override
    public void setRequiredData(String content, String requestCode) {
        this.notifyObservers(content);
    }

    @Override
    public void setLinker(Linker linker, String requestCode) {
        this.Linked = linker;
    }

    private boolean show(ListViewHolder holder, Pair<String, String> item){
        if(item == null) return false;
        return item.first.equals(holder.thisWord.getText().toString()) &&
                item.second.equals(holder.thisFont.getText().toString()) &&
                showOptions;
    }

    private void goToAction(String action){
        showOptions = false;
        notifyObservers(action);
        notifyDataSetChanged();
    }

    //FUNCIONS PÚBLIQUES:
    public void replaceAll(ArrayList<ListViewObject> AllItems, boolean withFlag){
        this.AllItems.clear();
        this.AllItems.addAll(AllItems);
        this.withFlag = withFlag;
    }

    public void setSelectedItem(Pair<String, String> SelectedItem, String requestCode){
        OptControler.setRigthButtonSelected(!(showOptions = true));
        this.SelectedItem = SelectedItem;
        this.showOptions = requestCode != null;
        notifyDataSetChanged();
    }

    public Pair<String, String> getSelectedItem(){
        return SelectedItem;
    }

    public static ListViewAdapter getCurrentInstance(){
        if(CurrentInstance == null) CurrentInstance = new ListViewAdapter();
        return CurrentInstance;
    }

    public boolean getDeleteButtonSelected(){
        return OptControler.getRigthButtonSelected();
    }
}
