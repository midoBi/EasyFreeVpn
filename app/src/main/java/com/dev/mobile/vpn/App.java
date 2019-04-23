package com.dev.mobile.vpn;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dev.mobile.vpn.data.local.UserDao;
import com.dev.mobile.vpn.data.model.UserResponse;
import com.dev.mobile.vpn.data.remote.ApiService;
import com.dev.mobile.vpn.http.InternetConnectionListener;
import com.dev.mobile.vpn.http.NetworkConnectionInterceptor;
import com.dev.mobile.vpn.http.Session;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class App extends Application {

    private ApiService apiService;
    private InternetConnectionListener mInternetConnectionListener;
    private Session session;

    public Session getSession() {
        if (session == null) {
            session = new Session() {
                @Override
                public boolean isLoggedIn() {
                    return UserDao.getInstance().getUser() != null;
                }

                @Override
                public void saveUser(UserResponse userResponse) {
                    UserDao.getInstance().saveUser(userResponse);
                }

                @Override
                public UserResponse getUser() {
                    return UserDao.getInstance().getUser();
                }

                @Override
                public String getToken() {
                    if (this.isLoggedIn() && this.getUser() != null) {
                        return this.getUser().getToken();
                    }
                    return null;
                }

                @Override
                public void invalidate() {
                    UserDao.getInstance().dropUser();
                }
            };
        }
        return session;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Paper.init(this);

    }

    public void setInternetConnectionListener(InternetConnectionListener listener) {
        mInternetConnectionListener = listener;
    }

    public void removeInternetConnectionListener() {
        mInternetConnectionListener = null;
    }

    public ApiService getApiService() {
        if (apiService == null) {
            apiService = provideRetrofit(ApiService.URL).create(ApiService.class);
        }
        return apiService;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Retrofit provideRetrofit(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);

        okhttpClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return App.this.isInternetAvailable();
            }

            @Override
            public void onInternetUnavailable() {
                if (mInternetConnectionListener != null) {
                    mInternetConnectionListener.onInternetUnavailable();
                }
            }
        });
        okhttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response mainResponse = chain.proceed(chain.request());
                Request mainRequest = chain.request();
                Session session = getSession();
                ApiService apiService = getApiService();
                if (session.isLoggedIn()) {
                    // if response code is 401 or 403, 'mainRequest' has encountered authentication error
                    if (mainResponse.code() == 401 || mainResponse.code() == 403) {
                        UserResponse userResponse = session.getUser();
                        String refresh_token = userResponse.getRefresh_token();
                        String email = userResponse.getEmail();
                        // request to login API to get fresh token
                        // synchronously calling login API
                        retrofit2.Response<UserResponse> loginResponse = apiService.sessionExpired(email,refresh_token).execute();
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
                }


                return mainResponse;
            }
        });

        return okhttpClientBuilder.build();
    }
}
