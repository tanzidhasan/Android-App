package com.example.nirob.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.nirob.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        FirebaseUser fm_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        if(fm_firebaseUser != null && sented.equals(fm_firebaseUser.getUid())){

            if(!currentUser.equals(user)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOreoNotification(remoteMessage);
                }else {
                    sendNotification(remoteMessage);
                }
            }

        }

    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userid", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);

        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, sound, icon);


        int i = 0;
        if(j > 0){
            i = j;
        }

        oreoNotification.getManager().notify(i, builder.build());

    }



    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userid", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), Integer.parseInt(icon)))
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        notificationManager.notify(i, builder.build());


    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        FirebaseUser nt_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(nt_firebaseUser != null){
            updateToken(token);
        }

    }

    private void updateToken(String uptoken) {


        FirebaseUser ut_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase ut_firebaseDatabase = FirebaseDatabase.getInstance();

        Token token = new Token(uptoken);

        ut_firebaseDatabase.getReference("Tokens").child(ut_firebaseUser.getUid()).setValue(token);


    }
}
