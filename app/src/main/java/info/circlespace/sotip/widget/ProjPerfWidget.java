/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import info.circlespace.sotip.sync.DataSyncAdptr;

/**
 * This class helps to implement the widget.
 *
 * The code in this class is based on code in Udacity's Advanced Android Development course's Github repo.
 */
public class ProjPerfWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ProjPerfIntentSvc.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, ProjPerfIntentSvc.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (DataSyncAdptr.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, ProjPerfIntentSvc.class));
        }
    }
}
