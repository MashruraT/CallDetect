package com.myapp.mt.calldetect;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MT on 2018-01-25.
 */

public class CallDetect extends BroadcastReceiver{

    MediaRecorder recorder;
    File audiofile;
    private boolean recordstarted = false;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            {
                String phnNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"Ringing "+phnNumber,Toast.LENGTH_LONG).show();
                Log.v("CallState","Incoming"+phnNumber);
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                String phnNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context,"Connected "+phnNumber,Toast.LENGTH_LONG).show();
                Log.v("CallState","Connected "+phnNumber);

                String out = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());


//                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
//                {
//                    File sampleDir = new File(Environment.DIRECTORY_MUSIC, "/Testrecordings");
//                    if (!sampleDir.exists()) {
//                        sampleDir.mkdirs();
//                    }
//                }
//                else
//                {
//                    File sampleDir = new File(context.getDir(Environment.DIRECTORY_RINGTONES,context.MODE_PRIVATE), "/Testrecordings");
//                    if (!sampleDir.exists()) {
//                        sampleDir.mkdirs();
//                    }
//                }
                //File sampleDir = new File(Environment.getRootDirectory(), "/Testrecordings");
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String pathDir = baseDir + "/Android/data/com.myapp.mt.calldetect/";

                File sampleDir = new File(pathDir + File.separator + "TestRecordings");
                if (!sampleDir.exists()) {
                    sampleDir.mkdirs();
                }

                String file_name = "Record"+out;
                try {
                    audiofile = File.createTempFile(file_name, ".amr", sampleDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audiofile.getAbsolutePath());
                try {
                    recorder.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("CallState","Writing file"+audiofile.getAbsolutePath());
                recorder.start();
                recordstarted = true;

                Log.v("CallState","Recording started");
                Toast.makeText(context,"Recording started",Toast.LENGTH_LONG).show();

            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE))
            {
                Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
                Log.v("CallState","Idle");
                if (recordstarted) {
                    Toast.makeText(context,"Finished recording",Toast.LENGTH_LONG).show();
                    Log.v("CallState","Recording complete");
                    recorder.stop();
                    recordstarted = false;
                    recorder.release();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
