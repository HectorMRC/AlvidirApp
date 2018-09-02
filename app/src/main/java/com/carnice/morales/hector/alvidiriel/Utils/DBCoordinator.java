package com.carnice.morales.hector.alvidiriel.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCoordinator extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "app.db.alvidiriel";

    DBCoordinator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MainDBContract.SQL_CREATE_MAINTABLE);
        db.execSQL(MainDBContract.SQL_CREATE_SYNCTABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MainDBContract.SQL_DELETE_MAINTABLE);
        db.execSQL(MainDBContract.SQL_DELETE_SYNCTABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
