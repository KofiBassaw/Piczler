package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matiyas on 11/14/15.
 */
public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    RelativeLayout rlCreateAccount;
    RippleView rpCreateAccount;
    EditText etEmail,etPassword;
    String emailString = "", passwordString = "";
    UserFunctions functions;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        rlCreateAccount = (RelativeLayout) findViewById(R.id.rlCreateAccount);
        rpCreateAccount = (RippleView) findViewById(R.id.rpCreateAccount);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        functions = new UserFunctions(this);
        pDialog = new ProgressDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setTitle("WELCOME");


        rlCreateAccount.setOnClickListener(this);
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
    public void onBackPressed() {
        startActivity(new Intent(this,SignIn.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlCreateAccount:
                rpCreateAccount.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                        emailString = functions.getText(etEmail);
                        passwordString = etPassword.getText().toString();
                        if(functions.isEmpty(emailString)){
                            etEmail.setError("Please provide your email");
                        }else if(functions.isEmpty(passwordString)){
                            etPassword.setError("Please provide your password");
                        }else{
                           sendData();
                            //startActivity(new Intent(SignUp.this, CompleteProject.class));
                        }


                    }
                });
                break;
        }
    }



    private void sendData(){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            pDialog.setMessage("Creating an account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load(StaticVariables.BASE_URL + "users")
                    .setHeader(StaticVariables.USERAGENT,functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID,functions.getPhoneID())
                    .progressDialog(pDialog)
                    .setBodyParameter(StaticVariables.EMAIL, emailString)
                    .setBodyParameter(StaticVariables.PASSWORD, passwordString)
                    .setBodyParameter(StaticVariables.CONFIRM, passwordString)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if(e != null){
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                               System.out.println("------------------------------- "+result);

                               if (result != null){
                                   JSONObject json = new JSONObject(result);


                                   JSONObject meta = functions.getJsonObject(json,StaticVariables.META);

                                   if(meta != null){
                                       int code  = functions.getInt(meta, StaticVariables.CODE);
                                       if (code == 200){

                                           JSONObject data = functions.getJsonObject(json,StaticVariables.DATA);
                                           if(data != null){

                                               MixpanelAPI mixpanel =
                                                       MixpanelAPI.getInstance(SignUp.this, StaticVariables.MIXPANEL_TOKEN);


                                               functions.setPref(StaticVariables.TOKEN, data.getString(StaticVariables.TOKEN));
                                               JSONObject user = functions.getJsonObject(data,StaticVariables.USER);
                                               String token = functions.getJsonString(data,StaticVariables.TOKEN);
                                               functions.setPref(StaticVariables.TOKEN,token);
                                               functions.setPref(StaticVariables.HASLOGEDIN,true);
                                               if(user != null){
                                               String instaID = functions.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                               String displayName = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                                               String locale = functions.getJsonString(user, StaticVariables.LOCALE);
                                               String country = functions.getJsonString(user, StaticVariables.COUNTRY);
                                               String email = functions.getJsonString(user, StaticVariables.EMAIL);
                                               String fullname = functions.getJsonString(user, StaticVariables.FULLNAME);
                                               String facebookID = functions.getJsonString(user, StaticVariables.FACEBOOKEID);
                                               String id = functions.getJsonString(user, StaticVariables.ID);
                                                   functions.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                   functions.setPref(StaticVariables.DISPLAYNAME, displayName);





                                                   functions.setPref(StaticVariables.LOCALE, locale);
                                                   functions.setPref(StaticVariables.COUNTRY, country);
                                                   functions.setPref(StaticVariables.EMAIL, email);
                                                   functions.setPref(StaticVariables.FULLNAME, fullname);
                                                   functions.setPref(StaticVariables.FACEBOOKEID, facebookID);
                                                   functions.setPref(StaticVariables.ID, id);

                                               JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);






                                                   try {
                                                       mixpanel.identify(id);
                                                       mixpanel.getPeople().set("name", displayName);
                                                       mixpanel.getPeople().set("email", email);
                                                       mixpanel.getPeople().set("Facebook Connected", "NO");
                                                       mixpanel.getPeople().set("Instagram Connected", "NO");
                                                       JSONObject props = new JSONObject();
                                                       props.put("Method", "Email");
                                                       mixpanel.track("SignUp", props);
                                                   } catch (JSONException ex) {
                                                       Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                                                   }






                                              if(picture !=  null){

                                                  JSONObject thumbNail = functions.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                  if(thumbNail != null){
                                                      String thumbURL = functions.getJsonString(thumbNail, StaticVariables.URL);
                                                      functions.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                  }
                                                  JSONObject mrJson = functions.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                  if(mrJson != null){
                                                      String mr = functions.getJsonString(mrJson, StaticVariables.URL);
                                                      functions.setPref(StaticVariables.MOBILE_RESOLUTION, mr);

                                                  }


                                              }


                                               }

                                           }else{
                                               functions.showMessage("Unable to retrieve data");
                                           }

                                           startActivity(new Intent(SignUp.this,CompleteProject.class));
                                           finish();
                                       }else if (code == 403){
                                           functions.showMessage(functions.getJsonString(meta,StaticVariables.ERROR_MESSAGE));
                                       }else{
                                           functions.showMessage(functions.getJsonString(meta,StaticVariables.DEBUG));
                                       }
                                   }else{
                                       functions.showMessage("Unable to create account");
                                   }

                               }else{
                                   functions.showMessage("Unable to create account");
                               }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            pDialog.dismiss();
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }


}
