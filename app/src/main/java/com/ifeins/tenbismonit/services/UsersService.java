package com.ifeins.tenbismonit.services;

import com.ifeins.tenbismonit.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author ifeins
 */

public interface UsersService {

    @POST("createUser")
    Call<Void> create(@Body User user);

    @POST("refreshUserData")
    Call<Void> refresh(@Body User user);
}
