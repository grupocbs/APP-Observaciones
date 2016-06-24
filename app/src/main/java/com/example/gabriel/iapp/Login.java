package com.example.gabriel.iapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.Tools;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {



    private String usuario = "";
    private String contraseña = "";
    private String mail = "";
    private String ip = "";
    private String puerto = "";
    private String imei = "";

    public static String SUPERVISOR = "";
    public static String SERVER="";
    public static String WS = "App_Login.aspx";
    public static String USUARIO = "";
    public static String CONTRASEÑA = "";
    public static String MAIL = "";
    public static String IP = "";
    public static String PUERTO = "";
    public static HttpURLConnection con = null;



    private ProgressDialog pDialog;
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USERNAME = "us";
    private static final String PREF_PASSWORD = "pass";
    private static final String PREF_IP = "ip";
    private static final String PREF_PUERTO = "puerto";
    private static final String PREF_MAIL = "mail";


    Funcion_JSONParser jParser = new Funcion_JSONParser();
    boolean log=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        try {


            SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String username = pref.getString(PREF_USERNAME, null);
            String password = pref.getString(PREF_PASSWORD, null);
            String ip = pref.getString(PREF_IP, null);
            String puerto = pref.getString(PREF_PUERTO, null);
            String mail = pref.getString(PREF_MAIL, null);


            if (username != null && password != null && ip != null) {
                ((EditText) findViewById(R.id.txt_usuario)).setText(username);
                ((EditText) findViewById(R.id.txt_contraseña)).setText(password);
                ((EditText) findViewById(R.id.txt_ip)).setText(ip);
                ((EditText) findViewById(R.id.txt_ip)).setVisibility(View.INVISIBLE);
                ((EditText) findViewById(R.id.txt_puerto)).setText(puerto);
                ((EditText) findViewById(R.id.txt_puerto)).setVisibility(View.INVISIBLE);
            }

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            ((TextView) findViewById(R.id.txt_version)).setText("Versión: " + version);


            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(Login.this, "Error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }




    }

    public void Ingresar(View view) {

        MiLoguin();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    public void onResume() {
        super.onResume();  // Always call the superclass method first


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ;

    private void MiLoguin() {


        if (Tools.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {


            if (((EditText) findViewById(R.id.txt_usuario)).getText().toString().length() > 0 && ((EditText) findViewById(R.id.txt_contraseña)).getText().toString().length() > 0 && ((EditText) findViewById(R.id.txt_ip)).getText().toString().length() > 0 && ((EditText) findViewById(R.id.txt_puerto)).getText().toString().length() > 0) {

                usuario=((EditText) findViewById(R.id.txt_usuario)).getText().toString();
                contraseña=((EditText) findViewById(R.id.txt_contraseña)).getText().toString();
                ip=((EditText) findViewById(R.id.txt_ip)).getText().toString();
                puerto=((EditText) findViewById(R.id.txt_puerto)).getText().toString();
                imei=Tools.getIMEI(Login.this);


                SERVER="http://" + ip.replace(" ","") + ":" + puerto.replace(" ","");

                new Logueo().execute();



            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Login");
                alertDialog.setMessage("Ingrese Usuario y Contraseña.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((EditText) findViewById(R.id.txt_usuario)).findFocus();
                    }
                });
                alertDialog.show();

            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Conectando...");
            alertDialog.setMessage("Verifique conexion a Internet.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((EditText) findViewById(R.id.txt_usuario)).findFocus();
                }
            });
            alertDialog.show();
        }
    }



    class Logueo extends AsyncTask<String, String, String> {
        JSONObject json;
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Ingresando. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {

            runOnUiThread(new Runnable() {
                public void run() {


                    try {

                        URL url = new URL(SERVER + "/" + WS);
                        try {
                            con = (HttpURLConnection)url.openConnection();
                            con.setConnectTimeout(2000);
                            if(con.getResponseCode()==HttpURLConnection.HTTP_OK) {

                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("us", usuario));
                                params.add(new BasicNameValuePair("pass", contraseña));
                                params.add(new BasicNameValuePair("ip", ip));
                                params.add(new BasicNameValuePair("puerto", puerto));
                                params.add(new BasicNameValuePair("imei", imei));



                                json = jParser.makeHttpRequest(SERVER + "/" + WS, "GET", params);
                                if (json != null) {
                                    try {

                                        if (json.toString().contains("error")) {
                                            log = false;
                                        } else {
                                            log = true;
                                            USUARIO = json.getString("us").replace(" ", "");
                                            CONTRASEÑA = json.getString("pass").replace(" ", "");
                                            IP = json.getString("ip").replace(" ", "");
                                            PUERTO = json.getString("puerto").replace(" ", "");
                                            MAIL = json.getString("mail").replace(" ", "");
                                            SUPERVISOR = json.getString("supervisor").replace(" ", "");

                                            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                                    .edit()
                                                    .putString(PREF_USERNAME, USUARIO)
                                                    .putString(PREF_PASSWORD, CONTRASEÑA)
                                                    .putString(PREF_IP, IP)
                                                    .putString(PREF_PUERTO, PUERTO)
                                                    .commit();
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        Toast.makeText(Login.this, "Respuesta de Servidor incorrecta:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        ((EditText) findViewById(R.id.txt_ip)).setVisibility(View.VISIBLE);
                                        ((EditText) findViewById(R.id.txt_puerto)).setVisibility(View.VISIBLE);
                                    }
                                } else {

                                    Toast.makeText(Login.this, "Respuesta de Servidor incorrecta", Toast.LENGTH_LONG).show();
                                    ((EditText) findViewById(R.id.txt_ip)).setVisibility(View.VISIBLE);
                                    ((EditText) findViewById(R.id.txt_puerto)).setVisibility(View.VISIBLE);
                                }



                            }
                            else
                            {
                                Toast.makeText(Login.this, "Servidor No disponible", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            Toast.makeText(Login.this, "No se encuentra el Servidor:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (MalformedURLException ex)
                    {
                        ex.printStackTrace();
                       // Toast.makeText(Login.this, "Direccion de Servidor incorrecta:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
            return null;
        }



        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (json != null) {
                        if (log) {
                            Intent mIntent = new Intent(Login.this, Principal.class);
                            mIntent.putExtra("us", USUARIO);
                            startActivityForResult(mIntent, 100);
                        } else {

                            Toast.makeText(Login.this, "Error de usuario y contraseña o IMEI no autorizado", Toast.LENGTH_LONG).show();
                            ((EditText) findViewById(R.id.txt_ip)).setVisibility(View.VISIBLE);
                            ((EditText) findViewById(R.id.txt_puerto)).setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(Login.this, "Respuesta de Servidor incorrecta", Toast.LENGTH_LONG).show();
                        ((EditText) findViewById(R.id.txt_ip)).setVisibility(View.VISIBLE);
                        ((EditText) findViewById(R.id.txt_puerto)).setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }



}