package com.example.gabriel.iapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.gabriel.iapp.Utils.Funcion_EnviarFotos;
import com.example.gabriel.iapp.Utils.Funcion_JSONParser;

public class Registro_CargaFotos extends ActionBarActivity {

	private Button camara;
	private Button buscar_foto;
	private ImageView imagen;
	private EditText nombreImagen;
	private Button upload;
	private Uri output;
	private String foto;
	private File file;
	String codobs;
	ProgressDialog pDialog;
	String path = "";
	private static final int ACTIVITY_SELECT_IMAGE = 1020, ACTIVITY_SELECT_FROM_CAMERA = 1040;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.registro_cargafotos);

		Bundle bundle = getIntent().getExtras();
		codobs = bundle.getString("codobs");

		nombreImagen = (EditText) findViewById(R.id.nombreImagen);


		camara = (Button) findViewById(R.id.camara);
		camara.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!nombreImagen.getText().toString().trim().replace("ñ", "n").equalsIgnoreCase("")) {
					getCamara();
				} else
					Toast.makeText(Registro_CargaFotos.this, "Debe nombrar el archivo primero",
							Toast.LENGTH_LONG).show();
			}

		});
		imagen = (ImageView) findViewById(R.id.imagen);

		upload = (Button) findViewById(R.id.button1);
		upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				serverUpdate();
			}

		});

		buscar_foto = (Button) findViewById(R.id.btn_buscar);
		buscar_foto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (!nombreImagen.getText().toString().trim().replace("ñ", "n").equalsIgnoreCase("")) {
					getGaleria();
				} else
					Toast.makeText(Registro_CargaFotos.this, "Debe nombrar el archivo primero",
							Toast.LENGTH_LONG).show();

			}

		});

	}


	private void getCamara() {
		foto = Environment.getExternalStorageDirectory() + "/"
				+ nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg";
		file = new File(foto);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		output = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, ACTIVITY_SELECT_FROM_CAMERA);
	}
	private void getGaleria() {
		foto = Environment.getExternalStorageDirectory() + "/"
				+ nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg";
		file = new File(foto);
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
		//output = Uri.fromFile(file);
		galleryIntent.setType("image/*");
		startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bitmap bit = null;
		ContentResolver cr = this.getContentResolver();
		if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
			output = data.getData();
		}

		try {
			bit = android.provider.MediaStore.Images.Media.getBitmap(cr, output);
			if (bit.getWidth() > bit.getHeight()) {
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				bit = Bitmap.createBitmap(bit , 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
			}

			//ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			//bit.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

			path = Environment.getExternalStorageDirectory() + "/" + nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg";

			FileOutputStream fOut = new FileOutputStream(path);
			bit.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
			fOut.flush();
			fOut.close(); // do not forget to close the stream

			MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());



			//File destination = new File(Environment.getExternalStorageDirectory(), nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg");

/*
			File destination = new File(Environment.getExternalStorageDirectory(), nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg");

			FileOutputStream fo;
			try {
				destination.createNewFile();
				fo = new FileOutputStream(destination);
				fo.write(bytes.toByteArray());
				fo.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*int rotate = 0;
			try {
				ExifInterface exif = new ExifInterface(
						destination.getAbsolutePath());

				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				Log.d("rotate", String.valueOf(orientation));
				switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						Log.d("rotate", "270");
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						Log.d("rotate", "180");
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						Log.d("rotate", "90");
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("rotate", "EX");
			}
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			Log.d("rotate", String.valueOf(rotate));
			bit = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);

			//bit= getResizedBitmap(bit,800,600);*/
			//bit = BitmapFactory.decodeFile(destination.getPath());






		}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		imagen.setImageBitmap(bit);


		if (bit != null && !bit.isRecycled()) {
			bit = null;
			System.gc();
		}
	}


	public String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

		if (immagex != null && !immagex.isRecycled()) {
			//immagex.recycle();
			immagex = null;
			System.gc();
		}
		return imageEncoded;
	}

	private void uploadFoto() {
		try {

			FileInputStream fstrm = new FileInputStream(path);

			Funcion_EnviarFotos hfu = new Funcion_EnviarFotos(Login.SERVER + "/App_OPOBCP_subircaptura.aspx", nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg", Login.USUARIO + "\\" + codobs);

			hfu.Send_Now(fstrm);
			Log.d("subio archivo", "camara");

		} catch (FileNotFoundException e) {
			Log.d("error subir archivo", "camara");
		}
	}
	private boolean onInsert() {

		Log.d("PATH", "onInsert()");


		Funcion_JSONParser jsonParser = new Funcion_JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("codobs", codobs));
		params.add(new BasicNameValuePair("us", Login.USUARIO));
		params.add(new BasicNameValuePair("nombre_imagen", nombreImagen.getText().toString().trim().replace("ñ", "n") + ".jpg"));


		boolean b = false;
		try {

			JSONObject json = jsonParser.makeHttpRequest(Login.SERVER + "/App_OPOBOJ_guarda_captura.aspx", "GET", params);
			JSONArray shirtObj = json.getJSONArray("registros"); // JSON Array
			b = true;

		} catch (JSONException e) {
			e.printStackTrace();

		}
		return b;
	}
	private void serverUpdate() {

		if (file.exists()) new ServerUpdate().execute();
		finish();
	}

	class ServerUpdate extends AsyncTask<String, String, String> {


		@Override
		protected String doInBackground(String... arg0) {

			uploadFoto();
			if (onInsert())
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(Registro_CargaFotos.this, "Imagen enviada",
								Toast.LENGTH_LONG).show();
					}
				});
			else
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(Registro_CargaFotos.this, "No se pudo enviar la imagen",
								Toast.LENGTH_LONG).show();
					}
				});
			return null;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Registro_CargaFotos.this);
			pDialog.setMessage("Actualizando Servidor, espere...");
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.dismiss();
		}
	}
}


