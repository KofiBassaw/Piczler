package com.piczler.piczler;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matiyas on 11/24/15.
 */
public class FaceBookProfile extends Fragment {


    String facebookId = "";
    UserFunctions functions;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    int totals;
ArrayList<GettersAndSetters>details;
    JsonArray images = new JsonArray();

    RecyclerView recyclerView;
    ProgressBar pbBar;
    Map<String, String> map2 = new HashMap<String, String>();
    TextView tvNoPhotos;
    SwipyRefreshLayout swipyrefreshlayout;
    PictureAdapter recyclerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View theLayout = inflater.inflate(
                R.layout.picture_fragment_piczler, container, false);
        details = new ArrayList<>();
       // StaticVariables.facebookMag = new ArrayList<>();
        functions = new UserFunctions(getActivity());
        facebookId = functions.getPref(StaticVariables.FACEBOOKEID, "");
        tvNoPhotos = (TextView) theLayout.findViewById(R.id.tvNoPhotos);
        recyclerView = (RecyclerView) theLayout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        swipyrefreshlayout = (SwipyRefreshLayout) theLayout.findViewById(R.id.swipyrefreshlayout);
        pbBar = (ProgressBar) theLayout.findViewById(R.id.pbBar);
        initRecyclerView();
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        swipyrefreshlayout.setEnabled(false);
        if(!facebookId.contentEquals("") && functions.getPref(StaticVariables.HASFACEBOOKLOGIN,false))
        {
            System.out.print("bbbbbbbbbbbbbbb:   here is valid");
            //retrieve facebook photos here to test


            try
            {
                pbBar.setVisibility(View.VISIBLE);
                Database db = new Database(getActivity());
                db.open();
                Cursor c = db.getSampleDetails(StaticVariables.FACEBOOKPICS,functions.getPref(StaticVariables.ID,""));
                if(c.getCount()>0)
                {
                    c.moveToFirst();
                   String jsonString = c.getString(c.getColumnIndex(Database.JSONSTRING));




                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new BindAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonString);
                    } else {
                        new BindAsync().execute(jsonString);
                    }
                }else
                {
                    getPics();
                }
                db.close();

            }catch (Exception ex)
            {
                ex.printStackTrace();
            }



            //




        }else {
            System.out.print("bbbbbbbbbbbbbbb:   here is not valid "+facebookId+" sample here: "+functions.getPref(StaticVariables.HASFACEBOOKLOGIN,false));
            tvNoPhotos.setVisibility(View.VISIBLE);
            tvNoPhotos.setText("Go to settings and connect your facebook account");
            pbBar.setVisibility(View.GONE);
        }

        return theLayout;
    }


    private  void getPics()
    {
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                retrieveImages(currentAccessToken);
            }
        };
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

                        Intent it = new Intent(getActivity(),SocialPictures.class);
                        it.putExtra(StaticVariables.POSITION,position);
                        it.putExtra(StaticVariables.MEDIA,images.toString());
                        startActivity(it);



                    }
                })
        );

    }




    private void retrieveImages(final AccessToken currentAccessToken)
    {
        pbBar.setVisibility(View.VISIBLE);
        Bundle params = new Bundle();
        params.putString("fields", "id,name");
        String url =  "/me/albums";
        new GraphRequest(
                currentAccessToken,
                url,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                       try{

                           JSONObject resp = response.getJSONObject();
                           System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbb albums "+resp.toString());

                               JSONArray data = functions.getJsonArray(resp, StaticVariables.DATA);
                               if(data != null){
                                   totals = data.length();

                                   retireveSinglePhoto(data,0, currentAccessToken);
                               }

                       }catch (Exception e){
                           e.printStackTrace();
                       }

                    }
                }
        ).executeAsync();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       if(!facebookId.contentEquals(""))
       {
           if(accessTokenTracker != null)
           accessTokenTracker.stopTracking();
       }
    }



    private void retireveSinglePhoto(final JSONArray data, final int pos, final AccessToken currentAccessToken){

        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb pos: "+pos);

         if(pos< totals)
         {
             String albumID = "";

            try{
                albumID = functions.getJsonString(data.getJSONObject(pos),StaticVariables.ID);
            }catch (Exception e)
            {

            }

             //access it here
             Bundle params = new Bundle();
             params.putString("fields", "id,link");
             String url =  "/"+albumID+"/photos";
             new GraphRequest(
                     currentAccessToken,
                     url,
                     params,
                     HttpMethod.GET,
                     new GraphRequest.Callback() {
                         public void onCompleted(GraphResponse response) {

                             try{
                                 System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbb single image"+response.toString());
                                 JSONObject resp = response.getJSONObject();

                                  JSONArray dataimage = functions.getJsonArray(resp,StaticVariables.DATA);
                                 if(dataimage != null)
                                 {
                                     for (int i=0; i<dataimage.length(); i++)
                                     {
                                         JSONObject c = dataimage.getJSONObject(i);
                                         String imageId = functions.getJsonString(c,StaticVariables.ID);
                                         getSingleImageUrl(imageId,currentAccessToken);
                                     }
                                 }
                                 retireveSinglePhoto(data,(pos+1),currentAccessToken);
                             }catch (Exception e){
                                 e.printStackTrace();
                                 pbBar.setVisibility(View.GONE);
                             }

                         }
                     }
             ).executeAsync();
         }else
         {
             //bind data here
             if(recyclerAdapter == null)
             {
                 recyclerAdapter = new PictureAdapter(details,getActivity());
                 recyclerView.setAdapter(recyclerAdapter);

             }else
             {
                 recyclerAdapter.notifyDataSetChanged();
             }


             pbBar.setVisibility(View.GONE);
             try
             {
                 if(details.size()>0)
                 {
                     Database db = new Database(getActivity());
                     db.open();
                     db.insertSampleDetails(functions.getPref(StaticVariables.ID, ""), StaticVariables.FACEBOOKPICS, images.toString());
                     db.close();
                 }

             }catch (Exception ex)
             {
                 ex.printStackTrace();
             }
         }
    }

    private void getSingleImageUrl(String imageId, AccessToken currentAccessToken)
    {


        //access it here
        Bundle params = new Bundle();
        params.putString("fields", "id,link,images");
        String url =  "/"+imageId;
        new GraphRequest(
                currentAccessToken,
                url,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try{

                            JSONObject resp = response.getJSONObject();
                            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbb single image url"+resp.toString());

                            JSONArray imagesArray =  functions.getJsonArray(resp, StaticVariables.IMAGES);

                            if(imagesArray != null)
                            {

                              if(imagesArray.length()>0){
                                  JSONObject thummb = imagesArray.getJSONObject(0);
                                  JSONObject big = imagesArray.getJSONObject((int)imagesArray.length()/2);
                                  GettersAndSetters Details = new GettersAndSetters();
                                  JsonObject add = new JsonObject();
                                  add.addProperty(StaticVariables.URL, functions.getJsonString(thummb, StaticVariables.SOURCE));

                                  if(map2.get(functions.getJsonString(thummb, StaticVariables.SOURCE)) == null)
                                  {
                                      Details.setCover(functions.getJsonString(thummb, StaticVariables.SOURCE));
                                      Details.setFileType(0);
                                      Details.setSelected(false);
                                      details.add(Details);
                                      images.add(add);
                                      StaticVariables.facebookMag.put(functions.getJsonString(thummb, StaticVariables.SOURCE), "" +0);
                                     // StaticVariables.facebookMag.add(Details);
                                      map2.put(functions.getJsonString(thummb, StaticVariables.SOURCE),functions.getJsonString(thummb, StaticVariables.SOURCE));
                                  }




                              }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }


    private void bindData(String jsonSTring)
    {
        try
        {
        JSONArray photos = new JSONArray(jsonSTring);


            for(int i=0; i<photos.length(); i++)
            {
                JSONObject oneOBj = photos.getJSONObject(i);
                GettersAndSetters Details = new GettersAndSetters();
                JsonObject add = new JsonObject();
                add.addProperty(StaticVariables.URL, functions.getJsonString(oneOBj, StaticVariables.URL));

                Details.setCover(functions.getJsonString(oneOBj, StaticVariables.URL));
                Details.setFileType(0);
                Details.setSelected(false);
                details.add(Details);
                images.add(add);
                StaticVariables.facebookMag.put(functions.getJsonString(oneOBj, StaticVariables.URL), "" +0);
                // StaticVariables.facebookMag.add(Details);
                map2.put(functions.getJsonString(oneOBj, StaticVariables.URL),functions.getJsonString(oneOBj, StaticVariables.URL));


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

            if(details.size()>0)
            {
                pbBar.setVisibility(View.GONE);
            }else
            {
                pbBar.setVisibility(View.VISIBLE);
            }

            recyclerAdapter = new PictureAdapter(details,getActivity());
            recyclerView.setAdapter(recyclerAdapter);
            getPics();

        }





    }



}
