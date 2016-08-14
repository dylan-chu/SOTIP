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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.data.SotipContract.ChartDataEntry;
import info.circlespace.sotip.sync.GroupedDataSet;
import info.circlespace.sotip.sync.PerformanceDataSet;


public class SdlcMethodFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SdlcMethodFragment.class.getSimpleName();

    private static final int LOADER_ID = 6;
    public static final int NUM_COLS = 3;

    private TextView mTotalProjs;
    private RecyclerView mLstVw;
    private SubsetAdapter mAdapter;
    private TextView mSdmDesc;

    private BoxChart mChart;
    private GroupedDataSet mDataSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frgm_sdlc_method, container, false);

        SotipApp.CHART_TYPE = SotipApp.CHART_TYPE_SDM;

        mTotalProjs = (TextView) rootView.findViewById(R.id.totalProjs);
        mLstVw = (RecyclerView) rootView.findViewById(R.id.lstVw);
        setupList();

        mSdmDesc = (TextView) rootView.findViewById(R.id.sdmDesc);

        mChart = (BoxChart) rootView.findViewById(R.id.boxChart);
        setupChart();

        SotipApp.isInitd(getActivity());

        return rootView;
    }


    private void setupList() {
        Resources res = getResources();
        mTotalProjs.setText(String.format(res.getString(R.string.a11y_total_projects), 0));

        SubsetAdapter.ItemClickHandler clickHdlr = new SubsetAdapter.ItemClickHandler() {
            @Override
            public void onClick(SubsetData data, SubsetAdapter.VwHldr vh) {
                if (!SotipApp.isInitd(getActivity())) {
                    return;
                }

                showSubsetInfo(data.getNdx());
            }
        };

        mAdapter = new SubsetAdapter(getActivity(), clickHdlr);
        mAdapter.setSelectedNdx(SotipApp.SDM_GROUP_NDX);
        mLstVw.setAdapter(mAdapter);

        GridLayoutManager layoutMgr = new GridLayoutManager(getActivity(), NUM_COLS);
        mLstVw.setLayoutManager(layoutMgr);

        List<SubsetData> lstItems = new ArrayList<SubsetData>();
        for (int i = 0; i < SotipApp.NUM_SDM_CATEGS; i++) {
            SubsetData data = new SubsetData(i, 0, Integer.toString(i + 1));
            lstItems.add(data);
        }

        mAdapter.setDataSet(lstItems);
    }


    private void setupChart() {
        mChart.setOnItemSelectedListener(new BoxChart.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int itemNdx) {
                if (!SotipApp.isInitd(getActivity())) {
                    return;
                }

                PerformanceDataSet dataSet = mDataSet.getDataSets()[SotipApp.SDM_GROUP_NDX];
                SotipApp.DATA_LVL = itemNdx;
                SotipApp.AGENCIES = dataSet.getAgencies(itemNdx);
                SotipApp.AGC_NDX = 0;

                Intent intent = new Intent(getActivity(), ProjListActivity.class);
                startActivity(intent);
            }
        });

        for (int i = 0; i < SotipApp.NUM_PERF_CATEGS; i++) {
            mChart.addItem(i, 0, SotipApp.fmtPerc(0.0f), SotipApp.getPerfColour(i));
        }

        mChart.onDataChanged();
    }


    @Override
    public void onStart() {
        super.onStart();
        Tracker tracker = ((SotipApp) getActivity().getApplication()).getTracker();
        tracker.setScreenName(SotipApp.getScreenName(SotipApp.CHART_TYPE_SDM));
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
                new String[]{Integer.toString(SotipApp.CHART_TYPE_SDM)},
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

        mDataSet = new GroupedDataSet(SotipApp.NUM_SDM_CATEGS);
        String chartData = data.getString(ChartDataEntry.COL_NDX_CHART_DATA);
        mDataSet.addData(chartData);

        String agcData = data.getString(ChartDataEntry.COL_NDX_AGENCIES);
        mDataSet.addAgencies(agcData);

        showListData();
        showSubsetInfo(SotipApp.SDM_GROUP_NDX);
    }


    private void showListData() {
        int total = mDataSet.getTotal();
        Resources res = getResources();
        mTotalProjs.setText(String.format(res.getString(R.string.a11y_total_projects), total));

        List<SubsetData> lstItems = new ArrayList<SubsetData>();
        for (int i = 0; i < SotipApp.NUM_SDM_CATEGS; i++) {
            SubsetData data = new SubsetData(i, mDataSet.getTotal(i), Integer.toString(i + 1));
            lstItems.add(data);
        }

        mAdapter.setDataSet(lstItems);
    }


    private void showSubsetInfo(int ndx) {
        SotipApp.SDM_GROUP_NDX = ndx;

        int sdmCode = ndx + 1;
        mSdmDesc.setText(sdmCode + " - " + SotipApp.getSdmDesc(sdmCode));

        PerformanceDataSet dataSet = mDataSet.getDataSets()[ndx];
        int[] data = dataSet.getTally();

        mChart.clearItems();

        for (int i = 0; i < SotipApp.NUM_PERF_CATEGS; i++) {
            String lbl = SotipApp.fmtPerc(dataSet.getPerc(i));
            mChart.addItem(i, data[i], lbl, SotipApp.getPerfColour(i));
        }

        mChart.onDataChanged();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
