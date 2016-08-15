/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

/**
 * This class holds the data necessary to populate the grid showing
 * the number of projects for each subset.
 */
public class SubsetData {

    private int mNdx;
    private int mCount;
    private String mLabel;


    public SubsetData(int ndx, int count, String label) {
        mNdx = ndx;
        mCount = count;
        mLabel = label;
    }


    public int getNdx() {
        return mNdx;
    }


    public int getCount() {
        return mCount;
    }


    public String getLabel() {
        return mLabel;
    }

}
