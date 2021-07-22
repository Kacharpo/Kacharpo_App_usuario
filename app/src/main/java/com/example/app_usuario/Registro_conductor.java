package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Registro_conductor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);
    }
    public void avanzar(View view){
        Intent i = new Intent(Registro_conductor.this, Mensaje.class);
        startActivity(i);
        finish();
    }
}