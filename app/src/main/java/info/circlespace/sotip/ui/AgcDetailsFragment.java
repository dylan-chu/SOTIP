/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;
import info.circlespace.sotip.data.SotipContract.AgencyEntry;


/**
 * Displays the details for an agency.
 */
public class AgcDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    public static final String LOG_TAG = AgcDetailsFragment.class.getSimpleName();
    public static final String DELIMITER = ";";

    private static final int LOADER_ID = 10;

    public static final String[] DATA_COLUMNS = {
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_CODE,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_NAME,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_MAIN_URL,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_ELECTRONIC_CONTACT,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_PHONE_NBR,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_MAIL_ADDR,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_LATITUDE,
            AgencyEntry.TABLE_NAME + "." + AgencyEntry.COL_LONGITUDE
    };

    public static final int COL_CODE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_MAIN_URL = 2;
    public static final int COL_ELECTRONIC_CONTACT = 3;
    public static final int COL_PHONE_NBR = 4;
    public static final int COL_MAIL_ADDR = 5;
    public static final int COL_LATITUDE = 6;
    public static final int COL_LONGITUDE = 7;

    private SupportMapFragment fragment;
    private GoogleMap map;
    private TextView mAgcCode;
    private TextView mAgcName;
    private TextView mMainUrl;
    private TextView mEcName;
    private TextView mEcUrl;
    private TextView mPhoneNbr;
    private TextView mMailAddr;
    private LatLng agcLoc;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_agc_details, container, false);

        mAgcCode = (TextView) rootView.findViewById(R.id.agcCode);
        mAgcName = (TextView) rootView.findViewById(R.id.agcName);
        mEcName = (TextView) rootView.findViewById(R.id.ecName);
        mEcUrl = (TextView) rootView.findViewById(R.id.ecUrl);
        mPhoneNbr = (TextView) rootView.findViewById(R.id.phoneNbr);
        mMailAddr = (TextView) rootView.findViewById(R.id.mailAddr);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onMapReady(GoogleMap gMap) {
        map = gMap;
        CameraPosition target = CameraPosition.builder().target(agcLoc).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        map.addMarker(new MarkerOptions().position(agcLoc));
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = AgencyEntry.buildAgencyCodeUri(SotipApp.AGENCY_CODE);

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

        mAgcCode.setText(data.getString(COL_CODE));
        mAgcName.setText(data.getString(COL_NAME));
        String ec = data.getString(COL_ELECTRONIC_CONTACT);
        String[] ecTokens = ec.split(DELIMITER);
        mEcName.setText(ecTokens[0]);
        mEcUrl.setText(ecTokens[1]);
        mPhoneNbr.setText(data.getString(COL_PHONE_NBR));
        mMailAddr.setText(SotipApp.fmtConcatStr(data.getString(COL_MAIL_ADDR)));

        double lat = data.getDouble(COL_LATITUDE);
        double lng = data.getDouble(COL_LONGITUDE);
        agcLoc = new LatLng(lat, lng);

        if (map == null) {
            fragment.getMapAsync(this);
        } else {
            map.addMarker(new MarkerOptions().position(agcLoc));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void showDetails() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

}
