package com.Appleto.FreeloaderUser.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Helper;
import com.Appleto.FreeloaderUser.Utils.PreferenceApp;
import com.Appleto.FreeloaderUser.fragment.ChangeLocationFragment;
import com.Appleto.FreeloaderUser.fragment.MapFragment;
import com.Appleto.FreeloaderUser.fragment.MyAccountFragment;
import com.Appleto.FreeloaderUser.fragment.RequestRideBottomFragment;
import com.Appleto.FreeloaderUser.fragment.ServiceInformationFragment;
import com.Appleto.FreeloaderUser.fragment.TermsAndConditionsFragment;
import com.Appleto.FreeloaderUser.model.UserRideDetailResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PreferenceApp prefs;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;


    // tags used to attach the fragments
    private static final String TAG_MAP = "map";
    private static final String TAG_LOCATION = "changeLocation";
    private static final String TAG_SERVICEINFO = "serviceInformation ";
    private static final String TAG_MYACCOUNT = "myAccount";
    private static final String TAG_TERMANDCONDITION = "termsAndConditions";
    public static String CURRENT_TAG = TAG_MAP;

    private String[] activityTitles;
    public static int navItemIndex = 0;

    private boolean shouldLoadMapFragOnBackPress = true;
    private Handler mHandler;
    public static ArrayList<UserRideDetailResponse.Datum> userDetailList;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);

        prefs = new PreferenceApp(this);
        mHandler = new Handler();
        userDetailList = new ArrayList<>();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Navigation view header
        navHeader = navigationView.getHeaderView(1);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        setUpNavigationView();
        setSupportActionBar(toolbar);

        navItemIndex = 0;
        CURRENT_TAG = TAG_MAP;
        if(navItemIndex == 0){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }
        loadMapFragment();
    }

    private Fragment getMapFragment() {
        switch (navItemIndex) {
            case 0:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;
            case 1:
                ChangeLocationFragment changeLocationFragment = new ChangeLocationFragment();
                return changeLocationFragment;
            case 2:
                ServiceInformationFragment serviceInformationFragment = new ServiceInformationFragment();
                return serviceInformationFragment;
            case 3:
                MyAccountFragment myAccountFragment = new MyAccountFragment();
                return myAccountFragment;
            case 4:
                TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
                return termsAndConditionsFragment;

            default:
                return new MapFragment();
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.user_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAP;
                        break;
                    case R.id.change_location:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_LOCATION;
                        break;

                    case R.id.nav_serviceinfo:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SERVICEINFO;
                        break;

                    case R.id.nav_my_account:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_MYACCOUNT ;
                        break;

                    case R.id.nav_terms_and_condition:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_TERMANDCONDITION;
                        break;

                    case R.id.nav_logout:
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        drawer.closeDrawers();
                        finish();
                        return true;

                    default:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MAP;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                if(navItemIndex == 0){
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }
                loadMapFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void loadMapFragment() {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getMapFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadMapFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MAP;
                loadMapFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }
}



