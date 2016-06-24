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

public class Listado_Pendientes extends ListFragment{

    public static String ESTADO="P";
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> ObservacionesListP;
    private static String WS = "App_OPOBOJ_lista.aspx";
    private static final String TAG_REGISTROS = "registros";
    private static final String TAG_ID = "ID";
    private static final String TAG_FECHA = "fecha";
    private static final String TAG_TIPO = "tipo";
    private static final String TAG_CLIENTE = "cliente";

    JSONArray JticketsP = null;
    Funcion_JSONParser jParserP = new Funcion_JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.listado_pendientes, container, false);

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

        TextView c = (TextView) v.findViewById(R.id.lblID);
        Intent mIntent= new Intent(getActivity().getApplicationContext(), Registro_Edita.class);

        mIntent.putExtra("ID", c.getText().toString());
        mIntent.putExtra("EDITA", "1");
        startActivityForResult(mIntent, 100);

    }

    public void listar(){
        new GetProductDetails().execute();
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
            ObservacionesListP = new ArrayList<HashMap<String, String>>();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("us", Principal.USUARIO));
            params.add(new BasicNameValuePair("estado", ESTADO));


            JSONObject jsonP = jParserP.makeHttpRequest(Login.SERVER + "/" + WS, "GET", params);

            try {
                JticketsP = jsonP.getJSONArray(TAG_REGISTROS);

                // looping through All Products
                for (int i = 0; i < JticketsP.length(); i++) {
                    JSONObject c = JticketsP.getJSONObject(i);

                    // Storing each json item in variable
                    String id = c.getString(TAG_ID);
                    String fecha = c.getString(TAG_FECHA);
                    String tipo = c.getString(TAG_TIPO);
                    String cliente = c.getString(TAG_CLIENTE);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    // adding each child node to HashMap key => value
                    map.put(TAG_ID, id);
                    map.put(TAG_FECHA, fecha);
                    map.put(TAG_TIPO, tipo);
                    map.put(TAG_CLIENTE, cliente);


                    // adding HashList to ArrayList
                    ObservacionesListP.add(map);
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }



        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (getActivity() != null) {

                        ListAdapter adapter = new SimpleAdapter(

                                getActivity(),
                                ObservacionesListP,
                                R.layout.fila_pendiente,
                                new String[]{TAG_FECHA, TAG_CLIENTE, TAG_TIPO, TAG_ID},
                                new int[]{R.id.lblFECHA, R.id.lblCLIENTE, R.id.lblTIPO, R.id.lblID});

                        setListAdapter(adapter);

                    }
                }
            });
        }


    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100) {
            // if result code 100
            if (resultCode == 100) {

              //  Log.d("Listado", "cambio el dataset");
            //    ObservacionesListP.clear();

             // new GetProductDetails().execute();
            }
        }

    }




}
