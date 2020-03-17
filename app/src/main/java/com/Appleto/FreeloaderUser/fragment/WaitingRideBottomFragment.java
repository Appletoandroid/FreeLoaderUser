package com.Appleto.FreeloaderUser.fragment;

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

public class WaitingRideBottomFragment extends BottomSheetDialogFragment {

    Button btnCancelTrip;
    TextView tvDriverAddress;
    PreferenceApp preferenceApp;
    String driver_location;

    public static WaitingRideBottomFragment getInstance() {
        return new WaitingRideBottomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.waiting_ride_bottom_fragment, container, false);

        tvDriverAddress = view.findViewById(R.id.tv_driver_address);
        btnCancelTrip = view.findViewById(R.id.btn_cancel_trip);

        preferenceApp = new PreferenceApp(getActivity());
        getDialog().setCanceledOnTouchOutside(false);

        driver_location = preferenceApp.getValue(Constants.pickup_location);
        tvDriverAddress.setText(driver_location);

        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.progress_show(getActivity());

                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                apiService
                        .user_cancle_ride(preferenceApp.getValue(Constants.user_ride_request_id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<JsonObject>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(JsonObject response) {
                                Common.progress_dismiss(getActivity());
                                Toast.makeText(getActivity(), response.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                                Fragment fragment = getFragmentManager().findFragmentByTag("WaitingRideBottomFragment");
                                if(fragment instanceof WaitingRideBottomFragment){
                                    getDialog().dismiss();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Common.progress_dismiss(getActivity());
                            }
                        });
            }
        });
        return view;
    }
}