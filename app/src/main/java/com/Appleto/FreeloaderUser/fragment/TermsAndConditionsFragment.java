package com.Appleto.FreeloaderUser.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.Appleto.FreeloaderUser.R;
import com.Appleto.FreeloaderUser.Utils.Common;
import com.Appleto.FreeloaderUser.retrofit2.ApiClient;
import com.Appleto.FreeloaderUser.retrofit2.ApiInterface;
import com.google.gson.JsonObject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermsAndConditionsFragment extends Fragment {


    private TextView tvName, tvContent;

    public TermsAndConditionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        //tvName = v.findViewById(R.id.term_tv_name);
        //tvContent = v.findViewById(R.id.term_tv_content);

        //fetchTermAndCondition();
        return v;
    }

    private void fetchTermAndCondition() {
        Common.progress_show(getActivity());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        apiService
                .setting("term")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        Common.progress_dismiss(getActivity());
                        try {
                            if (jsonObject.get("status").getAsString().equals("1")) {
                                if (jsonObject.getAsJsonArray("data").size() > 0) {
                                    JsonObject jsonObj = jsonObject.getAsJsonArray("data").getAsJsonObject();

                                    tvName.setText(jsonObj.get("name").getAsString());
                                    tvContent.setText(jsonObj.get("content").getAsString());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Network Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Common.progress_dismiss(getActivity());
                    }
                });
    }

}
