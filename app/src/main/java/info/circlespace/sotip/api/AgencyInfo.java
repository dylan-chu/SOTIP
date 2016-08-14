/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

/**
 * This class holds one item in the list of returned results for agencies
 */
public class AgencyInfo {

    // note: the variable names correspond exactly to the field names returned by the API
    private String ac;                      // agency code
    private String nm;                      // agency name
    private String mu;                      // main URL
    private String ec;                      // electronic contact
    private String pn;                      // phone number
    private String ma;                      // mailing address
    private double lat;                     // latitude
    private double lng;                     // longitude
    private String ud;                      // updated date


    public String getAc() {
        return ac;
    }

    public void setAc(String code) {
        this.ac = code;
    }


    public String getNm() {
        return nm;
    }

    public void setNm(String name) {
        this.nm = name;
    }


    public String getMu() {
        return mu;
    }

    public void setMu(String url) {
        this.mu = url;
    }


    public String getEc() {
        return ec;
    }

    public void setEc(String contact) {
        this.ec = contact;
    }


    public String getPn() {
        return pn;
    }

    public void setPn(String nbr) {
        this.pn = nbr;
    }


    public String getMa() {
        return ma;
    }

    public void setMa(String addr) {
        this.ma = addr;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }


    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    public String getUd() {
        return ud;
    }

    public void setUd(String date) {
        this.ud = ud;
    }

}
