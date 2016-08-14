/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import info.circlespace.sotip.data.SotipContract.ProjectEntry;
import info.circlespace.sotip.data.SotipContract.ChartDataEntry;
import info.circlespace.sotip.data.SotipContract.InvestmentEntry;
import info.circlespace.sotip.data.SotipContract.AgencyEntry;

/**
 * This class implements a content provider for the data in the SQLite database.
 */
public class SotipPrvdr extends ContentProvider {

    public static final String LOG_TAG = SotipPrvdr.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SotipDbHlpr mDbHelper;

    static final int PROJECTS = 100;
    static final int PROJECT = 110;
    static final int CHARTS = 200;
    static final int INVESTMENTS = 300;
    static final int INVESTMENT = 310;
    static final int AGENCIES = 400;
    static final int AGENCY = 410;


    @Override
    public boolean onCreate() {
        mDbHelper = new SotipDbHlpr(getContext());
        return true;
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SotipContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SotipContract.PATH_PROJECTS, PROJECTS);
        matcher.addURI(authority, SotipContract.PATH_PROJECTS + "/#", PROJECT);
        matcher.addURI(authority, SotipContract.PATH_CHARTS, CHARTS);
        matcher.addURI(authority, SotipContract.PATH_INVESTMENTS, INVESTMENTS);
        matcher.addURI(authority, SotipContract.PATH_INVESTMENTS + "/*", INVESTMENT);
        matcher.addURI(authority, SotipContract.PATH_AGENCIES, AGENCIES);
        matcher.addURI(authority, SotipContract.PATH_AGENCIES + "/*", AGENCY);
        return matcher;
    }


    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PROJECTS:
                return ProjectEntry.CONTENT_TYPE;
            case PROJECT:
                return ProjectEntry.CONTENT_ITEM_TYPE;
            case CHARTS:
                return ChartDataEntry.CONTENT_ITEM_TYPE;
            case INVESTMENTS:
                return InvestmentEntry.CONTENT_TYPE;
            case INVESTMENT:
                return InvestmentEntry.CONTENT_ITEM_TYPE;
            case AGENCIES:
                return AgencyEntry.CONTENT_TYPE;
            case AGENCY:
                return InvestmentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case PROJECTS: {
                retCursor = db.query(
                        ProjectEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PROJECT: {
                String projId = ProjectEntry.getIdFromUri(uri);
                retCursor = db.query(
                        ProjectEntry.TABLE_NAME,
                        projection,
                        ProjectEntry.ID_FILTER,
                        new String[]{projId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CHARTS: {
                retCursor = db.query(
                        ChartDataEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case INVESTMENTS: {
                retCursor = db.query(
                        InvestmentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case INVESTMENT: {
                String uniqInvmtId = InvestmentEntry.getUniqInvmtIdFromUri(uri);
                retCursor = db.query(
                        InvestmentEntry.TABLE_NAME,
                        projection,
                        InvestmentEntry.ID_FILTER,
                        new String[]{uniqInvmtId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AGENCIES: {
                retCursor = db.query(
                        AgencyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case AGENCY: {
                String code = AgencyEntry.getAgencyCodeFromUri(uri);
                retCursor = db.query(
                        AgencyEntry.TABLE_NAME,
                        projection,
                        AgencyEntry.ID_FILTER,
                        new String[]{code},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PROJECTS: {
                long _id = db.insert(ProjectEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ProjectEntry.buildUniqueItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CHARTS: {
                long _id = db.insert(ChartDataEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ChartDataEntry.buildUniqueItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case INVESTMENTS: {
                long _id = db.insert(InvestmentEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = InvestmentEntry.buildUniqueItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case AGENCIES: {
                long _id = db.insert(AgencyEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = AgencyEntry.buildUniqueItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case PROJECTS:
                rowsDeleted = db.delete(ProjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CHARTS:
                rowsDeleted = db.delete(ChartDataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVESTMENTS:
                rowsDeleted = db.delete(InvestmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case AGENCIES:
                rowsDeleted = db.delete(AgencyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PROJECTS:
                rowsUpdated = db.update(ProjectEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHARTS:
                rowsUpdated = db.update(ChartDataEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INVESTMENTS:
                rowsUpdated = db.update(InvestmentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case AGENCIES:
                rowsUpdated = db.update(InvestmentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int returnCount = 0;

        switch (match) {
            case PROJECTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.replace(ProjectEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CHARTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.replace(ChartDataEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case INVESTMENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.replace(InvestmentEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case AGENCIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.replace(AgencyEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
