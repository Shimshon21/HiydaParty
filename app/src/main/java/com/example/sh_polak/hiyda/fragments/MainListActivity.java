package com.example.sh_polak.hiyda.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.sh_polak.hiyda.Activities.MainActivity;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.adapters.RecycleAdapterTest;

import java.util.List;
import java.util.Map;

import static com.example.sh_polak.hiyda.R.layout.view;


/**
 * Created by sh-polak on 19/10/2017.
 */

public class MainListActivity extends Fragment implements AppConfig {
    RecyclerView partiesListViews;
    List<Map> result;
    ProgressBar mainProgressbar;
    Bundle bundle;
    TextView registerTextView;
    EditText search;
    ListView listView;
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.parties_mainlist_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v=view;
        appConfiguration();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPartyList();
    }

    @Override
    public void appConfiguration() {
        partiesListViews = (RecyclerView)v.findViewById(R.id.partyListFrag);
        mainProgressbar = (ProgressBar)v.findViewById(R.id.listFragViewProgress);
        mainProgressbar.setVisibility(View.VISIBLE);
        bundle =getActivity().getIntent().getExtras();
        registerTextView = (TextView)v.findViewById(R.id.RegisterParty);
        search=(EditText)v.findViewById(R.id.searcPart);
        partiesListViews.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void loadPartyList(){//Load images and party listviews
        Backendless.Data.of("A_publicist_user").find(new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                result = response;
                Log.i("result",result.toString());

                partiesListViews.setAdapter(new RecycleAdapterTest(getContext(), result));//cant load more than 10 items because of backe endlesss
                mainProgressbar.setVisibility(View.INVISIBLE);
                Log.i("url", result.get(0).get("PartyImage").toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("error" + " " + fault.getMessage());
                Toast.makeText(getContext(),"Error occoured loading the list please try again later",Toast.LENGTH_SHORT).show();
            }
        });
        //List<Map>result=Backendless.Persistence.of("A_publicist_user").find();
        //System.out.println(result);
        // partiesListViews.setAdapter(new PartyListAdapter(MainActivity.this,R.layout.row,result));
    }
    public void Logout(View view) {//logout from user_fragment
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                getActivity().finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getContext(), "Error occoured" + fault.getCode(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void AddtoFavorite (View v){
        if(((CheckBox)v).isChecked()){
            System.out.println("True");
        }
    }
}