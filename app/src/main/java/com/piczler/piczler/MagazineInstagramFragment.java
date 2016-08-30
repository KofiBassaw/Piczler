package com.piczler.piczler;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by matiyas on 8/14/16.
 */
public class MagazineInstagramFragment extends Fragment{

    RecyclerView recyclerView;
    ProgressBar pbBar;
    String media = "";
    UserFunctions functions;

    MagazineAdapter recyclerAdapter;

    SwipyRefreshLayout swipyrefreshlayout;
    TextView tvNoPhotos;
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

        swipyrefreshlayout.setEnabled(false);
        //fab_button = (FloatingActionButton) theLayout.findViewById(R.id.fab_button);
        //  fab_button.attachToRecyclerView(recyclerView);
        initRecyclerView();



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






            if(OrderMagazine.instDetails.size()>0)
            {
                recyclerAdapter = new MagazineAdapter(OrderMagazine.instDetails,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                pbBar.setVisibility(View.VISIBLE);


                Set<String> keys = StaticVariables.instaMag.keySet();
                if(keys.size()>0)
                {

                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                    {
                        new BindMapAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,media) ;
                    }else {
                        new BindMapAsync().execute(media);
                    }
                }else{
                    getInstaPictures();
                }



            }
        }else {

            tvNoPhotos.setVisibility(View.VISIBLE);
            pbBar.setVisibility(View.GONE);
        }










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
                        GettersAndSetters Details = OrderMagazine.instDetails.get(position);
                        if (Details.fileType == 0) {
                            if (Details.selected) {
                                //remove selection
                                OrderMagazine.instDetails.get(position).selected = false;
                                OrderMagazine.selected.remove(Details.cover);

                            } else {
                                //add selection
                                OrderMagazine.instDetails.get(position).selected = true;
                                OrderMagazine.selected.put(Details.cover, Details.serverID);


                            }
                            recyclerAdapter.notifyDataSetChanged();


                        }
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
            recyclerAdapter = new MagazineAdapter(OrderMagazine.instDetails,getActivity());
            recyclerView.setAdapter(recyclerAdapter);

            if(OrderMagazine.instDetails.size() == 0)
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
            ArrayList<GettersAndSetters> detailed = new ArrayList<>();
            JsonArray imeger = new JsonArray();

            if(data!=null)
            {
                for(int i=0; i<data.length(); i++)
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
                            detailed.add(Details);
                            Details.setSelected(false);
                            //  StaticVariables.instaMag.add(Details);
                            StaticVariables.instaMag.put(standardUrlString, "" + 0);
                            imeger.add(add);
                        }




                    }


                }
                OrderMagazine.instDetails = new ArrayList<>();

                OrderMagazine.instDetails = detailed;
            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    private void getInstaPictures() {

        ConnectionDetector cd=new ConnectionDetector(getActivity());
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load("GET", "https://api.instagram.com/v1/users/self/media/recent?access_token="+functions.getPref(StaticVariables.INSTAACCESSTOKEN,""))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("bbbbbbb: " + result);


                                if (result != null) {


                                    media = result;
                                    Database db = new Database(getActivity());
                                    db.open();
                                    db.insertSampleDetails(functions.getPref(StaticVariables.ID, ""), StaticVariables.INSTAGRAMPICS, media);
                                    db.close();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                        new BindAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, media);
                                    } else {
                                        new BindAsync().execute(media);
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
        if(OrderMagazine.instDetails.size()==0)
        {
            tvNoPhotos.setVisibility(View.VISIBLE);
            functions.showMessage(message);
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
                recyclerAdapter = new MagazineAdapter(OrderMagazine.instDetails,getActivity());
                recyclerView.setAdapter(recyclerAdapter);
            }else {
                recyclerAdapter.notifyDataSetChanged();
            }



            pbBar.setVisibility(View.GONE);



        }






    }




    private void bindMap()
    {
        try{
            Set<String> keys = StaticVariables.instaMag.keySet();
            GettersAndSetters Details;

            for(String i: keys)
            {
                Details = new GettersAndSetters();
                Details.setCover(i);
                Details.setSelected(false);
                Details.setType(0);
                OrderMagazine.instDetails.add(Details);
            }


        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



}
