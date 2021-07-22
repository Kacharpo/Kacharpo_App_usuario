package com.example.app_usuario;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Fragmento extends DialogFragment {
    public static final String ARGUMENTO_TITLE = "TITLE";
    public static final String ARGUMENTO_FULL = "FULL_SNIPPET";

    private String title;
    private String fullSnipper;

    public static Fragmento newInstance(String title, String fullSnipper){
        Fragmento fragment = new Fragmento();
        Bundle b = new Bundle();
        b.putString(ARGUMENTO_TITLE, title);
        b.putString(ARGUMENTO_FULL, fullSnipper);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        title = args.getString(ARGUMENTO_TITLE);
        fullSnipper = args.getString(ARGUMENTO_FULL);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(fullSnipper).create();
        return dialog;
    }
}