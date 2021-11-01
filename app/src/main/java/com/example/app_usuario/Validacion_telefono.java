package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class Validacion_telefono extends AppCompatActivity {

    private TextView tv_bienvenido,tv_ingresa,tv_intentos,tv_recibir;
    private EditText et_codigo1,et_codigo2,et_codigo3,et_codigo4,et_codigo5,et_codigo6;
    private Button btn_reenviar;

    private EditText et_telefono;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etxtPhoneCode;
    private String mVerificationId;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion_telefono);
        tv_bienvenido = (TextView)findViewById(R.id.tv_c_bienvenido);
        tv_ingresa = (TextView)findViewById(R.id.tv_c_ingresa);
        tv_intentos = (TextView)findViewById(R.id.tv_c_intentos);
        tv_recibir = (TextView)findViewById(R.id.tv_c_recibir);
        et_codigo1 = (EditText)findViewById(R.id.txt_c_codigo1);
        et_codigo2 = (EditText)findViewById(R.id.txt_c_codigo2);
        et_codigo3 = (EditText)findViewById(R.id.txt_c_codigo3);
        et_codigo4 = (EditText)findViewById(R.id.txt_c_codigo4);
        et_codigo5 = (EditText)findViewById(R.id.txt_c_codigo5);
        et_codigo6 = (EditText)findViewById(R.id.txt_c_codigo6);
        btn_reenviar = (Button)findViewById(R.id.btn_c_reenviar);

        et_telefono = (EditText) findViewById(R.id.txt_telefono);
        etxtPhoneCode = (EditText) findViewById(R.id.txt_c_codigo1);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("Es");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.w("onautstatechanged ++", "si hay algo");
                    //Toast.makeText(Validacion_telefono.this,  firebaseAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(Validacion_telefono.this, PrincipalMenuActivity.class);
                    //startActivity(intent);
                    //finish();
                }
            }
        };
    }

    public void requestCode(View view) {
        String phoneNumber = et_telefono.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
            return;

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:  ----" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.w("Veriffailed", "Peticion no valida", e);
                    //Toast.makeText(getApplicationContext(),"Peticion invalida",Toast.LENGTH_LONG);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.w("Veriffailed", "La cuota de sms fue exedida", e);
                    //Toast.makeText(getApplicationContext(),"La cuota de sms fue exedida",Toast.LENGTH_LONG);
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //poner botones que se muestren
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.w("enviando codigo --", " on code sent");

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                PhoneAuthProvider.ForceResendingToken mResendToken = token;
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60, TimeUnit.SECONDS, Validacion_telefono.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        //Called if it is not needed to enter verification code
                        signInWithCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        //incorrect phone number, verification code, emulator, etc.
                        Toast.makeText(Validacion_telefono.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //now the code has been sent, save the verificationId we may need it
                        super.onCodeSent(verificationId, forceResendingToken);
                        Toast.makeText(Validacion_telefono.this,"oncodesent ",Toast.LENGTH_LONG);
                        mVerificationId = verificationId;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String verificationId) {
                        //called after timeout if onVerificationCompleted has not been called
                        super.onCodeAutoRetrievalTimeOut(verificationId);
                        Toast.makeText(Validacion_telefono.this, "onCodeAutoRetrievalTimeOut :" + verificationId, Toast.LENGTH_SHORT).show();
                    }
                }
        );*/
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, " exito signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(Validacion_telefono.this, PrincipalMenuActivity.class);
                            startActivity(intent);
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, " fallo signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    /*private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Validacion_telefono.this, "Exito ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Validacion_telefono.this, "fallado " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/


    public void signIn(View view) {
        Log.w("signin --", " probando en sign in ");
        String code = etxtPhoneCode.getText().toString();
        if (TextUtils.isEmpty(code))
            return;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


}