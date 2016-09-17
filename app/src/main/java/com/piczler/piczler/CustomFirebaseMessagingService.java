package com.piczler.piczler;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by ptrack on 9/11/16.
 */
public class CustomFirebaseMessagingService extends  FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       // System.out.println("lllllllllllllllll: From: "+ remoteMessage.getFrom()+" and: Body: "+remoteMessage.getNotification().getBody());


        System.out.println("from received now");

        if (remoteMessage.getData().size() > 0) {
            System.out.println("From:  and: Data: "+remoteMessage.getData());
            Map<String, String> map = remoteMessage.getData();

            String media_id = map.get("media_id");
            getNotify(media_id);
            Intent it  = new Intent("com.piczler.piczler.broadcast");
            it.putExtra("type", "comment");
            it.putExtra(StaticVariables.ID,media_id);
            sendBroadcast(it);
            genrateNotification(map);
        }



    }



    private void genrateNotification(Map<String, String> map)
    {

        //API level 11
        Intent intent = new Intent(this, Pictures.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

         int type = Integer.parseInt(map.get("type"));
        String message = "";
        if(type == 0)
        {
            intent.putExtra(StaticVariables.FROM,StaticVariables.LIKETYPE);
            //comment type
            message = "new like";
        }else if(type == 1)
        {
            //like type
            intent.putExtra(StaticVariables.FROM,StaticVariables.COMMENTTYPE);
            message = "new comment";

        }
        intent.putExtra(StaticVariables.ID, map.get("media_id"));



        Drawable myDrawable =  getResources().getDrawable(R.drawable.notify);
        Bitmap inBit = ((BitmapDrawable)myDrawable).getBitmap();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);

        builder.setAutoCancel(true);
        builder.setTicker("Piczler");
        builder.setContentTitle("Notification");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.notify);
        builder.setLargeIcon(inBit);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("Click to read all news");   //API level 16
        builder.setNumber(100);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).build();

        builder.build();







        Notification	myNotication = builder.build();

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(1, myNotication);


    }






    private void getNotify(final String userID)
    {
        final UserFunctions functions = new UserFunctions(this);

        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later


            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
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
                                                Database db = new Database(CustomFirebaseMessagingService.this);
                                                db.open();
                                                db.insertSampleDetails(userID, StaticVariables.NOTIFICSTIONS, data.toString());
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

}
