package com.dev.mobile.vpn.ui.auth.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.mobile.vpn.App;
import com.dev.mobile.vpn.R;
import com.dev.mobile.vpn.data.local.UserDao;
import com.dev.mobile.vpn.data.model.UserResponse;
import com.dev.mobile.vpn.http.InternetConnectionListener;
import com.dev.mobile.vpn.ui.auth.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class SignupActivity extends AppCompatActivity implements InternetConnectionListener {

    private CompositeDisposable mCompositeDisposable;


    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        ButterKnife.bind(this);

        ((App) getApplication()).setInternetConnectionListener(this);
        mCompositeDisposable = new CompositeDisposable();


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        ((App) getApplication()).getApiService().createUSer(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<UserResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<UserResponse> userResponseResponse) {
                       onSignupSuccess(userResponseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Errror ==================>", e.getMessage());
                        onSignupFailed();
                    }

                    @Override
                    public void onComplete() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onSignupSuccess(Response<UserResponse> userResponseResponse) {
        _signupButton.setEnabled(true);
        int code =  userResponseResponse.code();

        if (userResponseResponse.body() != null && code == 201) {
            UserDao.getInstance().saveUser(userResponseResponse.body());
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        }else if (code == 404) {
            Toast.makeText(getApplicationContext(),"Email already exists", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(),"Error ", Toast.LENGTH_LONG).show();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    @Override
    public void onInternetUnavailable() {

        Toast.makeText(getBaseContext(), "Connection not found", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        ((App) getApplication()).removeInternetConnectionListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
}
