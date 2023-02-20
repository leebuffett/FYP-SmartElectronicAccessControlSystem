package com.example.smartelectronicaccesscontrolsystem.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.smartelectronicaccesscontrolsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Random;


public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getImageUrl());
        else
            showNotification(remoteMessage.getData());

    }

    private void showNotification(Map<String, String> data) {

        String title = data.get("title").toString();
        String body = data.get("body").toString();
        String uri = data.get("image").toString();
        //convert a uri to a stream
        InputStream inputStream;
        Bitmap pic = null;
        String stringUrl = String.valueOf(uri);
        try{
            inputStream = new java.net.URL(stringUrl).openStream();

            //open the image as bitmap
            pic = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String Notification_channel_id = "edmt.dev.androidfcmnew.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Notification_channel_id,"Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,Notification_channel_id);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(pic).setSummaryText(body));;


        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }

    private void showNotification(String title, String body, Uri uri) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String Notification_channel_id = "edmt.dev.androidfcmnew.test";

        //convert a uri to a stream
        InputStream inputStream;
        Bitmap pic = null;
        String stringUrl = String.valueOf(uri);
        try{
            inputStream = new java.net.URL(stringUrl).openStream();

            //open the image as bitmap
            pic = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Notification_channel_id,"Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Alert Message");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,Notification_channel_id);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(pic).setSummaryText(body));

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());



    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        if(firebaseUser!=null){
//            updateToken(s);
//        }
        Log.d("NEW_TOKEN","refresh token: "+s);
    }
//
//    private void updateToken(String refreshToken){
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token = new Token(refreshToken);
//        reference.child(firebaseUser.getUid()).setValue(token);
//    }

}
