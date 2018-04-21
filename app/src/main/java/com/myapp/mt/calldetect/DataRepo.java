package com.myapp.mt.calldetect;

/**
 * Created by IT001 on 23-Jun-16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DataRepo {
    private DBHelper dbHelper;

    public DataRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(Person person) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Person.KEY_age, person.age);
        values.put(Person.KEY_email,person.email);
        values.put(Person.KEY_name, person.name);
        values.put(Person.KEY_phone, person.phone);
        values.put(Person.KEY_audiosample1, person.audio_sample1);
        values.put(Person.KEY_audiosample1, person.audio_sample2);
        values.put(Person.KEY_emergencyname, person.emergency_name);
        values.put(Person.KEY_emergencyphone,person.emergency_phone);
        values.put(Person.KEY_emergencyemail, person.emergency_email);
        // Inserting Row
        long student_Id = db.insert(Person.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) student_Id;
    }
    public int saveLog(Person person) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Person.KEY_subjName, person.subjName);
        values.put(Person.KEY_time, person.time);
        values.put(Person.KEY_score,person.score);
        values.put(Person.KEY_decision, person.decision);
        // Inserting Row
        long SL_No = db.insert(Person.TABLE_History, null, values);
        db.close(); // Closing database connection
        return (int) SL_No;
    }
    public void delete(int student_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Person.TABLE, Person.KEY_ID + "= ?", new String[] { String.valueOf(student_Id) });
        db.close(); // Closing database connection
    }

    public void update(Person person) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Person.KEY_age, person.age);
        values.put(Person.KEY_email,person.email);
        values.put(Person.KEY_name, person.name);
        values.put(Person.KEY_phone, person.phone);
        Log.v("DB","Emergency Name: " + person.emergency_name);
        Log.v("DB","Emergency Phone: " + person.emergency_phone);
        Log.v("DB","Emergency Email: " + person.emergency_email);
        values.put(Person.KEY_emergencyname, person.emergency_name);
        values.put(Person.KEY_emergencyphone,person.emergency_phone);
        values.put(Person.KEY_emergencyemail, person.emergency_email);
        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Person.TABLE, values, Person.KEY_ID + "= ?", new String[] { String.valueOf(person.person_ID) });
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getPersonList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Person.KEY_ID + "," +
                Person.KEY_name + "," +
                Person.KEY_email + "," +
                Person.KEY_phone + "," +
                Person.KEY_age + "," +
                Person.KEY_emergencyname + "," +
                Person.KEY_emergencyphone + "," +
                Person.KEY_emergencyemail +
                " FROM " + Person.TABLE;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> person = new HashMap<String, String>();
                person.put("id", cursor.getString(cursor.getColumnIndex(Person.KEY_ID)));
                person.put("name", cursor.getString(cursor.getColumnIndex(Person.KEY_name)));
                personList.add(person);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return personList;

    }
    public String getFirstStudentEmergencyPhone() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Person.KEY_ID + "," +
                Person.KEY_name + "," +
                Person.KEY_email + "," +
                Person.KEY_phone + "," +
                Person.KEY_age + "," +
                Person.KEY_emergencyname + "," +
                Person.KEY_emergencyphone + "," +
                Person.KEY_emergencyemail +
                " FROM " + Person.TABLE;

        //Student student = new Student();
       // ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        String phone ="";
        if (cursor.moveToFirst()) {
         //   do {
           //     HashMap<String, String> student = new HashMap<String, String>();
            //    student.put("id", cursor.getString(cursor.getColumnIndex(Student.KEY_ID)));
             phone =     cursor.getString(cursor.getColumnIndex(Person.KEY_emergencyphone));
           //     studentList.add(student);

           // } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return phone;
    }
    public String getFirstStudentSelfName() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Person.KEY_ID + "," +
                Person.KEY_name + "," +
                Person.KEY_email + "," +
                Person.KEY_phone + "," +
                Person.KEY_age + "," +
                Person.KEY_emergencyname + "," +
                Person.KEY_emergencyphone + "," +
                Person.KEY_emergencyemail +
                " FROM " + Person.TABLE;

        //Student student = new Student();
        // ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        String name ="";
        if (cursor.moveToFirst()) {
            //   do {
            //     HashMap<String, String> student = new HashMap<String, String>();
            //    student.put("id", cursor.getString(cursor.getColumnIndex(Student.KEY_ID)));
            name =     cursor.getString(cursor.getColumnIndex(Person.KEY_name));
            //     studentList.add(student);

            // } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return name;
    }


    public ArrayList<HashMap<String, String>> getDepressionLog() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Person.KEY_subjName + "," +
                Person.KEY_time + "," +
                Person.KEY_score + "," +
                Person.KEY_decision+
                " FROM " + Person.TABLE_History;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> DepressionLog = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> Log = new HashMap<String, String>();
                Log.put("Name", cursor.getString(cursor.getColumnIndex(Person.KEY_subjName)));
                Log.put("Time", cursor.getString(cursor.getColumnIndex(Person.KEY_time)));
                Log.put("Score", cursor.getString(cursor.getColumnIndex(Person.KEY_score)));
                Log.put("Decision",cursor.getString(cursor.getColumnIndex(Person.KEY_decision)));
                DepressionLog.add(Log);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return DepressionLog;

    }
    public Person getPersonById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Person.KEY_ID + "," +
                Person.KEY_name + "," +
                Person.KEY_email + "," +
                Person.KEY_phone + "," +
                Person.KEY_age + "," +
                Person.KEY_emergencyname + "," +
                Person.KEY_emergencyphone + "," +
                Person.KEY_emergencyemail +
                " FROM " + Person.TABLE
                + " WHERE " +
                Person.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Person person = new Person();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                person.person_ID =cursor.getInt(cursor.getColumnIndex(Person.KEY_ID));
                person.name =cursor.getString(cursor.getColumnIndex(Person.KEY_name));
                person.email  =cursor.getString(cursor.getColumnIndex(Person.KEY_email));
                person.phone  =cursor.getString(cursor.getColumnIndex(Person.KEY_phone));
                person.age =cursor.getInt(cursor.getColumnIndex(Person.KEY_age));
                person.emergency_name =cursor.getString(cursor.getColumnIndex(Person.KEY_emergencyname));
                person.emergency_email  =cursor.getString(cursor.getColumnIndex(Person.KEY_emergencyemail));
                person.emergency_phone =cursor.getString(cursor.getColumnIndex(Person.KEY_emergencyphone));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return person;
    }

}