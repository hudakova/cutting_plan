package com.example.rezny_plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Desired extends AppCompatActivity {

    private ArrayList<Sada> items;
    private ArrayAdapter<Sada> itemsAdapter;
//priradenia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desired);

        ListView listView = findViewById(R.id.list_view_desired);

        Button pridat = findViewById(R.id.button_pridat_desired);
        pridat.setOnClickListener(view -> addItem(view));

        Bundle bundle = getIntent().getExtras();
//max dlzka v pravo hore
        TextView maxDlzkaTv = findViewById(R.id.max_zadana_dlzka);
        maxDlzkaTv.setText(String.valueOf(bundle.getInt("maxDlzka")));
//celkova dlzka hned pod tym
        TextView celkovaDlzkaTv = findViewById(R.id.sucet_zadanych_dlzok);
        celkovaDlzkaTv.setText(String.valueOf(bundle.getInt("celkovaDlzka")));
//pocitanie max dlzky aj celkovej po potvrdeni
        Button vypocet = findViewById(R.id.button_potvrdit_desired);
        vypocet.setOnClickListener(view -> {
            int maxDlzka = 0;
            int celkovaDlzka = 0;
            for (Sada sada : items) {
                if (sada.getDlzka() > maxDlzka)
                    maxDlzka = sada.getDlzka();
                celkovaDlzka += sada.getPocet() * sada.getDlzka();
            }
//tu sa to ulozi do druhej strany (do prehladu)
            Intent intent = new Intent(this, Prehlad.class);
            intent.putExtra("maxDlzka", bundle.getInt("maxDlzka"));
            intent.putExtra("celkovaDlzka", bundle.getInt("celkovaDlzka"));
            intent.putExtra("maxDlzka3", maxDlzka);
            intent.putExtra("celkovaDlzka3", celkovaDlzka);
            intent.putExtra("items", items);
            startActivity(intent);

        });

        //ArrayList<Sada> items = (ArrayList<Sada>) bundle.get("items");
        //itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        //listView.setAdapter(itemsAdapter);
//zadavanie poctov a dlzok
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Context context = getApplicationContext();
            Toast.makeText(context, "Položka odstránená", Toast.LENGTH_LONG).show();

            items.remove(position);
            itemsAdapter.notifyDataSetChanged();
            return true;
        });
    }
//pridanie itemu a podmienok
    private void addItem(View view) {
        EditText kus = findViewById(R.id.pocet_kusov_desired);
        EditText meter = findViewById(R.id.dlzka_kusu_desired);

        if (kus.getText().toString().trim().isEmpty() || meter.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Prosím, zadajte hodnoty", Toast.LENGTH_LONG).show();
        } else {
            try {
                int kusy = Integer.parseInt(kus.getText().toString().trim());
                int dlzka = Integer.parseInt(meter.getText().toString().trim());
                if (kusy <= 0 || dlzka <= 0) {
                    openDialog();
                } else {
                    Bundle bundle = getIntent().getExtras();
                    int maxDlzka1 = bundle.getInt("maxDlzka");
                    int celkovaDlzka1 = bundle.getInt("celkovaDlzka");
                    int celkovaDlzka = 0;
                    for (Sada sada : items) {
                        celkovaDlzka += sada.getPocet() * sada.getDlzka();
                    }
                    if (celkovaDlzka + (kusy * dlzka) > celkovaDlzka1)
                        open2Dialog();
                    else if (dlzka > maxDlzka1)
                        open2Dialog();
                    else
                        itemsAdapter.add(new Sada(kusy, dlzka));
                }
            } catch (NumberFormatException nfe) {
                openDialog();
            }
        }
    }
//dialogy
    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "materialy dialog");
    }
    private void open2Dialog() {
        MaterialyDialog materialyDialog = new MaterialyDialog();
        materialyDialog.show(getSupportFragmentManager(), "materialy dialog");
    }
}