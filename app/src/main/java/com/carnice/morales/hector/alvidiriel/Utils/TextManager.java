package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.content.res.Resources;

import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TextManager {

    private Context context;
    public TextManager(Context context){
        this.context = context;
    }
    public TextManager(){}

    public String transcribe(String original){
        original = original.replaceAll("à", "a");
        original = original.replaceAll("á", "a");
        original = original.replaceAll("ä", "a");

        original = original.replaceAll("è", "e");
        original = original.replaceAll("é", "e");
        original = original.replaceAll("ë", "e");

        original = original.replaceAll("y", "i");
        original = original.replaceAll("í", "i");
        original = original.replaceAll("ï", "i");

        original = original.replaceAll("ò", "o");
        original = original.replaceAll("ó", "o");
        original = original.replaceAll("ö", "o");

        original = original.replaceAll("w", "u");
        original = original.replaceAll("ú", "u");
        original = original.replaceAll("ü", "u");

        original = original.replaceAll("v", "b");
        original = original.replaceAll("p", "b");

        original = original.replaceAll("ae", "A");
        original = original.replaceAll("ll", "L");
        original = original.replaceAll("rr", "R");

        return original;
    }

    public String format(String original, boolean editable){
        String toReplace = editable? "\u9210" : context.getString(R.string.divider);
        String replacer = editable? context.getString(R.string.divider) : "\u9210";
        return original.replaceAll(toReplace, replacer);
    }

    public ArrayList<String> slicer(String content){
        ArrayList<String> slices = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(content, "\u9210");

        while(tokenizer.hasMoreTokens()) slices.add(tokenizer.nextToken());

        return slices;
    }

    private int parseInt(String string, int power){
        if(string.isEmpty()) return 0;
        int currentIndex = (string.charAt(string.length()-1) * 10^power);
        return string.length() == 1?
               currentIndex : parseInt(string.substring(0, string.length()-2), ++power) + currentIndex;
    }

    public int parseInt(String string){
        return parseInt(string, 0);
    }
}
