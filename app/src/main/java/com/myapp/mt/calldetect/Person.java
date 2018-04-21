package com.myapp.mt.calldetect;

/**
 * Created by IT001 on 23-Jun-16.
 */
public class Person {
    // Labels table name
    public static final String TABLE = "Student";
    public static final String TABLE_History = "History";
    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_age = "age";
    public static final String KEY_email = "email";
    public static final String KEY_phone = "phone";
    public static final String KEY_emergencyname = "emergencyName";
    public static final String KEY_emergencyemail = "emergencyEmail";
    public static final String KEY_emergencyphone = "emergencyPhone";
    public static final String KEY_audiosample1 = "audiosample1";
    public static final String KEY_audiosample2 = "audiosample2";

    public static final String KEY_Sl = "sl";
    public static final String KEY_time = "time";
    public static final String KEY_score = "score";
    public static final String KEY_decision = "decision";
    public static final String KEY_subjName = "subjname";


    // property help us to keep data
    public int person_ID;
    public String name;
    public int age;
    public String email;
    public String phone;
    public String emergency_name;
    public String emergency_email;
    public String emergency_phone;
    public String audio_sample1;
    public String audio_sample2;
    public boolean registered;

    public int sl_no;
    public String time;
    public double score;
    public int decision;
    public String subjName;
}