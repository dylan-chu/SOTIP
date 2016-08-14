/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.api;

import info.circlespace.sotip.SotipApp;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * This class defines all the interfaces used by Retrofit to make the API calls.
 */
public class ItDashboardApiSvc {
    public static final String BASE_URL = "https://rejlx3kywh.execute-api.us-east-1.amazonaws.com/beta/";
    public static final String PROJECTS_URL = "projects/{date}";
    public static final String CHARTS_URL = "charts/{date}";
    public static final String INVESTMENTS_URL = "investments/{date}";
    public static final String AGENCIES_URL = "agencies/{date}";


    public interface ProjectsApi {
        @GET(PROJECTS_URL)
        Call<ProjectsApiRes> getData(
                @Path(SotipApp.PARAM_DATE) String date
        );
    }


    public interface ChartsApi {
        @GET(CHARTS_URL)
        Call<ChartDataApiRes> getData(
                @Path(SotipApp.PARAM_DATE) String date
        );
    }


    public interface InvestmentsApi {
        @GET(INVESTMENTS_URL)
        Call<InvestmentsApiRes> getData(
                @Path(SotipApp.PARAM_DATE) String date
        );
    }


    public interface AgenciesApi {
        @GET(AGENCIES_URL)
        Call<AgenciesApiRes> getData(
                @Path(SotipApp.PARAM_DATE) String date
        );
    }

}
