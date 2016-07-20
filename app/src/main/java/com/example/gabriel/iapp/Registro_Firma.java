package com.example.gabriel.iapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gabriel.iapp.Utils.Firma.SignaturePad;
import com.example.gabriel.iapp.Utils.Funcion_EnviarFotos;
import com.example.gabriel.iapp.Utils.Funcion_JSONParser;
import com.example.gabriel.iapp.Utils.Tools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Registro_Firma extends Activity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private String codobs;
    private File photo;
    private String nombreImagen;
    ProgressDialog pDialog;
    Bitmap signatureBitmap;
    Boolean b;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.registro_firma);


        Bundle bundle=getIntent().getExtras();
        codobs = bundle.getString("codobs");

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Toast.makeText(firma.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               try {
                   signatureBitmap = mSignaturePad.getSignatureBitmap();
                   nombreImagen = "firma_" + Login.USUARIO + "_" + codobs + ".jpg";
                   photo = new File(Environment.getExternalStorageDirectory(), nombreImagen);
                   saveBitmapToJPG(signatureBitmap, photo);
                   scanMediaFile(photo);


                   if (Tools.isOnline((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
                       new SubirFirma().execute();
                       finish();
                   }

                   else {
                       AlertDialog alertDialog = new AlertDialog.Builder(Registro_Firma.this).create();
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
                 catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 1280) / bitmap.getWidth();
        float scaleHeight = ((float) 720) / bitmap.getHeight();
        matrix.postScale(scaleWidth,scaleHeight);

        //Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight(), matrix,true);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }
    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        Registro_Firma.this.sendBroadcast(mediaScanIntent);
    }
    class SubirFirma extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {

            uploadFoto();
            b=onInsert();
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registro_Firma.this);
            pDialog.setMessage("Enviando Firma, espere...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                    pDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if(b) {
                        Toast.makeText(Registro_Firma.this, "Firma enviada",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Registro_Firma.this, "Firma No enviada",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private void uploadFoto() {
        try {

            if(photo !=null && nombreImagen.length()>0) {
                FileInputStream fstrm = new FileInputStream(photo);
                Funcion_EnviarFotos hfu = new Funcion_EnviarFotos(Login.SERVER + "/App_OPOBCP_subircaptura.aspx", nombreImagen, Login.USUARIO + "\\" + codobs);
                hfu.Send_Now(fstrm);
            }
            else {
                Log.d("No subio archivo", "firma");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private boolean onInsert() {

        Funcion_JSONParser jsonParser = new Funcion_JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", codobs));
        params.add(new BasicNameValuePair("us", Login.USUARIO));
        params.add(new BasicNameValuePair("nombre_imagen_firma", nombreImagen));


        boolean b = false;
        try {

            JSONObject json = jsonParser.makeHttpRequest(Login.SERVER + "/App_OPOBOJ_guarda.aspx", "GET", params);
            JSONArray shirtObj = json.getJSONArray("registros"); // JSON Array
            b = true;

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return b;
    }


}
