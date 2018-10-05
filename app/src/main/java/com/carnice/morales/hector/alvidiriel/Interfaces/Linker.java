package com.carnice.morales.hector.alvidiriel.Interfaces;

public interface Linker<T,U> {
    T returnRequiredData(U requestCode);
    void setRequiredData(T content, U requestCode);

    void setLinker(Linker linker, U requestCode);
}
