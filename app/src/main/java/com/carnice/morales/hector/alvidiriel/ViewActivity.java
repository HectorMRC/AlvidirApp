package com.carnice.morales.hector.alvidiriel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener,
                                                               View.OnLongClickListener{

    //DECLARACIÓ D'OBJECTES:
    ImageButton TurnBack, ShowOptions;

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
    }
}
