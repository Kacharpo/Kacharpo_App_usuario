package com.example.app_usuario.Registro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_usuario.AdminSQLiteOpenHelper;
import com.example.app_usuario.ConfirmarCuenta;
import com.example.app_usuario.R;
import com.example.app_usuario.utils.InputValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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

public class   RegistroUsuario extends AppCompatActivity {


    //Crear variables
    private String modelName;
    StorageReference storageRef;
    ProgressDialog progressDialog;

    DaoRegistro dao = new DaoRegistro();
    String key ="1";

    private Spinner sp_tipo;
    private EditText et_nombre, et_apellido, et_fecha,et_numero , et_correo, et_contrasena, et_confirmar, et_ruta, et_licencia;
    private Button btn_aceptar;
    private ImageView img_control;
    private RadioButton rb_terminos;
    private int codigo = codigo(999999);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario_);

        et_nombre = (EditText)findViewById(R.id.txt_c_nombre);
        et_apellido = (EditText)findViewById(R.id.txt_c_apellido);
        et_fecha = (EditText)findViewById(R.id.txt_c_fecha);
        et_numero = (EditText)findViewById(R.id.txt_c_numero);
        et_correo = (EditText)findViewById(R.id.txt_c_correo);
        et_contrasena = (EditText)findViewById(R.id.txt_c_contrasena);
        et_confirmar = (EditText)findViewById(R.id.txt_c_confirmar);

        img_control = (ImageView)findViewById(R.id.img_perfil);
        btn_aceptar = (Button)findViewById(R.id.btn_c_aceptar);

        rb_terminos = (RadioButton)findViewById(R.id.rb_c_terminos);



        final String recipientEmail = "kacharpo.service@gmail.com";
        final String recipientPassword = "Kacharpo2000";
        final String subject = "Codigo de confrimacion";
        final String message = "Su codigo es: "+codigo;

        //AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "registro",null,1);

        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id_usuario = 1;
                String nombre = et_nombre.getText().toString();
                boolean nombre_b = InputValidation.isValidEditText(et_nombre,"Campo requerido");
                String apellido = et_apellido.getText().toString();
                boolean apellido_b = InputValidation.isValidEditText(et_apellido, "Campo requerido");
                String fecha = et_fecha.getText().toString();
                boolean fecha_b = InputValidation.isValidEditText(et_fecha,"Campo requerido");
                String numero = et_numero.getText().toString();
                boolean numero_b = InputValidation.isValidEditText(et_numero, "Campo requerido");
                String correo = et_correo.getText().toString();
                boolean correo_b = InputValidation.isValidEditText(et_correo, "Campo requerido");
                String contrasena = et_contrasena.getText().toString();
                boolean contrasena_b = InputValidation.isValidEditText(et_contrasena,"Campo requerido");
                String confirmar = et_confirmar.getText().toString();
                boolean confirmar_b = InputValidation.isValidEditText(et_confirmar,"Campo requerido");

                boolean terminos = rb_terminos.isChecked();

                if (nombre_b && apellido_b && fecha_b && numero_b && correo_b && contrasena_b && confirmar_b ) {
                    if (contrasena.equals(confirmar)) {
                        if (terminos == true) {
                            RegistroConstructor emp = new RegistroConstructor(key,nombre, apellido, fecha,numero , correo, contrasena);
                            dao.add(emp).addOnSuccessListener(suc ->
                            {
                                Toast.makeText(getApplicationContext(), "Record is inserted", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(er ->
                            {
                                Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_SHORT).show();

                            sendEmailWithGmail(recipientEmail,recipientPassword, et_correo.getText().toString(),subject,message);
                            datos();
                        } else {
                            Toast.makeText(getApplicationContext(), "Debes aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show();
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
        //Mensaje para cargar proceso
        progressDialog = new ProgressDialog(RegistroUsuario.this);
        progressDialog.setMessage("Fetching image...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        modelName = "Perfil.jpg";
        //Conexion con Firebase Storage
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        try{
            storageRef = mFirebaseStorage.getReference("Imagen/"+modelName);
            File localfile = File.createTempFile("tempfile",".jpg");
            storageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    //binding.imgLpWallpaper.setImageBitmap(bitmap);
                    img_control.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "Faileed", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void sendEmailWithGmail(final String recipientEmail, final String recipientPassword,
                                    String to, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(recipientEmail, recipientPassword);
            }
        });

        SenderAsyncTask task = new RegistroUsuario.SenderAsyncTask(session, recipientEmail, to, subject, message);
        task.execute();
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
        Intent correo = new Intent(getApplicationContext(), ConfirmarCuenta.class);
        correo.putExtra("EmailTo",et_correo.getText().toString());
        correo.putExtra("Codigo",""+codigo);
        startActivity(correo);
    }

    private int codigo(int max){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int numero = random.nextInt(max);
        return numero;
    }
}