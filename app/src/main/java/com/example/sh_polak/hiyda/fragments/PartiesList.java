package com.example.sh_polak.hiyda.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;


/**
 * Created by sh-polak on 19/10/2017.
 */

public class PartiesList extends Fragment implements AppConfig {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_list_fragment,container,false);
    }

    @Override
    public void appConfiguration() {

    }
}
