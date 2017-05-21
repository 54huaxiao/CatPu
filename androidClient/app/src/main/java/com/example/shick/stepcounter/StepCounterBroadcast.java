package com.example.shick.stepcounter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * Created by huaxiao on 2017/5/21.
 * 静态广播接收
 */

public class StepCounterBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.shick.stepcounter.MyBroadcast")) {
            Bundle bundle = intent.getExtras();
            String step = bundle.getString("step");
            String calorie = bundle.getString("calorie");
            String time = bundle.getString("time");
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.new_con);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            Notification.Builder builder = new Notification.Builder(context);
            String text = "跑步步数: " + step + "   用时: " + time + "   卡路里: " + calorie;
            builder.setContentTitle("猫扑跑步").setContentText(text).setTicker("您有一条新消息").setLargeIcon(icon).setSmallIcon(R.mipmap.new_con).setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }
}

