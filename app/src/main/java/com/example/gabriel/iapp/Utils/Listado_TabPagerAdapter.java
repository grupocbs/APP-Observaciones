package com.example.gabriel.iapp.Utils;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gabriel.iapp.Listado_Finalizados;
import com.example.gabriel.iapp.Listado_Pendientes;


public class Listado_TabPagerAdapter extends FragmentPagerAdapter  {


    public Listado_TabPagerAdapter(FragmentManager fm) {

        super(fm);

        // TODO Auto-generated constructor stub
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new Listado_Pendientes();
            case 1:
                return new Listado_Finalizados();
            default: return null;


          }


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 2; //No of Tabs
    }


}