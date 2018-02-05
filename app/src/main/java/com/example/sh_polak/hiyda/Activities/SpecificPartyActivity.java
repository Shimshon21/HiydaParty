package com.example.sh_polak.hiyda.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;
import com.example.sh_polak.hiyda.utils.PremissionManger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SpecificPartyActivity extends AppCompatActivity implements AppConfig {
    RelativeLayout layout,layout2;
    int capacity;
    String obid,nameParty,imageUrl,time,location;
    TextView title,freeCapacity,dateTime,place;
    ImageView imageParty, imageParty2;
    EditText attenders;
    ProgressBar loadinProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_activity_view);
        appConfiguration();
        setViewsDetails();
    }

    public void Submit(View view) {
        System.out.println(attenders.getText());
        HashMap contact = new HashMap();
        if (!attenders.getText().toString().isEmpty()) {
            contact.put("objectId", obid);
            contact.put("Capacity", capacity);
            if (capacity > 0)
                updateCapacityBcEndLss(contact);
        }else Toast.makeText(this,"Please enter number of Tickets",Toast.LENGTH_SHORT).show();
    }




    private void updateCapacityBcEndLss(final HashMap contact) {
        DataQueryBuilder dataQuery= DataQueryBuilder.create();
        dataQuery.setWhereClause("objectId = '"+obid+"'");
        if(Integer.parseInt(String.valueOf(attenders.getText()))<=capacity) {
            Backendless.Persistence.of("A_publicist_user").find(dataQuery, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(List<Map> response) {//TODO FiX bug adding new row in backendless instead updating row.
                    System.out.println(response);
                    capacity = capacity - (Integer.parseInt(String.valueOf(attenders.getText())));
                    contact.put("Capacity", capacity);
                    System.out.println(response);
                    response.add(contact);
                    Backendless.Data.of("A_publicist_user").save(contact, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map response) {
                            Toast.makeText(getApplicationContext(), "Bug error unavaible.", Toast.LENGTH_LONG).show();
                            freeCapacity.setText("number of Tickets left :" + response.get("Capacity").toString());
                        //    finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            System.out.println(fault.getDetail());
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    System.out.println(fault.getMessage());
                }
            });
        }else Toast.makeText(this,"You cant buy more tickets than there is",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void appConfiguration() {//activity config and backendless
        layout = (RelativeLayout) findViewById(R.id.mainLyt);
        LayoutInflater.from(this).inflate(R.layout.selected_party_activity, layout);
        title = (TextView) layout.findViewById(R.id.currentPartyTitle);
        freeCapacity = (TextView) layout.findViewById(R.id.detailCapacity);
        dateTime = (TextView) layout.findViewById(R.id.detailTime);
        place = (TextView) layout.findViewById(R.id.detailLocation);
        attenders = (EditText) layout.findViewById(R.id.numOfTickets);
        loadinProgress=(ProgressBar)layout.findViewById(R.id.selectedProgress);
        //imageParty = (ImageView) layout.findViewById(R.id.detailImage);
        imageParty2 = (ImageView) layout.findViewById(R.id.circleImage);
        layout2=(RelativeLayout)findViewById(R.id.viewsShow);
    }

    private void setViewsDetails() {
        //show progress bar + hide layout

        Bundle bundle = getIntent().getExtras();
        DataQueryBuilder builder = DataQueryBuilder.create();
        builder.setWhereClause(bundle.getString("objectId"));
        Backendless.Data.of("A_publicist_user").find(builder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> repnonse) {
                if(!repnonse.equals(null)) {
                    Map result = repnonse.get(0);
                    imageUrl = result.get("PartyImage").toString();
                    nameParty = result.get("name").toString();
                    time = result.get("DateTime").toString();
                    location = result.get("Location").toString();
                    capacity = Integer.parseInt(result.get("Capacity").toString());
                    Bitmap bmp = ImageLoadTask.getSingleImage(imageUrl);
                    //imageParty.setImageBitmap(bmp);
                    imageParty2.setImageBitmap(bmp);
                    title.setText(nameParty);
                    if (capacity > 0)
                        freeCapacity.setText("Tickets left :" + capacity);
                    else freeCapacity.setText("No more tickets left");
                    dateTime.setText(time);
                    place.setText(location);
                    loadinProgress.setVisibility(View.INVISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                } else {
                 Toast.makeText(getApplicationContext(),"Sorry something went wrong",Toast.LENGTH_SHORT).show();
                    finish();
                }
                //hide progress bar + show layout
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("searchItemError", fault.getDetail());

            }
        });

    }

}