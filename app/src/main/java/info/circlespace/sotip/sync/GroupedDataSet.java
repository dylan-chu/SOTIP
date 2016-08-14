/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;


public class GroupedDataSet implements SotipDataSet {

    public static final String CATEG_DELIMITER = ":";
    public static final String DELIMITER = "-";

    private int mNumSubsets;
    private PerformanceDataSet[] mDataSets;

    public GroupedDataSet(int numSubsets) {
        mNumSubsets = numSubsets;
        mDataSets = new PerformanceDataSet[mNumSubsets];
        for (int i = 0; i < mDataSets.length; i++) {
            mDataSets[i] = new PerformanceDataSet();
        }
    }


    public PerformanceDataSet[] getDataSets() {
        return mDataSets;
    }


    @Override
    public String getDataAsStr() {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < mNumSubsets; i++) {
            if (i > 0)
                buf.append(DELIMITER);
            buf.append(mDataSets[i].getDataAsStr());
        }

        return buf.toString();
    }


    @Override
    public void addData(String dataStr) {
        String[] dataSets = dataStr.split(DELIMITER);

        for (int i = 0; i < mNumSubsets; i++) {
            mDataSets[i].addData(dataSets[i]);
        }
    }


    @Override
    public int getTotal() {
        int total = 0;

        for (int i = 0; i < mNumSubsets; i++) {
            total += mDataSets[i].getTotal();
        }

        return total;
    }


    public int getTotal(int ndx) {
        return mDataSets[ndx].getTotal();
    }


    @Override
    public void addAgencies(String agenciesStr) {
        String[] agenciesItems = agenciesStr.split(CATEG_DELIMITER);

        for (int i = 0; i < mNumSubsets; i++) {
            String agcList = agenciesItems[i];
            mDataSets[i].addAgencies(agcList);
        }
    }
}
