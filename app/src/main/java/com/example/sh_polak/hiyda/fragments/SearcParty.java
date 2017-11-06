package com.example.sh_polak.hiyda.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.adapters.RecycleAdapterTest;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by sh-polak on 19/10/2017.
 */

public class SearcParty extends Fragment implements AppConfig {
    View root,row;
    ImageView searchBtn,image;
    EditText search;
    TextView name,date;
    List<Map> result;
    RecyclerView layout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       root =inflater.inflate(R.layout.parties_search_fragments,container,false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {// in fragemt u intilize views at onViewCreated NOT in ONCREATE.
        super.onViewCreated(view, savedInstanceState);
        root=view;
        appConfiguration();
        itemSearch();
    }

    public  void itemSearch(){// TODO: 19/10/2017  add method for search item from Backendless.
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                if (!search.getText().toString().isEmpty()){
                    queryBuilder.setWhereClause("name = " + "'" + search.getText().toString() + "'");
                    Backendless.Data.of("A_publicist_user").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(List<Map> response) {//null pointer when result is out of block need to invstigate
                        layout.setAdapter(new RecycleAdapterTest(getContext(),response));
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(getContext(), "notgg", Toast.LENGTH_SHORT).show();
                        }
                    });
                }}});}

    @Override
    public void appConfiguration() {
        searchBtn=(ImageView)root.findViewById(R.id.searchBtn);
        search=(EditText)root.findViewById(R.id.searchFragText);
        layout=(RecyclerView) root.findViewById(R.id.searchLayout);
        layout.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
