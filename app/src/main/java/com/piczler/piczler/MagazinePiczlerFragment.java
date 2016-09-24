package com.piczler.piczler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Set;

/**
 * Created by matiyas on 8/14/16.
 */
public class MagazinePiczlerFragment extends Fragment{

    RecyclerView recyclerView;
    ProgressBar pbBar;
    UserFunctions functions;

    MagazineAdapter recyclerAdapter;

    int limit = 20;
    SwipyRefreshLayout swipyrefreshlayout;


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

        initRecyclerView();
        //functions.showMessage(""+OrderMagazine.picDetails.size());
        if(OrderMagazine.picDetails.size()>0)
        {
            recyclerAdapter = new MagazineAdapter(OrderMagazine.picDetails,getActivity());
            recyclerView.setAdapter(recyclerAdapter);
        }else
        {
            Set<String> keys = StaticVariables.piczlerMag.keySet();
            if(keys.size()>0)
            {
                 new BindMapAsync().execute();
            }else{
                getMedia( OrderMagazine.offset, limit);
            }

        }



        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
               OrderMagazine.offset =  OrderMagazine.offset+20;
                getMedia( OrderMagazine.offset, limit);
            }
        });


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
                        GettersAndSetters Details = OrderMagazine.picDetails.get(position);
                        if (Details.fileType == 0) {
                            if (Details.selected) {
                                //remove selection
                                OrderMagazine.picDetails.get(position).selected = false;
                                OrderMagazine.selected.remove(Details.cover);

                            } else {
                                //add selection
                                OrderMagazine.picDetails.get(position).selected = true;
                                OrderMagazine.selected.put(Details.cover, Details.serverID);


                            }
                            recyclerAdapter.notifyDataSetChanged();


                        }
                    }
                })
        );

    }


    private void bindMap()
    {
        try{
            Set<String> keys = StaticVariables.piczlerMag.keySet();
            GettersAndSetters Details;

            for(String i: keys)
            {
                String idString = StaticVariables.piczlerMag.get(i);
                String idArray[] = idString.split("|");
                Details = new GettersAndSetters();
                Details.setCover(i);
                Details.setSelected(false);
                Details.setType(Integer.parseInt(idArray[1]));
                Details.setServerID(idArray[0]);
                OrderMagazine.picDetails.add(Details);
            }


        }catch (Exception ex)
        {
            ex.printStackTrace();
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
                                                        functions.setPref(StaticVariables.MEDIA, data.toString());
                                                        OrderMagazine.picDetails = new ArrayList<GettersAndSetters>();
                                                        new BindAsync().execute("",data.toString());

                                                }else
                                                {
                                                    OrderMagazine.offset = offset;
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



    private void revertOffset()
    {
        if(OrderMagazine.offset>0)
        {
            OrderMagazine.offset -= 20;
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

            if(OrderMagazine.offset == 0)
            {
                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new MagazineAdapter(OrderMagazine.picDetails,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }



            pbBar.setVisibility(View.GONE);



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


                    OrderMagazine.picDetails.add(Detail);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }





    class BindMapAsync extends AsyncTask<String, String, String> {

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
              bindMap();

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

            if(OrderMagazine.offset == 0)
            {
                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new MagazineAdapter(OrderMagazine.picDetails,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                if(recyclerAdapter == null)
                {
                    System.out.println("bbbbbbbb: hmmmmmmm");
                    recyclerAdapter = new MagazineAdapter(OrderMagazine.picDetails,getActivity());
                    recyclerView.setAdapter(recyclerAdapter);
                }else
                {
                    recyclerAdapter.notifyDataSetChanged();
                }

            }



            pbBar.setVisibility(View.GONE);



        }






    }




}
