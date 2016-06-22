package com.example.gabriel.iapp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.SpinnerItems_Clase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Principal extends Activity {

    private static final String WS_usuarios = "App_OPOBOJ_usuarios.aspx";
    private ProgressDialog Usuario_pDialog;
    Funcion_JSONParser jsonParser = new Funcion_JSONParser();
    private Spinner txt_usuario;
    ArrayList<SpinnerItems_Clase> ArrayUsuarios = new ArrayList<>();
    public static String USUARIO = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.principal);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if(isOnline()) {
            new CargarComboUsuarios().execute();

        }

        switch (Login.SUPERVISOR)
        {
            case "1-Ver":
            {
                ((Button) findViewById(R.id.btn_nuevo)).setEnabled(false);
                break;
            }
            case "2-Cargar":
            {
                ((Spinner) findViewById(R.id.txt_usuario)).setEnabled(false);
                break;
            }
            case "3-Administrar":
            {

                break;
            }
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


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())

            return true;
        else
            return false;
    }



    public void Crear(View view) {

        Intent mIntent= new Intent(this, Registro_Edita.class);
        startActivityForResult(mIntent, 100);


    }

    public void Listar(View view) {

        Intent mIntent= new Intent(this, Listado.class);
        mIntent.putExtra("us", this.USUARIO);
        startActivityForResult(mIntent, 100);


    }

    class CargarComboUsuarios extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            Usuario_pDialog = new ProgressDialog(Principal.this);
            Usuario_pDialog.setMessage("Cargando...");
            Usuario_pDialog.setIndeterminate(false);
            Usuario_pDialog.setCancelable(true);
            Usuario_pDialog.show();
        }
        protected String doInBackground(String... args) {
            JSONArray JEquipos = null;
            Funcion_JSONParser jParserE = new Funcion_JSONParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Login.USUARIO));
            JSONObject json = jParserE.makeHttpRequest(Login.SERVER + "/" + WS_usuarios, "GET", params);

            try {
                JEquipos = json.getJSONArray("registros");
                //spinnerArray = new String[JEquipos.length()];
                for (int i = 0; i < JEquipos.length(); i++) {
                    JSONObject c = JEquipos.getJSONObject(i);
                    ArrayUsuarios.add(new SpinnerItems_Clase(c.getString("id"), c.getString("tipo"),""));

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
            Usuario_pDialog.dismiss();


            runOnUiThread(new Runnable() {
                public void run() {

                    Spinner sp=(Spinner)findViewById(R.id.txt_usuario);
                    ArrayAdapter<SpinnerItems_Clase> adapter =new ArrayAdapter<SpinnerItems_Clase>(Principal.this,android.R.layout.simple_spinner_item, ArrayUsuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
                        {
                            USUARIO=((SpinnerItems_Clase) adapterView.getItemAtPosition(position)).getName();
                         //   Toast.makeText(adapterView.getContext(), USUARIO, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                            // vacio

                        }
                    });

                    ((Spinner) findViewById(R.id.txt_usuario)).setSelection(selectSpinnerItemByValue(ArrayUsuarios,Login.USUARIO));
                }
            });
        }
    }


}
