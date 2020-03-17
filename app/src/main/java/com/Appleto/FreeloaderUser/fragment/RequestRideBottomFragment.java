package com.Appleto.FreeloaderUser.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Constants;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RequestRideBottomFragment extends BottomSheetDialogFragment {

    //ImageView ivClose;
    Button btnReqRide, btnCancelRide;
    TextView tvOne, tvTwo, tvThree, tvFour, tvFive;
    TextView[] tvSelectedArray = new TextView[]{};
    TextView[] tvUnselectedArray = new TextView[]{};
    PreferenceApp prefs;

    public static RequestRideBottomFragment getInstance() {
        return new RequestRideBottomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.temp_request_ride_bottom_fragment, container, false);

        //ivClose = view.findViewById(R.id.iv_sheet_close);
        tvOne = view.findViewById(R.id.tv_one);
        tvTwo = view.findViewById(R.id.tv_two);
        tvThree = view.findViewById(R.id.tv_three);
        tvFour = view.findViewById(R.id.tv_four);
        tvFive = view.findViewById(R.id.tv_five);
        btnReqRide = view.findViewById(R.id.btn_user_request_trip);
        btnCancelRide = view.findViewById(R.id.btn_user_cancel_trip);

        prefs = new PreferenceApp(getActivity());
        getDialog().setCanceledOnTouchOutside(false);

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                prefs.setBooleanValue(Constants.userRequestForRide, false);
            }
        });

        btnReqRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvSelectedArray.length > 0) {

                    final String source_add = prefs.getValue(Constants.pickup_location);
                    final String source_lat = prefs.getValue(Constants.pickup_location_lat);
                    final String source_long = prefs.getValue(Constants.pickup_location_long);

                    final String dest_add = prefs.getValue(Constants.drop_location);
                    final String dest_lat = prefs.getValue(Constants.drop_location_lat);
                    final String dest_long = prefs.getValue(Constants.drop_location_long);

                    getDialog().setCanceledOnTouchOutside(false);

                    final Dialog dialogView = new Dialog(getActivity());
                    dialogView.setContentView(R.layout.request_ride_dialog);
                    dialogView.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogView.setCanceledOnTouchOutside(false);

                    TextView tvRideDesc = dialogView.findViewById(R.id.tv_ride_desc);
                    tvRideDesc.setText("Pickup " + tvSelectedArray.length + " Riders at " + source_add);

                    Button btnRequest = dialogView.findViewById(R.id.btn_request_ride);
                    btnRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                            apiService.
                                    add_ride_request(prefs.getValue(Constants.user_id), source_add, dest_add, source_lat, source_long,
                                            dest_lat, dest_long, String.valueOf(tvSelectedArray.length))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<JsonObject>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(JsonObject jsonObject) {
                                            try {
                                                if (jsonObject.get("status").getAsInt() == 1) {
                                                    JsonObject jsonData = jsonObject.get("data").getAsJsonObject();
                                                    prefs.setValue(Constants.user_ride_request_id, jsonData.get("ride_request_id").getAsString());
                                                    prefs.setValue(Constants.driver_status, jsonData.get("status").getAsString());

                                                    prefs.setValue(Constants.pickup_location_lat, jsonData.get("source_lat").getAsString());
                                                    prefs.setValue(Constants.pickup_location_long, jsonData.get("source_long").getAsString());
                                                    prefs.setValue(Constants.pickup_location, jsonData.get("source_address").getAsString());

                                                    prefs.setValue(Constants.drop_location_lat, jsonData.get("destination_lat").getAsString());
                                                    prefs.setValue(Constants.drop_location_long, jsonData.get("destination_long").getAsString());
                                                    prefs.setValue(Constants.drop_location, jsonData.get("destination_address").getAsString());

                                                    prefs.setBooleanValue(Constants.userRequestForRide, true);
                                                } else {
                                                    Toast.makeText(getActivity(), getResources().getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            prefs.setBooleanValue(Constants.userRequestForRide, false);
                                        }
                                    });
                            dialogView.dismiss();
                        }
                    });

                    Button btnCancel = dialogView.findViewById(R.id.btn_cancel_ride);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogView.dismiss();
                            prefs.setBooleanValue(Constants.userRequestForRide, false);
                        }
                    });
                    dialogView.show();
                } else {
                    Toast.makeText(getActivity(), "Please Select No. of Ride!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedArray = new TextView[]{tvOne};
                tvUnselectedArray = new TextView[]{tvTwo, tvThree, tvFour, tvFive};
                totalRide(tvSelectedArray, tvUnselectedArray);
            }
        });

        tvTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedArray = new TextView[]{tvOne, tvTwo};
                tvUnselectedArray = new TextView[]{tvThree, tvFour, tvFive};
                totalRide(tvSelectedArray, tvUnselectedArray);
            }
        });

        tvThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree};
                tvUnselectedArray = new TextView[]{tvFour, tvFive};
                totalRide(tvSelectedArray, tvUnselectedArray);
            }
        });

        tvFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree, tvFour};
                tvUnselectedArray = new TextView[]{tvFive};
                totalRide(tvSelectedArray, tvUnselectedArray);
            }
        });

        tvFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree, tvFour, tvFive};
                tvUnselectedArray = new TextView[]{};
                totalRide(tvSelectedArray, tvUnselectedArray);
            }
        });

        return view;
    }

    private void totalRide(TextView[] tvSelected, TextView[] tvUnselected) {
        if (tvSelected.length > 0) {
            for (int i = 0; i < tvSelected.length; i++) {
                tvSelected[i].setBackground(getResources().getDrawable(R.drawable.bg_dark_circle));
                tvSelected[i].setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        if (tvUnselected.length > 0) {
            for (int i = 0; i < tvUnselected.length; i++) {
                tvUnselected[i].setBackground(getResources().getDrawable(R.drawable.bg_white_circle));
                tvUnselected[i].setTextColor(Color.parseColor("#000000"));
            }
        }
    }

    private LatLng getLatLongFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }
}
