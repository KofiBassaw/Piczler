package com.piczler.piczler;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matiyas on 11/24/15.
 */
public class PiczlerProfile extends Fragment {

    ArrayList<GettersAndSetters> detail;
    RecyclerView recyclerView;
    ProgressBar pbBar;
    String media = "";
    UserFunctions functions;

    PictureAdapter recyclerAdapter;

    int offset =0;
    int limit = 20;
    SwipyRefreshLayout swipyrefreshlayout;


    //FloatingActionButton fab_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View theLayout = inflater.inflate(
                R.layout.picture_fragment_piczler, container, false);
        recyclerView = (RecyclerView) theLayout.findViewById(R.id.recyclerView);
        pbBar = (ProgressBar) theLayout.findViewById(R.id.pbBar);
        functions = new UserFunctions(getActivity());
        swipyrefreshlayout = (SwipyRefreshLayout) theLayout.findViewById(R.id.swipyrefreshlayout);

        detail = new ArrayList<>();
        UserProfile.offset = 0;


        //fab_button = (FloatingActionButton) theLayout.findViewById(R.id.fab_button);
      //  fab_button.attachToRecyclerView(recyclerView);
        initRecyclerView();
        media = functions.getPref(StaticVariables.MEDIA, "");
        System.out.println("bbbbbb: why");
        System.out.println("bbbbbb: "+ media);
        if(!media.contentEquals("")){
            new BindAsync().execute("",media);
        }else {
            pbBar.setVisibility(View.VISIBLE);
        }


        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                offset = offset+20;
                getMedia(offset, limit);
            }
        });
        getMedia(offset,limit);



        return theLayout;
    }



    private void initRecyclerView() {


        //recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

      //  int paddingTop = Utils.getToolbarHeight(this) + Utils.getTabsHeight(this);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));





        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent it = new Intent(getActivity(), Pictures.class);
                        it.putExtra(StaticVariables.POSITION, position);
                        it.putExtra(StaticVariables.FROM, StaticVariables.PICTURES);
                        it.putExtra(StaticVariables.MEDIA, media);
                        it.putExtra("myProfile", "yes");
                        it.putExtra(StaticVariables.ID,functions.getPref(StaticVariables.ID,"") );
                        startActivity(it);
                    }
                })
        );

    }


    private void bindData(String first, String oldArray){
    try{




        GettersAndSetters Detail;

        if(!first.contentEquals("")) {
            JSONObject c = new JSONObject(first);
            Detail = new GettersAndSetters();
            Detail.setSelected(false);
            System.out.println(first);
            Detail.setJsonString(c.toString());
            JSONObject media = functions.getJsonObject(c, StaticVariables.MEDIA);
            JSONObject images = functions.getJsonObject(media, StaticVariables.IMAGES);
            Detail.setFileType(functions.getInt(c, StaticVariables.TYPE));
            Detail.setServerID(functions.getJsonString(c, StaticVariables.ID));
            //tvLikes
            if (images != null) {
                JSONObject mobileRes = functions.getJsonObject(images, StaticVariables.MOBILE_RESULUTION);
                if (mobileRes != null) {
                    String url = functions.getJsonString(mobileRes, StaticVariables.URL);
                    Detail.setCover(url);
                    StaticVariables.piczlerMag.put(url, functions.getJsonString(c, StaticVariables.ID) + "|" + functions.getInt(c, StaticVariables.TYPE));
                }
            }
            if (offset == 0 || detail.size() == 0) {
                detail.add(Detail);
               // StaticVariables.piczlerMag.add(Detail);

            } else{
              //  StaticVariables.piczlerMag.add(0, Detail);
                detail.add(0, Detail);
        }



        }

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
                        StaticVariables.piczlerMag.put(url,functions.getJsonString(c, StaticVariables.ID)+"|"+functions.getInt(c,StaticVariables.TYPE));
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

            System.out.println("bbbbbbbb: "+offset+" length: "+ detail.size());
            if(offset == 0)
            {
                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new PictureAdapter(detail,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }


            pbBar.setVisibility(View.GONE);



        }





    }


    private void getMedia(final int offset, int limit){


        ConnectionDetector cd=new ConnectionDetector(getActivity());
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println("bbbbbb: "+StaticVariables.BASE_URL +"users/self/media?"+StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit);
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL +"users/self/media?"+StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit)
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
                                                        functions.setPref(StaticVariables.MEDIA, data.toString());
                                                        media = data.toString();
                                                        detail = new ArrayList<GettersAndSetters>();
                                                        new BindAsync().execute("",media);
                                                    }
                                                }else
                                                {
                                                    UserProfile.offset = offset;


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
                                                revertOffset();
                                            }

                                        } else if (code == 403 || code == 401) {
                                            pbBar.setVisibility(View.GONE);
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            revertOffset();
                                        } else {
                                            pbBar.setVisibility(View.GONE);
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            revertOffset();
                                        }
                                    } else {
                                        pbBar.setVisibility(View.GONE);
                                        functions.showMessage("Unable to retrieve media");
                                        revertOffset();
                                    }

                                } else {
                                    pbBar.setVisibility(View.GONE);
                                    functions.showMessage("Unable to retrieve media");
                                    revertOffset();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                revertOffset();
                            }
                        }
                    });


        } else {
            revertOffset();
            pbBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(
                StaticVariables.RELOADIMAGES));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("rror", "> " + e.getMessage());
        }
        super.onDestroy();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String type = intent.getStringExtra(StaticVariables.TYPE);

            if(type.contentEquals(StaticVariables.RELOAD)){
                String data = intent.getStringExtra(StaticVariables.DATA);
                new BindAsync().execute(data,"");
                offset = 0;
                getMedia(offset,limit);
            }else if(type.contentEquals("shift"))
            {

                int i = intent.getExtras().getInt("i");
               System.out.println("bbbbbb: status "+i);
                if (i == 1) {
                    swipyrefreshlayout.setEnabled(true);
                } else {
                    swipyrefreshlayout.setEnabled(false);
                }

            }

        }
    };


    private void revertOffset()
    {
        if(offset>0)
        {
            offset -= 20;
            UserProfile.offset -= 20;
        }
    }
}
