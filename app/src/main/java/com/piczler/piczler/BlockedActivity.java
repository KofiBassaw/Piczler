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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matiyas on 7/12/16.
 */
public class BlockedActivity extends AppCompatActivity {


    ProgressBar pbBar;
    TextView tvNousers;
    private Toolbar toolbar;
    ArrayList<GettersAndSetters> details;
    boolean hasUnblocked = false;
    ProgressDialog pDIalogi;
    UserFunctions functions;
    BlockedAdapter adopter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        tvNousers = (TextView) findViewById(R.id.tvNousers);
        details = new ArrayList<>();
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        initRecyclerView();
        getBlockedUsers();

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
                StaticVariables.UNBLOCK));
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

             unblockUser(intent.getExtras().getInt("pos"));

        }
    };






    private void unblockUser(final int position)
    {
        GettersAndSetters Details = details.get(position);
        String userID = Details.serverID;
        pDIalogi = new ProgressDialog(this);
        pDIalogi.setCancelable(true);
        pDIalogi.setMessage("Unblocking ...");
        pDIalogi.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("DELETE", StaticVariables.BASE_URL + "users/blocked/"+userID+"")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
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
                                            details.remove(position);
                                            adopter.notifyDataSetChanged();
                                            hasUnblocked = true;
                                            if(details.size() == 0)
                                                tvNousers.setVisibility(View.VISIBLE);



                                            try {

                                                MixpanelAPI mixpanel =
                                                        MixpanelAPI.getInstance(BlockedActivity.this, StaticVariables.MIXPANEL_TOKEN);

                                                mixpanel.getPeople().increment("Blocked Users",1);

                                            }catch (Exception ex)
                                            {
                                                ex.printStackTrace();
                                            }

                                        } else if (code == 403 || code == 401) {
                                            pDIalogi.dismiss();;
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));


                                        } else {
                                            pDIalogi.dismiss();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));


                                        }
                                    } else {
                                        pDIalogi.dismiss();
                                        functions.showMessage("Unable unblock user");


                                    }

                                } else {
                                    pDIalogi.dismiss();
                                    functions.showMessage("Unable to ubblock media");

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(hasUnblocked)
            setResult(RESULT_OK);

        super.onBackPressed();
    }

    private void bindData(String jsonString)
    {
        try
        {
            GettersAndSetters Details;
            JSONArray users = new JSONArray(jsonString);
            for(int i=0; i<users.length(); i++)
            {
                JSONObject oneUser  = users.getJSONObject(i);
                Details = new GettersAndSetters();
                Details.setName(functions.getJsonString(oneUser, StaticVariables.DISPLAYNAME));
                Details.setServerID(functions.getJsonString(oneUser, StaticVariables.ID));
                JSONObject pp = functions.getJsonObject(oneUser,StaticVariables.PROFILE_PICTURE);
                JSONObject mobile_resolution = functions.getJsonObject(pp,StaticVariables.MOBILE_RESOLUTION);
                Details.setCover(functions.getJsonString(mobile_resolution, StaticVariables.URL));
                details.add(Details);
            }


        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }





    class BindAsync extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        /**
         * Creating product
         * */
        @Override
        protected String doInBackground(String... args) {

            try{
                JSONArray jsonArray = new JSONArray(args[0]);
                bindData(args[0]);

            }catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }




            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {

                adopter = new BlockedAdapter(details, BlockedActivity.this);
                recyclerView.setAdapter(adopter);
            if(details.size() == 0)
                tvNousers.setVisibility(View.VISIBLE);
                pbBar.setVisibility(View.GONE);





        }





    }




    private void getBlockedUsers(){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
           // System.out.println(StaticVariables.CATEGORIES + "?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit);
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "users/blocked")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);
                                    functions.setPref(StaticVariables.HASCATEGORY,true);
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {


                                                    new BindAsync().execute(data.toString());



                                            } else {
                                                functions.showMessage("Unable to retrieve blocked users");
                                                tvNousers.setVisibility(View.VISIBLE);
                                            }

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            tvNousers.setVisibility(View.VISIBLE);
                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            tvNousers.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        functions.showMessage("Unable to retrieve blocked users");
                                        tvNousers.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    functions.showMessage("Unable to retrieve blocked users");
                                    tvNousers.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            tvNousers.setVisibility(View.VISIBLE);
        }


    }


}
