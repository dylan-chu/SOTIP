/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;


/**
 * This class holds one item in the list of returned results for IT projects
 */
public class ProjectInfo {


    // the variable names correspond exactly to the field names returned by the API
    private int id;                     // project id
    private String uii;                 // unique investment id
    private String it;                  // investment title
    private String ac;                  // agency code
    private String name;                // project name
    private String obj;                 // project objectives
    private int pp;                     // project performance
    private int ps;                     // project status
    private String sd;                  // start date
    private String cd;                  // completion date
    private int sv;                     // schedule variance
    private int svd;                    // schedule variance in days
    private double svp;                 // schedule variance in percent
    private double lcc;                 // lifecycle cost
    private int cv;                     // cost variance
    private double cvd;                 // cost variance in dollars
    private double cvp;                 // cost variance in percent
    private int pm;                     // PM experience level
    private int sdm;                    // software development methodology (SDM)
    private String osdm;                // other SDM
    private String ud;                  // updated date


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }


    public String getAc() {
        return ac;
    }

    public void setAc(String code) {
        this.ac = code;
    }


    public String getUii() {
        return uii;
    }

    public void setUii(String id) {
        this.uii = id;
    }


    public String getIt() {
        return it;
    }

    public void setIt(String title) {
        this.it = title;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getObj() {
        return obj;
    }

    public void setObj(String objectives) {
        this.obj = objectives;
    }


    public int getPp() {
        return pp;
    }

    public void setPp(int perfCode) {
        this.pp = perfCode;
    }


    public int getPs() {
        return ps;
    }

    public void setPs(int statusCode) {
        this.ps = statusCode;
    }


    public String getSd() {
        return sd;
    }

    public void setSd(String date) {
        this.sd = date;
    }


    public String getCd() {
        return cd;
    }

    public void setCd(String date) {
        this.cd = date;
    }


    public int getSv() {
        return sv;
    }

    public void setSv(int varCode) {
        this.sv = varCode;
    }


    public int getSvd() {
        return svd;
    }

    public void setSvd(int days) {
        this.svd = days;
    }


    public double getSvp() {
        return svp;
    }

    public void setSvp(double percent) {
        this.svp = percent;
    }


    public double getLcc() {
        return lcc;
    }

    public void setLcc(double cost) {
        this.lcc = cost;
    }


    public int getCv() {
        return cv;
    }

    public void setCv(int varCode) {
        this.cv = varCode;
    }


    public double getCvd() {
        return cvd;
    }

    public void setCvd(double amount) {
        this.cvd = amount;
    }


    public double getCvp() {
        return cvp;
    }

    public void setCvp(double percent) {
        this.cvp = percent;
    }


    public int getPm() {
        return pm;
    }

    public void setPm(int expLvlCode) {
        this.pm = expLvlCode;
    }


    public int getSdm() {
        return sdm;
    }

    public void setSdm(int methodCode) {
        this.sdm = methodCode;
    }


    public String getOsdm() {
        return osdm;
    }

    public void setOsdm(String methodology) {
        this.osdm = methodology;
    }


    public String getUd() {
        return ud;
    }

    public void setUd(String date) {
        this.ud = date;
    }


/*
    public static int getProjStatusCode( String status ) {
        int code = UNKNOWN_CODE;

        if ( status.equals( PROJ_STATUS_COMPLTD )) {
            return CODE_PROJ_STATUS_COMPLTD;
        } else if ( status.equals( PROJ_STATUS_IN_PROG )) {
            return CODE_PROJ_STATUS_IN_PROG;
        }

        return code;
    }


    public static int getCode( String codeStr ) {
        int code = UNKNOWN_CODE;

        if ( codeStr == null ) {
            return code;
        }

        if ( codeStr.length() == 0 ) {
            return code;
        }

        code = Integer.parseInt(codeStr.substring(0, 1));
        return code;
    }


    public static boolean getFlag( String flag ) {
        boolean isActive = false;

        if ( flag == null ) {
            return isActive;
        }

        if ( flag.equals( YES ) ) {
            return true;
        }

        return isActive;
    }
*/

}
