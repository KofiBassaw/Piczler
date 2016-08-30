package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.viethoa.RecyclerViewFastScroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by matiyas on 7/12/16.
 */
public class FriendActivity extends AppCompatActivity {


    ProgressBar pbBar;
    private Toolbar toolbar;
    ArrayList<GettersAndSetters> details;
    boolean hasUnblocked = false;
    ProgressDialog pDIalogi;
    UserFunctions functions;
    FriendAdapter adopter;
    RecyclerView recyclerView;
    TextView tvNousers,tvTitle;
    RecyclerViewFastScroller fastScroller;


    Map<Integer, String> numbers = new HashMap<Integer,String>();
    Map<Integer, String> names = new HashMap<Integer,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frined);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        tvNousers = (TextView) findViewById(R.id.tvNousers);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        details = new ArrayList<>();
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        fastScroller = (RecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        initRecyclerView();
        fastScroller.setRecyclerView(recyclerView);
        tvNousers.setText("No Contacts");
        tvTitle.setText("INVITE");

        if(!StaticVariables.loading)
        {
            details = StaticVariables.details;
            adopter = new FriendAdapter(details, FriendActivity.this);
            recyclerView.setAdapter(adopter);
            if(details.size() == 0)
                tvNousers.setVisibility(View.VISIBLE);
            pbBar.setVisibility(View.GONE);
            fastScroller.setUpAlphabet(StaticVariables.mAlphabetItems);
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
                StaticVariables.FRIEND));
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
                    numbers.put(pos,details.get(pos).phoneNum);

                }else
                {
                    //remove from map
                    names.remove(pos);
                    numbers.remove(pos);

                }
                adopter.notifyDataSetChanged();
            }else
            {
                if(type.contentEquals("loading"))
                {
                    details = StaticVariables.details;
                    adopter = new FriendAdapter(details, FriendActivity.this);
                    recyclerView.setAdapter(adopter);
                    if(details.size() == 0)
                        tvNousers.setVisibility(View.VISIBLE);
                    pbBar.setVisibility(View.GONE);

                    fastScroller.setUpAlphabet(StaticVariables.mAlphabetItems);
                }
            }


        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend, menu);


        return true;
    }





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
         sendContact();
     }

        return super.onOptionsItemSelected(item);
    }




private void sendContact()
{
    JsonArray contacts = new JsonArray();
    Set<Integer> keys = numbers.keySet();  //get all keys


    for(Integer i: keys)
    {
        JsonObject oneContact = new JsonObject();
        oneContact.addProperty(StaticVariables.NAME, names.get(i));
        oneContact.addProperty(StaticVariables.NUMBER, numbers.get(i));
        contacts.add(oneContact);
    }


    if(contacts.size() >0)
    {
        //send data here
        inviteContact(contacts);

    }else {
        functions.showMessage("Select at least one contact");
    }
}






    private void inviteContact(JsonArray contacts)
    {

        pDIalogi = new ProgressDialog(this);
        pDIalogi.setCancelable(true);
        pDIalogi.setMessage("Inviting contacts ...");
        pDIalogi.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST", StaticVariables.BASE_URL +"users/invite")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .setJsonArrayBody(contacts)
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
                                            functions.showMessage("invitation sent");
                                            details = StaticVariables.details;
                                            adopter = new FriendAdapter(details, FriendActivity.this);
                                            recyclerView.setAdapter(adopter);
                                            if(details.size() == 0)
                                                tvNousers.setVisibility(View.VISIBLE);
                                            pbBar.setVisibility(View.GONE);

                                            fastScroller.setUpAlphabet(StaticVariables.mAlphabetItems);
                                        } else if (code == 403 || code == 401) {
                                            pDIalogi.dismiss();;
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));


                                        } else {
                                            pDIalogi.dismiss();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));


                                        }
                                    } else {
                                        pDIalogi.dismiss();
                                        functions.showMessage("Unable invite contacts");


                                    }

                                } else {
                                    pDIalogi.dismiss();
                                    functions.showMessage("Unable to invite contacts");

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
