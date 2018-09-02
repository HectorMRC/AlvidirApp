package com.carnice.morales.hector.alvidiriel.Utils;

public class CustomObject {

    private String Cat;
    private String Alv;
    private int Flag;

    public CustomObject(String Cat, String Alv, int Flag){
        this.Cat = Cat;
        this.Alv = Alv;
        this.Flag = Flag;
    }

    public String getWord(){
        return Cat;
    }

    public String getTran(){
        return Alv;
    }

    public int getFlag() { return Flag; }
}
