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

import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter implements View.OnClickListener {

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<String> Content;

    public SliderAdapter(Context context, String content){
        this.context = context;

        Content = new ArrayList<>();
        Content = new TextManager(context).slicer(content);
        Content.add(""); //pàgina fantasma necessaria.
    }

    @Override
    public int getCount() {
        return Content.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ScrollView) object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_adapter, container, false);

        LinearLayout layout = view.findViewById(R.id.onItem_noInfo);
        TextView title = view.findViewById(R.id.onItem_title);
        TextView  content = view.findViewById(R.id.onItem_content);
        TextView lastPage = view.findViewById(R.id.warning);
        Button AddPageButton = view.findViewById(R.id.afegir_from_empty);
        AddPageButton.setOnClickListener(this);

        layout.setVisibility(position == Content.size()-1? View.VISIBLE : View.GONE);
        content.setVisibility(position == Content.size()-1? View.GONE : View.VISIBLE);

        lastPage.setText(context.getString(Content.size() > 1? R.string.lastPag_warn : R.string.noInfo_warn));
        title.setText(position == 0 && layout.getVisibility() == View.GONE?
                      context.getString(R.string.hint_for_info) : "");
        content.setText(Content.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ScrollView) object);
    }

    @Override
    public void onClick(View v) {
    }
}
