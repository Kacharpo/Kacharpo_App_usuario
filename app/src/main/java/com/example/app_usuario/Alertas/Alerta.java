package com.example.app_usuario.Alertas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_usuario.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Alerta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Alerta extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Alerta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment inicio.
     */
    // TODO: Rename and change types and number of parameters
    public static Alerta newInstance(String param1, String param2) {
        Alerta fragment = new Alerta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private CardView card1,card2,card3,card4,card5,card6,card7,card8;
    private View vista;

    private FragmentTransaction transaction;
    private Fragment fragmentMensaje,fragmentInundacion,fragmentServicio,fragmentTarjeta,
            fragmentAsaltante,fragmentTrafico,fragmentDisponibilidad,fragmentAsistencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_alerta, container, false);
        /*card1 = vista.findViewById(R.id.cv_c);
        card2 = vista.findViewById(R.id.cv_c1);
        card3 = vista.findViewById(R.id.cv_c2);
        card4 = vista.findViewById(R.id.cv_c3);
        card5 = vista.findViewById(R.id.cv_c4);
        card6 = vista.findViewById(R.id.cv_c5);
        card7 = vista.findViewById(R.id.cv_c6);
        card8 = vista.findViewById(R.id.cv_c7);

        fragmentMensaje = new AlertaMensaje();
        fragmentInundacion = new AlertaInundacion();
        fragmentServicio = new AlertaServicio();
        fragmentTarjeta = new AlertaTarjeta();
        fragmentAsaltante = new AlertaAsaltante();
        fragmentTrafico = new AlertaTrafico();
        fragmentDisponibilidad = new AlertaDisponibilidad();
        fragmentAsistencia = new AlertaAsistencia();

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //et_edt_Ubi.setVisibility(View.VISIBLE);
                //et_edt_num_Ubi.setVisibility(View.INVISIBLE);
                //transaction = (R.id.fm_c_boton,fragmentMensaje);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentInundacion);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentServicio);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentTarjeta);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentAsaltante);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentTrafico);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentDisponibilidad);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transaction = getSupportFragmentManager().beginTransaction().add(R.id.fm_c_boton,fragmentAsistencia);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }); */
        return vista;
    }

}