package com.example.sh_polak.hiyda.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.sh_polak.hiyda.Activities.AddPartyActivity;
import com.example.sh_polak.hiyda.Activities.FavoriteActivity;
import com.example.sh_polak.hiyda.Activities.LoginActivity;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.facebook.login.LoginManager;


/**
 * Created by sh-polak on 19/10/2017.
 */

public class UserFragment extends Fragment implements AppConfig {
    Button btnLogOut;
    View root;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.user_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view;
        appConfiguration();

    }

    @Override
    public void appConfiguration() {
        btnLogOut=(Button)root.findViewById(R.id.logoutBtn);
        TextView addParty = (TextView)root.findViewById(R.id.addParty);
        addParty.setOnClickListener((View view)-> startActivity(new Intent(getActivity(), AddPartyActivity.class)));
        logout(btnLogOut);
    }



    public void logout(Button btnLogOut){// logout from facebook / backendless User.
        btnLogOut.setOnClickListener(view -> {//logout from user
            Backendless.UserService.logout(new AsyncCallback<Void>() {

                @Override
                public void handleResponse(Void response) {
                    LoginActivity.facUserLogged.edit().clear();
                    LoginManager.getInstance().logOut();
                    getActivity().finish();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(getContext(), "Error occoured" + fault.getCode(), Toast.LENGTH_LONG).show();
                }
            });
        });


    }
}
