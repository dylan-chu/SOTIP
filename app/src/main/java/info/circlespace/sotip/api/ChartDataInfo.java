/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

/**
 * This class holds one item in the list of returned results for charts
 */
public class ChartDataInfo {

    // the variable names correspond exactly to the field names returned by the API
    private int type;                   // chart type
    private String data;                // chart data
    private String agcs;                // agencies associated with each data subset


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getAgcs() {
        return agcs;
    }

    public void setAgcs(String agcs) {
        this.agcs = agcs;
    }

}
