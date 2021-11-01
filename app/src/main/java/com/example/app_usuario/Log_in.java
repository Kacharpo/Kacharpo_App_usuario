package com.example.app_usuario;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app_usuario.Registro.DaoRegistro;
import com.example.app_usuario.Registro.RegistroConstructor;
import com.example.app_usuario.Registro.RegistroUsuario;
import com.example.app_usuario.menu.MensajeFragment;
import com.example.app_usuario.utils.InputValidation;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Log_in extends AppCompatActivity {
    private EditText et_usuario, et_contrasena;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth,nAuth;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";
    DatabaseReference nDatabase;
    DaoRegistro dao;
    String key =null;
    int c = 0;
    String email;
    private int codigo = codigo(999999);
    boolean g=false;
    String password;
    String nombre="";
    String apellido="";
    String fecha="";
    String numero="";
    String ruta="";
    String licencia="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        et_usuario = (EditText)findViewById(R.id.txt_usr);
        et_contrasena = (EditText)findViewById(R.id.txt_pass);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "Sesion iniciada con facebook : "+accessToken, Toast.LENGTH_SHORT);

            toast1.show();
            Intent a = new Intent(getApplicationContext(), PrincipalMenuActivity.class);
            startActivity(a);
        }

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Log in exitoso !", Toast.LENGTH_SHORT);

                toast1.show();
                Intent a = new Intent(getApplicationContext(), PrincipalMenuActivity.class);
                startActivity(a);
            }

            @Override
            public void onCancel() {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Cancelado", Toast.LENGTH_SHORT);

                toast1.show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Error : "+exception, Toast.LENGTH_SHORT);

                toast1.show();
                Log.d("debug ", "Error : "+exception);
            }
        });

        nAuth = FirebaseAuth.getInstance();

        dao = new DaoRegistro();

        nDatabase = FirebaseDatabase.getInstance().getReference();

        nDatabase.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String correo = snapshot.child("Correo").getValue().toString();
                    String contrasena = snapshot.child("Contrasena").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        nAuth.setLanguageCode("es");
        mAuth.setLanguageCode("es");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN )   {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
            }else{
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. "+task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }

        } else  {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent a = new Intent(getApplicationContext(), PrincipalMenuActivity.class);
                            checkUser();
                            startActivity(a);
//Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    public void Log_in_firebase(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //Intent i = new Intent(mGoogleSignInClient.getApplicationContext(), Registro_conductor.class);
        //startActivity(i);
        //finish();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void registro(View view){
        Intent i = new Intent(Log_in.this, RegistroUsuario.class);
        startActivity(i);
        finish();
    }
    public void recuperar_contra(View view){
        Intent i = new Intent(Log_in.this, RecuperarContra.class);
        startActivity(i);
        finish();
    }
    public void ingresar(View view){
        String usuario = et_usuario.getText().toString();
        boolean usuario_b = InputValidation.isValidEditText(et_usuario, getString(R.string.field_is_required));
        String contrasena = et_contrasena.getText().toString();
        boolean contrasena_b = InputValidation.isValidEditText(et_contrasena, getString(R.string.field_is_required));

        email = et_usuario.getText().toString();
        password = et_contrasena.getText().toString();

        if (TextUtils.isEmpty(email)) {
            et_usuario.setError("Email cannot be empty");
            et_usuario.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            et_contrasena.setError("Password cannot be empty");
            et_contrasena.requestFocus();
        } else {
            nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        databaseReference.child("Registro").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                        String mail= dataSnapshot.child("correo").getValue().toString();
                                        if(email.equals(mail)){

                                            String confirmado=dataSnapshot.child("confirmado").getValue().toString();
                                            if(confirmado.equals("true")){
                                                Intent correo = new Intent(getApplicationContext(), PrincipalMenuActivity.class);
                                                correo.putExtra("EmailTo",email);
                                                startActivity(correo);

                                            }else{
                                                datos();
                                            }
                                        }

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Toast.makeText(getApplicationContext(), "User logged in successfully", Toast.LENGTH_SHORT).show();



                    } else {
                        Toast.makeText(getApplicationContext(), "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void datos(){
        try {
            Intent correos = new Intent(getApplicationContext(), ConfirmarCuenta.class);
            correos.putExtra("EmailTo",email);
            correos.putExtra("Codigo",""+codigo);
            startActivity(correos);} catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    private int codigo(int max){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int numero = random.nextInt(max);
        return numero;
    }

    private final void checkUser() {
        FirebaseUser currentUser = this.mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent((Context)this, MensajeFragment.class);
            intent.putExtra("user", currentUser.getEmail());
            this.finish();
        }

    }
}