/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.AgencyInfo;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.data.SotipContract.ProjectEntry;

/**
 * Displays a list of projects for one agency.
 */
public class ProjListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = ProjListFragment.class.getSimpleName();
    private static final int LOADER_ID = 7;

    private GestureDetectorCompat mDetector;
    private ViewGroup mAgcHeader;
    private ImageView mAgcImg;
    private TextView mAgcName;
    private RecyclerView mListVw;
    private TextView mEmptyVw;
    private ProjListAdptr mAdapter;
    private FragmentListener mListener;

    private String mAgencyCode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frgm_proj_list, container, false);

        mAgencyCode = SotipApp.AGENCIES.get(SotipApp.AGC_NDX);

        // this detects whether the user has swiped left or right
        mDetector = new GestureDetectorCompat(getActivity(), new SwipeGestureListener() {
            @Override
            public boolean onSwipe(Direction dir) {
                if (dir == Direction.left) {
                    onLeftSwipe();
                } else if (dir == Direction.right) {
                    onRightSwipe();
                }
                return false;
            }
        });

        mAgcHeader = (ViewGroup) rootView.findViewById(R.id.agcHeader);
        mAgcHeader.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });

        mAgcImg = (ImageView) rootView.findViewById(R.id.agcImg);
        mAgcName = (TextView) rootView.findViewById(R.id.agcName);

        mListVw = (RecyclerView) rootView.findViewById(R.id.listVw);
        mEmptyVw = (TextView) rootView.findViewById(R.id.emptyVw);

        mAdapter = new ProjListAdptr(getActivity(), new ProjListAdptr.OnItemSelectedListener() {
            @Override
            public void onClick(ProjectInfo item, ProjListAdptr.VwHldr vh) {
                mListener.onItemSelected(item, vh);
            }
        });

        mAgcImg.setImageResource(SotipApp.getAgencyIcon(mAgencyCode));
        AgencyInfo agency = SotipApp.getAgency(mAgencyCode);
        mAgcName.setText(agency.getNm());
        mListVw.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListVw.setAdapter(mAdapter);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface FragmentListener {
        public void onItemSelected(ProjectInfo item, ProjListAdptr.VwHldr vh);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String selFilter = null;
        String[] selArgs = null;

        Uri uri = ProjectEntry.CONTENT_URI;

        switch (SotipApp.CHART_TYPE) {
            case SotipApp.CHART_TYPE_COST_VAR:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " + ProjectEntry.COST_VAR_FILTER;
                selArgs = new String[]{mAgencyCode, Integer.toString(SotipApp.DATA_LVL)};
                break;

            case SotipApp.CHART_TYPE_SCH_VAR:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " + ProjectEntry.SCH_VAR_FILTER;
                selArgs = new String[]{mAgencyCode, Integer.toString(SotipApp.DATA_LVL)};
                break;

            case SotipApp.CHART_TYPE_CMPLTD_PROJS:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " +
                        ProjectEntry.PROJ_STATUS_FILTER + " AND "
                        + ProjectEntry.PROJ_PERF_FILTER;
                selArgs = new String[]{mAgencyCode,
                        Integer.toString(SotipApp.CODE_PROJ_STATUS_COMPLTD),
                        Integer.toString(SotipApp.DATA_LVL)};
                break;

            case SotipApp.CHART_TYPE_IN_PROG_PROJS:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " +
                        ProjectEntry.PROJ_STATUS_FILTER + " AND "
                        + ProjectEntry.PROJ_PERF_FILTER;
                selArgs = new String[]{mAgencyCode,
                        Integer.toString(SotipApp.CODE_PROJ_STATUS_IN_PROG),
                        Integer.toString(SotipApp.DATA_LVL)};
                break;

            case SotipApp.CHART_TYPE_PM_LVL:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " +
                        ProjectEntry.PM_LVL_FILTER + " AND "
                        + ProjectEntry.PROJ_PERF_FILTER;
                selArgs = new String[]{mAgencyCode,
                        Integer.toString(SotipApp.PM_GROUP_NDX + 1),
                        Integer.toString(SotipApp.DATA_LVL)};
                break;

            case SotipApp.CHART_TYPE_SDM:
                selFilter = ProjectEntry.AGENCY_FILTER + " AND " +
                        ProjectEntry.SDM_FILTER + " AND "
                        + ProjectEntry.PROJ_PERF_FILTER;
                selArgs = new String[]{mAgencyCode,
                        Integer.toString(SotipApp.SDM_GROUP_NDX + 1),
                        Integer.toString(SotipApp.DATA_LVL)};
                break;
        }

        return new CursorLoader(getActivity(),
                uri,
                ProjListAdptr.DATA_COLUMNS,
                selFilter,
                selArgs,
                ProjListAdptr.SORT_HIERARCHY_ASC);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (data == null) {
            return;
        }

        if (data.getCount() == 0) {
            showNoItems();
            return;
        }

        mEmptyVw.setVisibility(View.GONE);
        mListVw.setVisibility(View.VISIBLE);

        if (SotipApp.IS_DUAL_PANE) {
            mListVw.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (mListVw.getChildCount() > 0) {
                        // data has been loaded so we select the first item in the list
                        mListVw.getViewTreeObserver().removeOnPreDrawListener(this);
                        RecyclerView.ViewHolder vh = mListVw.findViewHolderForAdapterPosition(0);
                        if (vh != null) {
                            mAdapter.selectView(vh);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

    }


    private void showNoItems() {
        mEmptyVw.setVisibility(View.VISIBLE);
        mListVw.setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mListVw) {
            mListVw.clearOnScrollListeners();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /**
     * Shows a list of projects for the previous agency.
     */
    public void onLeftSwipe() {
        SotipApp.AGC_NDX--;
        if (SotipApp.AGC_NDX < 0) {
            SotipApp.AGC_NDX = SotipApp.AGENCIES.size() - 1;
        }
        showProjects();
    }


    /**
     * Shows a list of projects for the next agency.
     */
    public void onRightSwipe() {
        SotipApp.AGC_NDX++;
        if (SotipApp.AGC_NDX >= SotipApp.AGENCIES.size()) {
            SotipApp.AGC_NDX = 0;
        }
        showProjects();
    }


    /**
     * Displays the logo and loads the projects for the agency.
     */
    private void showProjects() {
        mAgencyCode = SotipApp.AGENCIES.get(SotipApp.AGC_NDX);
        mAgcImg.setImageResource(SotipApp.getAgencyIcon(mAgencyCode));

        AgencyInfo agency = SotipApp.getAgency(mAgencyCode);
        mAgcName.setText(agency.getNm());

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    public void selectProject(int ndx) {
        RecyclerView.ViewHolder vh = mListVw.findViewHolderForAdapterPosition(ndx);
        mAdapter.selectView(vh);
    }

}
