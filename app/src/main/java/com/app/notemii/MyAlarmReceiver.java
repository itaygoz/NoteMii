package com.app.notemii;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyAlarmReceiver extends BroadcastReceiver {
    public static String TITLE = "TITLE";

    private Vibrator mVibrator;
    private Context mContext;
    private String  title;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        Log.e("onReceive", "alarm!!!");

        mVibrator =(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(1000);

        Bundle bundle = intent.getExtras();
        try{
            title = bundle.getString(TITLE);
        }catch(Exception e){
            e.printStackTrace();
        }

        notification(context, title);
    }


    public void notification(Context context, String message) {
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, BoardActivity.class);
        // Send data to NotificationView Class
//        intent.putExtra("title", title);
//        intent.putExtra("text", title);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.app_icon, "Previous", pIntent).build();
        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0")
                // Set Ticker Message
                .setTicker(message)
                // Set Title
                .setContentTitle(context.getString(R.string.app_name))
                // Set Text
                .setContentText(message)
                // Add an Action Button below Notification
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round);


        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
