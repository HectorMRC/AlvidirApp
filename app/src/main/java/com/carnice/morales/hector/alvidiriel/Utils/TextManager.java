package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.carnice.morales.hector.alvidiriel.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TextManager {

    private Context context;
    private static final String symbol = "\u2B24";

    public TextManager(Context context){
        this.context = context;
    }
    public TextManager(){}

    /*El resultat es el text en font HTML*/
    public Spanned tag(String html){
        html = "<i>".concat(html).concat("</i>"); //Text en cursiva
        html = "<font color = #76488c >".concat(html).concat("</font>"); //Text en color
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }

    /*Canvia tots els caracters d'original per aquells que la
    * typografia Alvidir reconeix.*/
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

    /*Transforma de caracter especial a l'etiqueta corresponent;
    * o viceversa.*/
    public Editable format(String original, boolean editable){
        if(original.isEmpty()) return new SpannableStringBuilder(original);
        String toReplace = editable? symbol : context.getResources().getString(R.string.divider);
        String replacer = editable? context.getResources().getString(R.string.divider) : symbol;

        /*Transformació de l'etiqueta, al simbol especial corresponent: */
        if(!editable) return new SpannableStringBuilder(original.replace(toReplace, replacer));

        /*Tranformació del simbol, a l'etiqueta corresponent.*/
        Editable sequence = new SpannableStringBuilder(original);
        for(int st, en = 0; (st = original.indexOf(toReplace, en)) != -1; en = st+1) {
            sequence.replace(st, st + 1, tag(replacer)); //Les etiquetes han de contenir color.
            original = sequence.toString();
        }

        return sequence;
    }

    /*Retorna un array amb tants textos com parrafs separats pel
    * symbol especial hi hagi.*/
    public ArrayList<String> slicer(String content){
        ArrayList<String> slices = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(content, symbol);

        while(tokenizer.hasMoreTokens())
            slices.add(clean(tokenizer.nextToken()));

        return slices;
    }

    /*Elimina els caracters presents a chars que es troben a l'inici i
    * final d'original.*/
    private String clearUselessChars(String original, String chars){
        if(original.isEmpty()) return original;
        //Per als caracters inicials:
        if(chars.contains(String.valueOf(original.charAt(0))))
            original = clearUselessChars(original.substring(1), chars);
        //Per als caracters finals:
        else if(chars.contains(String.valueOf(original.charAt(original.length()-1))))
            original = clearUselessChars(original.substring(0, original.length()-1), chars);

        return original;
    }

    public String clean(String original){
        return clearUselessChars(original, " \n");
    }

    /*Retorna un valor o altre segons el text introduit.
    * No te res a veure amb el Integer.valueOf(String).*/
    private int parseInt(String string, int power){
        if(string.isEmpty()) return 0;
        int currentIndex = (string.charAt(string.length()-1) * 10^power);
        return string.length() == 1?
               currentIndex : parseInt(string.substring(0, string.length()-2), ++power) + currentIndex;
    }

    public int parseInt(String string){
        return parseInt(string, 0);
    }

    public int getPosition(String origin, String subject, int rep){
        int position = -1;
        for(int itr = 0; itr < rep && position < origin.length(); itr++)
            position = origin.indexOf(subject, position+1);

        return position;
    }
}
