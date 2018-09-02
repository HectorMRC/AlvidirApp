package com.carnice.morales.hector.alvidiriel.Utils;

public class Statements {
    public String selection;
    public String[][] args;
    public String sortBy;

    /*pre: cert*/
    /*post: el parametres selection i args[] passen a ser null*/
    public void resetStatus(){
        selection = null;
        args = null;
    }

    /*pre: cert*/
    /*post: s'ha retornat cert si i només si selection i args son null*/
    public boolean isCurrentStateNull(){
        return selection == null && args == null;
    }
}
