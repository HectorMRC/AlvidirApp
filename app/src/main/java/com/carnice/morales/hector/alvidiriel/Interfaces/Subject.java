package com.carnice.morales.hector.alvidiriel.Interfaces;

public interface Subject {
    void setObserver(Observer observer);
    void notifyObservers(String action);
}
