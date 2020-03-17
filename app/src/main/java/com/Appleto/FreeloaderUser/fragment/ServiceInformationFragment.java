package com.Appleto.FreeloaderUser.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Appleto.FreeloaderUser.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceInformationFragment extends Fragment {


    public ServiceInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_information, container, false);
    }

}
