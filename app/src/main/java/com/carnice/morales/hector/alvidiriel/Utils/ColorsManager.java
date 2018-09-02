package com.carnice.morales.hector.alvidiriel.Utils;

import java.util.Random;

public class ColorsManager {

    //DECLARACIÓ D'OBJECTES:
    private int module = 0;
    private int[] ColorsArray = {0xAAADFF2F, 0xAA00FA9A, 0xAA66CDAA, 0xAA20B2AA,
                                 0xAA8FBC8F, 0xAA00FFFF, 0xAA7FFFD4, 0xAA4682B4,
                                 0xAAB0C4DE, 0xAA7B68EE, 0xAA800080, 0xAA9370DB,
                                 0xAAFF00FF, 0xAAE6E6FA, 0xAAFFFF00, 0xAAFFA500,
                                 0xAAFF4599, 0xAAFFA07A, 0xAADB7093, 0xAAC71585,
                                 0xAAFF1493, 0xAAFFB6C1, 0xAADC143C, 0xAACD5C5C};

    /*pre: cert*/
    /*post: s'ha retornat un color segons la posició marcada pel modul.*/
    public int getColor(){
        return ColorsArray[module++%ColorsArray.length];
    }

    /*pre: cert*/
    /*post: s'ha retornat el color situal a index-1.*/
    public int getColor(int index){
        return ColorsArray[(index-1)%ColorsArray.length];
    }

    /*pre: cert*/
    /*post: s'ha retornat un color aleatori.*/
    public int randColor(){
        Random rand = new Random();
        return ColorsArray[rand.nextInt(ColorsArray.length)];
    }
}
