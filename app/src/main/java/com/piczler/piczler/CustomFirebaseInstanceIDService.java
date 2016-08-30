package com.piczler.piczler;

import android.util.Log;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = CustomFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Value: " + refreshedToken);
        UserFunctions functions = new UserFunctions(this);
        functions.setPref(StaticVariables.HASGCM, false);
        functions.setPref(StaticVariables.GCM,refreshedToken);

    }




}