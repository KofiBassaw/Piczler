package com.piczler.piczler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.File;


/**
 * Created by bassaw on 24/11/2014.
 */
public class WebPageClassInstagram extends ActionBarActivity {

    protected WebView webView;
    ProgressBar pbBar;
    boolean success = false;
    String returnUrl;
    String orderUrl;
    String orderID;
    int response = -1;
    String message = "";
    UserFunctions function;
    ProgressDialog pDialog;
    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_page2);
       Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        webView = ((WebView)findViewById(R.id.webView));
        final ActionBar action=getSupportActionBar();
        pbBar= (ProgressBar) findViewById(R.id.pbBar);
        function = new UserFunctions(this);
       // action.hide();
        pbBar.setVisibility(View.VISIBLE);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...


                try{
                        setProgress(progress); //Make the bar disappear after URL is loaded

                    // Return the app name after finish loading
                    System.out.println("bbbbbb " + webView.getUrl());
                    if(progress>=100) {
                        pbBar.setVisibility(View.GONE);
                        System.out.println("bbbbbb " + webView.getUrl());
                        if (webView.getUrl().startsWith(StaticVariables.INSTAGRAM_CALLBACK_BASE))
                        {
                            if(!finished)
                            {

                                Uri uri = Uri.parse(webView.getUrl());
                                String code = uri.getQueryParameter("code");

                                if(code !=null)
                                {
                                    finished = true;
                                    getTokens(code);

                                }else
                                {
                                    String error_description = uri.getQueryParameter("error_description");
                                    AlertDialog dd = new AlertDialog.Builder(WebPageClassInstagram.this).create();
                                    dd.setMessage(error_description);
                                    dd.setButton(Dialog.BUTTON_NEGATIVE, "Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finished = false;
                                            webView.loadUrl(StaticVariables.INSTAGRAM_AUTH_URL);
                                        }
                                    });

                                    dd.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finished = false;
                                            onBackPressed();
                                        }
                                    });

                                    dd.show();
                                }
                            }




                        }
                    }else {
                        pbBar.setVisibility(View.VISIBLE);
                    }
                  //  action.hide();

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        // Load the URLs inside the WebView, not in the external web browser
        webView.setWebViewClient(new WebViewClient());
        webView.clearCache(true);

       // Map<String, String> headers = new HashMap<String, String>();
      //  headers.put("Authorization", "Basic" + Base64.encodeToString("kofieassaw" + ':' + "kofieassaw"),Base64.DEFAULT);

         returnUrl = StaticVariables.INSTAGRAM_CALLBACK_BASE;
        webView.loadUrl(StaticVariables.INSTAGRAM_AUTH_URL);

    }




    @Override
    public File getCacheDir()
    {
        // NOTE: this method is used in Android 2.1

        return getApplicationContext().getCacheDir();
    }

    @Override
    protected void onDestroy() {

        webView.clearCache(true);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (success)
            setResult(RESULT_OK);
            super.onBackPressed();


    }



    private void getTokens(final String code)
    {
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later



            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Retrieving access token...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load("POST", "https://api.instagram.com/oauth/access_token")
                    .progressDialog(pDialog)
                    .setBodyParameter("client_id", StaticVariables.INSTAGRAM_CLIENT_ID)
                    .setBodyParameter("client_secret", StaticVariables.INSTAGRAM_CLIENT_SECRET)
                    .setBodyParameter("grant_type", "authorization_code")
                    .setBodyParameter("redirect_uri", StaticVariables.INSTAGRAM_CALLBACK_BASE)
                    .setBodyParameter("code", code)
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
                                pDialog.dismiss();

                                if (result != null) {
                                  JSONObject json = new JSONObject(result);
                                    String access_token = function.getJsonString(json,"access_token");
                                    JSONObject user = json.getJSONObject("user");
                                    String id = function.getJsonString(user,"id");

                                    function.setPref(StaticVariables.HASINSTALINKED,true);
                                    function.setPref(StaticVariables.INSTAACCESSTOKEN,access_token);
                                    function.setPref(StaticVariables.INSTAGRAM_ID,id);
                                    updateFacebook();

                                } else {
                                    resendToken(code,"Unable to retrieve access token");
                                }

                            } catch (Exception ex) {
                                resendToken(code,"Unable to retrieve access token");
                                ex.printStackTrace();
                            }
                            pDialog.dismiss();
                        }
                    });


        } else {
            resendToken(code, "No internet Connection Please try again later");
           // Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }



    }



    private void resendToken(final String code, String message)
    {
        AlertDialog dd = new AlertDialog.Builder(WebPageClassInstagram.this).create();
        dd.setMessage(message);
        dd.setCancelable(false);
        dd.setButton(Dialog.BUTTON_NEGATIVE, "Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTokens(code);
            }
        });

        dd.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        dd.show();
    }









    private void updateFacebook(){
        pDialog = new ProgressDialog(this);
        pDialog = function.prepareDialog("Updating profile...",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/self")
                .setHeader(StaticVariables.USERAGENT, function.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, function.getPhoneID())
                .setHeader(StaticVariables.COKIE, function.getCokies())
                .setMultipartParameter(StaticVariables.DISPLAYNAME, function.getPref(StaticVariables.DISPLAYNAME, ""))
                .setMultipartParameter(StaticVariables.FULLNAME, function.getPref(StaticVariables.FULLNAME, ""))
                .setMultipartParameter(StaticVariables.INSTAGRAM_ID, function.getPref(StaticVariables.INSTAGRAM_ID,""))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error

                        try {
                            pDialog.dismiss();

                            if (result != null) {
                                System.out.println("----------------------- " + result);
                                JSONObject json = new JSONObject(result);

                                JSONObject meta = function.getJsonObject(json, StaticVariables.META);

                                if (meta != null) {
                                    int code = function.getInt(meta, StaticVariables.CODE);
                                    if (code == 200) {
                                        JSONObject data = function.getJsonObject(json, StaticVariables.DATA);
                                        if (data != null) {
                                            JSONObject user = function.getJsonObject(data, StaticVariables.USER);
                                            if (user != null) {
                                                String instaID = function.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                                String displayName = function.getJsonString(user, StaticVariables.DISPLAYNAME);
                                                String locale = function.getJsonString(user, StaticVariables.LOCALE);
                                                String country = function.getJsonString(user, StaticVariables.COUNTRY);
                                                String email = function.getJsonString(user, StaticVariables.EMAIL);
                                                String fullname = function.getJsonString(user, StaticVariables.FULLNAME);
                                                String id = function.getJsonString(user, StaticVariables.ID);
                                                function.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                function.setPref(StaticVariables.DISPLAYNAME, displayName);
                                                function.setPref(StaticVariables.LOCALE, locale);
                                                function.setPref(StaticVariables.COUNTRY, country);
                                                function.setPref(StaticVariables.EMAIL, email);
                                                function.setPref(StaticVariables.FULLNAME, fullname);
                                                function.setPref(StaticVariables.HASFACEBOOKLOGIN, true);
                                                function.setPref(StaticVariables.INSTAUPDATED, true);
                                                function.setPref(StaticVariables.ID, id);

                                                JSONObject picture = function.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

                                                if (picture != null) {

                                                    JSONObject thumbNail = function.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                    if (thumbNail != null) {
                                                        String thumbURL = function.getJsonString(thumbNail, StaticVariables.URL);
                                                        function.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                    }
                                                    JSONObject mrJson = function.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                    if (mrJson != null) {
                                                        String mr = function.getJsonString(mrJson, StaticVariables.URL);
                                                        function.setPref(StaticVariables.MOBILE_RESOLUTION, mr);


                                                    }
                                                    success = true;
                                                    onBackPressed();




                                                }


                                            }

                                        } else {
                                            //function.showMessage("Unable to update your profile with facebook details");
                                            reverseFaceBook("Unable to update your profile with instagram details");
                                        }


                                    } else if (code == 403) {
                                       // function.showMessage(function.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        reverseFaceBook(function.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                    } else if (code == 400) {
                                       // function.showMessage(function.getJsonString(meta, StaticVariables.DEBUG));
                                        reverseFaceBook(function.getJsonString(meta, StaticVariables.DEBUG));
                                    }
                                } else {
                                    //function.showMessage("Unable update profile");
                                    reverseFaceBook("Unable update profile");
                                }

                            } else {
                                //function.showMessage("Unable to update profile");
                                reverseFaceBook("Unable to update profile");
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }




    private void reverseFaceBook(String message)
    {
        AlertDialog dd = new AlertDialog.Builder(WebPageClassInstagram.this).create();
        dd.setMessage(message);
        dd.setCancelable(true);
        dd.setButton(Dialog.BUTTON_NEGATIVE, "Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFacebook();
            }
        });

        dd.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        dd.show();
    }
}
