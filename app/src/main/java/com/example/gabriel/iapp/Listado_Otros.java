package com.example.gabriel.iapp;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.Tools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Listado_Otros extends ListFragment{


    ArrayList<HashMap<String, String>> ObservacionesListF;
    private static String WS = "App_OPOBOJ_listaOtros.aspx";
    private static final String TAG_REGISTROS = "registros";
    private static final String TAG_ID = "ID";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_TIPO = "tipo";
    private static final String TAG_CLIENTE = "cliente";
    private static final String TAG_ESTADO = "estado";
    private static final String TAG_INFORMO = "informo";
    private ProgressDialog pDialog;


    JSONArray JticketsF = null;
    Funcion_JSONParser jParserF = new Funcion_JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.listado_otros, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (Tools.isOnline((ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            listar();
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Conectando...");
            alertDialog.setMessage("Verifique conexion a Internet.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Intent mIntent= new Intent(getActivity().getApplicationContext(), Registro_Edita.class);

        TextView i = (TextView) v.findViewById(R.id.lblID);
        TextView us = (TextView) v.findViewById(R.id.lblINFORMO);
        mIntent.putExtra("ID", i.getText().toString());
        mIntent.putExtra("USUARIO", us.getText().toString());
        mIntent.putExtra("EDITA", "0");

        startActivityForResult(mIntent, 100);

    }

    public void listar(){
        new GetProductDetails().execute();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100) {
            // if result code 100
            if (resultCode == 100) {

            }
        }

    }



    class GetProductDetails extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Cargando Observaciones. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {

            ObservacionesListF = new ArrayList<HashMap<String, String>>();


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Principal.USUARIO));


            JSONObject jsonF = jParserF.makeHttpRequest(Login.SERVER + "/" + WS, "GET", params);

            try {
                JticketsF = jsonF.getJSONArray(TAG_REGISTROS);

                // looping through All Products
                for (int i = 0; i < JticketsF.length(); i++) {
                    JSONObject c = JticketsF.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(TAG_ID);
                    String fecha = c.getString(TAG_FECHA);
                    String tipo = c.getString(TAG_TIPO);
                    String cliente=c.getString(TAG_CLIENTE);
                    String estado=c.getString(TAG_ESTADO);
                    String informo=c.getString(TAG_INFORMO);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    // adding each child node to HashMap key => value
                    map.put(TAG_ID, id);
                    map.put(TAG_FECHA, fecha);
                    map.put(TAG_TIPO, tipo);
                    map.put(TAG_CLIENTE, cliente);
                    map.put(TAG_ESTADO, estado);
                    map.put(TAG_INFORMO, informo);

                    ObservacionesListF.add(map);
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
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    if (getActivity() != null)
                    {

                        ListAdapter adapter = new SimpleAdapter(
                                getActivity(),
                                ObservacionesListF,
                                R.layout.fila_otros,
                                new String[]{TAG_FECHA, TAG_CLIENTE, TAG_TIPO, TAG_ID, TAG_ESTADO, TAG_INFORMO},
                                new int[]{R.id.lblFECHA, R.id.lblCLIENTE, R.id.lblTIPO, R.id.lblID,R.id.lblESTADO, R.id.lblINFORMO});

                        setListAdapter(adapter);
                    }

                }
            });
        }


    }
}
