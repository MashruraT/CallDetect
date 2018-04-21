package com.myapp.mt.calldetect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener{
    Button btnContinue;
    Button btnEdit;

    String myNumber=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(this);

        btnEdit = (Button) findViewById(R.id.btnedit);
        DataRepo repo = new DataRepo(this);

        ArrayList<HashMap<String, String>> personList =  repo.getPersonList();
        if(personList.size()==0) {
            btnEdit.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(this);
        }


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAPTURE_AUDIO_OUTPUT)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_NUMBERS)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.RECORD_AUDIO,Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAPTURE_AUDIO_OUTPUT,Manifest.permission.READ_PHONE_NUMBERS,Manifest.permission.READ_SMS},1);
//            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE))
//            {
//            }
        }
        else
        {
            System.out.println("Permission denied to detect incoming calls");
        }
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS)!= PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},1);
////            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE))
////            {
////            }
//        }
//        else
//        {
//            System.out.println("Permission denied to process outgoing calls");
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        btnEdit = (Button) findViewById(R.id.btnedit);
        DataRepo repo = new DataRepo(this);

        ArrayList<HashMap<String, String>> personList = repo.getPersonList();
        if (personList.size() == 0) {
            btnEdit.setVisibility(View.INVISIBLE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode)
        {
            case 1:{
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        myNumber = telephonyManager.getLine1Number();

                        Log.v("CallDetect", "Phone number: "+myNumber);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }

    }
    @Override
    public void onClick(View view){
        if (view == findViewById(R.id.btnContinue)) {
            DataRepo repo = new DataRepo(this);

            ArrayList<HashMap<String, String>> personList = repo.getPersonList();
            Log.v("CallDetect","Size of person list: "+personList.size());
            if (personList.size() == 0) {
                Intent intent = new Intent(this, PersonDetail.class);
                intent.putExtra("person_Id", 0);
                if(myNumber!=null)
                {
                    intent.putExtra("phone_number",myNumber);
                }

                startActivity(intent);
            } else {
                moveTaskToBack(true);
            }
        }
        else if (view == findViewById(R.id.btnedit)) {
            Intent objIndent = new Intent(this,PersonDetail.class);
            objIndent.putExtra("person_Id", 1);
            if(myNumber!=null)
            {
                objIndent.putExtra("phone_number",myNumber);
            }
            startActivity(objIndent);
        }
    }

}
