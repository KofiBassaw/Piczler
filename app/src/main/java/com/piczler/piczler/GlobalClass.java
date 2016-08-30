package com.piczler.piczler;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.viethoa.models.AlphabetItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import ly.img.android.ImgLySdk;
import ly.img.android.sdk.configuration.AbstractConfig;
import ly.img.android.sdk.filter.ColorFilterBW;
import ly.img.android.sdk.filter.ColorFilterBreeze;
import ly.img.android.sdk.filter.ColorFilterChest;
import ly.img.android.sdk.filter.ColorFilterCottonCandy;
import ly.img.android.sdk.filter.ColorFilterEvening;
import ly.img.android.sdk.filter.ColorFilterFall;
import ly.img.android.sdk.filter.ColorFilterFixie;
import ly.img.android.sdk.filter.ColorFilterFridge;
import ly.img.android.sdk.filter.ColorFilterHighContrast;
import ly.img.android.sdk.filter.ColorFilterLenin;
import ly.img.android.sdk.filter.ColorFilterMellow;
import ly.img.android.sdk.filter.ColorFilterOrchid;
import ly.img.android.sdk.filter.ColorFilterQuozi;
import ly.img.android.sdk.filter.ColorFilterSunset;

//import com.instabug.library.Instabug;

/**
 * Created by matiyas on 4/6/16.
 */
public class GlobalClass extends Application {

    UserFunctions functions;

    @Override
    public void onCreate() {
        super.onCreate();
        ImgLySdk.init(this);
        
        StaticVariables.colorFilterConfig.add(null);
        StaticVariables.colorFilterConfig.add(new ColorFilterBreeze());
        StaticVariables.colorFilterConfig.add(new ColorFilterOrchid());
        StaticVariables.colorFilterConfig.add(new ColorFilterChest());
        StaticVariables.colorFilterConfig.add(new ColorFilterEvening());
        StaticVariables.colorFilterConfig.add(new ColorFilterFixie());
        StaticVariables.colorFilterConfig.add(new ColorFilterCottonCandy());
        StaticVariables.colorFilterConfig.add(new ColorFilterBW());
        StaticVariables.colorFilterConfig.add(new ColorFilterLenin());
        StaticVariables.colorFilterConfig.add(new ColorFilterFridge());
        StaticVariables.colorFilterConfig.add(new ColorFilterQuozi());
        StaticVariables.colorFilterConfig.add(new ColorFilterHighContrast());
        StaticVariables.colorFilterConfig.add(new ColorFilterMellow());
        StaticVariables.colorFilterConfig.add(new ColorFilterFall());
        StaticVariables.colorFilterConfig.add(new ColorFilterSunset());

        functions = new UserFunctions(this);

        if(!functions.getPref(StaticVariables.TOKEN,"").contentEquals(""))
        {

            getCategories(0,100);
            getNotification();



            if(!functions.getPref(StaticVariables.HASGCM,false))
            {
                String gcm = functions.getPref(StaticVariables.GCM,"");

                if(!gcm.contentEquals(""))
                {
                    sendGcm(gcm);
                }
            }
        }

        StaticVariables.details = new ArrayList<>();

       // new BindCountries().execute();
        //bindCount();
       // new BindAsync().execute();
       // bindContact();

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
        {
            new BindAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) ;
        }else {
            new BindAsync().execute();
        }



    }


    private boolean IsChecked(String code)
    {
        String commaSep = functions.getPref(StaticVariables.LOCATIONCOMMASEPERATED,"");

        if(commaSep.contentEquals(""))
            return false;

        if(commaSep.contains(code+","))
            return  true;
        else
            return false;
    }

    private void bindCount()
    {
        try{
            StaticVariables.loadingCOUNTRIES = true;
            String jsonString = loadJSONFromAsset();
            System.out.println(jsonString);
            if(jsonString!=null)
            {
                GettersAndSetters Details;
                JSONArray countries = new JSONArray(jsonString);
                for(int i=0; i<countries.length(); i++)
                {
                    JSONObject one = countries.getJSONObject(i);
                    Details = new GettersAndSetters();
                    Details.setName(one.getString(StaticVariables.NAME));
                    Details.setCode(one.getString(StaticVariables.CODE));
                    Details.setSelected(IsChecked(one.getString(StaticVariables.CODE)));
                    StaticVariables.countries.add(Details);
                }
            }

        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        StaticVariables.loadingCOUNTRIES = false;
        Intent it = new Intent(StaticVariables.COUNTRIES);
        it.putExtra("type","loading");
        sendBroadcast(it);

    }

    private void bindContact()
    {
        StaticVariables.loading = true;
        ContactClass cc = new ContactClass(GlobalClass.this);
        StaticVariables.details = cc.getContactJson();
        Collections.sort( StaticVariables.details, new CustomComparator());


        for(int i=0; i< StaticVariables.details.size(); i++)
        {
            String name = StaticVariables.details.get(i).name;
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!StaticVariables.strAlphabets.contains(word)) {
                StaticVariables.strAlphabets.add(word);
                StaticVariables.mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }


        StaticVariables.loading = false;
        Intent it = new Intent(StaticVariables.FRIEND);
        it.putExtra("type","loading");
        sendBroadcast(it);
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
                                if (e != null) {
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);
                                    functions.setPref(StaticVariables.HASCATEGORY, true);
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {

                                                String oldString = functions.getPref(StaticVariables.CATEGORIESSAVED,"");
                                                 if(offset == 0 || oldString.contentEquals(""))
                                                 {
                                                     functions.setPref(StaticVariables.CATEGORIESSAVED,data.toString());
                                                 }else
                                                 {

                                                     JsonArray oldFeed = new JsonArray();
                                                     JsonArray newFeed = new JsonArray();

                                                     GsonBuilder gsonb = new GsonBuilder();
                                                     Gson gson = gsonb.create();

                                                     oldFeed = gson.fromJson(oldString,JsonArray.class);
                                                     newFeed = gson.fromJson(data.toString(),JsonArray.class);
                                                     oldFeed.addAll(newFeed);
                                                     functions.setPref(StaticVariables.CATEGORIESSAVED, oldFeed.toString());

                                                 }

                                                if(data.length() >= limit)
                                                {
                                                    getCategories((offset + limit + 1), limit);
                                                }



                                            }

                                        }
                                    }

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


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
                bindCount();
                StaticVariables.loading = true;
                ContactClass cc = new ContactClass(GlobalClass.this);
                StaticVariables.details = cc.getContactJson();
                Collections.sort( StaticVariables.details, new CustomComparator());


                for(int i=0; i< StaticVariables.details.size(); i++)
                {
                    String name = StaticVariables.details.get(i).name;
                    if (name == null || name.trim().isEmpty())
                        continue;

                    String word = name.substring(0, 1);
                    if (!StaticVariables.strAlphabets.contains(word)) {
                        StaticVariables.strAlphabets.add(word);
                        StaticVariables.mAlphabetItems.add(new AlphabetItem(i, word, false));
                    }
                }

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
         StaticVariables.loading = false;
            Intent it = new Intent(StaticVariables.FRIEND);
            it.putExtra("type","loading");
            sendBroadcast(it);


        }





    }












    class BindMagazines extends AsyncTask<String, String, String> {

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



        }





    }




    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("countries.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }









    private void getNotification(){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //

            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "users/activities?" + StaticVariables.OFFSET + "=" + 0 + "&&" + StaticVariables.LIMIT + "=" + 100)
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
                                    functions.setPref(StaticVariables.HASCATEGORY, true);
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {
                                                // String  dataString = functions.getPref(StaticVariables.CATEGORIES,"");


                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                    new BindAsync2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data.toString());
                                                } else {
                                                    new BindAsync2().execute(data.toString());
                                                }


                                            }

                                        }
                                    }

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        }


    }






    class BindAsync2 extends AsyncTask<String, String, String> {

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


                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject c = jsonArray.getJSONObject(i);
                    Intent it  = new Intent("com.piczler.piczler.broadcast");
                    it.putExtra("type", "notify");
                    it.putExtra(StaticVariables.ID,functions.getJsonString(c, "media_id"));
                    sendBroadcast(it);
                }

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




        }







    }


    private void sendGcm(String token)
    {


        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/push")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setBodyParameter("push_token",token)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error

                        try {

                            if (result != null) {
                                System.out.println("----------------------- " + result);
                                JSONObject json = new JSONObject(result);

                                JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                if (meta != null) {
                                    int code = functions.getInt(meta, StaticVariables.CODE);
                                    if (code == 200) {
                                       functions.setPref(StaticVariables.HASGCM,true);


                                    }
                                }
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }



}
