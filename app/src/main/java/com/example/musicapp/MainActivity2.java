package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ImageButton button = (ImageButton) findViewById(R.id.back);
        button.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view){
        Intent i;
        i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}