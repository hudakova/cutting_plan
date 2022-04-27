package com.example.rezny_plan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity  {
    Handler h = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Welcome.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }

}

