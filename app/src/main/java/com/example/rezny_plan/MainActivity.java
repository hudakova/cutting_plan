package com.example.rezny_plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
//definovanie
    private ArrayList<Sada> items;
    private ArrayAdapter<Sada> itemsAdapter;
//prepojenie s XML
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//prepojenie listview a tlacidla pridat(aby pridalo item po stlaceni)
        ListView listView = findViewById(R.id.list_view);

        Button pridat = findViewById(R.id.button_pridat_main);
        pridat.setOnClickListener(view -> addItem(view));
//tlacidlo potvrdit urobi vypocet a otvori Desired screen
        Button vypocet = findViewById(R.id.button_potvrdit_main);
        vypocet.setOnClickListener(v -> openDesired());
//odstranenie polozky
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);//tu je item xml style
        listView.setAdapter(itemsAdapter);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Context context = getApplicationContext();
            Toast.makeText(context, "Položka odstránená", Toast.LENGTH_LONG).show();

            items.remove(position);
            itemsAdapter.notifyDataSetChanged();
            return true;
        });
    }

    private void addItem(View view) {
        EditText kus = findViewById(R.id.pocet_kusov_main);
        EditText meter = findViewById(R.id.dlzka_kusu_main);

        if (kus.getText().toString().trim().isEmpty() || meter.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Prosím, zadajte hodnoty", Toast.LENGTH_LONG).show();
        } else {
            try {
                int kusy = Integer.parseInt(kus.getText().toString().trim());
                int dlzka = Integer.parseInt(meter.getText().toString().trim());
                if (kusy <= 0 || dlzka <= 0)
                    openDialog();
                else
                    itemsAdapter.add(new Sada(kusy, dlzka));
            } catch (NumberFormatException nfe) {
                openDialog();
            }
        }
    }


    private void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void openDesired() {
        int maxDlzka = 0;
        int celkovaDlzka = 0;
        for (Sada sada : items) {
            if (sada.getDlzka() > maxDlzka)
                maxDlzka = sada.getDlzka();
            celkovaDlzka += sada.getPocet() * sada.getDlzka();
        }

        Intent intent = new Intent(this, Desired.class);
        intent.putExtra("maxDlzka", maxDlzka);
        intent.putExtra("celkovaDlzka", celkovaDlzka);
        startActivity(intent);
    }
}