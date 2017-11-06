package com.example.sh_polak.hiyda.Activities;

import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.sh_polak.hiyda.R.id.eMail;

public class SignInActivity extends AppCompatActivity implements AppConfig {
    EditText mName, mPassword, mConfirmPass, mEmail;
    Spinner userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        appConfiguration();
        List<String> list = new ArrayList<>();
        list.add("Select user_fragment type");
        list.add("Normal User");
        list.add("Publicist User");
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
      userType.setAdapter(dataAdapter);

            }

    public void registerUser(View view) {
        String name = mName.getText().toString(), password = mPassword.getText().toString(), confimPass = mConfirmPass.getText().toString(), email = mEmail.getText().toString();
        Boolean vipUser = false;
        if (!userType.getSelectedItem().toString().equals("Select user_fragment type")) {
            if (isValid(name, password, confimPass) && password.equals(confimPass) && (userType != null)) {// Check validation of user_fragment input
                BackendlessUser user = new BackendlessUser();
                user.setProperty("name", name);
                user.setEmail(email);
                user.setPassword(password);
                if (userType.getSelectedItem().toString().equals("Publicist User"))
                    vipUser = true;
                user.setProperty("VIPUser", vipUser);
                userRegister(user);//Put user_fragment input in backendless users table
            } else Toast.makeText(this, "Wrong input values", Toast.LENGTH_SHORT).show();
        }else new AlertDialog.Builder(this).setMessage("Please select user_fragment Type").setNeutralButton("Got it!",null).show();

    }



    @Override
    public void appConfiguration() {
        mName = (EditText) findViewById(R.id.userName);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText)findViewById(R.id.eMail);
        mConfirmPass = (EditText) findViewById(R.id.confirmPassword);
        userType=(Spinner)findViewById(R.id.spinner);
     //   Backendless.initApp(getApplicationContext(),getString(R.string.keyId),getString(R.string.appId));
    }

    public Boolean isValid(String... valid) {//Check validation of user_fragment input
        if (!valid[0].isEmpty() && (!valid[1].isEmpty()) && (!valid[2].isEmpty())) {
            if ((valid[0].length() > 4) && (valid[1].length()) > 4 && (valid[2].length() > 4))
                return true;

        }
        return false;
    }


    @Override
    public void onBackPressed() {  // When pressing the BACK BUTTON in Sign Up Activity,will Animate with android's built in FADE IN & OUT
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }


    private void userRegister(BackendlessUser user){ // Put user_fragment input in backendless users table , and return to login activity
        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Toast.makeText(getApplicationContext(), "Registiration was complate successfully " + response.getObjectId(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Error occoured"+fault.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(fault.getMessage()+"------->"+fault.getCode());
            }
        });


    }
}

