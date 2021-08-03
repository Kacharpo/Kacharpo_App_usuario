package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    ImageView IMG_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        IMG_1=(ImageView)findViewById(R.id.Img_logo);
        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(100);
        animation.setFillAfter(true);
        IMG_1.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, BienvenidoActivity.class);
                startActivity(i);
                finish();
            }
        },3000);
    }
}