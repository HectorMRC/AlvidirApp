package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.Interfaces.Observer;
import com.carnice.morales.hector.alvidiriel.Interfaces.Subject;
import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter implements Subject{

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<String> Content;

    public SliderAdapter(Context context, String content){
        this.context = context;

        Content = new ArrayList<>();
        Content = new TextManager(context).slicer(content);
        //Com a minim hi ha d'haver una pàgina per poder informar de la carencia d'informació.
        if(Content.isEmpty()) Content.add("");
    }

    @Override
    public int getCount() {
        return Content.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_adapter, container, false);

        TextView title = view.findViewById(R.id.onItem_title);
        TextView  content = view.findViewById(R.id.onItem_content);

        if(Content.get(position).isEmpty()) title.setText(context.getString(R.string.hint_for_noinfo));
        //En cas de tenir un titol definit per @ al inici de pàgina:
        else if(Content.get(position).charAt(0) == '@' && Content.get(position).length() > 1){
            title.setText(Content.get(position).substring(1, Content.get(position).indexOf('\n', 0) > 0?
                          Content.get(position).indexOf('\n', 0) : Content.get(position).length()));
            content.setText(new TextManager().clean(Content.get(position).substring(title.getText().length()+1)));
        }
        else content.setText(Content.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ScrollView) object);
    }

    @Override
    public void setObserver(Observer observer) {

    }

    @Override
    public void notifyObservers(String action) {

    }
}
