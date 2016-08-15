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
import info.circlespace.sotip.sync.VarianceDataSet;

/**
 * Displays a diagram with the breakdown of the percentage of projects in each
 * variance category for schedule variance for all the projects.
 */
public class SchVarFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String LOG_TAG = SchVarFragment.class.getSimpleName();

    private static final int LOADER_ID = 2;

    private static final int NUM_CATEGS = 6;
    private TextView mTotalProjs;
    private ViewGroup[] mVarBoxes;
    private TextView[] mProjPercs;
    private VarianceDataSet mDataSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frgm_sch_var, container, false);

        mTotalProjs = (TextView) rootView.findViewById(R.id.totalProjs);

        // set up the view containers to be clickable
        mVarBoxes = new ViewGroup[NUM_CATEGS];
        mVarBoxes[0] = (ViewGroup) rootView.findViewById(R.id.varBox0);
        mVarBoxes[1] = (ViewGroup) rootView.findViewById(R.id.varBox1);
        mVarBoxes[2] = (ViewGroup) rootView.findViewById(R.id.varBox2);
        mVarBoxes[3] = (ViewGroup) rootView.findViewById(R.id.varBox3);
        mVarBoxes[4] = (ViewGroup) rootView.findViewById(R.id.varBox4);
        mVarBoxes[5] = (ViewGroup) rootView.findViewById(R.id.varBox5);

        for (int i = 0; i < NUM_CATEGS; i++) {
            mVarBoxes[i].setOnClickListener(this);
        }

        mProjPercs = new TextView[NUM_CATEGS];
        mProjPercs[0] = (TextView) rootView.findViewById(R.id.percNdx0);
        mProjPercs[1] = (TextView) rootView.findViewById(R.id.percNdx1);
        mProjPercs[2] = (TextView) rootView.findViewById(R.id.percNdx2);
        mProjPercs[3] = (TextView) rootView.findViewById(R.id.percNdx3);
        mProjPercs[4] = (TextView) rootView.findViewById(R.id.percNdx4);
        mProjPercs[5] = (TextView) rootView.findViewById(R.id.percNdx5);

        // start with displaying dummy data in the chart
        SotipApp.CHART_TYPE = SotipApp.CHART_TYPE_SCH_VAR;
        mDataSet = new VarianceDataSet();
        showChartData();

        // check whether the app is initialized
        SotipApp.isInitd(getActivity());

        return rootView;
    }


    /**
     * Displays the percentage of projects in a performance category along with a textual description
     * of that category.
     */
    private void showChartData() {
        Resources res = getResources();
        mTotalProjs.setText(String.format(res.getString(R.string.a11y_total_projects), mDataSet.getTotal()));

        for (int i = 0; i < NUM_CATEGS; i++) {
            float perc = mDataSet.getPerc(i);
            mProjPercs[i].setText(SotipApp.fmtPerc(perc));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Tracker tracker = ((SotipApp) getActivity().getApplication()).getTracker();
        tracker.setScreenName(SotipApp.getScreenName(SotipApp.CHART_TYPE_SCH_VAR));
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onClick(View vw) {

        // prevent the app from showing a list of projects if the app has not
        // successfully loaded data from the server at least once
        if (!SotipApp.isInitd(getActivity())) {
            return;
        }

        int vwId = vw.getId();

        switch (vwId) {
            case R.id.varBox0:
                SotipApp.DATA_LVL = SotipApp.UNDER_30;
                break;
            case R.id.varBox1:
                SotipApp.DATA_LVL = SotipApp.UNDER_10;
                break;
            case R.id.varBox2:
                SotipApp.DATA_LVL = SotipApp.UNDER_0;
                break;
            case R.id.varBox3:
                SotipApp.DATA_LVL = SotipApp.OVER_0;
                break;
            case R.id.varBox4:
                SotipApp.DATA_LVL = SotipApp.OVER_10;
                break;
            case R.id.varBox5:
                SotipApp.DATA_LVL = SotipApp.OVER_30;
                break;
            default:
                SotipApp.DATA_LVL = SotipApp.UNDER_30;
                break;
        }

        // store the category that was clicked along with the agencies associated with that category
        SotipApp.AGENCIES = mDataSet.getAgencies(SotipApp.DATA_LVL);
        SotipApp.AGC_NDX = 0;

        Intent intent = new Intent(getActivity(), ProjListActivity.class);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri chartUri = ChartDataEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                chartUri,
                ChartDataEntry.COMMON_DATA_COLUMNS,
                ChartDataEntry.ID_FILTER,
                new String[]{Integer.toString(SotipApp.CHART_TYPE_SCH_VAR)},
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

        mDataSet = new VarianceDataSet();
        String chartData = data.getString(ChartDataEntry.COL_NDX_CHART_DATA);
        mDataSet.addData(chartData);

        String agencies = data.getString(ChartDataEntry.COL_NDX_AGENCIES);
        mDataSet.addAgencies(agencies);

        showChartData();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
