package com.example.app_usuario.Registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app_usuario.ConfirmarCuenta;
import com.example.app_usuario.R;
import com.example.app_usuario.utils.InputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import kotlin.jvm.internal.Intrinsics;

public class RegistroUsuario extends AppCompatActivity {

    //Crear variables
    private String modelName;
    StorageReference storageRef;
    ProgressDialog progressDialog;

    private Spinner sp_tipo;
    private EditText et_nombre, et_apellido, et_fecha,et_numero , et_correo, et_contrasena, et_confirmar, et_ruta, et_licencia;
    private Button btn_aceptar;
    private ImageView img_control;
    private RadioButton rb_terminos;
    private int codigo = codigo(999999);
    DaoRegistro dao = new DaoRegistro();

    String key = "0";
    CardView cv_Camara,cv_Subir;
    int foto = 0,n=0;
    StorageReference nStorage;
    int INTENT = 0;
    int CAMARA_INTENT=1;
    int GALLERY_INTENT=2;
    String[] SUBIDAS = new String[100];
    Uri uri;
    StorageReference filePath;
    int PCAMARA =100;
    Bitmap imgBitmap;
    private Bitmap bitmap;
    private Uri photo;
    Intent var1;
    String fileName;
    FirebaseAuth mAuth;
    String error="";
    String nombre ;
    String apellido;
    String fecha;
    String numero ;
    String correo ;
    String contrasena ;
    String ruta ;
    String licencia ;
    String tipo;
    boolean k=true;
    int a=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro_usuario_);

            et_nombre = (EditText) findViewById(R.id.txt_c_nombre);
            et_apellido = (EditText) findViewById(R.id.txt_c_apellido);
            et_fecha = (EditText) findViewById(R.id.txt_c_fecha);
            et_numero = (EditText) findViewById(R.id.txt_c_numero);
            et_correo = (EditText) findViewById(R.id.txt_c_correo);
            et_contrasena = (EditText) findViewById(R.id.txt_c_contrasena);
            et_confirmar = (EditText) findViewById(R.id.txt_c_confirmar);
            img_control = (ImageView) findViewById(R.id.img_perfil);
            btn_aceptar = (Button) findViewById(R.id.btn_c_aceptar);
            rb_terminos = (RadioButton) findViewById(R.id.rb_c_terminos);
            cv_Camara = findViewById(R.id.cv_c_c);
            cv_Subir = findViewById(R.id.cv_c_s);


            final String recipientEmail = "kacharpo.service@gmail.com";
            final String recipientPassword = "Kacharpo2000";
            final String subject = "Codigo de confrimacion";
            final String message = "Su codigo es: " + codigo;

            //AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "registro",null,1);

            btn_aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                   DatabaseReference ref = database.getReference("Registro");
                   ref.removeValue(); */

                    int id_control = 1;
                    nombre = et_nombre.getText().toString();
                    boolean nombre_b = InputValidation.isValidEditText(et_nombre, "Campo requerido");
                    apellido = et_apellido.getText().toString();
                    boolean apellido_b = InputValidation.isValidEditText(et_apellido, "Campo requerido");
                    fecha = et_fecha.getText().toString();
                    boolean fecha_b = InputValidation.isValidEditText(et_fecha, "Campo requerido");
                    numero = et_numero.getText().toString();
                    boolean numero_b = InputValidation.isValidEditText(et_numero, "Campo requerido");
                    correo = et_correo.getText().toString();
                    boolean correo_b = InputValidation.isValidEditText(et_correo, "Campo requerido");
                    contrasena = et_contrasena.getText().toString();
                    boolean contrasena_b = InputValidation.isValidEditText(et_contrasena, "Campo requerido");
                    String confirmar = et_confirmar.getText().toString();
                    boolean confirmar_b = InputValidation.isValidEditText(et_confirmar, "Campo requerido");
                    boolean terminos = rb_terminos.isChecked();

                    if (nombre_b && apellido_b && fecha_b && numero_b && correo_b && contrasena_b && confirmar_b ) {
                        if (contrasena.equals(confirmar)) {
                            if (contrasena.length() >= 6) {
                                if (terminos == true) {
                                    if (foto == 1) {
                                        //createUser();
                                        String email = et_correo.getText().toString();
                                        String password = et_contrasena.getText().toString();

                                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show();

                                                    RegistroConstructor emp = new RegistroConstructor( nombre, apellido, fecha, numero, correo, contrasena,"false");
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                    databaseReference.child("Registro").addValueEventListener(new ValueEventListener() {

                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                int cont =0;
                                                                int a=0;
                                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                    cont++;
                                                                    key = ""+cont;
                                                                    String mail= dataSnapshot.child("correo").getValue().toString();
                                                                    if(mail==correo){
                                                                        a=1;
                                                                    }
                                                                }
                                                                if(a==0){
                                                                    Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();
                                                                    emp.setKey(key);
                                                                    key = emp.getKey();
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("nombre", nombre);
                                                                    hashMap.put("apellido", apellido);
                                                                    hashMap.put("fecha", fecha);
                                                                    hashMap.put("numero", numero);
                                                                    hashMap.put("correo", correo);
                                                                    hashMap.put("contrasena", contrasena);
                                                                    hashMap.put("confirmado", "false");
                                                                    dao.update(key, hashMap).addOnSuccessListener(suc ->
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                                                                    }).addOnFailureListener(er ->
                                                                    {
                                                                        Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    });
                                                                    datos();
                                                                }
                                                            }else{
                                                                emp.setKey(key);
                                                                key = emp.getKey();
                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                hashMap.put("nombre", nombre);
                                                                hashMap.put("apellido", apellido);
                                                                hashMap.put("fecha", fecha);
                                                                hashMap.put("numero", numero);
                                                                hashMap.put("correo", correo);
                                                                hashMap.put("contrasena", contrasena);
                                                                hashMap.put("confirmado", "false");
                                                                dao.update(key, hashMap).addOnSuccessListener(suc ->
                                                                {
                                                                    Toast.makeText(getApplicationContext(), "Record is updated", Toast.LENGTH_SHORT).show();
                                                                }).addOnFailureListener(er ->
                                                                {
                                                                    Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                                datos();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                } else {
                                                    et_correo.setText("");
                                                    error = "" + task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), "Registration Error:-" + error + "-", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                        correo_b = InputValidation.isValidEditText(et_correo, "!");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Debes tomarte una foto", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Debes aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                et_contrasena.setText("");
                                contrasena_b = InputValidation.isValidEditText(et_contrasena, "!");
                                et_confirmar.setText("");
                                confirmar_b = InputValidation.isValidEditText(et_confirmar, "!");
                                Toast.makeText(getApplicationContext(), "La contrseña debe tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            et_contrasena.setText("");
                            contrasena_b = InputValidation.isValidEditText(et_contrasena, "!");
                            et_confirmar.setText("");
                            confirmar_b = InputValidation.isValidEditText(et_confirmar, "!");
                            Toast.makeText(getApplicationContext(), "Contraseñas incorrectas", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cv_Camara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    checkPermissionCamera();

                }

            });

            cv_Subir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    abrirAlbum();
                }
            });

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PCAMARA);
            }

            mAuth = FirebaseAuth.getInstance();


            nStorage = FirebaseStorage.getInstance().getReference();
            img_control.setImageResource(R.drawable.perfil);
            mAuth.setLanguageCode("es");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }


    private class SenderAsyncTask extends AsyncTask<String, String, String> {

        private String from, to, subject, message;
        private ProgressDialog progressDialog;
        private Session session;

        public SenderAsyncTask(Session session, String from, String to, String subject, String message) {
            this.session = session;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RegistroUsuario.this, "", getString(R.string.sending_mail), true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(from));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                mimeMessage.setSubject(subject);
                mimeMessage.setContent(message, "text/html; charset=utf-8");
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    private void datos(){
        try {
            Intent correos = new Intent(getApplicationContext(), ConfirmarCuenta.class);
            correos.putExtra("EmailTo",et_correo.getText().toString());
            correos.putExtra("Codigo",""+codigo);
            correos.putExtra("key",key);
            correos.putExtra("nombre",nombre);
            correos.putExtra("apellido",apellido);
            correos.putExtra("fecha",fecha);
            correos.putExtra("numero",numero);
            correos.putExtra("correo",correo);
            correos.putExtra("contrasena",contrasena);
            correos.putExtra("ruta",ruta);
            correos.putExtra("licencia",licencia);

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

    private void abrirCamara(){
        try {
            var1 = new Intent("android.media.action.IMAGE_CAPTURE");
            photo = takeAndSavePicture();

            if (var1.resolveActivity(this.getPackageManager()) != null) {
                var1.putExtra("output", (Parcelable) photo);


                this.startActivityForResult(var1, CAMARA_INTENT);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error abrirCamara: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirAlbum(){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_INTENT);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if ((requestCode == CAMARA_INTENT && resultCode == RESULT_OK)) {
                // if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                //Bundle extras = data.getExtras();
                // imgBitmap = (Bitmap) extras.get("data") ;

                /*try {
                    filePath = nStorage.child("Perfil").child(fileName);

                    filePath.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            SUBIDAS[n] = fileName;
                            Toast.makeText(getApplicationContext(), "Imagen Subida " + SUBIDAS[n] + "", Toast.LENGTH_SHORT).show();
                            foto = 1;
                        }
                    });

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    //Conexion con Firebase Storage
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

                        storageRef = mFirebaseStorage.getReference("Perfil/" + fileName + "");
                        File localfile = File.createTempFile("tempfile", ".jpg");
                        storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                //binding.imgLpWallpaper.setImageBitmap(bitmap);
                                img_control.setImageBitmap(bitmap);
                            }
                        });

                    n++;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error insertar: "+e, Toast.LENGTH_SHORT).show();
                  //  abrirAlbum();

                }*/
                //INTENT=CAMARA_INTENT;

                // }else {

                // } Uri
                abrirAlbum();
            }
            if ((requestCode == GALLERY_INTENT && resultCode == RESULT_OK)) {

                Toast.makeText(getApplicationContext(), "SUbida", Toast.LENGTH_SHORT).show();
                uri = data.getData();
                modelName = "" + uri.getLastPathSegment() + ".jpg";
                filePath = nStorage.child("Perfil").child(modelName);

                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        SUBIDAS[n] = modelName;
                        Toast.makeText(getApplicationContext(), "Imagen Subida " + SUBIDAS[n] + "", Toast.LENGTH_SHORT).show();
                        foto = 1;
                    }
                });

                //Mensaje para cargar proceso
                progressDialog = new ProgressDialog(RegistroUsuario.this);
                progressDialog.setMessage("Fetching image...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Conexion con Firebase Storage
                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                try {
                    storageRef = mFirebaseStorage.getReference("Perfil/" + modelName + "");
                    File localfile = File.createTempFile("tempfile", ".jpg");
                    storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            //binding.imgLpWallpaper.setImageBitmap(bitmap);
                            img_control.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Faileed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                n++;
                INTENT = GALLERY_INTENT;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Activity: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private final Uri takeAndSavePicture() {

        OutputStream fos = null;
        File file = null;
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= 29) {
            ContentResolver resolver = this.getContentResolver();
            fileName = "Image_Profile" + System.currentTimeMillis() + ".jpg";
            ContentValues var7 = new ContentValues();
            var7.put("_display_name", fileName);
            var7.put("mime_type", "image/jpeg");
            var7.put("relative_path", "Pictures/");
            var7.put("is_pending", 1);
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, var7);

            try {
                OutputStream var10000;
                if (uri != null) {
                    var10000 = resolver.openOutputStream(uri);
                } else {
                    var10000 = null;
                }

                fos = var10000;
            } catch (FileNotFoundException var14) {
                var14.printStackTrace();
            }

            var7.clear();
            var7.put("is_pending", 0);
            if (uri != null) {
                resolver.update(uri, var7, (String) null, (String[]) null);
            }
        } else {
            String imageDir = String.valueOf(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            fileName = System.currentTimeMillis() + ".jpg";
            file = new File(imageDir, fileName);

            try {
                fos = (OutputStream) (new FileOutputStream(file));
            } catch (FileNotFoundException var13) {
                var13.printStackTrace();
            }
        }

        boolean var19;
        label60:
        {
            Bitmap var18 = this.bitmap;
            if (var18 != null) {
                if (var18.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                    var19 = true;
                    break label60;
                }
            }
            var19 = false;
        }

        boolean save = var19;
        if (save) {
            Toast.makeText(getApplicationContext(), "Picture save successfully", Toast.LENGTH_SHORT).show();
        }

        if (fos != null) {
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        if (file != null) {
            MediaScannerConnection.scanFile((Context) this, new String[]{file.toString()}, (String[]) null, (MediaScannerConnection.OnScanCompletedListener) null);
        }
        if(uri==null){
            takeAndSavePicture();
        }


        return uri;
    }

    private final void checkPermissionCamera() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission((Context) this, "android.permission.CAMERA") != 0) {
                    ActivityCompat.requestPermissions((Activity) this, new String[]{"android.permission.CAMERA"}, 100);
                } else {
                    abrirCamara();
                }
            } else {
                abrirCamara();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        try{
            Intrinsics.checkNotNullParameter(permissions, "permissions");
            Intrinsics.checkNotNullParameter(grantResults, "grantResults");
            if (requestCode == 100) {
                if (grantResults.length != 0 && grantResults[0] == 0) {
                    //this.abrirCamara();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();            }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();

        }
    }

}