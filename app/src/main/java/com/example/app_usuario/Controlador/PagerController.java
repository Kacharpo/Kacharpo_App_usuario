package com.example.app_usuario.Controlador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerController extends FragmentPagerAdapter {
    int numOfTabs;

    public PagerController(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numOfTabs = behavior;
    }

    @NonNull

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Inicio();
            case 1:
                return new Final();
            case 2:
                return new Tiempo();
            case 3:
                return new Conductor();
            case 4:
                return new Disponibilidad();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
