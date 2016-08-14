/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the returned data from the API call to retrieve data for IT projects
 */
public class ProjectsApiRes {

    private List<ProjectInfo> items = new ArrayList<ProjectInfo>();

    public List<ProjectInfo> getItems() {
        return items;
    }

    public void setItems(List<ProjectInfo> data) {
        this.items = data;
    }

}
