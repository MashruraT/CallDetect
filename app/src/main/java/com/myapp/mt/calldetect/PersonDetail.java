package com.myapp.mt.calldetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class PersonDetail extends Activity implements View.OnClickListener{

    Button btnSave ;//  btnDelete;
    Button btnClose;
    EditText editTextName;
    EditText editTextAge;
    EditText editTextEmail;
    EditText editTextPhone;
    EditText editTextEmergencyName;
    EditText editTextEmergencyEmail;
    EditText editTextEmergencyPhone;
    private int _Person_Id=0;
    private String _Phone_Number = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        btnSave = (Button) findViewById(R.id.btnSave);
      //  btnDelete = (Button) findViewById(R.id.btnDelete);
        //btnClose = (Button) findViewById(R.id.btnClose);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        editTextEmergencyName = (EditText) findViewById(R.id.editTextEmergencyName);
        editTextEmergencyEmail = (EditText) findViewById(R.id.editTextEmergencyEmail);
        editTextEmergencyPhone = (EditText) findViewById(R.id.editTextEmergencyPhone);

        btnSave.setOnClickListener(this);
      //  btnDelete.setOnClickListener(this);
        //btnClose.setOnClickListener(this);



        _Person_Id =0;
        _Phone_Number=null;
        Intent intent = getIntent();
        _Person_Id =intent.getIntExtra("person_Id", 0);
        _Phone_Number = intent.getStringExtra("phone_number");
        DataRepo repo = new DataRepo(this);
        Person person = new Person();
        person = repo.getPersonById(_Person_Id);

        editTextAge.setText(String.valueOf(person.age));
        editTextName.setText(person.name);
        editTextEmail.setText(person.email);
        editTextEmergencyName.setText(person.emergency_name);
        editTextEmergencyEmail.setText(person.emergency_email);
        editTextEmergencyPhone.setText(person.emergency_phone);

        if(_Phone_Number!=null)
        {
            editTextPhone.setText(_Phone_Number);
            editTextPhone.setInputType(InputType.TYPE_NULL);
            editTextPhone.setTextIsSelectable(false);
        }
        else
        {
            editTextPhone.setText(person.phone);
        }
    }



    public void onClick(View view) {
        if (view == findViewById(R.id.btnSave)){

            DataRepo repo = new DataRepo(this);
            Person person = new Person();
            person.name=editTextName.getText().toString();
            person.age= Integer.parseInt(editTextAge.getText().toString());
            person.email=editTextEmail.getText().toString();
            person.phone=editTextPhone.getText().toString();
            String myNumber = person.phone;
            person.emergency_name=editTextEmergencyName.getText().toString();
            person.emergency_phone=editTextEmergencyPhone.getText().toString();
            person.emergency_email=editTextEmergencyEmail.getText().toString();
            person.person_ID=_Person_Id;
            String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
            person.audio_sample1 = basedir + File.separator + "TestRecordings"+ "/"+myNumber+"_1.wav";
            person.audio_sample2 = basedir + File.separator + "TestRecordings"+ "/"+myNumber+"_2.wav";

            Log.v("DB","Name: " + person.name );
            Log.v("DB","Age: " + person.age );
            Log.v("DB","Email: " + person.email );
            Log.v("DB","Phone: " + person.phone );
            Log.v("DB","Emergency Name: " + person.emergency_name );
            Log.v("DB","Emergency Email: " + person.emergency_email );
            Log.v("DB","Emergency Phone: " + person.emergency_phone );
            Log.v("DB","Audio1: " + person.audio_sample1 );
            Log.v("DB","Audio2: " + person.audio_sample2 );
            if (_Person_Id==0){
                _Person_Id = repo.insert(person);

                Toast.makeText(this,"Your profile has been created!", Toast.LENGTH_SHORT).show();
//                finish();
//                moveTaskToBack(true);
                Intent intent = new Intent(this,RecordSample1.class);
                intent.putExtra("phone_number",myNumber);
//                intent.putExtra("Self_name", person.name);
                startActivity(intent);
            }else{
                repo.update(person);
                Toast.makeText(this,"Your profile info has been successfully updated", Toast.LENGTH_SHORT).show();
//                finish();
//                moveTaskToBack(true);
                Intent intent = new Intent(this,RecordSample1.class);
                intent.putExtra("phone_number",myNumber);
//                intent.putExtra("Self_name", person.name);
                startActivity(intent);
            }
       //     BandMainActivity bandMainActivity = new BandMainActivity(this);
            //bandMainActivity.start();
        }//else if (view== findViewById(R.id.btnDelete)){
           // StudentRepo repo = new StudentRepo(this);
           // repo.delete(_Student_Id);
          //  Toast.makeText(this, "Student Record Deleted", Toast.LENGTH_SHORT);

          //  BandMainActivity bandMainActivity = new BandMainActivity(this);
          // finish();

  //  }
//    else if (view== findViewById(R.id.btnClose)){
//        finish();
//    }


}

}
