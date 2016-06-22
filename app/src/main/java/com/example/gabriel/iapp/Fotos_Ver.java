package com.example.gabriel.iapp;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fotos_Ver extends ActionBarActivity {

    private ProgressDialog pDialog;
    Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_ver);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new TraerFoto().execute();

    }





    class TraerFoto extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Fotos_Ver.this);
            pDialog.setMessage("Cargando Foto. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            try {

                URL imgURI = new URL(getIntent().getStringExtra("url"));
                HttpURLConnection connection = (HttpURLConnection) imgURI.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                myBitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                // Log exception
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


                    String title = getIntent().getStringExtra("title");
                    TextView titleTextView = (TextView) findViewById(R.id.title);
                    titleTextView.setText(title);

                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    imageView.setImageBitmap(myBitmap);

                    if (myBitmap != null && !myBitmap.isRecycled())
                    {
                        myBitmap = null;
                        System.gc();
                    }
                }
            });
        }
    }
}
