package com.myapp.mt.calldetect;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Lobby extends Activity implements View.OnClickListener {
    Button btnEdit;
    Button btnClose;
    String myNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        btnClose= (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.btnEdit)) {
            DataRepo repo = new DataRepo(this);
            Person person = new Person();
            person = repo.getPersonById(1);

            myNumber=person.phone;

            Intent objIndent = new Intent(this,PersonDetail.class);
            objIndent.putExtra("person_Id", 1);
            objIndent.putExtra("phone_number", myNumber);
            startActivity(objIndent);
        }
        else if(view == findViewById(R.id.btnClose))
        {
            Log.v("Lobby","Clicked close");
            moveTaskToBack(true);
        }
    }
}