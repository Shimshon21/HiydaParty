package com.example.sh_polak.hiyda.Activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.adapters.PartyListAdapter;
import com.example.sh_polak.hiyda.adapters.RecycleAdapterTest;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;
import com.example.sh_polak.hiyda.utils.MySqlLite;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.List;
import java.util.Map;

import bolts.CancellationTokenRegistration;

public class MainActivity extends AppCompatActivity implements AppConfig {
    RecyclerView partiesListViews;
    List<Map> result;
    ProgressBar mainProgressbar;
    Bundle bundle;
    TextView registerTextView;
    EditText search;
    SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appConfiguration();
        loadPartyList();
        partiesListViews.setLayoutManager(new LinearLayoutManager(this));//u have to config layout manger for recycle view
        
       //setAdapterw for partiesListViews
    }

    private void loadPartyList(){//Load images and party listviews
        Backendless.Data.of("A_publicist_user").find(new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                result = response;
                Log.i("result",result.toString());
                partiesListViews.setAdapter(new RecycleAdapterTest(MainActivity.this, result));//cant load more than 10 items because of backe endlesss
                mainProgressbar.setVisibility(View.INVISIBLE);
                //partiesListViews.setAdapter(new RecycleAdapterTest(MainActivity.this,result));
               // loadImagesInBackground(result);//loading all images in background
                Log.i("url", result.get(0).get("PartyImage").toString());
            /*    partiesListViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        goToSpecificActivity(position);//if cliked go to SpecificPartyActivity and send specifc row from backendless
                    }
                });*/
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("error" + " " + fault.getMessage());
                Toast.makeText(getApplicationContext(),"Error occoured loading the list please try again later",Toast.LENGTH_SHORT).show();
            }
        });
        //List<Map>result=Backendless.Persistence.of("A_publicist_user").find();
        //System.out.println(result);
        // partiesListViews.setAdapter(new PartyListAdapter(MainActivity.this,R.layout.row,result));
    }

    private void loadImagesInBackground(final List<Map> result) {//Load images and put them in map for reuse
        ImageLoadTask.loadImages(result, new Runnable() {
            public void run() {
                partiesListViews.post(new Runnable() {
                    public void run() {
                        mainProgressbar.setVisibility(View.INVISIBLE);
                        System.out.println(result);

                    }
                });
            }
        });
    }

    private  void  goToSpecificActivity(final int position) {//if cliked go to SpecificPartyActivity and send specifc row objects from backendless
       final Intent i = new Intent(MainActivity.this, SpecificPartyActivity.class);
        Backendless.Data.of("A_publicist_user").find(new AsyncCallback<List<Map>>() {
                                                         @Override
                                                         public void handleResponse(List<Map> repnonse) {
                                                           Map result=repnonse.get(position);
                                                             i.putExtra("name", result.get("name").toString()).putExtra("time", result.get("DateTime").toString()).putExtra("capacity", Integer.parseInt(result.get("Capacity").toString())).putExtra("location", result.get("Location").toString()).putExtra("Image", result.get("PartyImage").toString()).putExtra("ObjectId", result.get("objectId").toString());//show current details of party  in specificParty
                                                             startActivity(i);
                                                         }

                                                         @Override
                                                         public void handleFault(BackendlessFault fault) {

                                                         }
                                                     });


    }

    public void Logout(View view) {//logout from user_fragment
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, "Error occoured" + fault.getCode(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startActivityRegisterParty(View view) {//go to AddPartyActivity
        if (bundle.getBoolean("typeUser", false)) {
            startActivity(new Intent(this, AddPartyActivity.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        } else Toast.makeText(this, "Only avaible to publicit user_fragment", Toast.LENGTH_LONG).show();
    }

    @Override
    public void appConfiguration() {
        partiesListViews = (RecyclerView) findViewById(R.id.partyList);
        mainProgressbar = (ProgressBar) findViewById(R.id.listViewProgress);
        mainProgressbar.setVisibility(View.VISIBLE);
        bundle = getIntent().getExtras();
        registerTextView = (TextView) findViewById(R.id.RegisterParty);
        search=(EditText)findViewById(R.id.searcPart);
        db=new MySqlLite(this).getReadableDatabase();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    public void searchParty(View view) {
        DataQueryBuilder queryBuilder= DataQueryBuilder.create();
        queryBuilder.setWhereClause("name = "+"'"+search.getText().toString()+"'");
        Backendless.Data.of("A_publicist_user").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> response) {
                partiesListViews.setAdapter(new RecycleAdapterTest(getApplicationContext(),response));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getApplicationContext(),"notgg",Toast.LENGTH_SHORT).show();
            }
        });
    }
}