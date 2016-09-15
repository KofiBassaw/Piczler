package com.piczler.piczler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matiyas on 8/20/16.
 */
public class PictureGrid extends AppCompatActivity {
    private Toolbar toolbar;
    TextView tvTitle;
    String title;
    String from ="";
    ProgressBar pbBar;
    TextView tvNoPhotos;
    int offset = 0;
    int limit = 20;
    SwipyRefreshLayout swipyrefreshlayout;
    ArrayList<GettersAndSetters> detail;
    RecyclerView recyclerView;
    UserFunctions functions;
    PictureAdapter recyclerAdapter;
    String media= "";
    Button bNoPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_grid);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNoPhotos = (TextView) findViewById(R.id.tvNoPhotos);
        bNoPhotos = (Button) findViewById(R.id.bNoPhotos);
        title = getIntent().getStringExtra(StaticVariables.TITLE);
        from = getIntent().getStringExtra(StaticVariables.FROM);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        swipyrefreshlayout = (SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        functions = new UserFunctions(this);
        detail = new ArrayList<>();
        initRecyclerView();
        tvTitle.setText(title);

        if(from.contentEquals(StaticVariables.CATEGORIES)){
            pbBar.setVisibility(View.VISIBLE);
            getFeeds( offset,  limit);
        }else if(from.contentEquals(StaticVariables.MEDIA))
        {
            media = getIntent().getStringExtra(StaticVariables.MEDIA);
            new BindAsync().execute(media);
        }



        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                offset = offset+20;
                getFeeds(offset, limit);
            }
        });



        bNoPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bNoPhotos.setVisibility(View.GONE);
                tvNoPhotos.setVisibility(View.GONE);
                pbBar.setVisibility(View.VISIBLE);
                getFeeds(offset, limit);

            }
        });

    }







    private void initRecyclerView() {


        //recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        //  int paddingTop = Utils.getToolbarHeight(this) + Utils.getTabsHeight(this);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));





        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent it = new Intent(PictureGrid.this, Pictures.class);
                        it.putExtra(StaticVariables.POSITION, position);
                        it.putExtra(StaticVariables.FROM, StaticVariables.CATEGORIES);
                        it.putExtra(StaticVariables.MEDIA, media);
                        it.putExtra(StaticVariables.ID,getIntent().getStringExtra(StaticVariables.ID));
                        startActivity(it);
                    }
                })
        );

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






    private void getFeeds(final  int offset, final int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL +"categories/"+getIntent().getStringExtra(StaticVariables.ID)+"?"+ StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                swipyrefreshlayout.setRefreshing(false);
                                pbBar.setVisibility(View.GONE);
                                if (e != null) {
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {

                                                    if (offset == 0) {
                                                        if(!data.toString().contentEquals(media)){
                                                            //functions.setPref(StaticVariables.MEDIA, data.toString());
                                                            media = data.toString();
                                                            detail = new ArrayList<GettersAndSetters>();
                                                            new BindAsync().execute(media);
                                                        }
                                                    } else {

                                                        GsonBuilder gsonb = new GsonBuilder();
                                                        Gson gson = gsonb.create();
                                                        JsonArray firstArray = gson.fromJson(media, JsonArray.class);

                                                        JsonArray newFeed = gson.fromJson(data.toString(), JsonArray.class);
                                                        firstArray.addAll(newFeed);
                                                        media = firstArray.toString();

                                                        new BindAsync().execute(data.toString());
                                                    }



                                            } else {
                                                functions.showMessage("Unable to retrieve data");
                                                reduceLimit();
                                                //revert();
                                            }

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            reduceLimit();
                                            //revert();
                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            reduceLimit();
                                            //revert();
                                        }
                                    } else {
                                        functions.showMessage("Unable to retrieve categories");
                                        reduceLimit();
                                        //revert();
                                    }

                                } else {
                                    reduceLimit();
                                    //revert();
                                    functions.showMessage("Unable to retrieve categories");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                reduceLimit();
                                //revert();
                            }
                        }
                    });


        } else {
            reduceLimit();
            //revert();
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }




    private void reduceLimit(){
        if(offset>0){
            offset -= 20;
        }

        if(detail.size() == 0)
        {
            tvNoPhotos.setVisibility(View.VISIBLE);
            bNoPhotos.setVisibility(View.VISIBLE);
        }
    }




    private void bindData(String oldArray){
        try{




            GettersAndSetters Detail;



            if(!oldArray.contentEquals(""))
            {
                JSONArray medArray = new JSONArray(oldArray);
                for(int i=0; i<medArray.length(); i++){
                    JSONObject c  = medArray.getJSONObject(i);
                    Detail = new GettersAndSetters();
                    Detail.setJsonString(c.toString());
                    Detail.setServerID(functions.getJsonString(c, StaticVariables.ID));
                    Detail.setFileType(functions.getInt(c,StaticVariables.TYPE));
                    JSONObject images = functions.getJsonObject(c, StaticVariables.IMAGES);
                    //tvLikes
                    if(images != null){
                        JSONObject mobileRes = functions.getJsonObject(images,StaticVariables.MOBILE_RESULUTION);
                        if(mobileRes != null){
                            String url = functions.getJsonString(mobileRes,StaticVariables.URL);
                            Detail.setCover(url);


                        }
                    }

                    //StaticVariables.piczlerMag.add(Detail);

                    detail.add(Detail);
                }
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

            System.out.println("bbbbbbbb: "+offset+" length: "+ detail.size());
            if(offset == 0)
            {
                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new PictureAdapter(detail,PictureGrid.this);
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }


            pbBar.setVisibility(View.GONE);



        }





    }


}
