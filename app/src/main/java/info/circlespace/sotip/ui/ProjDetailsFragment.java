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
import android.widget.ImageView;
import android.widget.TextView;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.data.SotipContract.ProjectEntry;


public class ProjDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = ProjDetailsFragment.class.getSimpleName();
    private static final int LOADER_ID = 8;

    public static final String[] DATA_COLUMNS = {
            ProjectEntry.TABLE_NAME + "." + ProjectEntry._ID,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_PROJ_NAME,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_PROJ_STATUS,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_START_DATE,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_COMPLTN_DATE,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_SCH_VAR,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_PROJ_LC_COST,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_COST_VAR,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_PM_EXP_LVL,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_SDLC_METHOD,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_OTHER_SDM,
            ProjectEntry.TABLE_NAME + "." + ProjectEntry.COL_OBJECTIVES
    };

    public static final int COL_ID = 0;
    public static final int COL_PROJ_NAME = 1;
    public static final int COL_PROJ_STATUS = 2;
    public static final int COL_START_DATE = 3;
    public static final int COL_COMPLTN_DATE = 4;
    public static final int COL_SCH_VAR = 5;
    public static final int COL_PROJ_LC_COST = 6;
    public static final int COL_COST_VAR = 7;
    public static final int COL_PM_EXP_LVL = 8;
    public static final int COL_SDLC_METHOD = 9;
    public static final int COL_OTHER_SDM = 10;
    public static final int COL_OBJECTIVES = 11;

    private TextView mProjName;
    private TextView mProjId;
    private TextView mProjDates;
    private TextView mProjCost;
    private TextView mPmExpLvl;
    private TextView mSdlcMethod;
    private TextView mProjObjs;
    private ImageView mSchVar;
    private ImageView mCostVar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_proj_details, container, false);

        mProjName = (TextView) rootView.findViewById(R.id.projName);
        mProjId = (TextView) rootView.findViewById(R.id.projId);
        mProjDates = (TextView) rootView.findViewById(R.id.projDates);
        mProjCost = (TextView) rootView.findViewById(R.id.projCost);
        mPmExpLvl = (TextView) rootView.findViewById(R.id.pmExpLvl);
        mSdlcMethod = (TextView) rootView.findViewById(R.id.sdlcMethod);
        mProjObjs = (TextView) rootView.findViewById(R.id.projObjs);
        mSchVar = (ImageView) rootView.findViewById(R.id.schVar);
        ;
        mCostVar = (ImageView) rootView.findViewById(R.id.costVar);
        ;

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = ProjectEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                uri,
                DATA_COLUMNS,
                ProjectEntry.INVMT_FILTER,
                new String[]{SotipApp.INVESTMENT_ID},
                ProjectEntry.PROJ_ID_ASC_SORT);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }

        if (data.getCount() == 0) {
            return;
        }

        ProjectInfo[] items = new ProjectInfo[data.getCount()];
        data.moveToPosition(-1);
        int ndx = 0;

        while (data.moveToNext()) {
            ProjectInfo item = new ProjectInfo();
            items[ndx++] = item;

            item.setID(data.getInt(COL_ID));
            item.setName(data.getString(COL_PROJ_NAME));
            item.setObj(data.getString(COL_OBJECTIVES));
            item.setPs(data.getInt(COL_PROJ_STATUS));
            item.setSd(data.getString(COL_START_DATE));
            item.setCd(data.getString(COL_COMPLTN_DATE));
            item.setSv(data.getInt(COL_SCH_VAR));
            item.setLcc(data.getDouble(COL_PROJ_LC_COST));
            item.setCv(data.getInt(COL_COST_VAR));
            item.setPm(data.getInt(COL_PM_EXP_LVL));
            item.setSdm(data.getInt(COL_SDLC_METHOD));
            item.setOsdm(data.getString(COL_OTHER_SDM));

            if (item.getID() == SotipApp.PROJECT_ID) {
                showProjDetails(item);
            }
        }

        SotipApp.AGENCY_PROJS = items;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void showDetails() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    private void showProjDetails(ProjectInfo item) {
        mProjName.setText(item.getName());
        mProjId.setText(Integer.toString(item.getID()));
        mProjDates.setText(SotipApp.displayDate(item.getSd()) + " - " + SotipApp.displayDate(item.getCd()));
        mProjCost.setText(SotipApp.fmtCost(item.getLcc()));
        mPmExpLvl.setText(SotipApp.getPmLvlDesc(item.getPm()));
        mSdlcMethod.setText(SotipApp.getSdmDesc(item.getSdm(), item.getOsdm()));
        mProjObjs.setText(item.getObj());
        mSchVar.setImageResource(SotipApp.getVarColour(item.getSv()));
        mCostVar.setImageResource(SotipApp.getVarColour(item.getCv()));
    }

}
