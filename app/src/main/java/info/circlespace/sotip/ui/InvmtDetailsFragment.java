/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

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

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.data.SotipContract.InvestmentEntry;

/**
 * Displays the details for an IT investment.
 */
public class InvmtDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = InvmtDetailsFragment.class.getSimpleName();
    private static final int LOADER_ID = 9;

    public static final String[] DATA_COLUMNS = {
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry._ID,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_UNIQ_INVMT_ID,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_TITLE,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_CIO_RATING,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_CONTRACTORS,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_CONTRACT_TYPES,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_URLS,
            InvestmentEntry.TABLE_NAME + "." + InvestmentEntry.COL_SUMMARY,
    };

    public static final int COL_ID = 0;
    public static final int COL_UNIQ_INVMT_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_CIO_RATING = 3;
    public static final int COL_CONTRACTORS = 4;
    public static final int COL_CONTRACT_TYPES = 5;
    public static final int COL_URLS = 6;
    public static final int COL_SUMMARY = 7;

    private TextView mInvmtTitle;
    private TextView mInvmtId;
    private TextView mCioRating;
    private TextView mInvmtCost;
    private TextView mNumProjs;
    private TextView mContractors;
    private TextView mContractTypes;
    private TextView mSummary;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_invmt_details, container, false);

        mInvmtTitle = (TextView) rootView.findViewById(R.id.invmtTitle);
        mInvmtId = (TextView) rootView.findViewById(R.id.invmtId);
        mCioRating = (TextView) rootView.findViewById(R.id.cioRating);
        mInvmtCost = (TextView) rootView.findViewById(R.id.invmtCost);
        mNumProjs = (TextView) rootView.findViewById(R.id.numProjs);
        mContractors = (TextView) rootView.findViewById(R.id.contractors);
        mContractTypes = (TextView) rootView.findViewById(R.id.contractTypes);
        mSummary = (TextView) rootView.findViewById(R.id.summary);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = InvestmentEntry.buildUniqInvmtIdUri(SotipApp.INVESTMENT_ID);

        return new CursorLoader(getActivity(),
                uri,
                DATA_COLUMNS,
                null,
                null,
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

        mInvmtId.setText(data.getString(COL_UNIQ_INVMT_ID));
        mInvmtTitle.setText(data.getString(COL_TITLE));
        mCioRating.setText(SotipApp.getCioRating(data.getInt(COL_CIO_RATING)));
        mContractors.setText(SotipApp.fmtConcatStr(data.getString(COL_CONTRACTORS)));
        mContractTypes.setText(SotipApp.fmtConcatStr(data.getString(COL_CONTRACT_TYPES)));
        mSummary.setText(data.getString(COL_SUMMARY));

        // calculate the number of projects and the total cost for the investment
        double totalCost = 0d;

        if (SotipApp.AGENCY_PROJS != null) {
            for (ProjectInfo item : SotipApp.AGENCY_PROJS) {
                totalCost += item.getLcc();
            }

            mNumProjs.setText(Integer.toString(SotipApp.AGENCY_PROJS.length));
        }

        mInvmtCost.setText(SotipApp.fmtCost(totalCost));
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void showDetails() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

}
