/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class helps with the implementation of the content provider.
 */
public class SotipContract {

    public static final String CONTENT_AUTHORITY = "info.circlespace.sotip";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PROJECTS = "projects";
    public static final String PATH_CHARTS = "charts";
    public static final String PATH_INVESTMENTS = "investments";
    public static final String PATH_AGENCIES = "agencies";


    public static final class ProjectEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROJECTS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECTS;

        public static final String TABLE_NAME = "projects";

        public static final String COL_AGC_CODE = "agc_code";
        public static final String COL_UNIQ_INVMT_ID = "uniq_invmt_id";
        public static final String COL_INVMT_TITLE = "invmt_title";
        public static final String COL_PROJ_NAME = "proj_name";
        public static final String COL_OBJECTIVES = "objectives";
        public static final String COL_PROJ_PERF = "proj_perf";
        public static final String COL_PROJ_STATUS = "proj_status";
        public static final String COL_START_DATE = "start_date";
        public static final String COL_COMPLTN_DATE = "compltn_date";
        public static final String COL_SCH_VAR = "sch_var";
        public static final String COL_SCH_VAR_IN_DAYS = "sch_var_days";
        public static final String COL_SCH_VAR_PERC = "sch_var_perc";
        public static final String COL_PROJ_LC_COST = "proj_lc_cost";
        public static final String COL_COST_VAR = "cost_var";
        public static final String COL_COST_VAR_DOLLARS = "cost_var_dollars";
        public static final String COL_COST_VAR_PERC = "cost_var_perc";
        public static final String COL_PM_EXP_LVL = "pm_exp_lvl";
        public static final String COL_SDLC_METHOD = "sdlc_method";
        public static final String COL_OTHER_SDM = "other_sdm";
        public static final String COL_UPDATED_DATE = "updated_date";

        public static final String ID_FILTER =
                TABLE_NAME + "." + _ID + " = ?";

        public static final String AGENCY_FILTER =
                TABLE_NAME + "." + COL_AGC_CODE + " = ?";

        public static final String INVMT_FILTER =
                TABLE_NAME + "." + COL_UNIQ_INVMT_ID + " = ?";

        public static final String COST_VAR_FILTER =
                TABLE_NAME + "." + COL_COST_VAR + " = ?";

        public static final String SCH_VAR_FILTER =
                TABLE_NAME + "." + COL_SCH_VAR + " = ?";

        public static final String PROJ_PERF_FILTER =
                TABLE_NAME + "." + COL_PROJ_PERF + " = ?";

        public static final String PROJ_STATUS_FILTER =
                TABLE_NAME + "." + COL_PROJ_STATUS + " = ?";

        public static final String PM_LVL_FILTER =
                TABLE_NAME + "." + COL_PM_EXP_LVL + " = ?";

        public static final String SDM_FILTER =
                TABLE_NAME + "." + COL_SDLC_METHOD + " = ?";

        public static final String PROJ_ID_ASC_SORT =
                TABLE_NAME + "." + _ID + " ASC";

        public static Uri buildUniqueItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class ChartDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHARTS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARTS;

        public static final String TABLE_NAME = "charts";

        public static final String COL_DATA = "data";
        public static final String COL_AGENCIES = "agencies";
        public static final String COL_UPDATED_DATE = "updated_date";

        public static final String[] COMMON_DATA_COLUMNS = {
                TABLE_NAME + "." + COL_DATA,
                TABLE_NAME + "." + COL_AGENCIES
        };

        public static final int COL_NDX_CHART_DATA = 0;
        public static final int COL_NDX_AGENCIES = 1;


        public static final String ID_FILTER =
                TABLE_NAME + "." + _ID + " = ? ";


        public static Uri buildUniqueItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static final class InvestmentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVESTMENTS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_INVESTMENTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_INVESTMENTS;

        public static final String TABLE_NAME = "investments";

        public static final String COL_UNIQ_INVMT_ID = "uniq_invmt_id";
        public static final String COL_AGC_CODE = "agc_code";
        public static final String COL_TITLE = "title";
        public static final String COL_SUMMARY = "summary";
        public static final String COL_NUM_PROJS = "num_projs";
        public static final String COL_LIFECYCLE_COST = "lc_cost";
        public static final String COL_CIO_RATING = "cio_rating";
        public static final String COL_CONTRACTORS = "contractors";
        public static final String COL_CONTRACT_TYPES = "contract_types";
        public static final String COL_URLS = "urls";
        public static final String COL_UPDATED_DATE = "updated_date";

        public static final String ID_FILTER =
                TABLE_NAME + "." + COL_UNIQ_INVMT_ID + " = ? ";


        public static Uri buildUniqueItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUniqInvmtIdUri(String uniqInvmtId) {
            return CONTENT_URI.buildUpon().appendPath(uniqInvmtId).build();
        }

        public static String getUniqInvmtIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class AgencyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGENCIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_AGENCIES;

        public static final String TABLE_NAME = "agencies";


        public static final String COL_CODE = "code";
        public static final String COL_NAME = "name";
        public static final String COL_MAIN_URL = "main_url";
        public static final String COL_ELECTRONIC_CONTACT = "e_contact";
        public static final String COL_PHONE_NBR = "phone_nbr";
        public static final String COL_MAIL_ADDR = "mail_addr";
        public static final String COL_LATITUDE = "latitude";
        public static final String COL_LONGITUDE = "longitude";
        public static final String COL_UPDATED_DATE = "updated_date";

        public static final String ID_FILTER =
                TABLE_NAME + "." + COL_CODE + " = ? ";

        public static final String[] COMMON_DATA_COLUMNS = {
                TABLE_NAME + "." + COL_CODE,
                TABLE_NAME + "." + COL_NAME,
                TABLE_NAME + "." + COL_MAIN_URL,
                TABLE_NAME + "." + COL_ELECTRONIC_CONTACT,
                TABLE_NAME + "." + COL_PHONE_NBR,
                TABLE_NAME + "." + COL_MAIL_ADDR,
                TABLE_NAME + "." + COL_LATITUDE,
                TABLE_NAME + "." + COL_LONGITUDE
        };

        public static final int COL_NDX_CODE = 0;
        public static final int COL_NDX_NAME = 1;
        public static final int COL_NDX_MAIN_URL = 2;
        public static final int COL_NDX_E_CONTACT = 3;
        public static final int COL_NDX_PHONE_NBR = 4;
        public static final int COL_NDX_MAIL_ADDR = 5;
        public static final int COL_NDX_LATITUDE = 6;
        public static final int COL_NDX_LONGITUDE = 7;

        public static Uri buildUniqueItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAgencyCodeUri(String agcCode) {
            return CONTENT_URI.buildUpon().appendPath(agcCode).build();
        }

        public static String getAgencyCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
