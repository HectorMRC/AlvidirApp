package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<String> Content;

    public SliderAdapter(Context context, String content){
        this.context = context;

        Content = new ArrayList<>();
        Content.add(content);
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

        TextView  content = view.findViewById(R.id.onItem_content);
        content.setText(Content.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
