package com.dev.mobile.vpn.http;

import com.dev.mobile.vpn.data.remote.ApiService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    private ApiService apiService;
    private Session session;

    public AuthorizationInterceptor(ApiService apiService, Session session) {
        this.apiService = apiService;
        this.session = session;
    }

    public AuthorizationInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        Request mainRequest = chain.request();

/*        if (session.isLoggedIn()) {
            // if response code is 401 or 403, 'mainRequest' has encountered authentication error
            if (mainResponse.code() == 401 || mainResponse.code() == 403) {
                UserResponse userResponse = session.getUser();
                String refresh_token = getAuthorizationHeader(userResponse.getRefresh_token());
                // request to login API to get fresh token
                // synchronously calling login API
                retrofit2.Response<UserResponse> loginResponse = apiService.sessionExpired(refresh_token).execute();
                if (loginResponse.isSuccessful()) {
                    // login request succeed, new token generated
                    UserResponse authorization = loginResponse.body();
                    // save the new token
                    session.saveUser(authorization);
                    // retry the 'mainRequest' which encountered an authentication error
                    // add new token into 'mainRequest' header and request again
                    Request.Builder builder = mainRequest.newBuilder().header("Authorization", session.getToken()).
                            method(mainRequest.method(), mainRequest.body());
                    mainResponse = chain.proceed(builder.build());
                }
            }
        }*/

        return mainResponse;
    }


}
