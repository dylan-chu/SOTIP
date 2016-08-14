/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the returned data from the API call to retrieve data for agencies
 */
public class AgenciesApiRes {

    private List<AgencyInfo> items = new ArrayList<AgencyInfo>();

    public List<AgencyInfo> getItems() {
        return items;
    }

    public void setItems(List<AgencyInfo> data) {
        this.items = data;
    }

}
