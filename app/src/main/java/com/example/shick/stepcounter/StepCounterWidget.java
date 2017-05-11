package com.example.shick.stepcounter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class StepCounterWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence step = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.step_counter_widget);
        views.setTextViewText(R.id.time, step);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent clickInt = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickInt, 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.step_counter_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.step_counter_widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int [] widgetIDs = appWidgetManager
                .getAppWidgetIds(new ComponentName(context,
                        StepCounterWidget.class));
        Bundle bundle = intent.getExtras();

        if (intent.getAction().equals(MessageActivity.STATICACTION)) {
            remoteViews.setTextViewText(R.id.step, bundle.getString("step") + "步");
            remoteViews.setTextViewText(R.id.time, bundle.getString("time"));
            remoteViews.setTextViewText(R.id.calorie, bundle.getString("calorie") + "卡");
            appWidgetManager.updateAppWidget(widgetIDs, remoteViews);
        }
    }
}

