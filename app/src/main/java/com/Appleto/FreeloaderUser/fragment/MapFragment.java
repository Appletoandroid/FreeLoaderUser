package com.Appleto.FreeloaderUser.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Common;
import com.Appleto.FreeloaderUser.Utils.Constants;
import com.Appleto.FreeloaderUser.Utils.MessageEvent;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.googleapi.FetchUrl;
import com.Appleto.FreeloaderUser.model.UserRideDetailResponse;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */

@SuppressLint("RestrictedApi")
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private PreferenceApp prefs;
    private Context mContext;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private FloatingActionButton flotingBtn;
    private TextView tvPickupPlace;
    private TextView tvDropPlace;

    private LinearLayout requestRideView;
    private LinearLayout waitStatusView;
    private LinearLayout acceptStatusView;

    private Button btnReqRideDialog;
    private Button btnCancelReqRideDialog;
    private Button btnCancelRideByUser;

    private TextView tvPickupLocation;
    private TextView tvDriverLocation;
    private TextView tvDriverName;

    GoogleApiClient googleApiClient;
    FusedLocationProviderClient fusedLocationClient;

    private Marker marker;
    private ArrayList<LatLng> markerPoint = new ArrayList<>();

    private TextView tvOne, tvTwo, tvThree, tvFour, tvFive;
    TextView[] tvSelectedArray = new TextView[]{};
    TextView[] tvUnselectedArray = new TextView[]{};
    private static final int EARTH_RADIUS = 6371;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        prefs = new PreferenceApp(mContext);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tvPickupPlace = v.findViewById(R.id.tv_map_pickup_place);
        tvPickupPlace.setTag(1);
        tvDropPlace = v.findViewById(R.id.tv_map_drop_place);
        tvDropPlace.setTag(2);

        requestRideView = v.findViewById(R.id.ll_request_ride_view);
        waitStatusView = v.findViewById(R.id.ll_request_ride_waiting_view);
        acceptStatusView = v.findViewById(R.id.ll_request_ride_accept_view);

        tvPickupLocation = v.findViewById(R.id.tv_request_ride_pickup_location);
        tvDriverLocation = v.findViewById(R.id.tv_accepted_driver_location);
        tvDriverName = v.findViewById(R.id.tv_accepted_driver_name);
        flotingBtn = v.findViewById(R.id.fab);

        tvOne = v.findViewById(R.id.tv_one);
        tvTwo = v.findViewById(R.id.tv_two);
        tvThree = v.findViewById(R.id.tv_three);
        tvFour = v.findViewById(R.id.tv_four);
        tvFive = v.findViewById(R.id.tv_five);

        btnReqRideDialog = v.findViewById(R.id.btn_user_request_trip);
        btnCancelReqRideDialog = v.findViewById(R.id.btn_user_cancel_trip);
        btnCancelRideByUser = v.findViewById(R.id.btn_request_ride_cancel);

        flotingBtn.setOnClickListener(this);
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvFive.setOnClickListener(this);
        btnReqRideDialog.setOnClickListener(this);
        btnCancelReqRideDialog.setOnClickListener(this);
        btnCancelRideByUser.setOnClickListener(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (marker != null) {
            marker.remove();
        }
        /*showMarkerPoint(location.getLatitude(), location.getLongitude());
        fetchMarkerLocation(marker, tvPickupPlace);*/

        setMarkerPoint(location.getLatitude(), location.getLongitude(), false, false);
        googleMap.addPolygon(createPolygonWithCircle(getActivity(), new LatLng(location.getLatitude(), location.getLongitude()), 1));
        //marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("My Title").snippet("My Snippet").icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_marker)));

        //stop location updates
        if (googleApiClient != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

        fetchDropLocation();
    }

    static PolygonOptions createPolygonWithCircle(Context context, LatLng center, int radius) {

        return new PolygonOptions()
                .fillColor(ContextCompat.getColor(context, R.color.grey_500_transparent))
                .addAll(createOuterBounds())
                .addHole(createHole(center, radius))
                .strokeWidth(0);
    }

    private static List<LatLng> createOuterBounds() {
        float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }

    private static Iterable<LatLng> createHole(LatLng center, int radius) {
        int points = 50; // number of corners of inscribed polygon

        double radiusLatitude = Math.toDegrees(radius / (float) EARTH_RADIUS);
        double radiusLongitude = radiusLatitude / Math.cos(Math.toRadians(center.latitude));

        List<LatLng> result = new ArrayList<>(points);

        double anglePerCircleRegion = 2 * Math.PI / points;

        for (int i = 0; i < points; i++) {
            double theta = i * anglePerCircleRegion;
            double latitude = center.latitude + (radiusLatitude * Math.sin(theta));
            double longitude = center.longitude + (radiusLongitude * Math.cos(theta));

            result.add(new LatLng(latitude, longitude));
        }

        return result;
    }

    private void fetchDropLocation() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                setMarkerPoint(point.latitude, point.longitude, false, true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == flotingBtn) {
            if (tvPickupPlace.getText().toString().equalsIgnoreCase("") ||
                    tvDropPlace.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(mContext, "Please Select Drop Location!!!", Toast.LENGTH_SHORT).show();
            } else {
                if (tvSelectedArray.length > 0) {
                    for (int i = 0; i < tvSelectedArray.length; i++) {
                        tvSelectedArray[i].setBackground(getResources().getDrawable(R.drawable.bg_dark_circle));
                        tvSelectedArray[i].setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }
                requestRideView.setVisibility(View.VISIBLE);
                waitStatusView.setVisibility(View.GONE);
                acceptStatusView.setVisibility(View.GONE);
                flotingBtn.setVisibility(View.GONE);
                resizeGoogleMap(getViewHeight(requestRideView));
            }
        } else if (v == tvOne) {
            tvSelectedArray = new TextView[]{tvOne};
            tvUnselectedArray = new TextView[]{tvTwo, tvThree, tvFour, tvFive};
            totalRide(tvSelectedArray, tvUnselectedArray);

        } else if (v == tvTwo) {
            tvSelectedArray = new TextView[]{tvOne, tvTwo};
            tvUnselectedArray = new TextView[]{tvThree, tvFour, tvFive};
            totalRide(tvSelectedArray, tvUnselectedArray);

        } else if (v == tvThree) {
            tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree};
            tvUnselectedArray = new TextView[]{tvFour, tvFive};
            totalRide(tvSelectedArray, tvUnselectedArray);

        } else if (v == tvFour) {
            tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree, tvFour};
            tvUnselectedArray = new TextView[]{tvFive};
            totalRide(tvSelectedArray, tvUnselectedArray);

        } else if (v == tvFive) {
            tvSelectedArray = new TextView[]{tvOne, tvTwo, tvThree, tvFour, tvFive};
            tvUnselectedArray = new TextView[]{};
            totalRide(tvSelectedArray, tvUnselectedArray);

        } else if (v == btnReqRideDialog) {
            userRequestForRide();

        } else if (v == btnCancelReqRideDialog) {
            prefs.setBooleanValue(Constants.userRequestForRide, false);
            requestRideView.setVisibility(View.GONE);
            waitStatusView.setVisibility(View.GONE);
            acceptStatusView.setVisibility(View.GONE);
            flotingBtn.setVisibility(View.VISIBLE);
            resizeGoogleMap(0);
        } else if (v == btnCancelRideByUser) {
            cancelRidebyUser();

        }
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

    private void userRequestForRide() {

        if (tvSelectedArray.length > 0) {
            final String source_add = prefs.getValue(Constants.pickup_location);
            final String source_lat = prefs.getValue(Constants.pickup_location_lat);
            final String source_long = prefs.getValue(Constants.pickup_location_long);

            final String dest_add = prefs.getValue(Constants.drop_location);
            final String dest_lat = prefs.getValue(Constants.drop_location_lat);
            final String dest_long = prefs.getValue(Constants.drop_location_long);

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

                                            tvPickupLocation.setText(prefs.getValue(Constants.pickup_location));
                                            prefs.setBooleanValue(Constants.userRequestForRide, true);
                                            waitStatusView.setVisibility(View.VISIBLE);
                                            requestRideView.setVisibility(View.GONE);
                                            acceptStatusView.setVisibility(View.GONE);
                                            flotingBtn.setVisibility(View.GONE);
                                            resizeGoogleMap(getViewHeight(waitStatusView));
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
                    requestRideView.setVisibility(View.VISIBLE);
                    waitStatusView.setVisibility(View.GONE);
                    acceptStatusView.setVisibility(View.GONE);
                    flotingBtn.setVisibility(View.GONE);
                    resizeGoogleMap(getViewHeight(requestRideView));
                }
            });
            dialogView.show();
        } else {
            Toast.makeText(getActivity(), "Please Select No. of Ride!!!", Toast.LENGTH_SHORT).show();
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_maps_api_key);
        return url;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onMessageEvent(MessageEvent event) {
        String rideStatus = event.message;

        if (rideStatus.equalsIgnoreCase("Your ride request rejected.")) {
            flotingBtn.setVisibility(View.VISIBLE);
            requestRideView.setVisibility(View.GONE);
            waitStatusView.setVisibility(View.GONE);
            acceptStatusView.setVisibility(View.GONE);
            resizeGoogleMap(0);

        } else if (rideStatus.equalsIgnoreCase("your ride request accepted.")) {
            acceptStatusView.setVisibility(View.VISIBLE);
            waitStatusView.setVisibility(View.GONE);
            requestRideView.setVisibility(View.GONE);
            flotingBtn.setVisibility(View.GONE);
            resizeGoogleMap(getViewHeight(acceptStatusView));
            checkRideStatus();

        } else if (rideStatus.equalsIgnoreCase("Thank You For This Ride.")) {

            final Dialog dialogView = new Dialog(getActivity());
            dialogView.setContentView(R.layout.finish_ride_dialog);
            dialogView.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogView.setCanceledOnTouchOutside(false);

            Button btnOk = dialogView.findViewById(R.id.btn_finish_ride);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogView.dismiss();
                    googleMap.clear();
                    markerPoint.clear();
                    double latitude = Double.parseDouble(prefs.getValue(Constants.drop_location_lat));
                    double longitude = Double.parseDouble(prefs.getValue(Constants.drop_location_long));
                    tvPickupPlace.setText("");
                    setMarkerPoint(latitude, longitude, false, false);

                    tvDropPlace.setText("");
                    flotingBtn.setVisibility(View.VISIBLE);
                    requestRideView.setVisibility(View.GONE);
                    waitStatusView.setVisibility(View.GONE);
                    acceptStatusView.setVisibility(View.GONE);
                    prefs.clearRideDetail();
                    resizeGoogleMap(0);
                    //googleMap.getUiSettings().setAllGesturesEnabled(true);
                }
            });
            dialogView.show();
        }

    }

    public void checkRideStatus() {
        Common.progress_show(getActivity());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        apiService
                .user_get_ride_byrequestid(prefs.getValue(Constants.user_ride_request_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserRideDetailResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(UserRideDetailResponse response) {
                        if (response.getStatus() == 1) {
                            UserRideDetailResponse.Datum rideModel;
                            if (response.getData().size() > 0) {
                                rideModel = response.getData().get(0);
                                prefs.setValue(Constants.user_ride_status, rideModel.getStatus());

                                prefs.setValue(Constants.pickup_location, rideModel.getSourceAddress());
                                prefs.setValue(Constants.drop_location, rideModel.getDestinationAddress());

                                prefs.setValue(Constants.driver_status, rideModel.getStatus());
                                prefs.setValue(Constants.driver_phone_no, rideModel.getDriverPhone());
                                prefs.setValue(Constants.driver_name, rideModel.getDriverName());

                                prefs.setValue(Constants.pickup_location_lat, rideModel.getSourceLat());
                                prefs.setValue(Constants.pickup_location_long, rideModel.getSourceLong());

                                if (!rideModel.getDriverLat().equals("") && !rideModel.getDriverLong().equalsIgnoreCase("")) {
                                    prefs.setValue(Constants.driver_location_lat, rideModel.getDriverLat());
                                    prefs.setValue(Constants.driver_location_long, rideModel.getDriverLong());

                                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                    try {
                                        List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(rideModel.getDriverLat()),
                                                Double.parseDouble(rideModel.getDriverLong()), 1);
                                        if (addressList != null && addressList.size() > 0) {
                                            Address address = addressList.get(0);
                                            String driverLoc = address.getAddressLine(0);
                                            prefs.setValue(Constants.driver_location, driverLoc);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    tvDriverName.setText(prefs.getValue(Constants.driver_name));
                                    tvDriverLocation.setText(prefs.getValue(Constants.driver_location));

                                    double latitude = Double.parseDouble(prefs.getValue(Constants.driver_location_lat));
                                    double longitude = Double.parseDouble(prefs.getValue(Constants.driver_location_long));
                                    markerPoint.add(new LatLng(latitude, longitude));
                                    setMarkerPoint(latitude, longitude, false, false);
                                    initroute();
                                }
                            }
                        }
                        Common.progress_dismiss(getActivity());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(getActivity());
                    }
                });
    }

    private void cancelRidebyUser() {
        Common.progress_show(getActivity());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        apiService
                .user_cancle_ride(prefs.getValue(Constants.user_ride_request_id))
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestRideView.setVisibility(View.GONE);
                                waitStatusView.setVisibility(View.GONE);
                                acceptStatusView.setVisibility(View.GONE);
                                flotingBtn.setVisibility(View.VISIBLE);
                                prefs.setBooleanValue(Constants.userRequestForRide, false);
                                resizeGoogleMap(0);
                            }
                        }, 1800);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(getActivity());
                    }
                });
    }

    private void initroute() {
        if (prefs.getValue(Constants.user_ride_status).equals("accept")) {

            if (markerPoint.size() > 1) {
                markerPoint.clear();
                googleMap.clear();
            }
            LatLng latLng2 = new LatLng(Double.valueOf(prefs.getValue(Constants.pickup_location_lat)), Double.valueOf(prefs.getValue(Constants.pickup_location_long)));
            markerPoint.add(latLng2);

            MarkerOptions options = new MarkerOptions();
            options.position(latLng2);

            if (markerPoint.size() == 1) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_marker));
            } else if (markerPoint.size() == 2) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
            }
            googleMap.addMarker(options);

            // Checks, whether start and end locations are captured
            if (markerPoint.size() >= 2) {
                LatLng origin = markerPoint.get(0);
                LatLng dest = markerPoint.get(1);
                String url = getDirectionsUrl(origin, dest);
                FetchUrl FetchUrl = new FetchUrl(googleMap);
                FetchUrl.execute(url);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            }
        }
    }

    private void setMarkerPoint(double latitude, double longitude, boolean isRideActive, boolean isDropPlace) {
        MarkerOptions dropLocMarkerOpt = new MarkerOptions();
        if (marker != null) {
            marker.remove();
        }
        LatLng latLng = new LatLng(latitude, longitude);
        dropLocMarkerOpt.position(latLng);
        dropLocMarkerOpt.title("Drop Position");
        dropLocMarkerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
        marker = googleMap.addMarker(dropLocMarkerOpt);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
        if (!isRideActive) {
            if (isDropPlace) {
                fetchMarkerLocation(marker, tvDropPlace);
            } else {
                fetchMarkerLocation(marker, tvPickupPlace);
            }
        }
    }


    private void fetchMarkerLocation(Marker mMarker, TextView textview) {
        String markerLocation = "";
        double markerlat = mMarker.getPosition().latitude;
        double markerlong = mMarker.getPosition().longitude;

        if (markerlat != 0 && markerlong != 0) {
            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(mMarker.getPosition().latitude, mMarker.getPosition().longitude, 1);

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    markerLocation = address.getAddressLine(0);

                    int tag = (int) textview.getTag();
                    if (tag == 1) {
                        prefs.setValue(Constants.pickup_location_lat, String.valueOf(markerlat));
                        prefs.setValue(Constants.pickup_location_long, String.valueOf(markerlong));
                        prefs.setValue(Constants.pickup_location, markerLocation);
                    } else {
                        prefs.setValue(Constants.drop_location_lat, String.valueOf(markerlat));
                        prefs.setValue(Constants.drop_location_long, String.valueOf(markerlong));
                        prefs.setValue(Constants.drop_location, markerLocation);
                    }
                    textview.setText(markerLocation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resizeGoogleMap(int viewheight) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = screenHeight - viewheight;
        mapFragment.getView().setLayoutParams(params);
    }

    public int getViewHeight(View view) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int deviceWidth = displaymetrics.widthPixels;
        //int deviceHeight = displaymetrics.heightPixels;

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }
}
