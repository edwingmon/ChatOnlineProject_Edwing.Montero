package com.example.damxat.FirebaseUtilities;
import com.example.damxat.Constants.*;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.d(Constants.TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
}
