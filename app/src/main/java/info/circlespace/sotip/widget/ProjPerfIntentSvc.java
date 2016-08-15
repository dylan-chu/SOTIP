/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.data.SotipContract;
import info.circlespace.sotip.sync.PerformanceDataSet;
import info.circlespace.sotip.ui.MainActivity;

/**
 * This class implements a service to update the widget.
 *
 * The code in this class is based on code in Udacity's Advanced Android Development course's Github repo.
 */
public class ProjPerfIntentSvc extends IntentService {


    private static final String[] CHART_COLUMNS = {
            SotipContract.ChartDataEntry.COL_DATA
    };

    // these indices must match the projection
    private static final int COL_NDX_DATA = 0;


    public ProjPerfIntentSvc() {
        super("ProjectPerfIntentSvc");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ProjPerfWidget.class));

        // get the the number of projects in each performance category for complete projects
        Cursor data = getContentResolver().query(SotipContract.ChartDataEntry.CONTENT_URI,
                CHART_COLUMNS,
                SotipContract.ChartDataEntry.ID_FILTER,
                new String[]{Integer.toString(SotipApp.CHART_TYPE_CMPLTD_PROJS)},
                null);

        if (data == null) {
            return;
        }

        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String chartData = data.getString( COL_NDX_DATA );
        PerformanceDataSet dataSet = new PerformanceDataSet();
        dataSet.addData( chartData );

        int total = dataSet.getTotal();
        data.close();

        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_project_perf;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // display the percentage of projects in each performance category in each performance bar in the UI
            views.setTextViewText(R.id.wgtPerfNdx0, SotipApp.fmtPerc(dataSet.getPerc(0) ));
            views.setTextViewText(R.id.wgtPerfNdx1, SotipApp.fmtPerc(dataSet.getPerc(1) ));
            views.setTextViewText(R.id.wgtPerfNdx2, SotipApp.fmtPerc(dataSet.getPerc(2) ));
            views.setTextViewText(R.id.wgtPerfNdx3, SotipApp.fmtPerc(dataSet.getPerc(3) ));
            views.setTextViewText(R.id.wgtPerfNdx4, SotipApp.fmtPerc(dataSet.getPerc(4) ));

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            launchIntent.putExtra( SotipApp.CHART_TYPE_KEY, SotipApp.CHART_TYPE_CMPLTD_PROJS );
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}

