package com.example.projecta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBTimeHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=7;

    public DBTimeHelper(Context context){
        super(context, "timedb", null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableSql="create table tb_time ("+
                "_id integer primary key autoincrement," +
                "time text not null"+");";

        sqLiteDatabase.execSQL(tableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion == 1){
            sqLiteDatabase.execSQL("drop table tb_time");
            onCreate(sqLiteDatabase);
        }
    }
}
