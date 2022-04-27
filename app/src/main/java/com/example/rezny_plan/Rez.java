package com.example.rezny_plan;

import java.util.HashMap;
import java.util.Map;

public class Rez {

    private final int dlzkaMaterialu;
    private final Map<Integer, Integer> pocetPodlaDlzky = new HashMap<>();

    public Rez(int dlzkaMaterialu, int... dlzkyKusov) {
        this.dlzkaMaterialu = dlzkaMaterialu;
        for (int kus : dlzkyKusov) {
            int pocet = 0;
            if (pocetPodlaDlzky.containsKey(kus))
                pocet = pocetPodlaDlzky.get(kus);
            pocetPodlaDlzky.put(kus, pocet + 1);
        }
    }

    public int pocetKusovDanejDlzky(int dlzkaKusu) {
        if (pocetPodlaDlzky.containsKey(dlzkaKusu))
            return pocetPodlaDlzky.get(dlzkaKusu);
        return 0;
    }
}

