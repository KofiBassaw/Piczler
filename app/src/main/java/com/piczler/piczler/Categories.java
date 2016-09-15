package com.piczler.piczler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
 * Created by matiyas on 11/19/15.
 */
public class Categories extends AppCompatActivity {

    ProgressBar pbBar;
    ArrayList<GettersAndSetters> details;
    UserFunctions functions;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    boolean currentCat = false;
    private Toolbar toolbar;
    String from;
    int limit = 20;
    int offset = 0;
    MenuItem nextDone;
    int counter = 0;

    SwipyRefreshLayout swipyrefreshlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //setTitle("CATEGORIES");
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        pbBar.setVisibility(View.VISIBLE);
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        swipyrefreshlayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        initRecyclerView();
        from = getIntent().getStringExtra(StaticVariables.FROM);

        String oldCat = functions.getPref(StaticVariables.CATEGORIES,"");
        if(!oldCat.contentEquals("")){
            new BindAsync().execute(oldCat);
        }
        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                offset = offset+20;
                getCategories(offset,limit);
            }
        });
        getCategories(offset,limit);
    }

    private void initRecyclerView() {


        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //  int leftRight = getResources().getDimensionPixelSize(R.dimen.leftright);
        //  int top = getResources().getDimensionPixelSize(R.dimen.top);
        //  recyclerView.addItemDecoration(new SpacesItemDecoration(leftRight,top));


    }



    private void bindData(JSONArray jsonArray){
    try{
        if(offset == 0)
        details = new ArrayList<>();


        GettersAndSetters Details;
      for(int i=0; i< jsonArray.length(); i++){
          JSONObject c = jsonArray.getJSONObject(i);
          Details = new GettersAndSetters();
          Details.setCover(functions.getJsonString(c, StaticVariables.COVER));
          Details.setName(functions.getJsonString(c, StaticVariables.NAME));
          Details.setId(functions.getInt(c, StaticVariables.ID));
          Details.setFollowers(functions.getInt(c, StaticVariables.FOLLOWERS));
          Details.setFrom(from);
          if(currentCat){
              Details.setFollowed(functions.getBoolean(c, StaticVariables.USERFOLLOW));
          }else{
              Details.setFollowed(functions.getPref(String.valueOf(functions.getInt(c,StaticVariables.ID)),functions.getBoolean(c, StaticVariables.USERFOLLOW)));
          }

          Details.setLoading(false);
          details.add(Details);
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
                recyclerAdapter = new RecyclerAdapter(details, Categories.this);
                recyclerView.setAdapter(recyclerAdapter);
                pbBar.setVisibility(View.GONE);
            }else{
                recyclerAdapter.notifyDataSetChanged();
            }




        }





    }




    private void getCategories(final int offset, final int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println(StaticVariables.CATEGORIES + "?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit);
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + StaticVariables.CATEGORIES + "?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
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
                                                String  dataString = functions.getPref(StaticVariables.CATEGORIES,"");

                                                if(!dataString.contentEquals(data.toString())){
                                                    currentCat = true;
                                                    if(offset == 0) {
                                                        functions.setPref(StaticVariables.CATEGORIES, data.toString());
                                                        if(functions.getPref(StaticVariables.CATEGORIESSAVED,"").contentEquals(""))
                                                        {
                                                            functions.setPref(StaticVariables.CATEGORIESSAVED,data.toString());
                                                        }
                                                    }

                                                        new BindAsync().execute(data.toString());



                                                }


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

    @Override
    protected void onResume() {
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                StaticVariables.FOLLOWNOTIFY));
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


            String type = intent.getStringExtra(StaticVariables.TYPE);
            int position = intent.getExtras().getInt(StaticVariables.POSITION);

            if (type.contentEquals(StaticVariables.FOLLOW)) {
                details.get(position).followed = true;
            } else if (type.contentEquals(StaticVariables.UNFOLLOW)) {
                details.get(position).followed = false;
            }
            details.get(position).loading = false;
            recyclerAdapter.notifyDataSetChanged();
            followCategory(position, type);
        }
    };





    private void followCategory(final int position, final String action){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/

             System.out.println("---------------- "+StaticVariables.BASE_URL + "categories/"+details.get(position).id+"/" + action);
            //
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST",StaticVariables.BASE_URL + "categories/"+details.get(position).id+"/" + action)
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
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == StaticVariables.SUCCESS_CODE) {
                                            if (action.contentEquals(StaticVariables.FOLLOW)) {
                                                details.get(position).followed = true;
                                            } else if (action.contentEquals(StaticVariables.UNFOLLOW)) {
                                                details.get(position).followed = false;
                                            }
                                            details.get(position).loading = false;
                                            recyclerAdapter.notifyDataSetChanged();

                                            if(action.contentEquals(StaticVariables.FOLLOW))
                                            {
                                                counter ++;
                                            }else
                                            {
                                                counter --;
                                            }
                                        }else{
                                            changeBack(action,position);
                                        }

                                    } else {
                                        //change it back to the old state
                                        changeBack(action,position);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                changeBack(action, position);
                            }
                        }
                    });


        } else {
            changeBack(action,position);
        }


    }

    private void changeBack(String action, int position){
        if (action.contentEquals(StaticVariables.FOLLOW)) {
          details.get(position).followed = false;

        } else if (action.contentEquals(StaticVariables.UNFOLLOW)) {
            details.get(position).followed = true;
        }
        details.get(position).loading = false;
        recyclerAdapter.notifyDataSetChanged();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category, menu);

        nextDone=menu.findItem(R.id.idNext);

        if (from == null)
            nextDone.setTitle("Next");
        else{
            nextDone.setTitle("Done");
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(from == null)
            startActivity(new Intent(this, MainHome.class));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.idNext) {
            if (from == null){
                if(counter < 4)
                {
                    functions.showMessage("Follow at least four categories");
                }else {
                    startActivity(new Intent(this, MainHome.class));
                    finish();
                }


        }else
            {
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void reduceOffset()
    {
        if(offset>0)
        {
            offset -=20;
        }
    }





}
