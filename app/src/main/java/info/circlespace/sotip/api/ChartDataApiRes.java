/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the returned data from the API call to retrieve data for charts
 */
public class ChartDataApiRes {

    private List<ChartDataInfo> items = new ArrayList<ChartDataInfo>();

    public List<ChartDataInfo> getItems() {
        return items;
    }

    public void setItems(List<ChartDataInfo> data) {
        this.items = data;
    }

}
