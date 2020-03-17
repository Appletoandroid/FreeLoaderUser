package com.Appleto.FreeloaderUser.retrofit2;


import com.Appleto.FreeloaderUser.model.LoginResponse;
import com.Appleto.FreeloaderUser.model.UserRideDetailResponse;
import com.google.gson.JsonObject;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by krypton on 12/15/2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Single<LoginResponse> login(@Field("password") String password,
                                @Field("email") String email,
                                @Field("fcm_token") String fcm_token,
                                @Field("user_type") String usertype,
                                @Field("login_type") String login_type,
                                @Field("social_id") String social_id);

    @FormUrlEncoded
    @POST("register")
    Single<JsonObject> register(@Field("first_name") String first_name,
                                    @Field("last_name") String last_name,
                                    @Field("password") String password,
                                    @Field("email") String email,
                                    @Field("user_type") String user_type,
                                    @Field("phone") String phone,
                                    @Field("fcm_token") String fcm_token
    );

    @FormUrlEncoded
    @POST("add_ride_request")
    Single<JsonObject> add_ride_request(@Field("user_id") String user_id,
                                @Field("source_address") String source_address,
                                @Field("destination_address") String destination_address,
                                @Field("source_lat") String source_lat,
                                @Field("source_long") String source_long,
                                @Field("destination_lat") String destination_lat,
                                @Field("destination_long") String destination_long,
                                @Field("riders") String riders
    );

    /*@FormUrlEncoded
    @POST("get_ride_for_user")
    Single<UserDetailResonnse> get_ride_for_user(@Field("user_id") String user_id);*/


    @FormUrlEncoded
    @POST("user_cancle_ride")
    Single<JsonObject> user_cancle_ride(@Field("ride_request_id") String ride_request_id);

    @FormUrlEncoded
    @POST("user_get_ride_byrequestid")
    Single<UserRideDetailResponse> user_get_ride_byrequestid(@Field("ride_request_id") String ride_request_id);

    @FormUrlEncoded
    @POST("change_password")
    Single<JsonObject> change_password(@Field("user_id") String user_id,
                                       @Field("old_password") String old_password,
                                       @Field("new_password") String new_password,
                                       @Field("conf_password") String conf_password);

    @FormUrlEncoded
    @POST("setting")
    Single<JsonObject> setting(@Field("type") String type);

}
