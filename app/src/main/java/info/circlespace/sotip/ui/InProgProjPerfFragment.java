/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.data.SotipContract.ChartDataEntry;
import info.circlespace.sotip.sync.PerformanceDataSet;

/**
 * Displays a breakdown of the percentage of projects in each performance category for in-progress projects.
 */
public class InProgProjPerfFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String LOG_TAG = InProgProjPerfFragment.class.getSimpleName();
    public static final int DEFAULT_CHART_VALUE = 100;

    private static final int LOADER_ID = 4;

    private TextView mTotalProjs;
    private PieChart mChart;
    private ViewGroup mDataBox;
    private TextView mPercOfProjs;
    private TextView mEstDesc;

    private PerformanceDataSet mDataSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frgm_in_prog_proj_perf, container, false);

        mTotalProjs = (TextView) rootView.findViewById(R.id.totalProjs);
        mChart = (PieChart) rootView.findViewById(R.id.pieChart);

        mDataBox = (ViewGroup) rootView.findViewById(R.id.dataBox);
        mDataBox.setOnClickListener(this);
        mPercOfProjs = (TextView) rootView.findViewById(R.id.percOfProjs);
        mEstDesc = (TextView) rootView.findViewById(R.id.estDesc);
        setupChart();

        SotipApp.CHART_TYPE = SotipApp.CHART_TYPE_IN_PROG_PROJS;
        SotipApp.isInitd(getActivity());

        return rootView;
    }


    /**
     * Sets up a click handler for the chart and display initial dummy data for it.
     */
    private void setupChart() {
        mChart.setOnCurrentItemChangedListener(new PieChart.OnCurrentItemChangedListener() {
            @Override
            public void onCurrentItemChanged(PieChart chart, int itemNdx) {
                // prevent the chart from transferring data to the data box
                // if the app has not successfully loaded data from the server at least once
                if (!SotipApp.isInitd(getActivity())) {
                    return;
                }

                mPercOfProjs.setText(SotipApp.fmtPerc(mDataSet.getPerc(itemNdx)));
                mEstDesc.setText(SotipApp.getPerfDesc(itemNdx));
                mDataBox.setBackground(getResources().getDrawable(SotipApp.getPerfDrawable(itemNdx)));
            }
        });

        final Resources res = getResources();
        mTotalProjs.setText(String.format(res.getString(R.string.a11y_total_projects), 0));
        mPercOfProjs.setText(SotipApp.fmtPerc(0.0f));

        mChart.addItem(DEFAULT_CHART_VALUE, getResources().getColor(R.color.black));
    }


    @Override
    public void onClick(View vw) {
        // prevent the app from showing a list of projects if the app has not
        // successfully loaded data from the server at least once
        if (!SotipApp.isInitd(getActivity())) {
            return;
        }

        int itemNdx = mChart.getCurrentItem();

        SotipApp.DATA_LVL = itemNdx;
        SotipApp.AGENCIES = mDataSet.getAgencies(SotipApp.DATA_LVL);
        SotipApp.AGC_NDX = 0;

        Intent intent = new Intent(getActivity(), ProjListActivity.class);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        Tracker tracker = ((SotipApp) getActivity().getApplication()).getTracker();
        tracker.setScreenName(SotipApp.getScreenName(SotipApp.CHART_TYPE_IN_PROG_PROJS));
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri chartUri = ChartDataEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                chartUri,
                ChartDataEntry.COMMON_DATA_COLUMNS,
                ChartDataEntry.ID_FILTER,
                new String[]{Integer.toString(SotipApp.CHART_TYPE_IN_PROG_PROJS)},
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }

        if (data.getCount() == 0) {
            return;
        }

        data.moveToFirst();

        mDataSet = new PerformanceDataSet();
        String chartData = data.getString(ChartDataEntry.COL_NDX_CHART_DATA);
        mDataSet.addData(chartData);

        String agcData = data.getString(ChartDataEntry.COL_NDX_AGENCIES);
        mDataSet.addAgencies(agcData);

        showChartData();
    }


    /**
     * Sets the content for the chart.
     */
    private void showChartData() {
        int total = mDataSet.getTotal();

        Resources res = getResources();
        mTotalProjs.setText(String.format(res.getString(R.string.a11y_total_projects), total));

        mChart.clearItems();

        for (int i = 0; i < mDataSet.getTally().length; i++) {
            mChart.addItem(mDataSet.getTally()[i], SotipApp.getPerfColour(i));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
