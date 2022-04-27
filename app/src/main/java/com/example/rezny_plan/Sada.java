package com.example.rezny_plan;

import java.io.Serializable;

public class Sada implements Serializable {

    private final int pocet;
    private final int dlzka;

    public Sada(int pocet, int dlzka) {
        this.pocet = pocet;
        this.dlzka = dlzka;
    }

    public int getPocet() {
        return pocet;
    }

    public int getDlzka() {
        return dlzka;
    }

    @Override
    public String toString() {
        return "" + pocet + " ks " + dlzka + "m";
    }
}
