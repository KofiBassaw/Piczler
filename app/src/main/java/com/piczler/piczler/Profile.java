package com.piczler.piczler;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matiyas on 4/24/16.
 */
public class Profile extends AppCompatActivity implements  AppBarLayout.OnOffsetChangedListener{

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    ImageView ivProfile;
    String userID;
    UserFunctions functions;
    String getBasicInfo = "";
    TextView tvUserName;
    private Toolbar toolbar;



    ArrayList<GettersAndSetters> detail;
    RecyclerView recyclerView;
    ProgressBar pbBar;
    String media = "";

    int offset =0;
    int limit = 10;
    SwipyRefreshLayout swipyrefreshlayout;
    PictureAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        userID = getIntent().getStringExtra(StaticVariables.ID);
        swipyrefreshlayout = (SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);
        functions = new UserFunctions(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        pbBar = (ProgressBar)findViewById(R.id.pbBar);
        detail = new ArrayList<>();
        bindUserProfile();
        getUserDetails();
        initRecyclerView();

        try
        {
            Database db = new Database(this);
            db.open();
            Cursor c = db.getSampleDetails(StaticVariables.USERMEDIAJSON,userID);
            if(c.getCount()>0)
            {
                c.moveToFirst();
                media = c.getString(c.getColumnIndex(Database.JSONSTRING));
                new BindAsync().execute("",media);

            }else {
                pbBar.setVisibility(View.VISIBLE);
            }
            c.close();
            db.close();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }



        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                offset = offset+10+1;
                getMedia(offset, limit);
            }
        });
        getMedia(offset, limit);


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
                        Intent it = new Intent(Profile.this,Pictures.class);
                        it.putExtra(StaticVariables.POSITION,position);
                        it.putExtra(StaticVariables.FROM,StaticVariables.PICTURES);
                        it.putExtra(StaticVariables.MEDIA,media);
                        it.putExtra(StaticVariables.ID,userID);
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
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {


        /*
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            ivProfile.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            ivProfile.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }

        */


        int  verticalOffset = Math.abs(i);
        int difference = appBarLayout.getTotalScrollRange();

        System.out.println("bbbbbb: difference: " + difference + " offset: " + verticalOffset);

        if (difference <= verticalOffset)
        {
            //enable
            swipyrefreshlayout.setEnabled(true);
        } else {
            swipyrefreshlayout.setEnabled(false);
        }

    }




    private void getUserDetails(){
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "users/"+userID)
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
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                            if (data != null) {

                                                JSONObject info = functions.getJsonObject(data,StaticVariables.USER);
                                                if(info!=null)
                                                {
                                                    Database db = new Database(Profile.this);
                                                    db.open();
                                                    db.insertSampleDetails(userID, StaticVariables.USERJSON, info.toString());
                                                    db.close();
                                                    bindUserProfile();
                                                }



                                            } else {
                                                if(getBasicInfo.contentEquals(""))
                                                functions.showMessage("Unable to retrieve user information");

                                            }

                                        } else if (code == 403 || code == 401) {
                                            if(getBasicInfo.contentEquals(""))
                                                  functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));


                                        } else {
                                            if(getBasicInfo.contentEquals(""))
                                                  functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));


                                        }
                                    } else {
                                        if(getBasicInfo.contentEquals(""))
                                              functions.showMessage("Unable to retrieve user information");

                                    }

                                } else {
                                    if(getBasicInfo.contentEquals(""))
                                          functions.showMessage("Unable to make search");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            if(getBasicInfo.contentEquals(""))
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();


        }


    }



    private void bindUserProfile()
    {
        try
        {
            Database db = new Database(this);
            db.open();
            Cursor c = db.getSampleDetails(StaticVariables.USERJSON,userID);
            if(c.getCount()>0)
            {
                c.moveToFirst();
                getBasicInfo = c.getString(c.getColumnIndex(Database.JSONSTRING));
                JSONObject json = new JSONObject(getBasicInfo);



                String name  = functions.getJsonString(json, StaticVariables.FULLNAME);
                if(name.contentEquals(""))
                {
                    name = functions.getJsonString(json, StaticVariables.DISPLAYNAME);
                }




                tvUserName.setText(name);

                JSONObject profilePic = functions.getJsonObject(json,StaticVariables.PROFILE_PICTURE);
                JSONObject mobileRes = functions.getJsonObject(profilePic,StaticVariables.MOBILE_RESOLUTION);

                System.out.println("bbbbbb: res "+ mobileRes.toString());
                String url = functions.getJsonString(mobileRes,StaticVariables.URL);
                System.out.println("bbbbbb: url "+ url);
                AQuery aq = new AQuery(this);
                ImageOptions op=new ImageOptions();
                op.fileCache = true;
                op.memCache=true;
                op.targetWidth = 0;
                op.fallback = R.drawable.adele;
                aq.id(ivProfile).image(url, op);

            }
            c.close();;
            c=null;
            db.close();


        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    private void bindData(String first, String oldArray){
        try{



            GettersAndSetters Detail;

            if(!first.contentEquals("")){
                JSONObject c  = new JSONObject(first);
                Detail = new GettersAndSetters();
                System.out.println(first);
                Detail.setJsonString(c.toString());
                Detail.setFileType(functions.getInt(c, StaticVariables.TYPE));
                JSONObject media = functions.getJsonObject(c,StaticVariables.MEDIA);
                JSONObject images = functions.getJsonObject(media, StaticVariables.IMAGES);
                Detail.setFileType(functions.getInt(c,StaticVariables.TYPE));
                //tvLikes
                if(images != null){
                    JSONObject mobileRes = functions.getJsonObject(images,StaticVariables.MOBILE_RESULUTION);
                    if(mobileRes != null){
                        String url = functions.getJsonString(mobileRes,StaticVariables.URL);
                        Detail.setCover(url);
                    }
                }
                detail.add(Detail);

            }

            if(!oldArray.contentEquals("")) {
                JSONArray medArray = new JSONArray(oldArray);
                for(int i=0; i<medArray.length(); i++){
                    JSONObject c  = medArray.getJSONObject(i);
                    Detail = new GettersAndSetters();
                    Detail.setJsonString(c.toString());
                    JSONObject images = functions.getJsonObject(c, StaticVariables.IMAGES);
                    //tvLikes
                    Detail.setFileType(functions.getInt(c,StaticVariables.TYPE));
                    if(images != null){
                        JSONObject mobileRes = functions.getJsonObject(images,StaticVariables.MOBILE_RESULUTION);
                        if(mobileRes != null){
                            String url = functions.getJsonString(mobileRes,StaticVariables.URL);
                            Detail.setCover(url);
                        }
                    }


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
                bindData(args[0],args[1]);

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
            pbBar.setVisibility(View.GONE);
            if(offset == 0)
            {
                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new PictureAdapter(detail,Profile.this);
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }



        }





    }



    private void getMedia(final int offset, int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL +"users/"+userID+"/media?"+StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit)
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
                                swipyrefreshlayout.setRefreshing(false);
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

                                                    if(offset == 0)
                                                    {

                                                        if(!data.toString().contentEquals(media)){
                                                            Database db = new Database(Profile.this);
                                                            db.open();
                                                            db.insertSampleDetails(userID, StaticVariables.USERMEDIAJSON, data.toString());
                                                            db.close();
                                                            media = data.toString();
                                                            detail = new ArrayList<GettersAndSetters>();
                                                            new BindAsync().execute("",media);
                                                        }
                                                    }else
                                                    {


                                                        GsonBuilder gsonb = new GsonBuilder();
                                                        Gson gson = gsonb.create();
                                                        JsonArray firstArray = gson.fromJson(media, JsonArray.class);

                                                        JsonArray newFeed = gson.fromJson(data.toString(), JsonArray.class);
                                                        firstArray.addAll(newFeed);
                                                        media = firstArray.toString();

                                                        new BindAsync().execute("",data.toString());
                                                    }




                                            } else {
                                                functions.showMessage("Unable to retrieve data");
                                            }

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                        }
                                    } else {
                                        functions.showMessage("Unable to retrieve media");
                                    }

                                } else {
                                    functions.showMessage("Unable to retrieve media");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }

}
