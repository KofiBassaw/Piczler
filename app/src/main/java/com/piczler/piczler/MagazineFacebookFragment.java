package com.piczler.piczler;

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
import java.util.Set;

/**
 * Created by matiyas on 8/14/16.
 */
public class MagazineFacebookFragment extends Fragment {


    String facebookId = "";
    UserFunctions functions;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    int totals;

    RecyclerView recyclerView;
    ProgressBar pbBar;
    //Map<String, String> map2 = new HashMap<String, String>();
    TextView tvNoPhotos;
    SwipyRefreshLayout swipyrefreshlayout;
    MagazineAdapter recyclerAdapter;
    Map<String, String> map2 = new HashMap<String, String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View theLayout = inflater.inflate(
                R.layout.picture_fragment_piczler, container, false);

        functions = new UserFunctions(getActivity());
        facebookId = functions.getPref(StaticVariables.FACEBOOKEID, "");
        tvNoPhotos = (TextView) theLayout.findViewById(R.id.tvNoPhotos);
        recyclerView = (RecyclerView) theLayout.findViewById(R.id.recyclerView);
        swipyrefreshlayout = (SwipyRefreshLayout) theLayout.findViewById(R.id.swipyrefreshlayout);
        pbBar = (ProgressBar) theLayout.findViewById(R.id.pbBar);
        initRecyclerView();
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        swipyrefreshlayout.setEnabled(false);


        if (OrderMagazine.faceDetails.size() > 0) {
            recyclerAdapter = new MagazineAdapter(OrderMagazine.instDetails, getActivity());
            recyclerView.setAdapter(recyclerAdapter);
        } else {


            if (!facebookId.contentEquals("") && functions.getPref(StaticVariables.HASFACEBOOKLOGIN, false)) {
                //retrieve facebook photos here to test


                try {
                    pbBar.setVisibility(View.VISIBLE);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new BindMapAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new BindMapAsync().execute();
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } else {
                tvNoPhotos.setVisibility(View.VISIBLE);
                pbBar.setVisibility(View.GONE);
            }


        }


        return theLayout;
    }


    private void getPics() {
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
        //
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
                        GettersAndSetters Details = OrderMagazine.instDetails.get(position);
                        if (Details.fileType == 0) {
                            if (Details.selected) {
                                //remove selection
                                OrderMagazine.faceDetails.get(position).selected = false;
                                OrderMagazine.selected.remove(Details.cover);

                            } else {
                                //add selection
                                OrderMagazine.faceDetails.get(position).selected = true;
                                OrderMagazine.selected.put(Details.cover, Details.serverID);


                            }
                            recyclerAdapter.notifyDataSetChanged();


                        }
                    }
                })
        );

    }


    private void retrieveImages(final AccessToken currentAccessToken) {
        pbBar.setVisibility(View.VISIBLE);
        Bundle params = new Bundle();
        params.putString("fields", "id,name");
        String url = "/me/albums";
        new GraphRequest(
                currentAccessToken,
                url,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {

                            JSONObject resp = response.getJSONObject();
                            System.out.println("++++++++++++++++++++ " + resp.toString());

                            JSONArray data = functions.getJsonArray(resp, StaticVariables.DATA);
                            if (data != null) {
                                totals = data.length();

                                retireveSinglePhoto(data, 0, currentAccessToken);
                            }

                        } catch (Exception e) {
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
        if (!facebookId.contentEquals("")) {
            if (accessTokenTracker != null)
                accessTokenTracker.stopTracking();
        }
    }


    private void retireveSinglePhoto(final JSONArray data, final int pos, final AccessToken currentAccessToken) {

        if (pos < totals) {
            String albumID = "";

            try {
                albumID = functions.getJsonString(data.getJSONObject(0), StaticVariables.ID);
            } catch (Exception e) {

            }

            //access it here
            Bundle params = new Bundle();
            params.putString("fields", "id,link");
            String url = "/" + albumID + "/photos";
            new GraphRequest(
                    currentAccessToken,
                    url,
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                            try {
                                System.out.println("++++++++++++++++++++ single image" + response.toString());
                                JSONObject resp = response.getJSONObject();

                                JSONArray dataimage = functions.getJsonArray(resp, StaticVariables.DATA);
                                if (dataimage != null) {
                                    for (int i = 0; i < dataimage.length(); i++) {
                                        JSONObject c = dataimage.getJSONObject(i);
                                        String imageId = functions.getJsonString(c, StaticVariables.ID);
                                        getSingleImageUrl(imageId, currentAccessToken);
                                    }
                                }
                                retireveSinglePhoto(data, (pos + 1), currentAccessToken);
                            } catch (Exception e) {
                                e.printStackTrace();
                                pbBar.setVisibility(View.GONE);
                            }

                        }
                    }
            ).executeAsync();
        } else {
            //bind data here
            recyclerAdapter = new MagazineAdapter(OrderMagazine.instDetails, getActivity());
            recyclerView.setAdapter(recyclerAdapter);
            pbBar.setVisibility(View.GONE);
        }
    }

    private void getSingleImageUrl(String imageId, AccessToken currentAccessToken) {


        //access it here
        Bundle params = new Bundle();
        params.putString("fields", "id,link,images");
        String url = "/" + imageId;
        new GraphRequest(
                currentAccessToken,
                url,
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {

                            JSONObject resp = response.getJSONObject();
                            System.out.println("++++++++++++++++++++ single image url" + resp.toString());

                            JSONArray imagesArray = functions.getJsonArray(resp, StaticVariables.IMAGES);

                            if (imagesArray != null) {

                                if (imagesArray.length() > 0) {
                                    JSONObject thummb = imagesArray.getJSONObject(0);
                                    JSONObject big = imagesArray.getJSONObject((int) imagesArray.length() / 2);
                                    GettersAndSetters Details = new GettersAndSetters();
                                    JsonObject add = new JsonObject();
                                    add.addProperty(StaticVariables.URL, functions.getJsonString(thummb, StaticVariables.SOURCE));

                                    if (map2.get(functions.getJsonString(thummb, StaticVariables.SOURCE)) == null) {
                                        Details.setCover(functions.getJsonString(thummb, StaticVariables.SOURCE));
                                        Details.setFileType(0);
                                        Details.setSelected(false);
                                        OrderMagazine.faceDetails.add(Details);
                                        StaticVariables.piczlerMag.put(functions.getJsonString(thummb, StaticVariables.SOURCE), "" + 0);
                                        map2.put(functions.getJsonString(thummb, StaticVariables.SOURCE), functions.getJsonString(thummb, StaticVariables.SOURCE));
                                    }


                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }


    private void bindMap() {
        try {
            Set<String> keys = StaticVariables.facebookMag.keySet();
            GettersAndSetters Details;

            for (String i : keys) {
                Details = new GettersAndSetters();
                Details.setCover(i);
                Details.setSelected(false);
                Details.setType(0);
                OrderMagazine.faceDetails.add(Details);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    class BindMapAsync extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        /**
         * Creating product
         */
        @Override
        protected String doInBackground(String... args) {

            try {
                bindMap();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {

                System.out.println("bbbbbbbb: hmmmmmmm");
                recyclerAdapter = new MagazineAdapter(OrderMagazine.faceDetails, getActivity());
                recyclerView.setAdapter(recyclerAdapter);



            pbBar.setVisibility(View.GONE);


        }


    }






}
