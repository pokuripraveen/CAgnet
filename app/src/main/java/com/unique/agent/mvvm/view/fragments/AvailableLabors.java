package com.unique.agent.mvvm.view.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unique.agent.R;
import com.unique.agent.databinding.LaborListBinding;
import com.unique.agent.mvvm.viewmodels.LaborListViewModel;

/**
 * Created by praveenpokuri on 23/08/17.
 */

public class AvailableLabors extends BaseFragment {
    LaborListBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.labor_list, container, false);
        mBinding.setLabors(new LaborListViewModel());

        mBinding.rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        //mBinding.rvContent.setAdapter(new LaborListAdapter(mUsers));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
