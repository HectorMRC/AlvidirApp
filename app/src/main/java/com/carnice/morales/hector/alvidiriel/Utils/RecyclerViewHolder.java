package com.carnice.morales.hector.alvidiriel.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carnice.morales.hector.alvidiriel.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView Title, MainContent;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        Title = itemView.findViewById(R.id.onItem_title);
        MainContent = itemView.findViewById(R.id.onItem_content);
    }
}
