package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.ContentValues;
import android.support.v4.util.Pair;
import java.util.HashSet;

public class ContentChecker {

    private HashSet<Pair<String, String>> Content;
    private String[] Key;

    public ContentChecker(String[] Key){
        this.Content = new HashSet<>();
        this.Key = Key;
    }

    public boolean check(ContentValues values){
        if(!Content.contains(new Pair<>(values.getAsString(Key[0]), values.getAsString(Key[1]))))
            Content.add(new Pair<>(values.getAsString(Key[0]), values.getAsString(Key[1])));
        else return false;

        return true;
    }
}
