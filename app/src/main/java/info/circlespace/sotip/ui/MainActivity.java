/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.sync.DataSyncAdptr;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private CoordinatorLayout mCoordinator;
    private boolean mIsDualPane;
    private Fragment mFrgm;
    private Fragment mFrgm2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        SotipApp.mCoordinator = mCoordinator;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SotipApp.IS_INITD = prefs.getBoolean(SotipApp.IS_INITD_KEY, false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Configuration config = getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            SotipApp.SCREEN_WIDTH = config.screenWidthDp;
            SotipApp.SCREEN_HEIGHT = config.screenHeightDp;
        } else {
            SotipApp.SCREEN_WIDTH = config.screenHeightDp;
            SotipApp.SCREEN_HEIGHT = config.screenWidthDp;
        }

        if (SotipApp.SCREEN_WIDTH >= SotipApp.TABLET_DP) {
            SotipApp.LOCKED_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }

        setRequestedOrientation(SotipApp.LOCKED_ORIENTATION);

        mFrgm = new CostVarFragment();
        showFragment(mFrgm);

        mIsDualPane = false;

        if (findViewById(R.id.dataFrgm2) != null) {
            SotipApp.IS_DUAL_PANE = true;
            mIsDualPane = true;
            mFrgm2 = new SchVarFragment();

            showFragment2(mFrgm2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DataSyncAdptr.initializeSyncAdapter(this);

        SotipApp.CHART_TYPE = SotipApp.CHART_TYPE_COST_VAR;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                SotipApp.CHART_TYPE = extras.getInt(SotipApp.CHART_TYPE_KEY);
            }
        }

        ((SotipApp) getApplication()).startTracking();
    }


    @Override
    public void onStart() {
        super.onStart();
        showChart(SotipApp.CHART_TYPE);
    }


    private void showChart(int chartType) {
        switch (chartType) {
            case SotipApp.CHART_TYPE_COST_VAR:
                mFrgm = new CostVarFragment();
                if (mIsDualPane) {
                    mFrgm2 = new SchVarFragment();
                }
                break;
            case SotipApp.CHART_TYPE_SCH_VAR:
                if (mIsDualPane) {
                    mFrgm = new CostVarFragment();
                    mFrgm2 = new SchVarFragment();
                } else {
                    mFrgm = new SchVarFragment();
                }
                break;
            case SotipApp.CHART_TYPE_CMPLTD_PROJS:
                mFrgm = new CmpltdProjPerfFragment();
                if (mIsDualPane) {
                    mFrgm2 = new InProgProjPerfFragment();
                }
                break;
            case SotipApp.CHART_TYPE_IN_PROG_PROJS:
                if (mIsDualPane) {
                    mFrgm = new CmpltdProjPerfFragment();
                    mFrgm2 = new InProgProjPerfFragment();
                } else {
                    mFrgm = new InProgProjPerfFragment();
                }
                break;
            case SotipApp.CHART_TYPE_PM_LVL:
                mFrgm = new PmExpLvlFragment();
                if (mIsDualPane) {
                    mFrgm2 = new SdlcMethodFragment();
                }
                break;
            case SotipApp.CHART_TYPE_SDM:
                if (mIsDualPane) {
                    mFrgm = new PmExpLvlFragment();
                    mFrgm2 = new SdlcMethodFragment();
                } else {
                    mFrgm = new SdlcMethodFragment();
                }
                break;
            default:
                mFrgm = new CostVarFragment();
                if (mIsDualPane) {
                    mFrgm2 = new SchVarFragment();
                }
                break;
        }

        showFragment(mFrgm);
        if (mIsDualPane) {
            showFragment2(mFrgm2);
        }

    }

    private void showFragment(Fragment frgm) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.dataFrgm, frgm);
        ft.commit();
    }

    private void showFragment2(Fragment frgm) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.dataFrgm2, frgm);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SotipApp.UPD_DATE_KEY, "1900-01-01");
            editor.putBoolean(SotipApp.IS_INITD_KEY, false);
            editor.commit();

            DataSyncAdptr.syncImmediately(this);
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int chartType = -1;

        if (id == R.id.projCostNavItem) {
            chartType = SotipApp.CHART_TYPE_COST_VAR;
        } else if (id == R.id.projSchNavItem) {
            chartType = SotipApp.CHART_TYPE_SCH_VAR;
        } else if (id == R.id.projPerfNavItem) {
            chartType = SotipApp.CHART_TYPE_CMPLTD_PROJS;
        } else if (id == R.id.projRiskNavItem) {
            chartType = SotipApp.CHART_TYPE_IN_PROG_PROJS;
        } else if (id == R.id.pmExpLvlNavItem) {
            chartType = SotipApp.CHART_TYPE_PM_LVL;
        } else if (id == R.id.sdlcMtdlgyNavItem) {
            chartType = SotipApp.CHART_TYPE_SDM;
        }

        showChart(chartType);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
