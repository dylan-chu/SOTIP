/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the returned data from the API call to retrieve data for IT investments
 */
public class InvestmentsApiRes {

    private List<InvestmentInfo> items = new ArrayList<InvestmentInfo>();

    public List<InvestmentInfo> getItems() {
        return items;
    }

    public void setItems(List<InvestmentInfo> data) {
        this.items = data;
    }

}
