package com.piczler.piczler;

import android.content.Intent;
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
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matiyas on 8/21/16.
 */
public class NotificationsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    ProgressBar pbBar;
    UserFunctions functions;
    RecyclerView recyclerView;
    SwipyRefreshLayout swipyrefreshlayout;
    int limit = 20;
    int offset = 0;
    NotifiactionAdapter recyclerAdapter;
    ArrayList<GettersAndSetters> details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        swipyrefreshlayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        initRecyclerView();
        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                offset = offset + 20;
                getNotification(offset, limit);
            }
        });

        pbBar.setVisibility(View.VISIBLE);
        getNotification(offset, limit);

    }


    private void initRecyclerView() {


        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));






        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                         GettersAndSetters Details = details.get(position);
                        Intent it = new Intent(NotificationsActivity.this, Pictures.class);
                        it.putExtra(StaticVariables.ID,Details.serverID);
                        if(Details.type == 0)
                        {
                            it.putExtra(StaticVariables.FROM,StaticVariables.LIKETYPE);
                            //comment type

                        }else if(Details.type == 1)
                        {
                            //like type
                            it.putExtra(StaticVariables.FROM,StaticVariables.COMMENTTYPE);

                        }
                        startActivity(it);

                    }
                })
        );
    }




    private void bindData(JSONArray jsonArray){
        try{
            if(offset == 0)
                details = new ArrayList<>();


            GettersAndSetters Details;
            for(int i=0; i< jsonArray.length(); i++){
                JSONObject c = jsonArray.getJSONObject(i);
                System.out.println("bbbbbbbbbbbbbbbbbbb:  "+c.toString());
                Details = new GettersAndSetters();
                Details.setName(functions.getJsonString(c,"description"));
                Details.setServerID(functions.getJsonString(c, "media_id"));
                Details.setType(functions.getInt(c, "type"));
                details.add(Details);

                Intent it  = new Intent("com.piczler.piczler.broadcast");
                it.putExtra("type", "comment");
                it.putExtra(StaticVariables.ID,functions.getJsonString(c, "media_id"));
                sendBroadcast(it);

            }
        }catch (Exception e){
            e.printStackTrace();
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
                bindData(jsonArray);

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
            if (offset == 0){
                recyclerAdapter = new NotifiactionAdapter(details, NotificationsActivity.this);
                recyclerView.setAdapter(recyclerAdapter);
                pbBar.setVisibility(View.GONE);
            }else{
                recyclerAdapter.notifyDataSetChanged();
            }




        }





    }





    private void getNotification(final int offset, final int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //

            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "users/activities?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                swipyrefreshlayout.setRefreshing(false);
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
                                               // String  dataString = functions.getPref(StaticVariables.CATEGORIES,"");


                                                    new BindAsync().execute(data.toString());




                                            } else {
                                                reduceOffset();
                                                functions.showMessage("Unable to retrieve data");
                                            }

                                        } else if (code == 403 || code == 401) {
                                            reduceOffset();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        } else {
                                            reduceOffset();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                        }
                                    } else {
                                        reduceOffset();
                                        functions.showMessage("Unable to retrieve categories");
                                    }

                                } else {
                                    reduceOffset();
                                    functions.showMessage("Unable to retrieve categories");
                                }

                            } catch (Exception ex) {
                                reduceOffset();
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            reduceOffset();
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }




    private void reduceOffset()
    {
        if(offset>0)
        {
            offset -=20;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notification, menu);


        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {

          details = new ArrayList<>();
            recyclerAdapter = new NotifiactionAdapter(details, NotificationsActivity.this);
            recyclerView.setAdapter(recyclerAdapter);

        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
