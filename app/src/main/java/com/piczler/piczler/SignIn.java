package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.facebook.FacebookSdk;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by matiyas on 11/14/15.
 */
public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    RelativeLayout rlEmail,rlForgot,rlLogin,rlFaceBook;
    RippleView rpEmail,rpForgot,rpLogin,rpFacebook;
    UserFunctions functions;

    EditText etEmail,etPassword;
    String emailString = "", passwordString = "";
    ProgressDialog pDialog;
    //CallbackManager callbackManager;
    String facebook_accessToken="";
    String faceBookEmail="", facebookUserIduserId ="";
    private final int FACEBOOKINTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        rlEmail = (RelativeLayout) findViewById(R.id.rlEmail);
        rlForgot = (RelativeLayout) findViewById(R.id.rlForgot);
        rlLogin = (RelativeLayout) findViewById(R.id.rlLogin);
        rlFaceBook = (RelativeLayout) findViewById(R.id.rlFaceBook);
        rpEmail = (RippleView) findViewById(R.id.rpEmail);
        rpForgot = (RippleView) findViewById(R.id.rpForgot);
        rpLogin = (RippleView) findViewById(R.id.rpLogin);
        rpFacebook = (RippleView) findViewById(R.id.rpFacebook);
        setSupportActionBar(toolbar);
        functions = new UserFunctions(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  setTitle("WELCOME");
        rlEmail.setOnClickListener(this);
        rlForgot.setOnClickListener(this);
        rlLogin.setOnClickListener(this);
        rlFaceBook.setOnClickListener(this);
        pDialog = new ProgressDialog(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);



    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FACEBOOKINTENT && resultCode == RESULT_OK)
        {
            Bundle bb=  data.getExtras();
            facebook_accessToken = bb.getString("facebook_accessToken");
            facebookUserIduserId = bb.getString("facebookUserIduserId");


            loginwithfacebook();
        }
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlEmail:
                rpEmail.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        startActivity(new Intent(SignIn.this, SignUp.class));
                        finish();
                    }
                });

                break;
            case R.id.rlForgot:
                rpForgot.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        startActivity(new Intent(SignIn.this, ResetPassword.class));
                    }
                });

                break;
            case R.id.rlLogin:
                rpLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        emailString = functions.getText(etEmail);
                        passwordString = etPassword.getText().toString();
                        if(functions.isEmpty(emailString)){
                            etEmail.setError("Please provide your email");
                        }else if(functions.isEmpty(emailString)){
                            etPassword.setError("Please provide your password");
                        }else{
                            sendData();
                        }

                    }
                });

                break;
            case R.id.rlFaceBook:

                rpFacebook.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {





                        if(facebook_accessToken == "")
                        {
                            Intent it = new Intent(SignIn.this, FacebookMain.class);
                            startActivityForResult(it,FACEBOOKINTENT);
                           // startFaceBook();
                        }else {
                            loginwithfacebook();
                        }


                    }
                });
                break;
        }
    }


    /*

    private void startFaceBook(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile", "email","user_photos"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        facebook_accessToken = loginResult.getAccessToken().getToken().toString();
                        facebookUserIduserId = loginResult.getAccessToken().getUserId();

                        loginwithfacebook();


                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(SignIn.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(SignIn.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                    }
                });
    }

    */


    private void sendData(){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load(StaticVariables.BASE_URL + "users/login")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .progressDialog(pDialog)
                    .setBodyParameter(StaticVariables.EMAIL, emailString)
                    .setBodyParameter(StaticVariables.PASSWORD, passwordString)
                    .setBodyParameter(StaticVariables.METHOD, "0")
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
                                                        MixpanelAPI.getInstance(SignIn.this, StaticVariables.MIXPANEL_TOKEN);
                                                functions.setPref(StaticVariables.TOKEN, data.getString(StaticVariables.TOKEN));
                                                JSONObject user = functions.getJsonObject(data,StaticVariables.USER);
                                                String token = functions.getJsonString(data,StaticVariables.TOKEN);
                                                functions.setPref(StaticVariables.TOKEN,token);
                                                functions.setPref(StaticVariables.HASLOGEDIN,true);
                                                functions.setPref(StaticVariables.HASCATEGORY,true);
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
                                                        mixpanel.track("SignIn", props);
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

                                            //startActivity(new Intent(SignIn.this,MainHome.class));
                                            startActivity(new Intent(SignIn.this,MainHome.class));
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



    private void loginwithfacebook(){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load(StaticVariables.BASE_URL + "users/login")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .progressDialog(pDialog)
                    .setBodyParameter(StaticVariables.FACEBOOKEID, facebookUserIduserId)
                    .setBodyParameter(StaticVariables.ACCESSTOKEN, facebook_accessToken)
                    .setBodyParameter(StaticVariables.METHOD, "1")
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
                                            MixpanelAPI mixpanel =
                                                    MixpanelAPI.getInstance(SignIn.this, StaticVariables.MIXPANEL_TOKEN);
                                            JSONObject data = functions.getJsonObject(json,StaticVariables.DATA);
                                            if(data != null){
                                                functions.setPref(StaticVariables.TOKEN, data.getString(StaticVariables.TOKEN));
                                                JSONObject user = functions.getJsonObject(data,StaticVariables.USER);
                                                String token = functions.getJsonString(data,StaticVariables.TOKEN);
                                                functions.setPref(StaticVariables.TOKEN,token);
                                                functions.setPref(StaticVariables.HASLOGEDIN,true);
                                                functions.setPref(StaticVariables.HASFACEBOOKLOGIN,true);
                                                functions.setPref(StaticVariables.ACCESSTOKEN, facebook_accessToken);
                                                functions.setPref(StaticVariables.FACEBOOKEID, facebookUserIduserId);
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





                                                    try {
                                                        mixpanel.identify(id);
                                                        mixpanel.getPeople().set("name", displayName);
                                                        mixpanel.getPeople().set("email", email);
                                                        mixpanel.getPeople().set("Facebook Connected", "YES");
                                                        mixpanel.getPeople().set("Instagram Connected", "NO");
                                                        JSONObject props = new JSONObject();
                                                        props.put("Method", "Facebook");
                                                        mixpanel.track("SignIn", props);
                                                    } catch (JSONException ex) {
                                                        Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                                                    }

                                                    JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

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

                                            //startActivity(new Intent(SignIn.this,MainHome.class));
                                            startActivity(new Intent(SignIn.this,Categories.class));
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
