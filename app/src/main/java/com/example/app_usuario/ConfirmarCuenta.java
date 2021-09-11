package com.example.app_usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmarCuenta extends AppCompatActivity {

    private TextView tv_bienvenido,tv_ingresa,tv_intentos,tv_recibir;
    private EditText et_codigo1,et_codigo2,et_codigo3,et_codigo4,et_codigo5,et_codigo6;   private EditText et_codigo;
    private Button btn_reenviar;
    String codigotxt = "";
    int c = 5,codigon ;
    String codigo = "";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_cuenta);

        tv_bienvenido = (TextView)findViewById(R.id.tv_c_bienvenido);
        tv_ingresa = (TextView)findViewById(R.id.tv_c_ingresa);
        tv_intentos = (TextView)findViewById(R.id.tv_c_intentos);
        tv_recibir = (TextView)findViewById(R.id.tv_c_recibir);
        et_codigo = (EditText)findViewById(R.id.txt_c_codigo);
        btn_reenviar = (Button)findViewById(R.id.btn_c_reenviar);

        final String recipientEmail = "kacharpo.service@gmail.com";
        final String recipientPassword = "Kacharpo2000";
        final String subject = "Codigo de confrimacion";
        final String message = "Su codigo es: "+ getIntent().getStringExtra("Codigo");
        final String emailto = getIntent().getStringExtra("EmailTo");
        final String codigo = getIntent().getStringExtra("Codigo");

        Intent aceptar = new Intent(this,RecuperarContra.class);

        btn_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendEmailWithGmail(recipientEmail,recipientPassword,emailto,subject,message);
            }
        });

        et_codigo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String codigotxt = et_codigo.getText().toString();
                //codigo
                if(codigotxt.equals("123456")){
                    Intent aceptar = new Intent(getApplicationContext(),RecuperarContra.class);
                    startActivity(aceptar);
                }

                return false;
            }
        });
    }
/*
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

        SenderAsyncTask task = new SenderAsyncTask(session, recipientEmail, to, subject, message);
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
            progressDialog = ProgressDialog.show(ConfirmarCuenta.this, "", getString(R.string.sending_mail), true);
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
    private int codigo(int max){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int numero = random.nextInt(max);
        return numero;
    }
      }*/

}