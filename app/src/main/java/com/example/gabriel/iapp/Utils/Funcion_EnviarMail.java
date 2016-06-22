package com.example.gabriel.iapp.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gabriel.iapp.Login;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Belal on 10/30/2015.
 */

//Class is extending AsyncTask because this class is going to perform a networking operation
public class Funcion_EnviarMail extends AsyncTask<Void,Void,Void> {
    private static final String WS_MailConf = "App_OPOBOJ_MailConf.aspx";
    //Declaring Variables
    private Context context;
    private Session session;
    private String smtp;
    //Information to send email
    private String emailTo1;
    private String emailTo2;
    private String subject;
    private String message;

    private String mailfrom;
    private String password;
    private String Copia;

    private String port;
    private String ssl;
    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    private String ID;
    private String SUPERVISOR;
    JSONObject Mailconf;
    //Class Constructor
    public Funcion_EnviarMail(Context context, String ID, String SUPERVISOR, String MAILFROM, String CONTRASEÑA, String CC){
        //Initializing variables
        this.context = context;
        this.ID = ID;
        this.SUPERVISOR = SUPERVISOR;
        this.mailfrom=MAILFROM;
        this.password=CONTRASEÑA;
        if(CC.length()>0)
        {
            Copia=CC;
        }
        else
        {
            Copia="";
        }

    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Funcion_JSONParser jParserE = new Funcion_JSONParser();
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("us", Login.USUARIO));

            JSONObject json = jParserE.makeHttpRequest(Login.SERVER + "/" + WS_MailConf, "GET", params1);
            Log.d("mailconf: ", json.toString());
            try {
                JSONArray shirtObj = json.getJSONArray("registros"); // JSON Array
                Mailconf = shirtObj.getJSONObject(0);


                String cuerpo = Mailconf.getString("cuerpo");
                cuerpo = cuerpo.replace("[codigo]", ID);
                cuerpo = cuerpo.replace("[supervisor]", SUPERVISOR);

                this.emailTo1 = Mailconf.getString("direccion_recibe1");
                this.emailTo2 = Mailconf.getString("direccion_recibe2");
                this.subject = Mailconf.getString("titulo");
                this.message = cuerpo;
                //this.mailfrom = Mailconf.getString("direccion_envia");
                //this.password = Mailconf.getString("pass_smtp");
                this.port = Mailconf.getString("puerto_smtp");
                this.smtp = Mailconf.getString("direccion_smtp");

                if (Mailconf.getString("ssl").equals("S")) {
                    this.ssl = "true";
                } else {
                    this.ssl = "false";
                }


                Properties props = new Properties();
                props.put("mail.smtp.host", smtp);
                props.put("mail.smtp.socketFactory.port", port);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", ssl);
                props.put("mail.smtp.port", port);

                //Creating a new session
                session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            //Authenticating the password
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(mailfrom, password);
                            }
                        });


                MimeMessage mm = new MimeMessage(session);

                mm.setFrom(new InternetAddress(mailfrom));
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo1));
                mm.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo2));
                if(Copia.length()>0)
                {
                    mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Copia));
                }
                mm.setSubject(subject);
                mm.setContent(message, "text/html");
                Transport.send(mm);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



}