/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.api.ProjectInfo;

/**
 * Displays and manages the list of projects.
 */
public class ProjListActivity extends AppCompatActivity implements ProjListFragment.FragmentListener {

    public static final String LOG_TAG = ProjListActivity.class.getSimpleName();

    private ProjListFragment mListFrgm;

    private ViewPager mVwPager;
    private ProjDetailsFragment mProjDetailsFrgm;
    private InvmtDetailsFragment mInvmtDetailsFrgm;
    private AgcDetailsFragment mAgcDetailsFrgm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proj_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // locks the screen configuration
        setRequestedOrientation(SotipApp.LOCKED_ORIENTATION);

        mListFrgm = (ProjListFragment) getSupportFragmentManager().findFragmentById(R.id.listFrgm);

        if (SotipApp.IS_DUAL_PANE) {
            mVwPager = (ViewPager) findViewById(R.id.vwPager);
            setupViewPager(mVwPager);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        mProjDetailsFrgm = new ProjDetailsFragment();
        mInvmtDetailsFrgm = new InvmtDetailsFragment();
        mAgcDetailsFrgm = new AgcDetailsFragment();

        Resources res = getResources();

        adapter.addFragment(mProjDetailsFrgm, res.getString(R.string.proj_tab));
        adapter.addFragment(mInvmtDetailsFrgm, res.getString(R.string.invmt_tab));
        adapter.addFragment(mAgcDetailsFrgm, res.getString(R.string.agc_tab));

        viewPager.setAdapter(adapter);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onItemSelected(ProjectInfo item, ProjListAdptr.VwHldr vh) {
        SotipApp.PROJECT_ID = item.getID();
        SotipApp.INVESTMENT_ID = item.getUii();
        SotipApp.AGENCY_CODE = item.getAc();

        if (SotipApp.IS_DUAL_PANE) {
            mVwPager.setCurrentItem(0);

            // when a next item is selected, display the project details
            mProjDetailsFrgm.showDetails();

            if (mInvmtDetailsFrgm.isVisible())
                mInvmtDetailsFrgm.showDetails();

            if (mAgcDetailsFrgm.isVisible())
                mAgcDetailsFrgm.showDetails();

        } else {
            Intent intent = new Intent(this, ProjDetailsActivity.class);
            startActivity(intent);
        }
    }

}
