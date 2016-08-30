package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by matiyas on 7/14/16.
 */
public class LocationActivity extends AppCompatActivity {


SwitchCompat switch_compat;
    boolean loading = false;
    ProgressBar pbBar;
    private Toolbar toolbar;
    ArrayList<GettersAndSetters> details;
    boolean hasUnblocked = false;
    ProgressDialog pDIalogi;
    UserFunctions functions;
    LocationAdapter adopter;
    RecyclerView recyclerView;

    Map<Integer, String> codes = new HashMap<Integer,String>();
    Map<Integer, String> names = new HashMap<Integer,String>();

    boolean locationMonitor = false;
    String locComma = "";
    String locText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        switch_compat= (SwitchCompat) findViewById(R.id.switch_compat);
        initRecyclerView();

        locationMonitor =  functions.getPref(StaticVariables.HASLOCATION,false);
        locComma =  functions.getPref(StaticVariables.LOCATIONCOMMASEPERATED,"");

        switch_compat.setChecked(!locationMonitor);

        pbBar.setVisibility(View.GONE);
        switch_compat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                functions.setPref(StaticVariables.HASLOCATION,!switch_compat.isChecked());
                if(!switch_compat.isChecked())
                {
                    //show location view Holder
                    if(StaticVariables.loadingCOUNTRIES)
                    {
                        loading = true;
                        //show a progress bar
                        pbBar.setVisibility(View.VISIBLE);
                    }else
                    {
                        details = StaticVariables.countries;
                        pbBar.setVisibility(View.GONE);
                        loading = false;
                        //show list
                        adopter = new LocationAdapter(details, LocationActivity.this);
                        recyclerView.setAdapter(adopter);
                    }
                }else
                {
                    loading = false;
                    details = new ArrayList<GettersAndSetters>();
                    pbBar.setVisibility(View.GONE);
                    adopter = new LocationAdapter(details, LocationActivity.this);
                    recyclerView.setAdapter(adopter);
                    //hide list

                }
            }
        });

        if(locationMonitor)
        {
            if(StaticVariables.loadingCOUNTRIES)
            {
                loading = true;
                //show a progress bar
                pbBar.setVisibility(View.VISIBLE);
            }else
            {
                details = StaticVariables.countries;
                pbBar.setVisibility(View.GONE);
                loading = false;
                //show list
                adopter = new LocationAdapter(details, LocationActivity.this);
                recyclerView.setAdapter(adopter);
            }
        }


    }


    private void initRecyclerView() {


        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //  int leftRight = getResources().getDimensionPixelSize(R.dimen.leftright);
        //  int top = getResources().getDimensionPixelSize(R.dimen.top);
        //  recyclerView.addItemDecoration(new SpacesItemDecoration(leftRight,top));


    }


    @Override
    protected void onResume() {
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                StaticVariables.COUNTRIES));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("rror", "> " + e.getMessage());
        }
        super.onDestroy();
    }




    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if(type == null)
            {

                int pos = intent.getExtras().getInt("pos");
                details.get(pos).selected = intent.getExtras().getBoolean("check");


                if(intent.getExtras().getBoolean("check")) {
                    //addd to map
                    names.put(pos,details.get(pos).name);
                    codes.put(pos,details.get(pos).code);

                }else
                {
                    //remove from map
                    names.remove(pos);
                    codes.remove(pos);

                }
                adopter.notifyDataSetChanged();
            }else
            {
                if(type.contentEquals("loading"))
                {
                    if(loading == true)
                    {
                        loading = false;
                        details = StaticVariables.countries;
                        adopter = new LocationAdapter(details, LocationActivity.this);
                        recyclerView.setAdapter(adopter);
                        pbBar.setVisibility(View.GONE);
                    }


                }
            }


        }
    };




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            onBackPressed();
        }else if(id == R.id.action_done)
        {
            //send contact list here
            //sendContact();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        String locationSeperated = "";
        String names = "";
        if(!functions.getPref(StaticVariables.HASLOCATION,false))
        {
           functions.setPref(StaticVariables.LOCATIONCOMMASEPERATED, "");
            functions.setPref(StaticVariables.LOCATIONMAESSEPERATED,"");
        }else
        {

            for(int i =0; i<details.size(); i++)
            {
                GettersAndSetters Details = details.get(i);

                if(Details.selected)
                {

                    if(locationSeperated.length()>0) {
                        locationSeperated += ",";
                        names += ",";
                    }

                    locationSeperated+=Details.code;
                    names+=Details.name;
                }


            }
            if(locationSeperated.length()>0)
                locationSeperated+=",";
            functions.setPref(StaticVariables.LOCATIONCOMMASEPERATED,locationSeperated);
            functions.setPref(StaticVariables.LOCATIONMAESSEPERATED,names);
        }

        System.out.println("bbbbbbbb: locationMonitor: "+locationMonitor+"   HASLOCATION: "+functions.getPref(StaticVariables.HASLOCATION,false));
        if(locationMonitor != functions.getPref(StaticVariables.HASLOCATION,false))
        {
            setResult(RESULT_OK);
        }else {
            if(!locationSeperated.contentEquals(locComma))
            {
                setResult(RESULT_OK);
            }
        }





        super.onBackPressed();
    }
}
