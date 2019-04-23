package com.dev.mobile.vpn.data.remote;

import com.dev.mobile.vpn.data.model.CountryResponse;
import com.dev.mobile.vpn.data.model.UserResponse;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    String URL = "https://vpnapi.herokuapp.com";

    @POST("/users")
    @FormUrlEncoded
    Observable<Response<UserResponse>> createUSer(@Field("email") String userName,
                                        @Field("password") String password);


    @POST("/auth")
    @FormUrlEncoded
    Observable<Response<UserResponse>> loginUser(@Field("email") String userName,
                                                  @Field("password") String password);


    @POST("/refresh")
    @FormUrlEncoded
    Call<UserResponse> sessionExpired(@Field("email") String email, @Field("refresh_token") String refresh_token);


    @GET("/vpns/country")
    Observable<Response<List<CountryResponse>>> countries();



}
