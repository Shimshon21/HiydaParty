package com.example.sh_polak.hiyda.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.example.sh_polak.hiyda.Interface.AppConfig;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.PremissionManger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPartyActivity extends AppCompatActivity implements AppConfig {
    EditText partyName, partLocation, dateTime, capacity;
    int WRITE_IMAGE = 3,IMAGEREQUEST = 1,REQUEST_IMAGE = 2;
    ImageView partyImage;
    Uri selectedImage;
    File f;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parties_list);
        appConfiguration();
    }

    public void getImage(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGEREQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK) && (requestCode == IMAGEREQUEST)){
            partyImage.setImageURI(data.getData());
           selectedImage = data.getData();
            f = new File(getPath(selectedImage));
        }
    }
    @Override
    public void appConfiguration() {
        PremissionManger.check(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_IMAGE);
        PremissionManger.check(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_IMAGE);
       // Backendless.initApp(getApplicationContext(), getString(R.string.appId), getString(R.string.keyId));
        partyName = (EditText) findViewById(R.id.partyName);
        partLocation = (EditText) findViewById(R.id.location);
        partyImage = (ImageView) findViewById(R.id.partyImage);
        dateTime = (EditText)findViewById(R.id.date);
        capacity =(EditText)findViewById(R.id.capacity);

        //TODO - CALENDER ONCLICK
        DatePickerDialog.OnDateSetListener mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        updateLabel();
                    }
                };
        dateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateTime.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateDisplay() {
        this.dateTime.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/")
                        .append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    //TODO - CALENDER ONCLICK - END

    // }


    public void uploadFileAData(View view) {

        uploadFileData();
        String partyName=this.partyName.getText().toString(),partyLocation=this.partLocation.getText().toString();
        if((!partyName.isEmpty())&&(!partyLocation.isEmpty())) {
            HashMap data = new HashMap();
            data.put("name", partyName);
            data.put("Location", partyLocation);
            data.put("Capacity", Integer.parseInt(this.capacity.getText().toString()));
            data.put("DateTime", this.dateTime.getText().toString());
            data.put("PartyIMage", "https://api.backendless.com/8D2F016F-E52B-27B6-FF9E-6961F9F6D600/1C6F50E9-DC11-7A14-FF62-E133FC0FD900/files/PartyImages/" + f.getName());
            uploadPartyInfo(data);
        } else {
                Toast.makeText(getApplicationContext(),"One or more of the details was incorrect please try again",Toast.LENGTH_SHORT).show();
            }
        }

    private void uploadPartyInfo(HashMap data) {
            Backendless.Persistence.of("A_publicist_user").save(data,new AsyncCallback<Map>() {
                @Override
                public void handleResponse(Map response) {
                    Toast.makeText(getApplicationContext(),"Upload data successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(getApplicationContext(),"Error Occuered",Toast.LENGTH_SHORT).show();
                    System.out.println(fault.getDetail());
                }
            });
        }


    private void uploadFileData() {
        Backendless.Files.upload(f, "PartyImages", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(BackendlessFile response) {
                System.out.println("Image uploded succsfully");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                System.out.println("error" + fault.getMessage());
            }
        });
    }

    /*
        public void UploadPArtyImage(Uri image){
            f = new File(getPath(image));
            Backendless.Files.upload(f, "PartyImages", new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(BackendlessFile response) {
                    System.out.println("sucssessfull");

                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    System.out.println("error" + fault.getMessage());

                }
            });*/
    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }//u dont get the full path from the intent so u have to call a method that do that for u
        // this will only work for images selected from gallery source stackoverflow
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }


}