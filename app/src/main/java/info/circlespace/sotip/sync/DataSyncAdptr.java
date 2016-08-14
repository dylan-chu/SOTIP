/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.AgenciesApiRes;
import info.circlespace.sotip.api.AgencyInfo;
import info.circlespace.sotip.api.ChartDataApiRes;
import info.circlespace.sotip.api.ChartDataInfo;
import info.circlespace.sotip.api.InvestmentInfo;
import info.circlespace.sotip.api.InvestmentsApiRes;
import info.circlespace.sotip.api.ItDashboardApiSvc;
import info.circlespace.sotip.api.ProjectInfo;
import info.circlespace.sotip.api.ProjectsApiRes;
import info.circlespace.sotip.data.SotipContract.InvestmentEntry;
import info.circlespace.sotip.data.SotipContract.ChartDataEntry;
import info.circlespace.sotip.data.SotipContract.ProjectEntry;
import info.circlespace.sotip.data.SotipContract.AgencyEntry;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class implements a sync adapter to periodically retrieve data from the backend.
 * <p/>
 * The code in this class is based on code in Udacity's Advanced Android Development course's Github repo.
 */
public class DataSyncAdptr extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = DataSyncAdptr.class.getSimpleName();
    public static final int TIMEOUT = 60;
    public static final String ACTION_DATA_UPDATED = "info.circlespace.sotip.ACTION_DATA_UPDATED";
    public static final int SYNC_INTERVAL = 60 * 360;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private String mToday;
    private String mDataUpdDate;


    public DataSyncAdptr(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        /**
         * The default before a call times out is 10 seconds .
         * This is not enough time to get the data from the backend.
         * So, we need to increase the timeout period to much higher.
         */
        OkHttpClient okClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS).build();

        Retrofit retro = new Retrofit.Builder()
                .baseUrl(ItDashboardApiSvc.BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Calendar now = GregorianCalendar.getInstance();
        mToday = SotipApp.fmtDate(now.getTime());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDataUpdDate = prefs.getString(SotipApp.UPD_DATE_KEY, SotipApp.DEFAULT_DATE);

        // first retrieve the data for charts
        getChartData(retro);
    }


    public void getChartData(final Retrofit retro) {
        ItDashboardApiSvc.ChartsApi dataSvc = retro.create(ItDashboardApiSvc.ChartsApi.class);
        Call<ChartDataApiRes> call = dataSvc.getData(mDataUpdDate);

        call.enqueue(new Callback<ChartDataApiRes>() {
            @Override
            public void onResponse(Call<ChartDataApiRes> call, Response<ChartDataApiRes> response) {
                ChartDataApiRes apiRes = response.body();
                if (apiRes == null) {
                    return;
                }

                List<ChartDataInfo> items = apiRes.getItems();
                if (items.size() != 0) {

                    Vector<ContentValues> itemsVector = new Vector<ContentValues>(items.size());

                    for (ChartDataInfo item : items) {
                        itemsVector.add(extractChartData(item));
                    }


                    if (itemsVector.size() > 0) {
                        ContentValues[] itemsArray = new ContentValues[itemsVector.size()];
                        itemsVector.toArray(itemsArray);
                        getContext().getContentResolver().bulkInsert(ChartDataEntry.CONTENT_URI, itemsArray);
                    }
                }

                // we have completed pulling data from 1 out of 4 API endpoints
                SotipApp.INIT_PERC = 25;
                getProjectsData(retro);
            }

            @Override
            public void onFailure(Call<ChartDataApiRes> call, Throwable t) {
            }
        });
    }


    private ContentValues extractChartData(ChartDataInfo item) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(ChartDataEntry._ID, item.getType());
        itemValues.put(ChartDataEntry.COL_DATA, item.getData());
        itemValues.put(ChartDataEntry.COL_AGENCIES, item.getAgcs());
        itemValues.put(ChartDataEntry.COL_UPDATED_DATE, mToday);
        return itemValues;
    }


    public void getProjectsData(final Retrofit retro) {
        ItDashboardApiSvc.ProjectsApi dataSvc = retro.create(ItDashboardApiSvc.ProjectsApi.class);
        Call<ProjectsApiRes> call = dataSvc.getData(mDataUpdDate);

        call.enqueue(new Callback<ProjectsApiRes>() {
            @Override
            public void onResponse(Call<ProjectsApiRes> call, Response<ProjectsApiRes> response) {
                ProjectsApiRes apiRes = response.body();
                if (apiRes == null) {
                    return;
                }

                List<ProjectInfo> items = apiRes.getItems();
                if (items.size() != 0) {

                    Vector<ContentValues> itemsVector = new Vector<ContentValues>(items.size());

                    for (ProjectInfo item : items) {
                        itemsVector.add(extractProject(item));
                    }

                    if (itemsVector.size() > 0) {
                        ContentValues[] itemsArray = new ContentValues[itemsVector.size()];
                        itemsVector.toArray(itemsArray);
                        getContext().getContentResolver().bulkInsert(ProjectEntry.CONTENT_URI, itemsArray);
                    }
                }

                // we have completed pulling data from 2 out of 4 API endpoints
                SotipApp.INIT_PERC = 50;
                getInvestmentsData(retro);
            }

            @Override
            public void onFailure(Call<ProjectsApiRes> call, Throwable t) {
            }
        });
    }


    private ContentValues extractProject(ProjectInfo item) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(ProjectEntry._ID, item.getID());
        itemValues.put(ProjectEntry.COL_AGC_CODE, item.getAc());
        itemValues.put(ProjectEntry.COL_UNIQ_INVMT_ID, item.getUii());
        itemValues.put(ProjectEntry.COL_INVMT_TITLE, item.getIt());
        itemValues.put(ProjectEntry.COL_PROJ_NAME, item.getName());
        itemValues.put(ProjectEntry.COL_OBJECTIVES, item.getObj());
        itemValues.put(ProjectEntry.COL_PROJ_PERF, item.getPp());
        itemValues.put(ProjectEntry.COL_PROJ_STATUS, item.getPs());
        itemValues.put(ProjectEntry.COL_START_DATE, item.getSd());
        itemValues.put(ProjectEntry.COL_COMPLTN_DATE, item.getCd());
        itemValues.put(ProjectEntry.COL_SCH_VAR, item.getSv());
        itemValues.put(ProjectEntry.COL_SCH_VAR_IN_DAYS, item.getSvd());
        itemValues.put(ProjectEntry.COL_SCH_VAR_PERC, item.getSvp());
        itemValues.put(ProjectEntry.COL_PROJ_LC_COST, item.getLcc());
        itemValues.put(ProjectEntry.COL_COST_VAR, item.getCv());
        itemValues.put(ProjectEntry.COL_COST_VAR_DOLLARS, item.getCvd());
        itemValues.put(ProjectEntry.COL_COST_VAR_PERC, item.getCvp());
        itemValues.put(ProjectEntry.COL_PM_EXP_LVL, item.getPm());
        itemValues.put(ProjectEntry.COL_SDLC_METHOD, item.getSdm());
        itemValues.put(ProjectEntry.COL_OTHER_SDM, item.getOsdm());
        itemValues.put(ProjectEntry.COL_UPDATED_DATE, item.getUd());
        return itemValues;
    }


    public void getInvestmentsData(final Retrofit retro) {
        ItDashboardApiSvc.InvestmentsApi dataSvc = retro.create(ItDashboardApiSvc.InvestmentsApi.class);
        Call<InvestmentsApiRes> call = dataSvc.getData(mDataUpdDate);

        call.enqueue(new Callback<InvestmentsApiRes>() {
            @Override
            public void onResponse(Call<InvestmentsApiRes> call, Response<InvestmentsApiRes> response) {
                InvestmentsApiRes apiRes = response.body();
                if (apiRes == null) {
                    return;
                }

                List<InvestmentInfo> items = apiRes.getItems();
                if (items.size() != 0) {

                    Vector<ContentValues> itemsVector = new Vector<ContentValues>(items.size());

                    for (InvestmentInfo item : items) {
                        itemsVector.add(extractInvestment(item));
                    }

                    if (itemsVector.size() > 0) {
                        ContentValues[] itemsArray = new ContentValues[itemsVector.size()];
                        itemsVector.toArray(itemsArray);
                        getContext().getContentResolver().bulkInsert(InvestmentEntry.CONTENT_URI, itemsArray);
                    }
                }

                // we have completed pulling data from 3 out of 4 API endpoints
                SotipApp.INIT_PERC = 75;
                getAgenciesData(retro);
            }

            @Override
            public void onFailure(Call<InvestmentsApiRes> call, Throwable t) {
            }
        });
    }


    private ContentValues extractInvestment(InvestmentInfo item) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(InvestmentEntry.COL_UNIQ_INVMT_ID, item.getUii());
        itemValues.put(InvestmentEntry.COL_AGC_CODE, item.getAc());
        itemValues.put(InvestmentEntry.COL_TITLE, item.getIt());
        itemValues.put(InvestmentEntry.COL_CIO_RATING, item.getCio());
        itemValues.put(InvestmentEntry.COL_CONTRACTORS, item.getContractorsAsStr());
        itemValues.put(InvestmentEntry.COL_CONTRACT_TYPES, item.getContractTypesAsStr());
        itemValues.put(InvestmentEntry.COL_URLS, item.getUrls().get(0));
        itemValues.put(InvestmentEntry.COL_SUMMARY, item.getSum());
        itemValues.put(InvestmentEntry.COL_NUM_PROJS, 0);
        itemValues.put(InvestmentEntry.COL_LIFECYCLE_COST, 0.0);
        itemValues.put(InvestmentEntry.COL_UPDATED_DATE, item.getUd());
        return itemValues;
    }


    public void getAgenciesData(final Retrofit retro) {
        ItDashboardApiSvc.AgenciesApi dataSvc = retro.create(ItDashboardApiSvc.AgenciesApi.class);
        Call<AgenciesApiRes> call = dataSvc.getData(mDataUpdDate);

        call.enqueue(new Callback<AgenciesApiRes>() {
            @Override
            public void onResponse(Call<AgenciesApiRes> call, Response<AgenciesApiRes> response) {
                AgenciesApiRes apiRes = response.body();
                if (apiRes == null) {
                    return;
                }

                List<AgencyInfo> items = apiRes.getItems();
                if (items.size() != 0) {

                    Vector<ContentValues> itemsVector = new Vector<ContentValues>(items.size());

                    for (AgencyInfo item : items) {
                        itemsVector.add(extractAgency(item));
                    }

                    if (itemsVector.size() > 0) {
                        ContentValues[] itemsArray = new ContentValues[itemsVector.size()];
                        itemsVector.toArray(itemsArray);
                        getContext().getContentResolver().bulkInsert(AgencyEntry.CONTENT_URI, itemsArray);
                    }
                }

                // we have completed pulling data from all API endpoints
                SotipApp.INIT_PERC = 100;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SotipApp.UPD_DATE_KEY, mToday);
                editor.putBoolean(SotipApp.IS_INITD_KEY, true);
                editor.commit();

                SotipApp.IS_INITD = true;
                SotipApp.addAgencies(items);

                updateWidgets();
            }

            @Override
            public void onFailure(Call<AgenciesApiRes> call, Throwable t) {
            }
        });
    }


    private ContentValues extractAgency(AgencyInfo item) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(AgencyEntry.COL_CODE, item.getAc());
        itemValues.put(AgencyEntry.COL_NAME, item.getNm());
        itemValues.put(AgencyEntry.COL_MAIN_URL, item.getMu());
        itemValues.put(AgencyEntry.COL_ELECTRONIC_CONTACT, item.getEc());
        itemValues.put(AgencyEntry.COL_PHONE_NBR, item.getPn());
        itemValues.put(AgencyEntry.COL_MAIL_ADDR, item.getMa());
        itemValues.put(AgencyEntry.COL_LATITUDE, item.getLat());
        itemValues.put(AgencyEntry.COL_LONGITUDE, item.getLng());
        itemValues.put(AgencyEntry.COL_UPDATED_DATE, item.getUd());
        return itemValues;
    }


    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }


    /**
     * Helper method to schedule the sync adapter periodic execution.
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to have the sync adapter sync immediately
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        DataSyncAdptr.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
