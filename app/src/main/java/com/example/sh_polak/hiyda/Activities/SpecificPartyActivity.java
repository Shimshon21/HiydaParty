package com.example.sh_polak.hiyda.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.sh_polak.hiyda.utils.ImageLoadTask;
import com.example.sh_polak.hiyda.utils.PremissionManger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SpecificPartyActivity extends AppCompatActivity implements AppConfig {
    RelativeLayout layout;
    int capacity;
    String obid;
    TextView title,freeCapacity,dateTime,place;
    ImageView imageParty, imageParty2;
    EditText attenders;

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
            Backendless.Data.of("A_publicist_user").find(dataQuery, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(List<Map> response) {
                    System.out.println(response);
                    capacity = capacity - (Integer.parseInt(String.valueOf(attenders.getText())));
                    contact.put("Capacity", capacity);
                    System.out.println(response);
                    response.add(contact);
                    Backendless.Data.of("A_publicist_user").save(contact, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map response) {
                            Toast.makeText(getApplicationContext(), "Ticket purchase was successfull", Toast.LENGTH_LONG).show();
                            freeCapacity.setText("number of Tickets left :" + response.get("Capacity").toString());
                            finish();
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
        //imageParty = (ImageView) layout.findViewById(R.id.detailImage);
        imageParty2 = (ImageView) layout.findViewById(R.id.circleImage);
    }

    private void setViewsDetails() {
        Bundle bundle = getIntent().getExtras();
        Bitmap bmp=ImageLoadTask.getSingleImage( bundle.getString("Image"));
        //imageParty.setImageBitmap(bmp);
        imageParty2.setImageBitmap(bmp);
        title.setText(bundle.getString("name"));
        capacity=bundle.getInt("capacity");
        if (capacity > 0)
        freeCapacity.setText("Tickets left :"+ capacity);
        else freeCapacity.setText("No more tickets left");
        dateTime.setText("Date and Time :"+bundle.getString("time"));
        place.setText("Location : "+bundle.getString("location"));
        obid=bundle.getString("ObjectId");
    }

    public void startMapActivity(View view) {
        PremissionManger.check(this, Manifest.permission.ACCESS_FINE_LOCATION,0);
        Intent i =new Intent(this,MapsActivity.class);
        startActivity(i);
    }
}