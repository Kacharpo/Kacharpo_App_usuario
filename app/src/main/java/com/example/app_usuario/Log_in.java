package com.example.app_usuario;

import  androidx.appcompat.app.AppCompatActivity ;

import android.content.Context;
import  android.content.Intent ;
import  android.database.Cursor ;
import  android.database.sqlite.SQLiteDatabase ;
import  android.os.Bundle ;
import android.util.Log;
import  android.view.View ;
import  android.widget.EditText ;
import android.widget.TextView;
import  android.widget.Toast ;

import androidx.annotation.NonNull;

import com.example.app_usuario.Registro.RegistroUsuario;
import com.example.app_usuario.menu.MensajeFragment;
import com.example.app_usuario.utils.InputValidation;
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

public class Log_in extends AppCompatActivity {
    private EditText et_usuario, et_contrasena,et1,et2;
    private TextView tv_recuperar;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        et_usuario = (EditText)findViewById(R.id.txt_usr);
        et_contrasena = (EditText)findViewById(R.id.txt_pass);
        tv_recuperar = (TextView)findViewById(R.id.tv_c_recuperar);
//habilitamos para que se pueda visualizar el action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Indicamos donde esta la imagen para el action bar
        getSupportActionBar().setIcon(R.drawable.ic_launcher_foreground);
        //emperejamos las variable con el xml editText usuario y password

        tv_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperar_contra();
            }
        });

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
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
    private final void checkUser() {
        FirebaseUser currentUser = this.mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent((Context)this, MensajeFragment.class);
            intent.putExtra("user", currentUser.getEmail());
            this.finish();
        }

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
    public void recuperar_contra(){
        Intent i = new Intent(Log_in.this, RecuperarContra.class);
        startActivity(i);
        finish();
    }
    public void recuperar_contraseña(View view){
        Intent i = new Intent(Log_in.this, RecuperarContra.class);
        startActivity(i);
        finish();
    }

    public void ingresar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "registro",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        String usuario = et_usuario.getText().toString();
        boolean usuario_b = InputValidation.isValidEditText(et_usuario, getString(R.string.field_is_required));
        String contrasena = et_contrasena.getText().toString();
        boolean contrasena_b = InputValidation.isValidEditText(et_contrasena, getString(R.string.field_is_required));

        if(usuario_b && contrasena_b){
            Cursor fila = db.rawQuery
                    ("select correo, contrasena from registro_usuario where correo = '"+usuario+"' and contrasena = '"+contrasena+"'" ,null);
            if(fila.moveToFirst()) {
                if(usuario.equals(fila.getString(0)) && contrasena.equals(fila.getString(1))){
                    Intent i = new Intent(Log_in.this, PrincipalMenuActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(this, "Usuario y/o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "No exite el registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

}