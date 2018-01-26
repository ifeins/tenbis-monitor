package com.ifeins.tenbis.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ifeins
 */

public class TenbisMonitorService {

    private static final String BASE_URL = "https://us-central1-tenbis-monitor.cloudfunctions.net";

    private static TenbisMonitorService sInstance = new TenbisMonitorService();

    private UsersService mUsersService;

    public static TenbisMonitorService getInstance() {
        return sInstance;
    }

    private TenbisMonitorService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mUsersService = retrofit.create(UsersService.class);
    }

    public UsersService getUsersService() {
        return mUsersService;
    }
}
