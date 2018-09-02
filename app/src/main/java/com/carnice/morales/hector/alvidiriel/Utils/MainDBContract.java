package com.carnice.morales.hector.alvidiriel.Utils;

import android.provider.BaseColumns;

public class MainDBContract {

    private MainDBContract() {}

    class FeedEntry implements BaseColumns {
        static final String MAIN_TABLE_NAME = "DBAlvidiriel";
        static final String COLUMN_TYPE = "Tipus";
        static final String COLUMN_WORD = "Català";
        static final String COLUMN_TRAN = "Alvidir";
        static final String COLUMN_INFO = "Interés";
        static final String COLUMN_SKIN = "Herencia";

        static final String SYNC_TABLE_NAME = "DBLastSync";
        static final String COLUMN_LAST_SYNC = "Sync_Datetime";
    }

    public static final String SQL_CREATE_MAINTABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    MainDBContract.FeedEntry.MAIN_TABLE_NAME + " (" +
                    MainDBContract.FeedEntry.COLUMN_TYPE + " VARCHAR(32) NOT NULL, " +
                    MainDBContract.FeedEntry.COLUMN_WORD + " VARCHAR(32), " +
                    MainDBContract.FeedEntry.COLUMN_TRAN + " VARCHAR(32), " +
                    MainDBContract.FeedEntry.COLUMN_INFO + " TEXT, " +
                    MainDBContract.FeedEntry.COLUMN_SKIN + " INTEGER DEFAULT 0, " +
                    "PRIMARY KEY (" +
                    MainDBContract.FeedEntry.COLUMN_WORD + ", " +
                    MainDBContract.FeedEntry.COLUMN_TRAN + "));";

    public static final String SQL_CREATE_SYNCTABLE =
            "CREATE TABLE IF NOT EXISTS " +
                    MainDBContract.FeedEntry.SYNC_TABLE_NAME + " (" +
                    MainDBContract.FeedEntry.COLUMN_LAST_SYNC + " DATETIME, " +
                    "PRIMARY KEY (" +
                    MainDBContract.FeedEntry.COLUMN_LAST_SYNC + "));";

    public static final String SQL_DELETE_MAINTABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.MAIN_TABLE_NAME + ";";

    public static final String SQL_DELETE_SYNCTABLE =
            "DROP TABLE IF EXISTS " + FeedEntry.SYNC_TABLE_NAME + ";";
}
