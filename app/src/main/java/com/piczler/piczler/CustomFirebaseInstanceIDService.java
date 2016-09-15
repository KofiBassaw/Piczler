package com.piczler.piczler;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ptrack on 9/11/16.
 */

public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        UserFunctions functions = new UserFunctions(this);
        functions.setPref(StaticVariables.HASGCM,false);
        functions.setPref(StaticVariables.GCM,refreshedToken);
    }
}
