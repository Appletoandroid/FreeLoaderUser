package com.Appleto.FreeloaderUser.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Common;
import com.Appleto.FreeloaderUser.Utils.Constants;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.google.gson.JsonObject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtFname, edtLname, edtEmail, edtPassword, edtPhoneNo;
    ImageView btnSignup, btnback;
    PreferenceApp preferenceApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFname = findViewById(R.id.signup_ed_first_name);
        edtLname = findViewById(R.id.signup_ed_last_name);
        edtEmail = findViewById(R.id.signup_ed_email);
        edtPhoneNo = findViewById(R.id.signup_ed_phone_no);
        edtPassword = findViewById(R.id.signup_ed_password);

        btnSignup = findViewById(R.id.signup_iv_register);
        btnback = findViewById(R.id.signup_iv_back);

        btnSignup.setOnClickListener(this);
        btnback.setOnClickListener(this);

        preferenceApp = new PreferenceApp(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSignup){
            if (!Common.isEmptyEditText(edtFname,"Please enter first name") &&
                    !Common.isEmptyEditText(edtLname,"Please enter last name") &&
                    !Common.isEmptyEditText(edtEmail,"Please enter email") &&
                    !Common.isEmptyEditText(edtPhoneNo,"Please enter phone no") &&
                    !Common.isEmptyEditText(edtPassword,"Please enter password")){

                if(preferenceApp.getValue(Constants.fcm_token).equals("")){
                    Toast.makeText(SignUpActivity.this, "FCM Token Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    String fname = edtFname.getText().toString();
                    String lname = edtLname.getText().toString();
                    String email = edtEmail.getText().toString();
                    String phone = edtPhoneNo.getText().toString();
                    String password = edtPassword.getText().toString();

                    register(fname, lname, email, phone, password, preferenceApp.getValue(Constants.fcm_token));
                }
            }
        } else if(v == btnback){
            onBackPressed();
        }
    }

    private void register(String fname, String lname, String email, String phone, String password, String token){
        Common.progress_show(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        apiService
                .register(fname, lname, password, email, "customer", phone, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Common.progress_dismiss(SignUpActivity.this);
                        try{
                            if (jsonObject.get("status").getAsString().equals("1")){
                                Toast.makeText(SignUpActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                }, 1800);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(SignUpActivity.this);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, SignInWithActivity.class));
        finish();
    }
}
