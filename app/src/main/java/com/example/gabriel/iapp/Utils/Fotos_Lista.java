package com.example.gabriel.iapp.Utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gabriel.iapp.R;


public class Fotos_Lista extends ArrayAdapter<Fotos_Clase> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Fotos_Clase> data = new ArrayList<Fotos_Clase>();

    public Fotos_Lista(Context context, int layoutResourceId, ArrayList<Fotos_Clase> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Fotos_Clase item = data.get(position);
        holder.imageTitle.setText(item.getTitle());


        holder.image.setImageBitmap(Bitmap.createScaledBitmap(item.getImage(),80,100,false));
        //holder.image.setImageBitmap(item.getImage());
        System.gc();

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}