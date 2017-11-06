package com.example.sh_polak.hiyda.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements AppConfig {

    EditText mName, mPassword, mEmail; // Declaring variables
    CheckBox checkBox;
    boolean typeUser, rememberMe = false, isCliked;
    LoginButton loginButton;
    CallbackManager callbackManager;
  public static SharedPreferences facUserLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Backendless.initApp(getApplicationContext(), getString(R.string.appId), getString(R.string.keyId));//Todo task need to check the bug java.lang.RuntimeException: Could not register device on Backendless server: Not existing user_fragment token - D4EB5525-C23F-99C3-FFB0-D9BF24E18500. Relogin user_fragment to update your user_fragment token
        setContentView(R.layout.activity_login);
        facUserLogged=getPreferences(MODE_PRIVATE);
        // FacebookSdk.sdkInitialize(getApplicationContext()); also need to remembar on first time the facebook would put another haskey of its own that sould be in facebook api need to be reasarched
        AppEventsLogger.activateApp(getApplication());
        appConfiguration(); // Configuring views/backendless
        callbackManager = CallbackManager.Factory.create();
       loginButton = (LoginButton) findViewById(R.id.login_button);
        facebookLogin();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isValidLogin();


    }


    public void Login(View view) {  //backend less method - login the user_fragment with username and password ,also check if the user_fragment want to stay in
        Backendless.UserService.login(mName.getText().toString(), mPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                System.out.println(response.toString() + "   ");
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_LONG).show();
                typeUser = (Boolean) response.getProperty("VIPUser");
                startMainActivity(typeUser); // start activity MainActivity and put extra typeUser
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), "Wrong details please Try again.", Toast.LENGTH_LONG).show();
                System.out.println(fault.getCode() + " " + fault.getMessage() + "" + fault.getDetail());
            }
        }, rememberMe);
    }

    void startMainActivity(Boolean response) {  // register the device and put extra typeUser and strat activity MainActivity.
        registerDevice();// Register device to pushnotification backendless
        Intent i = new Intent(this, FragnentActivity.class);
        i.putExtra("typeUser", response);
        System.out.println(response);
        startActivity(i);
    }

    public void startSignUpUserActivty(View view) { // start activity SignInActivity
        startActivity(new Intent(this, SignInActivity.class));
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);  //ANIMATION - slides in when presing "not a member?sign up now" and moves to SignInActivity
    }

    private void registerDevice() { // register the device for push notification backenddless - (might cause some token error - solution - log out and log in)
        Backendless.Messaging.registerDevice("23206336394", "default", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                try {
                    Toast.makeText(getApplicationContext(), "This phone registered succseefuly", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error occoured pleas relogin again", Toast.LENGTH_LONG).show(); //
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(), fault.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void rememberMeClick(View view) {//Remember ths user_fragment request to stay logged in
        rememberMe = !rememberMe;
    }

    @Override
    public void appConfiguration() {// Configuring views/backendless
        mName = (EditText) findViewById(R.id.loginName);
        mPassword = (EditText) findViewById(R.id.loginPass);
        mEmail = (EditText) findViewById(R.id.loginEmail);
        checkBox = (CheckBox) findViewById(R.id.stayIn);
        Typeface tf = Typeface.createFromAsset(getAssets(), "superfruit.ttf");
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setTypeface(tf);
         }

    public void isValidLogin() {//check if user_fragment still Log
       Log.i("checkUserLoged!!!!",facUserLogged.getString("userLoggedInId",""));
        if((!facUserLogged.getString("userLoggedInId","").isEmpty())&&!facUserLogged.getString("userLoggedInname","").isEmpty()){
            startMainActivity(typeUser);
        }else {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() { // Check if user_fragment is registered or not,if registred,go to method "startMainActivity"
                @Override
                public void handleResponse(Boolean isLoggedIn) {
                    if (isLoggedIn)
                        startMainActivity(typeUser);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    System.out.println("unregistered");
                }
            });
        }
    }
    public void facebookLogin(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Backendless.UserService.loginWithFacebookSdk(loginResult.getAccessToken().getToken(), new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(getApplicationContext(), "login successfull", Toast.LENGTH_LONG).show();
                        Log.e("!!!!!!UserTokenFacebook",loginResult.toString()+"  "+response.toString());
                        Intent i = new Intent(getApplicationContext(),FragnentActivity.class);
                        facUserLogged.edit().putString("userLoggedInId",response.getUserId().toString());
                        facUserLogged.edit().putString("userLoggedInname",response.getProperty("name").toString());
                        facUserLogged.edit().apply();
                        i.putExtra("typeUser", false);
                        startActivity(i);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(), fault.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println(error.getMessage());
            }
        });


    }

}



