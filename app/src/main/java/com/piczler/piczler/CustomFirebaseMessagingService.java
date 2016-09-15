package com.piczler.piczler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * Created by ptrack on 9/11/16.
 */
public class CustomFirebaseMessagingService extends  FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("lllllllllllllllll: From: "+ remoteMessage.getFrom()+" and: Body: "+remoteMessage.getNotification().getBody());

    }
}
