package com.example.gabriel.iapp;
import android.content.Intent;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.gabriel.iapp.Utils.Listado_TabPagerAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;




public class Listado extends FragmentActivity {
    ViewPager Tab;
    Listado_TabPagerAdapter TabAdapter;
    ActionBar actionBar;



   ActionBar.TabListener tabListener = new ActionBar.TabListener(){

        @Override
        public void onTabReselected(android.app.ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            // TODO Auto-generated method stub
          // Tab.setCurrentItem(tab.getPosition());

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {




            Tab.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(android.app.ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado);



   /*    Bundle bundle=getIntent().getExtras();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("us", bundle.getString("us")));*/

        menu();

    }


    protected void onPause()
    {
        super.onPause();


    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    private void menu()
    {


        TabAdapter=null;
        TabAdapter = new Listado_TabPagerAdapter(getSupportFragmentManager());

        Tab=null;
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("position", String.valueOf(position));
                actionBar=null;
                actionBar = getActionBar();
                actionBar.setSelectedNavigationItem(position);
            }
        });
        Tab.setAdapter(TabAdapter);


        actionBar=null;
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        actionBar.removeAllTabs();
        actionBar.addTab(actionBar.newTab().setText("Pendientes").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Finalizados").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Compartidos").setTabListener(tabListener));



    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100) {
            // if result code 100

        }

        if (resultCode == 100) {
            this.recreate();
        }

    }







}