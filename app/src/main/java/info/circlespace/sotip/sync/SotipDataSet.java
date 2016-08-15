/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

/**
 * Base class for holding data for the charts.
 */
public interface SotipDataSet {

    public String getDataAsStr();

    public void addData(String dataStr);

    public int getTotal();

    public void addAgencies(String agcsStr);

}
