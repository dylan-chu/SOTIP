/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.data.SotipContract;
import info.circlespace.sotip.sync.PerformanceDataSet;
import info.circlespace.sotip.ui.MainActivity;


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
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ProjPerfWidget.class));

        Cursor data = getContentResolver().query(SotipContract.ChartDataEntry.CONTENT_URI,
                CHART_COLUMNS,
                SotipContract.ChartDataEntry.ID_FILTER,
                new String[]{Integer.toString(SotipApp.CHART_TYPE_CMPLTD_PROJS)},
                null);

        if (data == null) {
            Log.d( "widget", "data is null" );
            return;
        }

        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        String chartData = data.getString( COL_NDX_DATA );
        Log.d( "widget", "chartData: " + chartData );
        PerformanceDataSet dataSet = new PerformanceDataSet();
        dataSet.addData( chartData );

        int total = dataSet.getTotal();
        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width
            //int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            //int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
            //int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
            int layoutId = R.layout.widget_project_perf;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
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

/*
    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
*/
}

