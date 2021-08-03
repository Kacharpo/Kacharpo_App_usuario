package com.example.app_usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BienvenidoActivity extends AppCompatActivity {

    ViewPager mViewPager;

    // images array
    int[] images = {R.drawable.camion,
            R.drawable.ic_stat_asientorojo,
            R.drawable.ic_stat_asientoverde};
    ViewPagerAdapter mViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);

        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(BienvenidoActivity.this, images);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public void avanzarLogin(View view) {
        Intent ven=new Intent(this, Log_in.class);
        //lanzamos la actividad
        startActivity(ven);
    }
}
