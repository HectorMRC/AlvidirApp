package com.carnice.morales.hector.alvidiriel.Utils;

import java.util.ArrayList;

public class Transcriber {

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

    public ArrayList<String> slicer(String string){
        return null;
    }
}
