/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

import java.util.ArrayList;
import java.util.List;


public class VarianceDataSet implements SotipDataSet {

    public static final String CATEG_DELIMITER = "-";
    public static final String DELIMITER = ",";

    public static final int NUM_CATEGORIES = 6;

    private int[] mDataSet;
    private float[] mPercSet;
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


    private void calcPercs() {
        float total = (float) getTotal();

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            mPercSet[i] = mDataSet[i] / total;
        }

    }


    @Override
    public int getTotal() {
        int total = 0;

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            total += mDataSet[i];
        }

        return total;
    }


    @Override
    public void addAgencies(String agenciesStr) {
        String[] agenciesItems = agenciesStr.split(CATEG_DELIMITER);

        for (int i = 0; i < NUM_CATEGORIES; i++) {
            String agcList = agenciesItems[i];
            String[] agcs = agcList.split(",");
            for (int j = 0; j < agcs.length; j++) {
                mAgencies[i].add(agcs[j]);
            }
        }
    }


    public List<String> getAgencies(int ndx) {
        return mAgencies[ndx];
    }

}
