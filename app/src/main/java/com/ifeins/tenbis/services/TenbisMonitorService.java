package com.ifeins.tenbis.services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mUsersService = retrofit.create(UsersService.class);
    }

    public UsersService getUsersService() {
        return mUsersService;
    }
}
