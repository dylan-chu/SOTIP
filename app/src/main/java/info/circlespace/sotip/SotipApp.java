/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.circlespace.sotip.api.AgencyInfo;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.data.SotipContract;
import info.circlespace.sotip.data.SotipContract.AgencyEntry;
import info.circlespace.sotip.sync.DataSyncAdptr;

/**
 * This class contains a number of helper functions.
 *
 * todo: refactor into multiple classes
 */
public class SotipApp extends Application {

    // for managing the loading of data from the server
    public static final String IS_INITD_KEY = "is.initd";
    public static boolean IS_INITD = false;
    public static boolean IS_LOADING = false;
    public static final int API_CALL_ERR = 100;
    public static final int API_DATA_ERR = 200;
    public static CoordinatorLayout mCoordinator;
    public static Handler mMainHandler;
    public static ProgressBar mLoadingIndic;
    public static String LOAD_MSG = "";
    public static String INIT_MSG = "";
    public static String API_CALL_ERR_MSG = "";
    public static String API_DATA_ERR_MSG = "";

    public static final String UPD_DATE_KEY = "upd.date";
    public static final String PARAM_DATE = "date";
    public static final String DEFAULT_DATE = "1900-01-01";

    // for managing the display configuration
    public static boolean IS_DUAL_PANE = false;
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;
    public static int LOCKED_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static final int TABLET_DP = 600;

    // text formatters
    public static NumberFormat PERC_FMTR = NumberFormat.getPercentInstance();
    public static final int MAX_DECIMALS = 1;
    public static NumberFormat COST_FMTR = NumberFormat.getCurrencyInstance();
    public static final double COST_BASE = 1000000d;
    public static DateFormat DISPLAY_DATE_FMTR = DateFormat.getDateInstance();
    public static SimpleDateFormat DATE_FMTR = new SimpleDateFormat("yyyy-MM-dd");

    public static Map<String, AgencyInfo> ALL_AGENCIES = null;

    // data constants
    public static final String PROJ_STATUS_IN_PROG = "In-Progress";
    public static final String PROJ_STATUS_COMPLTD = "Completed";
    public static final int CODE_PROJ_STATUS_IN_PROG = 50;
    public static final int CODE_PROJ_STATUS_COMPLTD = 100;

    public static final String CODE_005_USDA = "005";
    public static final String CODE_006_COMM = "006";
    public static final String CODE_007_DEF = "007";
    public static final String CODE_009_HHS = "009";
    public static final String CODE_010_INT = "010";
    public static final String CODE_011_JUST = "011";
    public static final String CODE_012_LABOR = "012";
    public static final String CODE_014_STATE = "014";
    public static final String CODE_015_TREAS = "015";
    public static final String CODE_016_SSA = "016";
    public static final String CODE_018_EDU = "018";
    public static final String CODE_019_ENER = "019";
    public static final String CODE_020_EPA = "020";
    public static final String CODE_021_TRANS = "021";
    public static final String CODE_023_GSA = "023";
    public static final String CODE_024_DHS = "024";
    public static final String CODE_025_HUD = "025";
    public static final String CODE_026_NASA = "026";
    public static final String CODE_027_OPM = "027";
    public static final String CODE_028_SBA = "028";
    public static final String CODE_029_VET = "029";
    public static final String CODE_184_USAID = "184";
    public static final String CODE_202_USACE = "202";
    public static final String CODE_393_NARA = "393";
    public static final String CODE_422_NSF = "422";
    public static final String CODE_429_NRC = "429";

    public static String CHART_TYPE_KEY = "chart.type";
    public static int CHART_TYPE = 0;
    public static final int CHART_TYPE_COST_VAR = 1;
    public static final int CHART_TYPE_SCH_VAR = 2;
    public static final int CHART_TYPE_CMPLTD_PROJS = 3;
    public static final int CHART_TYPE_IN_PROG_PROJS = 4;
    public static final int CHART_TYPE_PM_LVL = 5;
    public static final int CHART_TYPE_SDM = 6;
    public static String[] CHART_NAMES;

    public static final int OVER_30 = 5;
    public static final int OVER_10 = 4;
    public static final int OVER_0 = 3;
    public static final int UNDER_0 = 2;
    public static final int UNDER_10 = 1;
    public static final int UNDER_30 = 0;

    public static int NUM_PERF_CATEGS = 5;
    public static String[] PERF_DESCS;
    public static int[] PERF_COLOURS;

    public static int NUM_PM_LVL_CATEGS = 9;
    public static String[] PM_LVL_DESCS;

    public static int NUM_SDM_CATEGS = 6;
    public static String[] SDM_DESCS;
    public static int SDM_OTHER = 6;

    public static String[] CIO_RATINGS;

    // for managing the selection of a category and transitioning to a list of projects
    public static int GROUP_NDX = -1;
    public static int PM_GROUP_NDX = 0;
    public static int SDM_GROUP_NDX = 0;
    public static int DATA_LVL = -1;
    public static List<String> AGENCIES = null;
    public static int AGC_NDX = -1;
    public static ProjectInfo[] AGENCY_PROJS = null;
    public static int PROJECT_ID = 0;
    public static String INVESTMENT_ID = "000";
    public static String AGENCY_CODE = "000";

    private Tracker mTracker;


    @Override
    public void onCreate() {
        super.onCreate();

        setupData();
        loadAllAgencies();
    }


    /**
     * Sets up data and formatting constants.
     */
    private void setupData() {
        PERC_FMTR.setMaximumFractionDigits(MAX_DECIMALS);

        Resources res = getResources();
        INIT_MSG = res.getString( R.string.init_msg );
        API_CALL_ERR_MSG = res.getString( R.string.api_call_error_msg );
        API_DATA_ERR_MSG = res.getString( R.string.null_api_response_msg );

        PERF_COLOURS = new int[NUM_PERF_CATEGS];
        PERF_COLOURS[0] = res.getColor(R.color.material_green);
        PERF_COLOURS[1] = res.getColor(R.color.material_blue);
        PERF_COLOURS[2] = res.getColor(R.color.material_amber);
        PERF_COLOURS[3] = res.getColor(R.color.material_orange);
        PERF_COLOURS[4] = res.getColor(R.color.material_red);

        PERF_DESCS = res.getStringArray(R.array.perf_descs);
        PM_LVL_DESCS = res.getStringArray(R.array.pm_exp_lvls);
        SDM_DESCS = res.getStringArray(R.array.sdlc_methods);
        CIO_RATINGS = res.getStringArray(R.array.cio_ratings);

        CHART_NAMES = res.getStringArray(R.array.chart_names);
    }


    /**
     * Preloads the data for all the agencies.  The number of records for agencies is very small so
     * we don't have to do the preload on a background thread.
     */
    public void loadAllAgencies() {
        ALL_AGENCIES = new HashMap<String, AgencyInfo>();

        Cursor cursor = getContentResolver().query(SotipContract.AgencyEntry.CONTENT_URI,
                AgencyEntry.COMMON_DATA_COLUMNS,
                null,
                null,
                null);

        if (cursor == null) {
            return;
        }

        try {
            while (cursor.moveToNext()) {
                AgencyInfo agency = new AgencyInfo();
                agency.setAc(cursor.getString(AgencyEntry.COL_NDX_CODE));
                agency.setNm(cursor.getString(AgencyEntry.COL_NDX_NAME));
                agency.setMu(cursor.getString(AgencyEntry.COL_NDX_MAIN_URL));
                agency.setEc(cursor.getString(AgencyEntry.COL_NDX_E_CONTACT));
                agency.setPn(cursor.getString(AgencyEntry.COL_NDX_PHONE_NBR));
                agency.setMa(cursor.getString(AgencyEntry.COL_NDX_MAIL_ADDR));
                agency.setLat(cursor.getDouble(AgencyEntry.COL_NDX_LATITUDE));
                agency.setLng(cursor.getDouble(AgencyEntry.COL_NDX_LONGITUDE));
                ALL_AGENCIES.put(agency.getAc(), agency);
            }
        } finally {
            cursor.close();
        }

    }


    /**
     * Shows a loading indicator.
     */
    public static void showLoadingIndicator( final boolean shouldShow ) {
        if ( mMainHandler == null )
            return;

        // this allows this function to be called from a thread that is not the main UI thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if (shouldShow) {
                    mLoadingIndic.setVisibility(View.VISIBLE);
                } else {
                    mLoadingIndic.setVisibility(View.GONE);
                }
            }
        };
        mMainHandler.post(myRunnable);
    }


    /**
     * Indicates a data load is beginning.
     */
    public static void startLoading() {
        IS_LOADING = true;
        updateLoadingPerc(0);
        showLoadingIndicator(true);
    }


    /**
     * Updates the percentage completed for the data load.
     */
    public static void updateLoadingPerc( double perc ) {
        LOAD_MSG =  INIT_MSG + ": " + PERC_FMTR.format( perc );
    }


    /**
     * Indicates a data load has ended with an error.
     */
    public static void stopLoading( int errorCode ) {
        IS_LOADING = false;
        showLoadingIndicator(false);

        switch (errorCode) {
            case API_CALL_ERR:
                LOAD_MSG = API_CALL_ERR_MSG;
                break;
            case API_DATA_ERR:
                LOAD_MSG = API_DATA_ERR_MSG;
                break;

        }
    }


    /**
     * Indicates a data load has ended successfully.
     */
    public static void completeLoading( Context ctx, String loadDate, List<AgencyInfo> items ) {
        IS_LOADING = false;
        updateLoadingPerc( 1 );
        showLoadingIndicator(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(UPD_DATE_KEY, loadDate);
        editor.putBoolean(IS_INITD_KEY, true);
        editor.commit();

        IS_INITD = true;
        addAgencies( items );
    }


    /**
     * Appends new agencies to the list of agencies.
     */
    public static void addAgencies(List<AgencyInfo> items) {
        for (AgencyInfo item : items) {
            ALL_AGENCIES.put(item.getAc(), item);
        }
    }


    /**
     * Retrieves an agency from the list of agencies.
     */
    public static AgencyInfo getAgency(String code) {
        AgencyInfo agency = ALL_AGENCIES.get(code);
        if (agency == null) {
            agency = new AgencyInfo();
            agency.setAc(code);
            agency.setNm(code);
        }

        return agency;
    }


    /**
     * Returns the logo for an agency.
     */
    public static int getAgencyIcon(String agencyCode) {

        if (agencyCode.equals(CODE_005_USDA)) {
            return R.drawable.d005_usda_80;
        } else if (agencyCode.equals(CODE_006_COMM)) {
            return R.drawable.d006_comm_80;
        } else if (agencyCode.equals(CODE_007_DEF)) {
            return R.drawable.d007_def_80;
        } else if (agencyCode.equals(CODE_009_HHS)) {
            return R.drawable.d009_hhs_80;
        } else if (agencyCode.equals(CODE_010_INT)) {
            return R.drawable.d010_int_80;
        } else if (agencyCode.equals(CODE_011_JUST)) {
            return R.drawable.d011_just_80;
        } else if (agencyCode.equals(CODE_012_LABOR)) {
            return R.drawable.d012_labor_80;
        } else if (agencyCode.equals(CODE_014_STATE)) {
            return R.drawable.d014_state_80;
        } else if (agencyCode.equals(CODE_015_TREAS)) {
            return R.drawable.d015_treas_80;
        } else if (agencyCode.equals(CODE_016_SSA)) {
            return R.drawable.d016_ssa_80;
        } else if (agencyCode.equals(CODE_018_EDU)) {
            return R.drawable.d018_edu_80;
        } else if (agencyCode.equals(CODE_019_ENER)) {
            return R.drawable.d019_ener_80;
        } else if (agencyCode.equals(CODE_020_EPA)) {
            return R.drawable.d020_epa_80;
        } else if (agencyCode.equals(CODE_021_TRANS)) {
            return R.drawable.d021_trans_80;
        } else if (agencyCode.equals(CODE_023_GSA)) {
            return R.drawable.d023_gsa_80;
        } else if (agencyCode.equals(CODE_024_DHS)) {
            return R.drawable.d024_dhs_80;
        } else if (agencyCode.equals(CODE_025_HUD)) {
            return R.drawable.d025_hud_80;
        } else if (agencyCode.equals(CODE_026_NASA)) {
            return R.drawable.d026_nasa_80;
        } else if (agencyCode.equals(CODE_027_OPM)) {
            return R.drawable.d027_opm_80;
        } else if (agencyCode.equals(CODE_028_SBA)) {
            return R.drawable.d028_sba_80;
        } else if (agencyCode.equals(CODE_029_VET)) {
            return R.drawable.d029_vet_80;
        } else if (agencyCode.equals(CODE_184_USAID)) {
            return R.drawable.d184_usaid_80;
        } else if (agencyCode.equals(CODE_202_USACE)) {
            return R.drawable.d202_usace_80;
        } else if (agencyCode.equals(CODE_393_NARA)) {
            return R.drawable.d393_nara_80;
        } else if (agencyCode.equals(CODE_422_NSF)) {
            return R.drawable.d422_nsf_80;
        } else if (agencyCode.equals(CODE_429_NRC)) {
            return R.drawable.d429_nrc_80;
        }

        //return R.drawable.d000_unknown;
        return R.drawable.d005_usda_40;
    }


    /**
     * Returns the color to represent a variance category.
     */
    public static int getVarColour(int varCode) {
        switch (varCode) {
            case 5:
                return R.drawable.red_800_circle_12;
            case 4:
                return R.drawable.red_600_circle_12;
            case 3:
                return R.drawable.red_400_circle_12;
            case 2:
                return R.drawable.green_400_circle_12;
            case 1:
                return R.drawable.green_600_circle_12;
            case 0:
                return R.drawable.green_800_circle_12;
        }

        return R.drawable.black_circle_12;
    }


    /**
     * Returns the color to represent a performance category.
     */
    public static int getPerfDrawable(int perfCode) {
        switch (perfCode) {
            case 4:
                return R.drawable.red_box;
            case 3:
                return R.drawable.orange_box;
            case 2:
                return R.drawable.amber_box;
            case 1:
                return R.drawable.blue_box;
            case 0:
                return R.drawable.green_box;
        }

        return R.drawable.amber_box;
    }


    public static String getPerfDesc(int ndx) {
        return PERF_DESCS[ndx];
    }


    public static int getPerfColour(int ndx) {
        return PERF_COLOURS[ndx];
    }


    public static String getPmLvlDesc(int code) {
        return PM_LVL_DESCS[code - 1];
    }


    public static String getSdmDesc(int code) {
        return SDM_DESCS[code - 1];
    }


    /**
     * Returns the full description of a software development methodology if it is "Other"
     * @return
     */
    public static String getSdmDesc(int code, String otherSdm) {
        if (code == SDM_OTHER)
            return otherSdm;

        return SDM_DESCS[code - 1];
    }


    public static String getCioRating(int code) {
        return code + " - " + CIO_RATINGS[code - 1];
    }


    public static String fmtDate(Date date) {
        return DATE_FMTR.format(date);
    }


    public static String displayDate(String date) {
        String dateStr = "?";

        try {
            Date dt = DATE_FMTR.parse(date);
            dateStr = DISPLAY_DATE_FMTR.format(dt);
        } catch (ParseException pe) {

        }

        return dateStr;
    }


    public static String fmtPerc(float perc) {
        return PERC_FMTR.format(perc);
    }


    public static String fmtCost(double cost) {
        return COST_FMTR.format(cost * COST_BASE);
    }


    public static String fmtConcatStr(String concatStr) {
        return concatStr.replace(";", "\n");
    }


    public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.analytics);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker() {
        startTracking();
        return mTracker;
    }

    public static String getScreenName(int ndx) {
        return CHART_NAMES[ndx - 1];
    }


    /**
     * Determines if the app has successfully loaded data from the server at least once.
     */
    public static boolean isInitd(Context ctx) {
        if (IS_INITD) {
            return true;
        }

        if (!isConnected(ctx)) {
            Resources res = ctx.getResources();
            showStatusMsg( res.getString(R.string.conn_internet_msg) );
            return false;
        }

        if (!IS_LOADING) {
            DataSyncAdptr.syncImmediately(ctx);
        }

        showStatusMsg(LOAD_MSG);

        return false;
    }


    /**
     * Checks whether there is a connection to the Internet
     */
    public static boolean isConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /**
     * Displays a snackbar for an Activity.
     */
    public static void showStatusMsg(String msg) {
        if (mCoordinator == null) {
            return;
        }

        Snackbar snackbar = Snackbar.make(mCoordinator, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
