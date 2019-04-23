package com.dev.mobile.vpn.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dev.mobile.vpn.App;
import com.dev.mobile.vpn.R;
import com.dev.mobile.vpn.data.local.CountryDao;
import com.dev.mobile.vpn.data.local.UserDao;
import com.dev.mobile.vpn.data.model.CountryResponse;
import com.dev.mobile.vpn.ui.auth.login.LoginActivity;
import com.dev.mobile.vpn.ui.main.MainActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (CountryDao.getInstance().getCountries() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        redirectToActivity();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Timber.e("exception", e.getMessage());
                    }

                }


            }, SPLASH_DISPLAY_LENGTH);
        } else {
            getListCounties();
        }
    }

    private void getListCounties() {
        ((App) getApplication()).getApiService().countries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<List<CountryResponse>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<List<CountryResponse>> listResponse) {
                        if (listResponse.isSuccessful() && listResponse.body() != null) {
                            List<CountryResponse> mList = listResponse.body();
                            CountryDao.getInstance().putCountries(mList);
                        }
                        redirectToActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Error ", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void redirectToActivity() {
        if (UserDao.getInstance().getUser() != null) {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(i);
            finish();

        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(i);
            finish();

        }
    }
}
