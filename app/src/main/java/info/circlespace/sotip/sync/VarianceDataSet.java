/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds data for each variance category.
 */
public class VarianceDataSet implements SotipDataSet {

    public static final String CATEG_DELIMITER = "-";
    public static final String DELIMITER = ",";

    public static final int NUM_CATEGORIES = 6;

    // the number of projects in each variance category
    private int[] mDataSet;
    // the percentage of the projects in each variance category
    private float[] mPercSet;
    // the agencies which have projects in each variance category
    private List<String>[] mAgencies;


    public VarianceDataSet() {
        mDataSet = new int[NUM_CATEGORIES];
        mPercSet = new float[NUM_CATEGORIES];
        mAgencies = new List[NUM_CATEGORIES];

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            mDataSet[i] = 0;
            mPercSet[i] = 0.0f;
            mAgencies[i] = new ArrayList<String>();
        }
    }


    public int[] getTally() {
        return mDataSet;
    }


    /**
     * Returns the number of projects for each variance category as a string
     */
    @Override
    public String getDataAsStr() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            if (i > 0)
                buf.append(DELIMITER);
            buf.append(mDataSet[i]);
        }

        return buf.toString();
    }


    /**
     * Parses a string which contains the number of projects in each variance category
     */
    @Override
    public void addData(String dataStr) {
        String[] dataItems = dataStr.split(DELIMITER);

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            int numItems = 0;
            if (dataItems[i].length() > 0)
                numItems = Integer.parseInt(dataItems[i]);
            mDataSet[i] = mDataSet[i] + numItems;
        }

        calcPercs();
    }


    public float getPerc(int ndx) {
        return mPercSet[ndx];
    }


    /**
     * Calculates the percent of projects for each variance category based on the number of projects for each category.
     */
    private void calcPercs() {
        float total = (float) getTotal();

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            mPercSet[i] = mDataSet[i] / total;
        }
    }


    /**
     * Returns the total number of projects for all variance categories.
     */
    @Override
    public int getTotal() {
        int total = 0;

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            total += mDataSet[i];
        }

        return total;
    }


    /**
     * Parses a string which contains a list of agency codes for each variance category.
     */
    @Override
    public void addAgencies(String agenciesStr) {
        String[] agenciesItems = agenciesStr.split(CATEG_DELIMITER);

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            String agcList = agenciesItems[i];
            String[] agcs = agcList.split( DELIMITER);

            for (int j = 0; j < agcs.length; j++) {
                mAgencies[i].add(agcs[j]);
            }
        }
    }


    /**
     * Returns a list of agencies for a variance category.
     */
    public List<String> getAgencies(int ndx) {
        return mAgencies[ndx];
    }

}
