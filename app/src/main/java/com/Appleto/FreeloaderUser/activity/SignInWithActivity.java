package com.Appleto.FreeloaderUser.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Common;
import com.Appleto.FreeloaderUser.Utils.Constants;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.model.LoginResponse;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignInWithActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    ImageView btnSignIn;
    ImageView btnSignUp;
    ImageView btnGPlusLogin;
    ImageView btnFbLogin;
    private PreferenceApp preferenceApp;
    private GoogleApiClient mGoogleApiClient;

    private static int GOOGLE_PLUS_LOGIN_CODE = 100;
    private static int FACEBOOK_LOGIN_CODE = 101;

    //private Facebook facebook;
    //private AsyncFacebookRunner mAsyncRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with);

        btnSignIn = findViewById(R.id.iv_signin_withlogin);
        btnSignUp = findViewById(R.id.iv_signup_withlogin);
        btnGPlusLogin = findViewById(R.id.iv_login_with_gplus);
        btnFbLogin = findViewById(R.id.iv_login_with_fb);

        btnSignIn.setOnClickListener(this);
        btnFbLogin.setOnClickListener(this);
        btnGPlusLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        preferenceApp = new PreferenceApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSignIn) {
            startActivity(new Intent(SignInWithActivity.this, LoginActivity.class));
        } else if (v == btnGPlusLogin) {
            loginWithGooglePlus();
        } else if (v == btnFbLogin) {
            loginWithFb();
        } else if (v == btnSignUp) {
            startActivity(new Intent(SignInWithActivity.this, SignUpActivity.class));
        }
    }

    private void loginWithGooglePlus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_PLUS_LOGIN_CODE);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        try {
            if (result.isSuccess()) {
                GoogleSignInAccount googleAccDetail = result.getSignInAccount();

                String personName = googleAccDetail.getDisplayName();
                preferenceApp.setValue(Constants.user_name, personName);
                String socialId = googleAccDetail.getId();
                String socialName = "google";
                String user_type = "customer";
                String token = preferenceApp.getValue(Constants.fcm_token);
                String email = googleAccDetail.getEmail();

                String personPhotoUrl = googleAccDetail.getPhotoUrl().toString();
                login("", email, token, user_type, socialName, socialId);
            } else {
                Toast.makeText(this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void login(String password, String username, String token, String user_type, String login_type, String social_id){
        Common.progress_show(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        apiService
                .login(password, username, token, user_type, login_type, social_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(LoginResponse response) {
                        Common.progress_dismiss(SignInWithActivity.this);
                        try{
                            if (response.getStatus() == 1){
                                Toast.makeText(SignInWithActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                preferenceApp.setValue(Constants.user_id, response.getData().getUserId());
                                preferenceApp.setValue(Constants.user_phone, response.getData().getPhone());
                                preferenceApp.setValue(Constants.user_name, response.getData().getEmail());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SignInWithActivity.this, StartActivity.class));
                                    }
                                }, 1800);
                            } else {
                                Toast.makeText(SignInWithActivity.this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(SignInWithActivity.this);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_PLUS_LOGIN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void loginWithFb() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
