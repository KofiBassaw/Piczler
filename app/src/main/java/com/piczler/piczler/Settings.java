package com.piczler.piczler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

/**
 * Created by matiyas on 12/13/15.
 */
public class Settings extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    TextView tvEmail;
    UserFunctions functions;

    RelativeLayout rlLogout,rlEditProfile,rlBlocked,rlTellFriend,rlLocation,rlLegal,rlHelp;
    RippleView rpLogout,rpEditProfile,rpBlocked,rpTellFriend,rpLocation,rpLegal,rpHelp;
    private static final int UNBLOCKED = 1;
    private static final int INSAGRAMLINKED = 2;
    private static final int LOCATIONCHANGED = 3;
    boolean hasUnblocked = false;
    boolean hasLoggedOut = false;
    CallbackManager callbackManager;
    CheckBox cbFacebook,cbInstagram;
    String facebook_accessToken="";
    String faceBookEmail="", facebookUserIduserId ="";
    TextView tvLocText;

    ProgressDialog pDialog;
    private final int FACEBOOKINTENT = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        cbFacebook = (CheckBox) findViewById(R.id.cbFacebook);
        cbInstagram = (CheckBox) findViewById(R.id.cbInstagram);
        functions = new UserFunctions(this);
        rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
        rlEditProfile = (RelativeLayout) findViewById(R.id.rlEditProfile);
        rlTellFriend = (RelativeLayout) findViewById(R.id.rlTellFriend);
        rlHelp = (RelativeLayout) findViewById(R.id.rlHelp);
        rlLocation = (RelativeLayout) findViewById(R.id.rlLocation);
        rlBlocked = (RelativeLayout) findViewById(R.id.rlBlocked);
        rlLegal = (RelativeLayout) findViewById(R.id.rlLegal);
        rpLogout = (RippleView) findViewById(R.id.rpLogout);
        rpEditProfile = (RippleView) findViewById(R.id.rpEditProfile);
        rpBlocked = (RippleView) findViewById(R.id.rpBlocked);
        rpTellFriend = (RippleView) findViewById(R.id.rpTellFriend);
        rpLocation = (RippleView) findViewById(R.id.rpLocation);
        rpLegal = (RippleView) findViewById(R.id.rpLegal);
        rpHelp = (RippleView) findViewById(R.id.rpHelp);
        tvLocText = (TextView) findViewById(R.id.tvLocText);





        tvEmail.setText(functions.getPref(StaticVariables.EMAIL, ""));


        rlLogout.setOnClickListener(this);
        rlEditProfile.setOnClickListener(this);
        rlBlocked.setOnClickListener(this);
        rlTellFriend.setOnClickListener(this);
        rlLocation.setOnClickListener(this);
        rlLegal.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        cbInstagram.setOnClickListener(this);


        boolean isLog = isFacebookLoggedIn();
        boolean hasFaceBookLogedIn = functions.getPref(StaticVariables.HASFACEBOOKLOGIN,false);

        if(isLog && hasFaceBookLogedIn)
        {
            //set button to checked
            cbFacebook.setChecked(true);
        }else {
            //set button to unchecked
            cbFacebook.setChecked(false);

        }


        if(functions.getPref(StaticVariables.INSTAUPDATED,false) && functions.getPref(StaticVariables.HASINSTALINKED,false))
        {
            cbInstagram.setChecked(true);
        }else {
            cbInstagram.setChecked(false);
        }

        cbFacebook.setOnClickListener(this);

        changeLoc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UNBLOCKED && resultCode == RESULT_OK)
        {
            hasUnblocked = true;
        }else if(requestCode == LOCATIONCHANGED && resultCode == RESULT_OK)
        {
            changeLoc();
            hasUnblocked = true;
            Intent it = new Intent("com.piczler.piczler.broadcast");
            it.putExtra("type","bindStaffs");
            sendBroadcast(it);
        }else if(requestCode == INSAGRAMLINKED)
        {
            if(resultCode == RESULT_OK)
           cbInstagram.setChecked(true);
            else
                cbInstagram.setChecked(false);
        }else  if(requestCode == FACEBOOKINTENT && resultCode == RESULT_OK)
        {
            Bundle bb=  data.getExtras();
            facebook_accessToken = bb.getString("facebook_accessToken");
            facebookUserIduserId = bb.getString("facebookUserIduserId");
            functions.setPref(StaticVariables.ACCESSTOKEN, facebook_accessToken);
            updateFacebook();

        }else if(requestCode == FACEBOOKINTENT && resultCode != RESULT_OK)
        {
            cbFacebook.setChecked(false);
        }
    }


    private void changeLoc()
    {

        boolean  locationMonitor =  functions.getPref(StaticVariables.HASLOCATION,false);
        String  locComma =  functions.getPref(StaticVariables.LOCATIONMAESSEPERATED,"");
        if(locationMonitor)
        {
            tvLocText.setText(locComma);
        }else
        {
            tvLocText.setText("Global");
        }

    }

    @Override
    public void onBackPressed() {
        if(hasUnblocked && !hasLoggedOut)
        {
            Bundle bb = new Bundle();
            bb.putString("type","unblock");
            Intent it = new Intent();
            it.putExtras(bb);
            setResult(RESULT_OK, it);
        }
        super.onBackPressed();
    }

    private void logOut()
    {

        try
        {
           Database db = new Database(this);
            db.open();
               db.deleteAllData();
            db.close();
            functions.deletePref();
            Bundle bb = new Bundle();
            bb.putString("type", "logout");
            Intent it = new Intent();
            it.putExtras(bb);
            setResult(RESULT_OK, it);
            hasLoggedOut = true;
            onBackPressed();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }




    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.rlLogout:
                rpLogout.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        AlertDialog dd = new AlertDialog.Builder(Settings.this).create();
                        dd.setMessage("Are you sure you want to logout?");
                        dd.setButton(Dialog.BUTTON_NEGATIVE, "LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logOut();
                            }
                        });

                        dd.setButton(Dialog.BUTTON_POSITIVE, "CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                        dd.show();
                    }


                });
                break;

            case R.id.rlEditProfile:
                rpEditProfile.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,CompleteProject.class);
                        it.putExtra(StaticVariables.FROM, "settings");
                        startActivity(it);
                    }
                });
                break;
            case R.id.rlTellFriend:
                rpTellFriend.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,FriendActivity.class);
                        startActivity(it);
                    }
                });
                break;
            case R.id.rlLocation:
                rpLocation.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,LocationActivity.class);
                        startActivityForResult(it, LOCATIONCHANGED);
                    }
                });
                break;
            case R.id.rlLegal:
                rpLegal.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,WebPageClass.class);
                        it.putExtra(StaticVariables.URL,"http://piczler.com/privacy");
                        it.putExtra(StaticVariables.TITLE,getString(R.string.legal));
                        startActivity(it);
                    }
                });
                break;
            case R.id.rlHelp:
                rpHelp.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,WebPageClass.class);
                        it.putExtra(StaticVariables.URL,"http://piczler.com/help");
                        it.putExtra(StaticVariables.TITLE,"Frequently ask questions");
                        startActivity(it);
                    }
                });
                break;
            case R.id.rlBlocked:
                rpBlocked.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(Settings.this,BlockedActivity.class);
                        startActivityForResult(it,UNBLOCKED);
                    }
                });
                break;

            case R.id.cbFacebook:

                if(!cbFacebook.isChecked())
                {
                    LoginManager.getInstance().logOut();
                    functions.setPref(StaticVariables.FACEBOOKEID, "");
                    functions.setPref(StaticVariables.ACCESSTOKEN, "");
                    functions.setPref(StaticVariables.HASFACEBOOKLOGIN,false);
                }else
                {
                    boolean isLog = isFacebookLoggedIn();
                    boolean hasFaceBookLogedIn = functions.getPref(StaticVariables.HASFACEBOOKLOGIN,false);

                    if(isLog)
                    {
                        //resend the token to the server
                        facebook_accessToken = functions.getPref(StaticVariables.ACCESSTOKEN,"");
                        facebookUserIduserId = functions.getPref(StaticVariables.FACEBOOKEID,"");
                        updateFacebook();

                    }else if(!isLog)
                    {
                        //login with facebook
                        //startFaceBook();
                        Intent it = new Intent(Settings.this, FacebookMain.class);
                        startActivityForResult(it, FACEBOOKINTENT);

                    }
                }




                break;
            case R.id.cbInstagram:

                if(cbInstagram.isChecked())
                {
                    if(functions.getPref(StaticVariables.HASINSTALINKED,false))
                    {
                        //update userdetails with instagram
                        updateInsta();

                    }else
                    {
                        //start instagram webpage
                        Intent it = new Intent(this,WebPageClassInstagram.class);
                        startActivityForResult(it,INSAGRAMLINKED);
                    }
                }else
                {
                    //logout from instagram
                    functions.setPref(StaticVariables.HASINSTALINKED, false);
                    functions.setPref(StaticVariables.INSTAUPDATED, false);
                    functions.setPref(StaticVariables.INSTAGRAM_ID, null);
                    functions.setPref(StaticVariables.INSTAACCESSTOKEN,null);
                    cbFacebook.setChecked(false);
                    try{
                         Database db = new Database(this);
                        db.open();
                        db.deleteSampleJson(functions.getPref(StaticVariables.ID, ""), StaticVariables.INSTAGRAMPICS);
                        db.close();
                    }catch (Exception ex){

                    }



                }

                break;
        }
    }


/*

    private void startFaceBook(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(Settings.this, Arrays.asList("public_profile", "email", "user_photos"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        facebook_accessToken = loginResult.getAccessToken().getToken().toString();
                        facebookUserIduserId = loginResult.getAccessToken().getUserId();
                        functions.setPref(StaticVariables.ACCESSTOKEN, facebook_accessToken);
                        updateFacebook();


                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(Settings.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        cbFacebook.setChecked(false);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(Settings.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                        cbFacebook.setChecked(false);
                    }
                });
    }


    */

    public boolean isFacebookLoggedIn(){
        return AccessToken.getCurrentAccessToken() != null;
    }




    private void updateFacebook(){
        pDialog = new ProgressDialog(this);
        pDialog = functions.prepareDialog("Updating profile...",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/self")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setMultipartParameter(StaticVariables.DISPLAYNAME, functions.getPref(StaticVariables.DISPLAYNAME, ""))
                .setMultipartParameter(StaticVariables.FULLNAME, functions.getPref(StaticVariables.FULLNAME, ""))
                .setMultipartParameter(StaticVariables.FACEBOOKEID, facebookUserIduserId)
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

                                JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                if (meta != null) {
                                    int code = functions.getInt(meta, StaticVariables.CODE);
                                    if (code == 200) {
                                        JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                        if (data != null) {
                                            JSONObject user = functions.getJsonObject(data, StaticVariables.USER);
                                            if (user != null) {
                                                String instaID = functions.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                                String displayName = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                                                String locale = functions.getJsonString(user, StaticVariables.LOCALE);
                                                String country = functions.getJsonString(user, StaticVariables.COUNTRY);
                                                String email = functions.getJsonString(user, StaticVariables.EMAIL);
                                                String fullname = functions.getJsonString(user, StaticVariables.FULLNAME);
                                                String id = functions.getJsonString(user, StaticVariables.ID);
                                                functions.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                functions.setPref(StaticVariables.DISPLAYNAME, displayName);
                                                functions.setPref(StaticVariables.LOCALE, locale);
                                                functions.setPref(StaticVariables.COUNTRY, country);
                                                functions.setPref(StaticVariables.EMAIL, email);
                                                functions.setPref(StaticVariables.FULLNAME, fullname);
                                                functions.setPref(StaticVariables.FACEBOOKEID, facebookUserIduserId);
                                                functions.setPref(StaticVariables.ACCESSTOKEN, facebook_accessToken);
                                                functions.setPref(StaticVariables.HASFACEBOOKLOGIN, true);
                                                functions.setPref(StaticVariables.ID, id);
                                                cbFacebook.setChecked(true);

                                                JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

                                                if (picture != null) {

                                                    JSONObject thumbNail = functions.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                    if (thumbNail != null) {
                                                        String thumbURL = functions.getJsonString(thumbNail, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                    }
                                                    JSONObject mrJson = functions.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                    if (mrJson != null) {
                                                        String mr = functions.getJsonString(mrJson, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.MOBILE_RESOLUTION, mr);

                                                    }


                                                }


                                            }

                                        } else {
                                            functions.showMessage("Unable to update your profile with facebook details");
                                            reverseFaceBook();
                                        }



                                    } else if (code == 403) {
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        reverseFaceBook();
                                    } else if(code ==  400){
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                        reverseFaceBook();
                                    }
                                } else {
                                    functions.showMessage("Unable update profile");
                                    reverseFaceBook();
                                }

                            } else {
                                functions.showMessage("Unable to update profile");
                                reverseFaceBook();
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }



    private void reverseFaceBook()
    {

        AlertDialog dd = new AlertDialog.Builder(this).create();
        dd.setMessage("Unable to update profile with your facebook deatils");
        dd.setCancelable(false);
        dd.setButton(Dialog.BUTTON_NEGATIVE, "RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFacebook();
            }
        });


        dd.setButton(Dialog.BUTTON_POSITIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cbFacebook.setChecked(false);

            }
        });


        dd.show();
    }










    private void updateInsta(){
        pDialog = new ProgressDialog(this);
        pDialog = functions.prepareDialog("Updating profile...",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/self")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setMultipartParameter(StaticVariables.DISPLAYNAME, functions.getPref(StaticVariables.DISPLAYNAME, ""))
                .setMultipartParameter(StaticVariables.FULLNAME, functions.getPref(StaticVariables.FULLNAME, ""))
                .setMultipartParameter(StaticVariables.INSTAGRAM_ID, functions.getPref(StaticVariables.INSTAGRAM_ID,""))
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

                                JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                if (meta != null) {
                                    int code = functions.getInt(meta, StaticVariables.CODE);
                                    if (code == 200) {
                                        JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                        if (data != null) {
                                            JSONObject user = functions.getJsonObject(data, StaticVariables.USER);
                                            if (user != null) {
                                                String instaID = functions.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                                String displayName = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                                                String locale = functions.getJsonString(user, StaticVariables.LOCALE);
                                                String country = functions.getJsonString(user, StaticVariables.COUNTRY);
                                                String email = functions.getJsonString(user, StaticVariables.EMAIL);
                                                String fullname = functions.getJsonString(user, StaticVariables.FULLNAME);
                                                String id = functions.getJsonString(user, StaticVariables.ID);
                                                functions.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                functions.setPref(StaticVariables.DISPLAYNAME, displayName);
                                                functions.setPref(StaticVariables.LOCALE, locale);
                                                functions.setPref(StaticVariables.COUNTRY, country);
                                                functions.setPref(StaticVariables.EMAIL, email);
                                                functions.setPref(StaticVariables.FULLNAME, fullname);
                                                functions.setPref(StaticVariables.HASFACEBOOKLOGIN, true);
                                                functions.setPref(StaticVariables.INSTAUPDATED, true);
                                                functions.setPref(StaticVariables.ID, id);

                                                JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

                                                if (picture != null) {

                                                    JSONObject thumbNail = functions.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                    if (thumbNail != null) {
                                                        String thumbURL = functions.getJsonString(thumbNail, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                    }
                                                    JSONObject mrJson = functions.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                    if (mrJson != null) {
                                                        String mr = functions.getJsonString(mrJson, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.MOBILE_RESOLUTION, mr);


                                                    }


                                                }


                                            }

                                        } else {
                                            //function.showMessage("Unable to update your profile with facebook details");
                                            reverseInsta("Unable to update your profile with instagram details");
                                        }


                                    } else if (code == 403) {
                                        // function.showMessage(function.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        reverseInsta(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                    } else if (code == 400) {
                                        // function.showMessage(function.getJsonString(meta, StaticVariables.DEBUG));
                                        reverseInsta(functions.getJsonString(meta, StaticVariables.DEBUG));
                                    }
                                } else {
                                    //function.showMessage("Unable update profile");
                                    reverseInsta("Unable update profile");
                                }

                            } else {
                                //function.showMessage("Unable to update profile");
                                reverseInsta("Unable to update profile");
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }




    private void reverseInsta(String message)
    {
        AlertDialog dd = new AlertDialog.Builder(Settings.this).create();
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
               cbInstagram.setChecked(false);
            }
        });

        dd.show();
    }
}
