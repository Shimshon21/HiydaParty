package com.example.sh_polak.hiyda.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.MySqlLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sh-polak on 19/10/2017.
 */

    public class Myorders extends Fragment implements AppConfig {
    RecyclerView faveList;
    View root;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.parties_list_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appConfiguration();

    }

    @Override
    public void appConfiguration() {
      faveList = (RecyclerView) root.findViewById(R.id.myOrder);
        db = new MySqlLite(getContext()).getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM favorites",null);
        for(results.moveToFirst(); !results.isAfterLast(); results.moveToNext()){//iterate results (row is changing)
            String name = results.getString(1);//first column (the only column in this example)
            String id = results.getString(0);
            String imageUrl = results.getString(2);
            String partyDate = results .getString(3);
            System.out.println(id +" "+imageUrl + " "+name + " " + partyDate);

        }
    }
}
