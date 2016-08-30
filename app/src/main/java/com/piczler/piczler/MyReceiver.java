package com.piczler.piczler;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.viethoa.models.AlphabetItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;


public class MyReceiver extends BroadcastReceiver {

Context mContext;

    //fhhd


	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
   

    	 
         if(intent.getAction().contentEquals("com.piczler.piczler.broadcast")) {
             mContext = context;
             String type = intent.getStringExtra("type");

             if(type.contentEquals("comment"))
             {
                 String userID = intent.getStringExtra(StaticVariables.ID);
                 getComment(userID);
             }else if(type.contentEquals("notify"))
             {
                 String userID = intent.getStringExtra(StaticVariables.ID);
                 getNotify(userID);
             }else if(type.contentEquals("bindStaffs"))
             {

                 if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                 {
                     new BindCountries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) ;
                 }else {
                     new BindCountries().execute();
                 }

             }
         }
	}


    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = mContext.getAssets().open("countries.json");

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
                    Details.setSelected(false);
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
        mContext.sendBroadcast(it);

    }







    private void getComment(final String userID)
    {

        ConnectionDetector cd=new ConnectionDetector(mContext);
        if(cd.isConnectingToInternet()){//chang this function later

          final UserFunctions functions = new UserFunctions(mContext);


            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(mContext)
                    .load("GET", StaticVariables.BASE_URL + "media/"+userID+"/comments?" + StaticVariables.OFFSET + "=0" + "&&" + StaticVariables.LIMIT + "=100")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {
                                               Database db = new Database(mContext);
                                                db.open();
                                                db.insertSampleDetails(userID,StaticVariables.COMMENT_JSON,data.toString());
                                                db.close();

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





    private void getNotify(final String userID)
    {

        ConnectionDetector cd=new ConnectionDetector(mContext);
        if(cd.isConnectingToInternet()){//chang this function later

          final UserFunctions functions = new UserFunctions(mContext);


            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(mContext)
                    .load("GET", StaticVariables.BASE_URL + "media/" +userID)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                            if (data != null) {
                                               Database db = new Database(mContext);
                                                db.open();
                                                db.insertSampleDetails(userID,StaticVariables.NOTIFICSTIONS,data.toString());
                                                db.close();

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





    private boolean IsChecked(String code)
    {
        UserFunctions functions = new UserFunctions(mContext);
        String commaSep = functions.getPref(StaticVariables.LOCATIONCOMMASEPERATED,"");

        if(commaSep.contentEquals(""))
            return false;

        if(commaSep.contains(code + ","))
            return  true;
        else
            return false;
    }






    class BindCountries extends AsyncTask<String, String, String> {

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




            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            StaticVariables.loadingCOUNTRIES = false;
            Intent it = new Intent(StaticVariables.COUNTRIES);
            it.putExtra("type","loading");
            mContext.sendBroadcast(it);


        }





    }


}


