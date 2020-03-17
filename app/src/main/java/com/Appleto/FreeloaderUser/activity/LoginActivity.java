package com.Appleto.FreeloaderUser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Common;
import com.Appleto.FreeloaderUser.Utils.Constants;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.model.LoginResponse;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.facebook.FacebookSdk;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btnLogin, btnBack;
    EditText ed_email, ed_password;
    PreferenceApp preferenceApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());

        ed_password = findViewById(R.id.login_edt_password);
        ed_email = findViewById(R.id.login_edt_email);

        btnLogin = findViewById(R.id.login_iv_signin);
        btnBack = findViewById(R.id.login_iv_back);

        btnBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        preferenceApp = new PreferenceApp(this);
        ed_email.setText("user@gmail.com");
        ed_password.setText("123456");

    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            onBackPressed();

        } else if (v == btnLogin) {
            if (!Common.isEmptyEditText(ed_email, "Please enter email") &&
                    !Common.isEmptyEditText(ed_password, "Please enter password")) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                    String newToken = instanceIdResult.getToken();
                    new PreferenceApp(getApplicationContext()).setValue(Constants.fcm_token, newToken);
                    if (preferenceApp.getValue(Constants.fcm_token).equals("")) {
                        Toast.makeText(LoginActivity.this, "FCM Token Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Common.progress_show(this);
                        String login_type = "";
                        String social_id = "";
                        String user_type = "customer";
                        String username = ed_email.getText().toString();
                        String password = ed_password.getText().toString();
                        login(password, username, preferenceApp.getValue(Constants.fcm_token), user_type, login_type, social_id);
                    }
                });
            }
        }
    }

    private void login(String password, String username, String token, String user_type, String login_type, String social_id) {

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
                        Common.progress_dismiss(LoginActivity.this);
                        try {
                            if (response.getStatus() == 1) {
                                Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                preferenceApp.setValue(Constants.user_id, response.getData().getUserId());
                                preferenceApp.setValue(Constants.user_name, response.getData().getFirstName() + " " + response.getData().getLastName());
                                preferenceApp.setValue(Constants.user_phone, response.getData().getPhone());
                                preferenceApp.setValue(Constants.user_login_status, "true");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(LoginActivity.this, StartActivity.class));
                                    }
                                }, 1800);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(LoginActivity.this);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this, StartActivity.class));
        finish();
    }
}
