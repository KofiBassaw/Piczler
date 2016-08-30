package com.piczler.piczler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

/**
 * Created by matiyas on 8/21/16.
 */
public class FacebookMain extends AppCompatActivity {

    CallbackManager callbackManager;
    String facebook_accessToken="";
    String faceBookEmail="", facebookUserIduserId ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        startFaceBook();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }





    private void startFaceBook(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(FacebookMain.this, Arrays.asList("public_profile", "email", "user_photos"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        facebook_accessToken = loginResult.getAccessToken().getToken().toString();
                        facebookUserIduserId = loginResult.getAccessToken().getUserId();

                        Bundle bb = new Bundle();
                        bb.putString("facebook_accessToken",facebook_accessToken);
                        bb.putString("facebookUserIduserId", facebookUserIduserId);
                        Intent it = new Intent();
                        it.putExtras(bb);
                        setResult(RESULT_OK,it);
                        onBackPressed();


                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(FacebookMain.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(FacebookMain.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                        onBackPressed();
                    }
                });
    }

}
