package com.example.gabriel.iapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.SpinnerItems_Clase;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.example.gabriel.iapp.Utils.Funcion_EnviarMail;
import com.example.gabriel.iapp.Utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class Registro_Edita extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    // defino variables/prop que voy a usar
    private TextView txt_id;
    private TextView txt_fecha;
    private Spinner txt_tipoob;
    private Spinner txt_codcli;
    private EditText txt_observ;
    private TextView txt_correc;
    private ImageView imageFirma;

    private String id;
    private String fecha="";
    private String tipo="";
    private String cliente="";
    private String obs="";
    private String correc="";


    private String edita="0";
    private ProgressDialog pDialog;
    private ProgressDialog UltimoId_Dialog;
    private ProgressDialog Cliente_pDialog;
    private ProgressDialog Tipo_pDialog;
    Funcion_JSONParser jsonParser = new Funcion_JSONParser();
    private static final String WS_ultimoid = "App_OPOBOJ_lista.aspx";

    private static final String WS_tipos = "App_OPOBOJ_tipos.aspx";
    private static final String WS_clientes = "App_OPOBOJ_clientes.aspx";
    private static final String WS_update_observacion = "App_OPOBOJ_guarda.aspx";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_Tickets = "registros";
    private static final String TAG_ID = "ID";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_TIPOOB = "tipo";
    private static final String TAG_CODCLI = "cliente";
    private static final String TAG_OBSERV = "obs";
    private static final String TAG_CORREC = "correccion";
    private static final String TAG_FIRMA = "firma1";
    protected static final String TAG = "basic-location-sample";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private String mLatitudeText="0";
    private String mLongitudeText="0";
    JSONObject shirt;


    ArrayList<SpinnerItems_Clase> ArrayTipos = new ArrayList<>();
    ArrayList<SpinnerItems_Clase> ArrayClientes = new ArrayList<>();
    private boolean alta=false;
    private boolean gps=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.registro_edita);



        TextView t=(TextView) findViewById(R.id.txt_fecha);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        t.setText(sdf.format(c.getTime()));

        buildGoogleApiClient();

        switch (Login.SUPERVISOR)
        {

            case "1-Ver":
            {
                ((Spinner) findViewById(R.id.txt_tipoob)).setEnabled(false);
                ((Spinner) findViewById(R.id.txt_codcli)).setEnabled(false);
                ((EditText) findViewById(R.id.txt_observ)).setEnabled(false);
                ((EditText) findViewById(R.id.txt_correc)).setEnabled(false);
                ((Button) findViewById(R.id.btn_cerrar)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btn_agregarfirma)).setVisibility(View.INVISIBLE);
                break;
            }
            case "2-Cargar":
            {
                ((EditText) findViewById(R.id.txt_correc)).setEnabled(false);
                break;
            }
            case "3-Administrar":
            {
                ((EditText) findViewById(R.id.txt_correc)).setEnabled(true);
                break;
            }
        }


        if (Tools.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Bundle bundle=getIntent().getExtras();
            CargarCombos();

            if(bundle!=null && bundle.getString("ID")!=null) {
                alta=false;
                edita = bundle.getString("EDITA");

                if(edita.equals("0"))
                {
                    ((Spinner) findViewById(R.id.txt_tipoob)).setEnabled(false);
                    ((Spinner) findViewById(R.id.txt_codcli)).setEnabled(false);
                    ((EditText) findViewById(R.id.txt_observ)).setEnabled(false);
                    ((EditText) findViewById(R.id.txt_correc)).setEnabled(false);
                    ((Button) findViewById(R.id.btn_cerrar)).setVisibility(View.INVISIBLE);
                    ((Button) findViewById(R.id.btn_agregarfirma)).setVisibility(View.INVISIBLE);
                }
                id = bundle.getString("ID");
                llenarDatos();
            }
            else
            {
                alta=true;
                ((Button) findViewById(R.id.btn_agregarcaptura)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btn_agregarfirma)).setVisibility(View.INVISIBLE);
            }




        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Conectando...");
            alertDialog.setMessage("Verifique conexion a Internet.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }

    }

    private void CargarCombos()
    {
        new CargarUltimoId().execute();
        new CargarComboTipos().execute();
        new CargarComboClientes().execute();

    }


    public void onClick(View view) {

    }

    //configuracion GPS
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void onConnected(Bundle connectionHint) {
        CheckGPS();
    }

    private void CheckGPS() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            gps=true;
            mLatitudeText=String.valueOf(mLastLocation.getLatitude());
            mLongitudeText=String.valueOf(mLastLocation.getLongitude());
        } else {
            gps=false;
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setMessage(R.string.no_location_detected);
            dialogo.setCancelable(false);
            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  //  startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    finish();
                }
            });

            dialogo.show();

        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    protected void onResume()
    {
        super.onResume();
        if(!gps)
        {
            SystemClock.sleep(1000);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100) {
            this.recreate();

        }

        if (resultCode == 100) {
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_ticket, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //carga de datos
    public void llenarDatos(){
        // primero recupero el intent

        new CargarObservacion().execute();
    }

    class CargarObservacion extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registro_Edita.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {


                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("us", Principal.USUARIO));
                        params.add(new BasicNameValuePair("id", id));

                        JSONObject json = jsonParser.makeHttpRequest(Login.SERVER + "/" + WS_ultimoid, "GET", params);
                        JSONArray shirtObj = json.getJSONArray(TAG_Tickets); // JSON Array

                        // get first product object from JSON Array
                        shirt = shirtObj.getJSONObject(0);
                        // Asocio los view en XML con las variables que creamos anteriormente
                        txt_id = (TextView) findViewById(R.id.txt_id);
                        txt_fecha=(TextView) findViewById(R.id.txt_fecha);
                        txt_tipoob=(Spinner) findViewById(R.id.txt_tipoob);
                        txt_codcli=(Spinner) findViewById(R.id.txt_codcli);
                        txt_observ=(EditText) findViewById(R.id.txt_observ);
                        txt_correc=(EditText) findViewById(R.id.txt_correc);
                        imageFirma=(ImageView) findViewById(R.id.image_firma);

                            // seteamos los datos de Shirt en los editText




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
             //   }
          //  });

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    try {

                        String url = shirt.getString(TAG_FIRMA).replace(" ", "%20");
                        if (url.length() > 0) {
                            url = url.replace("\\\\127.0.0.1\\web_services", Login.SERVER);
                            url = url.replace("\\", "/");

                            try {
                                URL Url = new URL(url);
                                HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();

                                InputStream input = connection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);


                                imageFirma.setImageBitmap(myBitmap);
                                if (myBitmap != null && !myBitmap.isRecycled()) {
                                    myBitmap = null;
                                    System.gc();
                                }

                            } catch (IOException e) {
                                // Log exception
                                e.printStackTrace();
                            }
                        }


                        txt_id.setText(shirt.getString(TAG_ID));
                        txt_fecha.setText(shirt.getString(TAG_FECHA));
                        ((Spinner) findViewById(R.id.txt_tipoob)).setSelection(selectSpinnerItemByValue(ArrayTipos, shirt.getString(TAG_TIPOOB)));
                        ((Spinner) findViewById(R.id.txt_codcli)).setSelection(selectSpinnerItemByValue(ArrayClientes, shirt.getString(TAG_CODCLI)));
                        txt_observ.setText(shirt.getString(TAG_OBSERV));
                        txt_correc.setText(shirt.getString(TAG_CORREC));
                    }
                 catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            });
        }
    }
    class GuardarObservacion extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registro_Edita.this);
            pDialog.setMessage("Almacenando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("codobs", id));
            params.add(new BasicNameValuePair("us", Login.USUARIO));
            params.add(new BasicNameValuePair("fecha", fecha));
            params.add(new BasicNameValuePair("tipoob", tipo));
            params.add(new BasicNameValuePair("codcli",  cliente));
            params.add(new BasicNameValuePair("observ",  obs));
            params.add(new BasicNameValuePair("coordn", mLatitudeText + "," + mLongitudeText));
            params.add(new BasicNameValuePair("correc",  correc));
            params.add(new BasicNameValuePair("usuario_carga",  Principal.USUARIO));



            if(!alta) {
                params.add(new BasicNameValuePair("id", id));
            }


           try {

               JSONObject json = jsonParser.makeHttpRequest(Login.SERVER + "/" + WS_update_observacion, "GET", params);
               JSONArray shirtObj = json.getJSONArray(TAG_Tickets); // JSON Array




            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            pDialog.dismiss();

        }
    }



    class CargarUltimoId extends AsyncTask<String, String, String> {
        private String ultimoid = "";

        protected void onPreExecute() {
            super.onPreExecute();
            UltimoId_Dialog = new ProgressDialog(Registro_Edita.this);
            UltimoId_Dialog.setMessage("Cargando...");
            UltimoId_Dialog.setIndeterminate(false);
            UltimoId_Dialog.setCancelable(true);
            UltimoId_Dialog.show();
        }

        protected String doInBackground(String... args) {


            Funcion_JSONParser jParserE = new Funcion_JSONParser();
            JSONArray JIdentifico = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("ultimoid", "1"));
            params.add(new BasicNameValuePair("us", Login.USUARIO));

            JSONObject json = jParserE.makeHttpRequest(Login.SERVER + "/" + WS_ultimoid, "GET", params);
            Log.d("ultimoid: ", json.toString());
            try {
                JIdentifico = json.getJSONArray("registros");

                for (int i = 0; i < JIdentifico.length(); i++) {
                    JSONObject c1 = JIdentifico.getJSONObject(i);
                    ultimoid = c1.getString("Ultimo");


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            UltimoId_Dialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    TextView t = (TextView) findViewById(R.id.txt_id);
                    t.setText(ultimoid);

                }
            });
        }
    }
    class CargarComboClientes extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            Cliente_pDialog = new ProgressDialog(Registro_Edita.this);
            Cliente_pDialog.setMessage("Cargando...");
            Cliente_pDialog.setIndeterminate(false);
            Cliente_pDialog.setCancelable(true);
            Cliente_pDialog.show();
        }
        protected String doInBackground(String... args) {
            JSONArray JEquipos = null;
            Funcion_JSONParser jParserE = new Funcion_JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Login.USUARIO));
            JSONObject json = jParserE.makeHttpRequest(Login.SERVER + "/" + WS_clientes, "GET", params);

            try {
                JEquipos = json.getJSONArray("registros");
                //spinnerArray = new String[JEquipos.length()];
                for (int i = 0; i < JEquipos.length(); i++) {
                    JSONObject c = JEquipos.getJSONObject(i);
                    ArrayClientes.add(new SpinnerItems_Clase(c.getString("id"), c.getString("cliente"),""));

                }



                //String name = spinner.getSelectedItem().toString();
                //String id = spinnerMap.get(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            Cliente_pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    Spinner sp=(Spinner)findViewById(R.id.txt_codcli);
                    ArrayAdapter<SpinnerItems_Clase> adapter =new ArrayAdapter<SpinnerItems_Clase>(Registro_Edita.this,android.R.layout.simple_spinner_item, ArrayClientes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);


                }
            });
        }
    }
    class CargarComboTipos extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            Tipo_pDialog = new ProgressDialog(Registro_Edita.this);
            Tipo_pDialog.setMessage("Cargando...");
            Tipo_pDialog.setIndeterminate(false);
            Tipo_pDialog.setCancelable(true);
            Tipo_pDialog.show();
        }
        protected String doInBackground(String... args) {
            JSONArray JIdentifico = null;
            Funcion_JSONParser jParserN = new Funcion_JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Login.USUARIO));
            JSONObject json1 = jParserN.makeHttpRequest(Login.SERVER + "/" + WS_tipos, "GET", params);
            try {
                JIdentifico = json1.getJSONArray("registros");

                for (int i = 0; i < JIdentifico.length(); i++) {
                    JSONObject c1 = JIdentifico.getJSONObject(i);
                    ArrayTipos.add(new SpinnerItems_Clase(c1.getString("id"),c1.getString("tipo"),c1.getString("texto")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            Tipo_pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    Spinner sp=(Spinner)findViewById(R.id.txt_tipoob);
                    ArrayAdapter<SpinnerItems_Clase> adapter =new ArrayAdapter<SpinnerItems_Clase>(Registro_Edita.this,android.R.layout.simple_spinner_item, ArrayTipos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
                        {
                            if(alta) {
                                txt_observ= (EditText) findViewById(R.id.txt_observ);
                                txt_observ.setText(((SpinnerItems_Clase) adapterView.getItemAtPosition(position)).getTexto());
                               // Toast.makeText(adapterView.getContext(), ((SpinnerItems_Clase) adapterView.getItemAtPosition(position)).getTexto(), Toast.LENGTH_SHORT).show();
                            }
                            //   Toast.makeText(adapterView.getContext(), USUARIO, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                            // vacio

                        }
                    });

                }
            });
        }
    }
    private int selectSpinnerItemByValue(ArrayList ar, String value) {

        int index=0;
        for (int i = 0; i < ar.size(); i++) {
            if (((SpinnerItems_Clase)ar.get(i)).getName().equals(value)) {
                index=i;
            }
        }
        return index;
    }

    //botones
    public void guardarCerrar(View view) {

        if (Tools.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {

            txt_fecha = (TextView) findViewById(R.id.txt_fecha);
            txt_tipoob = (Spinner) findViewById(R.id.txt_tipoob);
            txt_codcli = (Spinner) findViewById(R.id.txt_codcli);
            txt_observ= (EditText) findViewById(R.id.txt_observ);
            txt_id= (TextView) findViewById(R.id.txt_id);
            txt_correc= (EditText) findViewById(R.id.txt_correc);


            if (txt_tipoob.getSelectedItem().toString().length() > 0 && txt_codcli.getSelectedItem().toString().length() > 0 && txt_observ.getText().toString().length() > 0) {
                fecha = txt_fecha.getText().toString();
                tipo = ((SpinnerItems_Clase) txt_tipoob.getSelectedItem()).getId();
                cliente = ((SpinnerItems_Clase) txt_codcli.getSelectedItem()).getId();
                obs = txt_observ.getText().toString();
                id = txt_id.getText().toString();
                correc = txt_correc.getText().toString();

                                // starting background task to update
            new GuardarObservacion().execute();

                if(alta) {
                    Funcion_EnviarMail sm = new Funcion_EnviarMail(Registro_Edita.this, id, Login.USUARIO, Login.MAIL, Login.CONTRASEÑA,"");
                    sm.execute();
                }
                else
                {
                    if(Login.SUPERVISOR.equals("3-Administrar"))
                    {
                        Funcion_EnviarMail sm = new Funcion_EnviarMail(Registro_Edita.this, id, Login.USUARIO, Login.MAIL, Login.CONTRASEÑA,Principal.USUARIO + "@grupo-cbs.com");
                        sm.execute();
                    }
                }


                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setMessage("Registro almacenado.");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cuando hago click en OK finalizo la Activity
                        Intent i = getIntent();
                        setResult(100, i);
                        finish();// cierro la activity editar
                    }
                });

                dialogo.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("QHSE Observaciones");
                alertDialog.setMessage("Verifique Tipo de Observacion, Cliente y Observacion");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((TextView) findViewById(R.id.txt_fecha)).findFocus();
                    }
                });
                alertDialog.show();
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Conectando...");
            alertDialog.setMessage("Verifique conexion a Internet solo por WIFI para Actualizar.");
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
        Intent mIntent= new Intent(this, Fotos_Galeria.class);
        mIntent.putExtra("EDITA", edita);
        mIntent.putExtra("codobs",id);
        startActivityForResult(mIntent, 100);
    }
    public void agregar_firma(View view){
        Intent mIntent= new Intent(this, Registro_Firma.class);
        mIntent.putExtra("EDITA", edita);
        mIntent.putExtra("codobs",id);
        startActivityForResult(mIntent, 100);
    }
}
