package com.example.sh_polak.hiyda.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sh-polak on 30/10/2017.
 */

public class MySqlLite extends SQLiteOpenHelper {
   private static final String dbName="MyDataBase";
    String[] initialStat={"CREATE TABLE  IF NOT EXISTS  favorites (id INTEGER PRIMARY KEY, partyName VARCHAR(10),Image Blob,partyDate DATETIME,isFavorite BIT)","CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY ,userName VARCHAR(10),UserId VARCHAR(10))"};
    public MySqlLite(Context context) {
        super(context,dbName, null, 1);
    }
    private void initDb(SQLiteDatabase db){
        for(String table:initialStat){
            db.execSQL(table);
        }
    }//sql create the data base
    @Override
    public void onCreate(SQLiteDatabase db) {
       initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
