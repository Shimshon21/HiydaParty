package com.example.sh_polak.hiyda.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.fragments.Myorders;
import com.example.sh_polak.hiyda.fragments.MainListActivity;
import com.example.sh_polak.hiyda.fragments.PartiesList;
import com.example.sh_polak.hiyda.fragments.SearcParty;
import com.example.sh_polak.hiyda.fragments.UserFragment;

import java.util.HashMap;
import java.util.Map;

public class FragnentActivity extends AppCompatActivity  {
   private static Map<String,Fragment>fragmentMap=new HashMap<>();
    Button searchBtn;
    FragmentTransaction transaction;
    static {
        fragmentMap.put("UserFragement",new UserFragment());
        fragmentMap.put("SearcParty",new SearcParty());
        fragmentMap.put("PartiesList",new PartiesList());
        fragmentMap.put("Myorders",new Myorders());
        fragmentMap.put("MainListActivity",new MainListActivity());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        FragmentTransaction  transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.section,new MainListActivity());
        transaction.commit();
    }



    public void chooseFragments(View view) {//show fragment in screen by tag of the button he was pressed.
        String name= view.getTag().toString();
        System.out.println(view.getTag());
       FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.section,fragmentMap.get(name));
        transaction.commit();
    }

    public void startMap(View view) {
        startActivity(new Intent(this,MapsActivity.class));
    }
}
