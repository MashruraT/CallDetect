package com.myapp.mt.calldetect;

/**
 * Created by IT001 on 23-Jun-16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_PERSON = "CREATE TABLE " + Person.TABLE  + "("
                + Person.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Person.KEY_name + " TEXT, "
                + Person.KEY_age + " INTEGER, "
                + Person.KEY_email + " TEXT  ,"
                + Person.KEY_phone + " TEXT  ,"
                + Person.KEY_emergencyname + " TEXT, "
                + Person.KEY_emergencyemail + " TEXT, "
                + Person.KEY_emergencyphone + " TEXT, "
                + Person.KEY_audiosample1 + " TEXT, "
                + Person.KEY_audiosample2 + " TEXT )";

        db.execSQL(CREATE_TABLE_PERSON);
        Log.v("DatabaseCreate","User table created successfully");


        String CREATE_TABLE_HISTORY = "CREATE TABLE " + Person.TABLE_History  + "("
                + Person.KEY_Sl  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Person.KEY_subjName + " TEXT, "
                + Person.KEY_time + " TEXT, "
                + Person.KEY_score + " INTEGER, "
                + Person.KEY_decision + " INTEGER )";

        db.execSQL(CREATE_TABLE_HISTORY);
        Log.v("DatabaseCreate","Log table created successfully");
        //Toast.makeText(this.getActivity(), "No Log saved!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Person.TABLE);

        // Create tables again
        onCreate(db);

    }

}