package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Mensaje extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);
    }
    public void avanzar(View view){
        Intent i = new Intent(Mensaje.this, Log_in.class);
        startActivity(i);
        finish();
    }
}