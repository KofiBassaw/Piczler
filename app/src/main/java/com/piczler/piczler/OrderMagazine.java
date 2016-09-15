package com.piczler.piczler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by matiyas on 8/14/16.
 */



public class OrderMagazine extends AppCompatActivity {



    ViewPager pager;
    ViewPagerMagazineAdapter adapter;
    public static ArrayList<GettersAndSetters> picDetails;
    public static ArrayList<GettersAndSetters> faceDetails;
    public static ArrayList<GettersAndSetters> instDetails;
    // SlidingTabLayout tabs;
    int Numboftabs = 3;
    CharSequence Titles[]={"Piczler","Facebook","Instagram"};
    public static RelativeLayout mToolbarContainer;
    public static FragmentManager fragmentManager;
    private Toolbar toolbar;
    UserFunctions functions;
    private int[] imageResId = { R.drawable.piczler, R.drawable.facebook,R.drawable.camerasample};



    public  static int offset = 0;
    public static Map<String, String> selected = new HashMap<String,String>();

ProgressDialog pDIalogi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine);
        mToolbarContainer = (RelativeLayout) findViewById(R.id.toolbarContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fragmentManager = getSupportFragmentManager();
        functions = new UserFunctions(this);

        picDetails = new ArrayList<>();
        faceDetails = new ArrayList<>();
        instDetails = new ArrayList<>();

       // picDetails = StaticVariables.piczlerMag;
       // faceDetails = StaticVariables.facebookMag;
       // instDetails = StaticVariables.instaMag;

        offset = getIntent().getExtras().getInt("offset");




        adapter =  new ViewPagerMagazineAdapter(fragmentManager,Titles,Numboftabs,this);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.materialup_tabs);
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }


    }


    @Override
    protected void onDestroy() {

        picDetails = null;
        faceDetails = null;
        instDetails = null;

        super.onDestroy();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.magazine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order) {
            Set<String> keys = selected.keySet();


            if(keys.size() >= 5)
            {
                JsonArray pages = new JsonArray();
                for(String i: keys)
                {
                    JsonObject oneContact = new JsonObject();
                    oneContact.addProperty("url", i);
                    oneContact.addProperty("media_id", selected.get(i));
                    pages.add(oneContact);
                }
                JsonObject json = new JsonObject();
                json.add("pages",pages);
                prepareMagazine(json);

            }else {
                functions.showMessage("select at least 20 pictures");
            }

            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void  prepareMagazine(final JsonObject json )
    {

        final Dialog credDialog=new Dialog(this);
        credDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        credDialog.setContentView(R.layout.pincode_dialog);

        RelativeLayout rlContinue = (RelativeLayout) credDialog.findViewById(R.id.rlContinue);
        final RippleView rpContine = (RippleView) credDialog.findViewById(R.id.rpContinue);
        final TextView tvContinue = (TextView) credDialog.findViewById(R.id.tvContinue);
        final EditText etPincode = (EditText) credDialog.findViewById(R.id.etPincode);

        rlContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpContine.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        String name = etPincode.getText().toString().trim();


                        if(name.contentEquals("")){
                            functions.showMessage("enter magazine name");
                        }else
                        {
                            credDialog.dismiss();
                            json.addProperty("name",name);
                            orderNow(json);
                        }

                    }
                });
            }
        });


        credDialog.show();


    }





    private void orderNow(JsonObject pages)
    {

        pDIalogi = new ProgressDialog(this);
        pDIalogi.setCancelable(true);
        pDIalogi.setMessage("Sending order ...");
        pDIalogi.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST", StaticVariables.BASE_URL +"magazines")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .setJsonObjectBody(pages)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    //System.out.println("---------------------------------- error");
                                }
                                System.out.println("bbbbbb: " + result);
                                pDIalogi.dismiss();
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {


                                            try
                                            {
                                                MixpanelAPI mixpanel =
                                                        MixpanelAPI.getInstance(OrderMagazine.this, StaticVariables.MIXPANEL_TOKEN);

                                                mixpanel.getPeople().set("Magazine Order", "YES");
                                                mixpanel.track("Magazine Order");


                                            }catch (Exception ex)
                                            {
                                                ex.printStackTrace();
                                            }

                                            AlertDialog dd = new AlertDialog.Builder(OrderMagazine.this).create();
                                            dd.setTitle("Order Completed");
                                            dd.setCancelable(false);
                                            dd.setMessage("Your magazine order was completed. We will get back to you shortly");
                                            dd.setButton(Dialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    onBackPressed();
                                                }
                                            });


                                            dd.show();


                                        } else if (code == 403 || code == 401) {
                                            pDIalogi.dismiss();;
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));


                                        } else {
                                            pDIalogi.dismiss();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));


                                        }
                                    } else {
                                        pDIalogi.dismiss();
                                        functions.showMessage("Unable to order magazine");


                                    }

                                } else {
                                    pDIalogi.dismiss();
                                    functions.showMessage("Unable to order magazine");

                                }

                            } catch (Exception ex) {
                                pDIalogi.dismiss();
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            pDIalogi.dismiss();
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();


        }

    }

}
