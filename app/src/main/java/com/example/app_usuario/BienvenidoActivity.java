package com.example.app_usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
public class BienvenidoActivity extends AppCompatActivity implements PrimerFragment.OnFragmentInteractionListener,
        SegundoFragment.OnFragmentInteractionListener,tercerFragment.OnFragmentInteractionListener
{
private LinearLayout linearPuntos;
private TextView [] puntosSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bienvenido);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

       linearPuntos=findViewById(R.id.idLinearPuntos);
       agregarIndicadorPuntos(0);

       viewPager.addOnPageChangeListener(viewListener);


    }

    private void agregarIndicadorPuntos(int pos) {
        puntosSlide=new TextView[3];
        linearPuntos.removeAllViews();

        for(int i=0; i<puntosSlide.length; i++){
            puntosSlide[i]=new TextView(this);
            puntosSlide[i].setText(Html.fromHtml("&#8226"));
            puntosSlide[i].setTextSize(35);
            puntosSlide[i].setTextColor(getResources().getColor(R.color.colorBlancoTransparente));
            linearPuntos.addView(puntosSlide[i]);
        }
        if(puntosSlide.length>0){
            puntosSlide[pos].setTextColor(getResources().getColor(R.color.colorBlanco));
        }
    }

    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            agregarIndicadorPuntos(i);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void OnFragmentInteraction(Uri uri) {

    }

    public static class PlaceholderFragment extends  Fragment{
        private static final String ARG_SECTION_NUMER="section_number";

        public PlaceholderFragment(){

        }

        public static  Fragment newInstance(int sectionNumber){
            Fragment fragment= null;
            switch (sectionNumber){
                case 1:fragment=new PrimerFragment();break;
                case 2:fragment=new SegundoFragment();break;
                case 3:fragment=new tercerFragment();break;
            }
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView textView;
            View rootView=inflater.inflate(R.layout.fragment_primer, container,false);
            textView= (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Hello World from section: {getArguments().getInt{ARG_SE...");
            return rootView;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter( FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }
    }
}
