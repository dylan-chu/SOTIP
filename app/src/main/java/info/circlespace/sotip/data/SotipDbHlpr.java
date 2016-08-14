/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.circlespace.sotip.data.SotipContract.ProjectEntry;
import info.circlespace.sotip.data.SotipContract.ChartDataEntry;
import info.circlespace.sotip.data.SotipContract.InvestmentEntry;
import info.circlespace.sotip.data.SotipContract.AgencyEntry;

/**
 * This class helps to interface with the SQLite database.
 */
public class SotipDbHlpr extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "sotip.db";


    public SotipDbHlpr(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        final String CREATE_PROJECTS_TBL_SQL = "CREATE TABLE " + ProjectEntry.TABLE_NAME + " (" +
                ProjectEntry._ID + " INTEGER PRIMARY KEY," +
                ProjectEntry.COL_AGC_CODE + " TEXT NOT NULL, " +
                ProjectEntry.COL_UNIQ_INVMT_ID + " TEXT NOT NULL, " +
                ProjectEntry.COL_INVMT_TITLE + " TEXT NOT NULL, " +
                ProjectEntry.COL_PROJ_NAME + " TEXT NOT NULL, " +
                ProjectEntry.COL_OBJECTIVES + " TEXT NOT NULL, " +
                ProjectEntry.COL_PROJ_PERF + " INTEGER NOT NULL, " +
                ProjectEntry.COL_PROJ_STATUS + " INTEGER NOT NULL, " +
                ProjectEntry.COL_START_DATE + " TEXT NOT NULL, " +
                ProjectEntry.COL_COMPLTN_DATE + " TEXT NOT NULL, " +
                ProjectEntry.COL_SCH_VAR + " TEXT NOT NULL, " +
                ProjectEntry.COL_SCH_VAR_IN_DAYS + " INTEGER NOT NULL, " +
                ProjectEntry.COL_SCH_VAR_PERC + " REAL NOT NULL, " +
                ProjectEntry.COL_PROJ_LC_COST + " REAL NOT NULL, " +
                ProjectEntry.COL_COST_VAR + " TEXT NOT NULL, " +
                ProjectEntry.COL_COST_VAR_DOLLARS + " REAL NOT NULL, " +
                ProjectEntry.COL_COST_VAR_PERC + " REAL NOT NULL, " +
                ProjectEntry.COL_PM_EXP_LVL + " INTEGER NOT NULL, " +
                ProjectEntry.COL_SDLC_METHOD + " INTEGER NOT NULL, " +
                ProjectEntry.COL_OTHER_SDM + " TEXT NULL, " +
                ProjectEntry.COL_UPDATED_DATE + " TEXT NOT NULL " +
                " );";

        final String CREATE_CHART_DATA_TBL_SQL = "CREATE TABLE " + ChartDataEntry.TABLE_NAME + " (" +
                ChartDataEntry._ID + " INTEGER PRIMARY KEY," +
                ChartDataEntry.COL_DATA + " TEXT NOT NULL, " +
                ChartDataEntry.COL_AGENCIES + " TEXT NOT NULL, " +
                ChartDataEntry.COL_UPDATED_DATE + " TEXT NOT NULL " +
                " );";

        final String CREATE_INVESTMENTS_TBL_SQL = "CREATE TABLE " + InvestmentEntry.TABLE_NAME + " (" +
                InvestmentEntry._ID + " INTEGER PRIMARY KEY," +
                InvestmentEntry.COL_UNIQ_INVMT_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                InvestmentEntry.COL_AGC_CODE + " TEXT NOT NULL, " +
                InvestmentEntry.COL_TITLE + " TEXT NOT NULL, " +
                InvestmentEntry.COL_SUMMARY + " TEXT NOT NULL, " +
                InvestmentEntry.COL_NUM_PROJS + " INTEGER NOT NULL, " +
                InvestmentEntry.COL_LIFECYCLE_COST + " REAL NOT NULL, " +
                InvestmentEntry.COL_CIO_RATING + " INTEGER NOT NULL, " +
                InvestmentEntry.COL_CONTRACTORS + " TEXT NOT NULL, " +
                InvestmentEntry.COL_CONTRACT_TYPES + " TEXT NOT NULL, " +
                InvestmentEntry.COL_URLS + " TEXT NOT NULL, " +
                InvestmentEntry.COL_UPDATED_DATE + " TEXT NOT NULL " +
                " );";

        final String CREATE_AGENCIES_TBL_SQL = "CREATE TABLE " + AgencyEntry.TABLE_NAME + " (" +
                AgencyEntry._ID + " INTEGER PRIMARY KEY," +
                AgencyEntry.COL_CODE + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                AgencyEntry.COL_NAME + " TEXT NOT NULL, " +
                AgencyEntry.COL_MAIN_URL + " TEXT NOT NULL, " +
                AgencyEntry.COL_ELECTRONIC_CONTACT + " TEXT NOT NULL, " +
                AgencyEntry.COL_PHONE_NBR + " TEXT NOT NULL, " +
                AgencyEntry.COL_MAIL_ADDR + " TEXT NOT NULL, " +
                AgencyEntry.COL_LATITUDE + " REAL NOT NULL, " +
                AgencyEntry.COL_LONGITUDE + " REAL NOT NULL, " +
                AgencyEntry.COL_UPDATED_DATE + " TEXT NOT NULL " +
                " );";

        sqLiteDb.execSQL(CREATE_PROJECTS_TBL_SQL);
        sqLiteDb.execSQL(CREATE_CHART_DATA_TBL_SQL);
        sqLiteDb.execSQL(CREATE_INVESTMENTS_TBL_SQL);
        sqLiteDb.execSQL(CREATE_AGENCIES_TBL_SQL);

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int oldVer, int newVer) {
        sqLiteDb.execSQL("DROP TABLE IF EXISTS " + ProjectEntry.TABLE_NAME);
        sqLiteDb.execSQL("DROP TABLE IF EXISTS " + ChartDataEntry.TABLE_NAME);
        sqLiteDb.execSQL("DROP TABLE IF EXISTS " + InvestmentEntry.TABLE_NAME);
        sqLiteDb.execSQL("DROP TABLE IF EXISTS " + AgencyEntry.TABLE_NAME);
        onCreate(sqLiteDb);
    }
}
