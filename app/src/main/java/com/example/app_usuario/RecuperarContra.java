package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecuperarContra extends AppCompatActivity {

    private EditText et_correo;
    private TextView tv_olvidaste, tv_introducir;
    private Button btn_enviar;
    private ImageView img_recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        et_correo = (EditText)findViewById(R.id.txt_c_correoContra);
        tv_olvidaste = (TextView)findViewById(R.id.tv_c_olvidaste);
        tv_introducir = (TextView)findViewById(R.id.tv_c_introducir);
        btn_enviar = (Button)findViewById(R.id.btn_c_enviar);
        img_recuperar = (ImageView)findViewById(R.id.img_c_recuperar);
    }


}