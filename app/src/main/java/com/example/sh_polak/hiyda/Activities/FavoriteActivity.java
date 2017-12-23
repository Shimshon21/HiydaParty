package com.example.sh_polak.hiyda.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.MySqlLite;

import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {
       RecyclerView recyclerView;
       SQLiteDatabase db;
       List<Map> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_activity);
        db= new MySqlLite(this).getWritableDatabase();
      //  recyclerView=(RecyclerView)findViewById(R.id.FavoriteList);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String a ="SElECT partyName FROM favorites";
        Cursor c=db.rawQuery(a,null);
    }
}
