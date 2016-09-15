package com.piczler.piczler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by matiyas on 11/24/15.
 */
public class InstagramProfile extends Fragment {


    ArrayList<GettersAndSetters> detail;
    RecyclerView recyclerView;
    ProgressBar pbBar;
    String media = "";
    UserFunctions functions;

    PictureAdapter recyclerAdapter;

    SwipyRefreshLayout swipyrefreshlayout;
    TextView tvNoPhotos;
    int counter = 0;
    int counterHoold = 0;
    JsonArray images = new JsonArray();

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
        tvNoPhotos = (TextView) theLayout.findViewById(R.id.tvNoPhotos);
        swipyrefreshlayout = (SwipyRefreshLayout) theLayout.findViewById(R.id.swipyrefreshlayout);
        detail = new ArrayList<>();

       // swipyrefreshlayout.setEnabled(false);
        //fab_button = (FloatingActionButton) theLayout.findViewById(R.id.fab_button);
        //  fab_button.attachToRecyclerView(recyclerView);
        initRecyclerView();


        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                getInstaPictures();
            }
        });
        if(functions.getPref(StaticVariables.INSTAUPDATED,false))
        {

            try
            {
                Database db = new Database(getActivity());
                db.open();
                Cursor c = db.getSampleDetails(StaticVariables.INSTAGRAMPICS,functions.getPref(StaticVariables.ID,""));
                if(c.getCount()>0)
                {
                    c.moveToFirst();
                    media = c.getString(c.getColumnIndex(Database.JSONSTRING));
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                    {
                        new BindAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,media) ;
                    }else {
                        new BindAsync().execute(media);
                    }

                }else {
                    pbBar.setVisibility(View.VISIBLE);
                }
                c.close();
                db.close();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
            getInstaPictures();

        }else {

            tvNoPhotos.setVisibility(View.VISIBLE);
            pbBar.setVisibility(View.GONE);
            tvNoPhotos.setText("Go to settings and connect your instagram account");
        }





        return theLayout;
    }

    private void getInstaPictures() {

        ConnectionDetector cd=new ConnectionDetector(getActivity());
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load("GET", "https://api.instagram.com/v1/users/self/media/recent?access_token="+functions.getPref(StaticVariables.INSTAACCESSTOKEN,"")+"&count="+(counter+20))
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
                                        System.out.println("bbbbbbb: " + result);


                                        if (result != null) {


                                           media = result;

                                            if(counterHoold == 0)
                                            {
                                                Database db = new Database(getActivity());
                                                db.open();
                                                db.insertSampleDetails(functions.getPref(StaticVariables.ID, ""), StaticVariables.INSTAGRAMPICS, media);
                                                db.close();
                                            }


                                            JSONObject json = new JSONObject(result);
                                            JSONArray data = functions.getJsonArray(json,StaticVariables.DATA);


                                            if(data.length()>detail.size())
                                            {
                                                counterHoold = counter;
                                                counter += 20;
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                    new BindAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, media);
                                                } else {
                                                    new BindAsync().execute(media);
                                                }
                                            }


                                        } else {
                                            resendToken("Unable to retrieve access token");
                                        }

                                    } catch (Exception ex) {
                                        resendToken("Unable to retrieve access token");
                                        ex.printStackTrace();
                                    }
                                }
                            });


        } else {
            resendToken("No internet Connection Please try again later");
            // Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }
    }


    private void resendToken(String message)
    {
        pbBar.setVisibility(View.GONE);
        if(detail.size()==0)
        {
            tvNoPhotos.setVisibility(View.VISIBLE);
            functions.showMessage(message);
        }

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

                        Intent it = new Intent(getActivity(), SocialPictures.class);
                        it.putExtra(StaticVariables.POSITION, position);
                        it.putExtra(StaticVariables.MEDIA, images.toString());
                        startActivity(it);


                    }
                })
        );

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

                pbBar.setVisibility(View.GONE);
                System.out.println("bbbbbbbb: hmmmmmmm");

            if(counterHoold == 0)
            {
                recyclerAdapter = new PictureAdapter(detail,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }


               if(detail.size() == 0)
               {
                   tvNoPhotos.setVisibility(View.VISIBLE);
               }else
               {
                   tvNoPhotos.setVisibility(View.GONE);
               }

            // pbBar.setVisibility(View.GONE);



        }





    }




    private void bindData(String jsonObject)
    {
        try
        {
            JSONObject json = new JSONObject(jsonObject);
            GettersAndSetters Details;
            JSONArray data = functions.getJsonArray(json,StaticVariables.DATA);

            if(data!=null)
            {

                if(counterHoold == 0)
                {
                    images = new JsonArray();
                    detail = new ArrayList<>();
                }
                //StaticVariables.instaMag = new ArrayList<>();
                for(int i=counterHoold; i<data.length(); i++)
                {
                    JSONObject onPhoto = data.getJSONObject(i);
                    JSONObject imagesImage = functions.getJsonObject(onPhoto,"images");
                    if(imagesImage!= null)
                    {
                      JSONObject thumbnail = functions.getJsonObject(imagesImage,"thumbnail");
                        if(thumbnail!=null)
                        {
                             Details = new GettersAndSetters();
                            String thumbUrlString = functions.getJsonString(thumbnail,StaticVariables.URL);
                            Details.setCover(thumbUrlString);
                            Details.setFileType(0);

                            JSONObject standard =  functions.getJsonObject(imagesImage, "standard_resolution");

                            String standardUrlString = functions.getJsonString(standard, StaticVariables.URL);
                            JsonObject add = new JsonObject();
                            add.addProperty(StaticVariables.URL, standardUrlString);
                            detail.add(Details);
                            Details.setSelected(false);
                          //  StaticVariables.instaMag.add(Details);
                            StaticVariables.piczlerMag.put(standardUrlString, "" + 0);


                            images.add(add);
                        }




                    }


                }



            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
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
          if(type.contentEquals("shift"))
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
}
