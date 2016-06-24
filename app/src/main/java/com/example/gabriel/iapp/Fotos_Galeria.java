package com.example.gabriel.iapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import com.example.gabriel.iapp.Utils.Fotos_Clase;
import com.example.gabriel.iapp.Utils.Fotos_Lista;
import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.Tools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Fotos_Galeria extends ActionBarActivity {
    private GridView gridView;
    private Fotos_Lista gridAdapter;
    String codobs="";
    ArrayList<HashMap<String, String>> ObservacionesListF;
    private static String WS_all_observaciones = "App_OPOBOJ_capturas.aspx";
    private static final String TAG_REGISTROS = "registros";
    private static final String TAG_URL = "url";
    private static final String TAG_TITULO = "titulo";
    JSONArray JticketsF = null;
    Funcion_JSONParser jParserF = new Funcion_JSONParser();
    private ProgressDialog pDialog;
    ArrayList<Fotos_Clase> imageItems= new ArrayList();
    private String edita="1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_galeria);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle=getIntent().getExtras();
        codobs = bundle.getString("codobs");
        edita = bundle.getString("EDITA");







        switch (Login.SUPERVISOR)
        {

            case "1-Ver":
            {
                ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.INVISIBLE);
                break;
            }
            case "2-Cargar":
            {
                if(edita.equals("0"))
                {
                    ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.INVISIBLE);

                }
                else
                {
                    ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.VISIBLE);
                }
                break;
            }
            case "3-Administrar":
            {
                if(edita.equals("0"))
                {
                    ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.INVISIBLE);

                }
                else
                {
                    ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.VISIBLE);
                }
                break;
            }
        }


        Carga_Grilla();
    }

    private void Carga_Grilla() {
        if (Tools.isOnline((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
            new GetProductDetails().execute();
        }

            else {
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



    public void agregar_captura(View view){
        Intent mIntent= new Intent(this, Registro_CargaFotos.class);
        mIntent.putExtra("codobs",codobs);
        startActivityForResult(mIntent, 100);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==100) {
            // if result code 100
            try {
                Carga_Grilla();
            }catch(Exception ex){
                Log.i("agrega",ex.getMessage());
            }
            if (resultCode == 100) {


            }
        }


    }



    class GetProductDetails extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Fotos_Galeria.this);
            pDialog.setMessage("Cargando Fotos. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {

            imageItems.clear();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Login.USUARIO));
            params.add(new BasicNameValuePair("codobs", codobs));

            JSONObject jsonF = jParserF.makeHttpRequest(Login.SERVER + "/" + WS_all_observaciones, "GET", params);

            try {
                JticketsF = jsonF.getJSONArray(TAG_REGISTROS);



                // looping through All Products
                for (int i = 0; i < JticketsF.length(); i++) {
                    JSONObject c = JticketsF.getJSONObject(i);

                    // Storing each json item in variable
                    String url = c.getString(TAG_URL).replace(" ", "%20");
                    url = url.replace("\\\\127.0.0.1\\web_services", Login.SERVER);
                    url = url.replace("\\", "/");


                    Log.e("URL FINAL ", url);
                    String titulo = c.getString(TAG_TITULO);

                    try {
                        URL Url = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);


                        imageItems.add(new Fotos_Clase(myBitmap, titulo, Url.toString()));
                        if (myBitmap != null && !myBitmap.isRecycled()) {
                            myBitmap = null;
                            System.gc();
                        }

                    } catch (IOException e) {
                        // Log exception
                        e.printStackTrace();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new Fotos_Lista(Fotos_Galeria.this, R.layout.fotos_galeria_item, imageItems);
                    gridView.setAdapter(gridAdapter);

                    gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            Fotos_Clase item = (Fotos_Clase) parent.getItemAtPosition(position);

                            Intent intent = new Intent(Fotos_Galeria.this, Fotos_Ver.class);
                            intent.putExtra("title", item.getTitle());
                            intent.putExtra("url", item.getUrl());
                            startActivityForResult(intent, 100);

                        }
                    });

                    if(imageItems.size()==3)
                    {
                        ((Button) findViewById(R.id.btn_nuevo)).setVisibility(View.INVISIBLE);

                    }
                }
            });
        }
    }
}