/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import java.util.List;

/**
 * This class holds one item in the list of returned results for IT investments
 */
public class InvestmentInfo {

    public static final String NOT_AVAILABLE = "n/a";
    public static final String DELIMITER = ";";

    // note: the variable names correspond exactly to the field names returned by the API
    private String uii;                     // unique investment id
    private String ac;                      // agency code
    private String it;                      // investment title
    private String sum;                     // summary of investment
    private int cio;                        // CIO rating
    private int np;                         // number of projects
    private double lcc;                     // lifecycle cost
    private List<String> c;                 // list of contractors
    private List<String> ct;                // list of contract types
    private List<String> urls;              // list of related urls
    private String ud;                      // updated date


    public String getUii() {
        return uii;
    }

    public void setUii(String id) {
        this.uii = id;
    }


    public String getAc() {
        return ac;
    }

    public void setAc(String code) {
        this.ac = code;
    }


    public String getIt() {
        return it;
    }

    public void setIt(String title) {
        this.it = title;
    }


    public String getSum() {
        return sum;
    }

    public void setSum(String summary) {
        this.sum = summary;
    }


    public int getCio() {
        return cio;
    }

    public void setCio(int rating) {
        cio = rating;
    }


    public int getNp() {
        return np;
    }

    public void setNp(int num) {
        np = num;
    }


    public double getLcc() {
        return lcc;
    }

    public void setLcc(double cost) {
        lcc = cost;
    }


    public List<String> getC() {
        return c;
    }

    public void setC(List<String> contractors) {
        this.c = contractors;
    }

    public String getContractorsAsStr() {
        StringBuffer buf = new StringBuffer();
        int count = 0;

        for (String contractor : c) {
            if (contractor.equals(NOT_AVAILABLE))
                continue;

            if (count > 0)
                buf.append(DELIMITER);

            buf.append(contractor);
            count++;
        }

        return buf.toString();
    }


    public List<String> getCt() {
        return ct;
    }

    public void setCt(List<String> contractTypes) {
        this.ct = contractTypes;
    }

    public String getContractTypesAsStr() {
        StringBuffer buf = new StringBuffer();
        int count = 0;

        for (String contractType : ct) {
            if (contractType.equals(NOT_AVAILABLE))
                continue;

            if (count > 0)
                buf.append(DELIMITER);

            buf.append(contractType);
            count++;
        }

        return buf.toString();
    }


    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }


    public String getUd() {
        return ud;
    }

    public void setUd(String date) {
        this.ud = ud;
    }

}

