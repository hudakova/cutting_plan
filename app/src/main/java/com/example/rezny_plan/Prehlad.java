package com.example.rezny_plan;

import android.app.LauncherActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Prehlad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//priradenie stylu ku kodu
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prehlad);
        Bundle bundle = getIntent().getExtras();
//hodnoty v boxoch hned hore
        TextView maxDlzka = findViewById(R.id.zadana_dlzka_prehlad);
        maxDlzka.setText(String.valueOf(bundle.getInt("maxDlzka")));

        TextView celkovaDlzka = findViewById(R.id.vypocet_dlzok_main_prehlad);
        celkovaDlzka.setText(String.valueOf(bundle.getInt("celkovaDlzka")));

        TextView maxDlzka3 = findViewById(R.id.maxdlzka_desired_prehlad);
        maxDlzka3.setText(String.valueOf(bundle.getInt("maxDlzka3")));

        TextView celkovaDlzka3 = findViewById(R.id.vypocet_dlzok_desired_prehlad);
        celkovaDlzka3.setText(String.valueOf(bundle.getInt("celkovaDlzka3")));
//sposoby rezania
        List<Item> items = new ArrayList<>();
        ArrayList<Sada> sady = (ArrayList<Sada>) bundle.get("items");
        for (Sada sada : sady) {
            items.add(new Item(sada.getDlzka(), sada.getPocet()));
        }
        CuttingStock cuttingStock = new CuttingStock(bundle.getInt("maxDlzka"), items);
        cuttingStock.calculatePlan();

        List<String> plan = new ArrayList<>();
        int pocetRezov = 0;
        for (Cutting cutting : cuttingStock.cuttingPlan) {
            StringBuilder rez = new StringBuilder();
            rez.append("Rez ").append(++pocetRezov).append(". Odpad ").append(cutting.waste);
            for (Map.Entry<Integer, Integer> e : cutting.countByLength.entrySet())
                rez.append('\n').append(e.getKey()).append(" * ").append(e.getValue());
            plan.add(rez.toString());
        }

        //TextView sposobyRezania = findViewById(R.id.sposoby_rezania);
        //sposobyRezania.setText(plan.toString());

        ListAdapter planAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, plan);

        ListView sposoby = findViewById(R.id.sprez);
        sposoby.setAdapter(planAdapter);

        double[][] matica = new double[sady.size() + 1][pocetRezov + sady.size() + 1];

        //ucelova funkcia
        matica[0][0] = 0;
        StringBuilder ucelova = new StringBuilder();
        int j = 0;
        ucelova.append("Účelová funkcia:").append('\n');
        for (Cutting c : cuttingStock.cuttingPlan) {
            ucelova.append(" + ").append(c.waste).append("x").append(++j);

            matica[0][j] = c.waste;
            matica[0][j + sady.size()] = 0;
        }
        ucelova.append(" --> min");
        TextView model = findViewById(R.id.model);
        model.setText(ucelova.toString());

//podmienky
        StringBuilder podm = new StringBuilder();
        int indexSady = 0;
        podm.append("Podmienky:").append('\n');
        for (Sada sada : sady) {
            matica[indexSady + 1][0] = sada.getPocet();
            int indexRezu = 0;
            for (Cutting c : cuttingStock.cuttingPlan) {
                Integer count = c.countByLength.get(sada.getDlzka());
                if (count == null) count = 0;
                if (indexRezu > 0)
                    podm.append(" + ");
                matica[indexSady + 1][indexRezu + 1] = count;
                podm.append(count).append('x').append(++indexRezu);
            }
            matica[indexSady + 1][pocetRezov + 1 + indexSady] = -1;
            indexSady++;
            podm.append(" >= ").append(sada.getPocet()).append('\n');
        }
        int k = 0;
        TextView podmienky = findViewById(R.id.podmienky);
        podmienky.setText(podm.toString());

        vypisMatice(matica, pocetRezov, sady.size());

        //vynasobenie (-1)

        for (int r = 1; r < matica.length; r++) {
            for (int s = 0; s < matica[r].length; s++)
                matica[r][s] *= -1;
        }
        vypisMatice(matica, pocetRezov, sady.size());

        Set<Integer> pouziteRiadkyPivotov = new HashSet<>();
        Set<Integer> pouziteStlpcePivotov = new HashSet<>();
////////
//CYKLUS--------------------------------------------------------------------------------------------
        for (int simplexKrok = 0; simplexKrok < (pocetRezov); simplexKrok++) {
            // vyber min hodnoty v ucelovom riadku
            int riadokPivota = -1;
            double min = Integer.MAX_VALUE;
            for (int r = 1; r < (sady.size()); r++) {
                if (!pouziteRiadkyPivotov.contains(r)) {
                    if (matica[0][r] < min) {
                        min = matica[r][0];
                        riadokPivota = r;
                    }
                }
            }
            if (riadokPivota == -1)
                break;

            pouziteRiadkyPivotov.add(riadokPivota);

// delenie riadku x0 s pivotacnym riadkom indexPivota
            double[] podiely = new double[matica.length - 1]; //od x1 po posledne s
            double minDelenie = Integer.MAX_VALUE;
            int stlpecPivota = -1;
            for (int s = 1; s < matica.length; s++) {
                if (!pouziteStlpcePivotov.contains(s)) {
                    double pivotRiadokHodnota = matica[riadokPivota][s];
                    if (pivotRiadokHodnota != 0)
                        podiely[s - 1] = matica[0][s] / pivotRiadokHodnota;
                    if ((podiely[s - 1] < minDelenie) && (podiely[s - 1] != 0)) {
                        minDelenie = podiely[s - 1];
                        stlpecPivota = s;
                    }
                }
            }
            if (stlpecPivota != -1)
                pouziteStlpcePivotov.add(stlpecPivota);

            StringBuilder pb = new StringBuilder();
            pb.append("podiely, stlpec=" + stlpecPivota + "\n");
            for (int i = 0; i < podiely.length; i++) {
                if (i > 0) pb.append(", ");
                pb.append(podiely[i]);
            }
            System.out.println(pb.toString());
            System.out.println("minimalne delenie=" + minDelenie);

            if (stlpecPivota != -1) {
                double pivot = matica[riadokPivota][stlpecPivota];
                System.out.println("pivot=" + pivot);
////////////
                double matica1[][] = new double[matica.length][matica[0].length];

                for (int s = 0; s < (pocetRezov + sady.size() + 1); s++)
                    matica1[riadokPivota][s] = matica[riadokPivota][s] / pivot;
                for (int r = 0; r < matica1.length; r++) {
                    if (r != riadokPivota) {
                        for (int s = 0; s < matica1[r].length; s++) {
                            matica1[r][s] = matica[r][s] - matica[r][stlpecPivota] * matica1[riadokPivota][s];
                        }
                    }
                }
                vypisMatice(matica1, pocetRezov, sady.size());

                matica = matica1;
            }
        }
///////////////////////////////////////////////////////////////////////////////////////

        pouziteRiadkyPivotov.clear();
        pouziteStlpcePivotov.clear();

        for (int simplexKrok = 0; simplexKrok < (sady.size()); simplexKrok++) {
            // vyber min hodnoty v ucelovom riadku
            int stlpecPivota = -1;
            double min = Integer.MAX_VALUE;
            for (int s = 1; s < (pocetRezov); s++) {
                if (!pouziteStlpcePivotov.contains(s)) {
                    if (matica[0][s] < min) {
                        min = matica[0][s];
                        stlpecPivota = s;
                    }
                }
            }
            if (stlpecPivota == -1)
                break;

            pouziteStlpcePivotov.add(stlpecPivota);

// delenie riadku x0 s pivotacnym riadkom indexPivota
            double[] podiely = new double[matica.length - 1]; //od x1 po posledne s
            double minDelenie = Integer.MAX_VALUE;
            int riadokPivota = -1;
            for (int r = 1; r < matica.length; r++) {
                if (!pouziteRiadkyPivotov.contains(r)) {
                    double pivotStlpecHodnota = matica[stlpecPivota][r];
                    if (pivotStlpecHodnota != 0)
                        podiely[r - 1] = matica[r][0] / pivotStlpecHodnota;
                    if ((podiely[r - 1] < minDelenie) && (podiely[r - 1] != 0)) {
                        minDelenie = podiely[r - 1];
                        riadokPivota = r;
                    }
                }
            }
            if (riadokPivota != -1)
                pouziteRiadkyPivotov.add(riadokPivota);

            StringBuilder pb = new StringBuilder();
            pb.append("podiely, riadok=" + riadokPivota + "\n");
            for (int i = 0; i < podiely.length; i++) {
                if (i > 0) pb.append(", ");
                pb.append(podiely[i]);
            }
            System.out.println(pb.toString());
            System.out.println("minimalne delenie=" + minDelenie);

            if (stlpecPivota != -1) {
                double pivot = matica[riadokPivota][stlpecPivota];
                System.out.println("pivot=" + pivot);
                double matica1[][] = new double[matica.length][matica[0].length];

                for (int s = 0; s < (pocetRezov + sady.size() + 1); s++)
                    matica1[riadokPivota][s] = matica[riadokPivota][s] / pivot;
                for (int r = 0; r < matica1.length; r++) {
                    if (r != riadokPivota) {
                        for (int s = 0; s < matica1[r].length; s++) {
                            matica1[r][s] = matica[r][s] - matica[r][stlpecPivota] * matica1[riadokPivota][s];
                        }
                    }
                }
                vypisMatice(matica1, pocetRezov, sady.size());

                matica = matica1;
            }
        }
    }


    //vypisanie matice
    private void vypisMatice(double[][] matica, int pocetRezov, int pocetSad) {
//hlavicka (x1, ..., sn)
        StringBuilder hlavicka = new StringBuilder();
        hlavicka.append("matica").append('\n');
        hlavicka.append("x0");
        for (int h = 0; h < pocetRezov; h++)
            hlavicka.append('\t').append('\t').append('\t').append('x').append(h + 1);
        for (int h = 0; h < pocetSad; h++)
            hlavicka.append('\t').append('\t').append('\t').append('s').append(h + 1);
        System.out.println(hlavicka.toString());

        for (int r = 0; r < matica.length; r++) {
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < matica[r].length; s++) {
                if (s > 0) sb.append('\t').append('\t').append('\t');
                sb.append(ds(matica[r][s]));
            }
            System.out.println(sb.toString());
        }
    }

    private String ds(double d) {
        if (d == (int) d) return String.valueOf((int) d);
        return String.valueOf(d);
    }
}