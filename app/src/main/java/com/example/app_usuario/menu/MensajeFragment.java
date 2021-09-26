package com.example.app_usuario.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app_usuario.ChatActivity;
import com.example.app_usuario.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MensajeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MensajeFragment extends Fragment {
    ListView userListView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> users = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MensajeFragment() {

    }


    public static MensajeFragment newInstance(String param1, String param2) {
        MensajeFragment fragment = new MensajeFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mensaje,container,false);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userListView = v.findViewById(R.id.userListView);
        databaseReference.child("RegistroConstructor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String email = dataSnapshot.child("correo").getValue().toString();
                        if (!email.equals(mAuth.getCurrentUser().getEmail())){
                            users.add(email);
                        }
                    }
                    arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, users);
                    userListView.setAdapter(arrayAdapter);

                }else{
                    Toast.makeText(getActivity(), "Failed to load table", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("correo", users.get(position));
                startActivity(intent);
            }
        });
        return v;
    }
}