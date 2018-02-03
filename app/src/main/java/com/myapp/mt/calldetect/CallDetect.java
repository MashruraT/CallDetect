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
 *
 * Created by MT on 2018-01-25.
 */

public class CallDetect extends BroadcastReceiver{

    MediaRecorder recorder;
    File audiofile;
    private boolean recordstarted;
    private boolean ringing = false;
    private int lastState = TelephonyManager.CALL_STATE_IDLE;
    private String savedNumber;
    private boolean isIncoming;

//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        try {
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
//            {
//
//                Log.v("CallState","Entering ringing state. Recordstarted: "+recordstarted);
//                String phnNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Toast.makeText(context,"Ringing "+phnNumber,Toast.LENGTH_LONG).show();
//                Log.v("CallState","Incoming"+phnNumber);
//                ringing =true;
//                recordstarted=false;
//
//                Log.v("CallState","Exiting ringing state. Recordstarted: "+recordstarted);
//            }
//            else if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK))
//            {
//                Log.v("CallState","Entering offhook state. Recordstarted: "+recordstarted);
//
//                String phnNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Toast.makeText(context,"Connected "+phnNumber,Toast.LENGTH_LONG).show();
//                Log.v("CallState","Connected "+phnNumber);
//                Log.v("CallState","Recordstarted: "+recordstarted);
//
////                if(!recordstarted)
////                {
////                    String out = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
////                    String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
////                    String pathDir = baseDir + "/Android/data/com.myapp.mt.calldetect/";
////
////                    File sampleDir = new File(pathDir + File.separator + "TestRecordings");
////                    if (!sampleDir.exists()) {
////                        sampleDir.mkdirs();
////                    }
////
////                    String file_name = "Record"+out;
////                    try {
////                        audiofile = File.createTempFile(file_name, ".amr", sampleDir);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
////
////                    recorder = new MediaRecorder();
//////                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
////
////                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
////                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
////                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
////                    recorder.setOutputFile(audiofile.getAbsolutePath());
////                    try {
////                        recorder.prepare();
////
////                    } catch (IllegalStateException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    recorder.start();
////                    recordstarted = true;
////
////                    Log.v("CallState","Writing file"+audiofile.getAbsolutePath());
////                    Log.v("CallState","Recording started");
////                    Toast.makeText(context,"Recording started",Toast.LENGTH_LONG).show();
////                }
//                Log.v("CallState","Exiting offhook state. Recordstarted: "+recordstarted);
//
//            }
//            else if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE))
//            {
//                Log.v("CallState","Entering idle state. Recordstarted: "+recordstarted);
//                Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
//                Log.v("CallState","Idle");
//
//                Toast.makeText(context,"Finished recording",Toast.LENGTH_LONG).show();
//                Log.v("CallState","Recording Status: "+recordstarted);
//                if(recordstarted)
//                {
//                    Log.v("CallState","Recording Status: "+recordstarted);
//                    recorder.stop();
//                    recorder.release();
//                }
//                recordstarted=false;
//                Log.v("CallState","Exiting idle state. Recordstarted: "+recordstarted);
//
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        startRecording();
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.v("CallState","Calling " +savedNumber);
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else
            {
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            onCallStateChanged(context, state, number);
        }
    }

    public void onCallStateChanged(Context context, int state, String number) {
        //If I comment out this if statement, the Idle-state condition is never executed.
        //If it is left as it is, when the call is over, it toasts the Idle-state condition, but when exactly the recording stops in unknown.
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
//                callStartTime = new Date();
                savedNumber = number;
//                onIncomingCallReceived(context, number, callStartTime);
                Log.v("CallState","Ringing: "+number);
                Toast.makeText(context,"Ringing: "+number,Toast.LENGTH_LONG).show();

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(!recordstarted)
                    startRecording(context);
                Log.v("CallState","Call connected");
                Toast.makeText(context,"Call connected",Toast.LENGTH_LONG).show();

//                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
//                    isIncoming = false;
//                    //callStartTime = new Date();
//                    startRecording(context);
//                    Log.v("CallState","Outgoing call connected");
//                    Toast.makeText(context,"Outgoing call connected",Toast.LENGTH_LONG).show();
//                    //onOutgoingCallStarted(context, savedNumber, callStartTime);
//                } else {
//                    isIncoming = true;
//                    //callStartTime = new Date();
//                    startRecording(context);
//
//                    Log.v("CallState","Incoming call connected "+savedNumber);
//                    Toast.makeText(context,"Incoming call connected "+ savedNumber,Toast.LENGTH_LONG).show();
//                    //onIncomingCallAnswered(context, savedNumber, callStartTime);
//                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if(recordstarted)
                    stopRecording(context);
                Log.v("CallState","Idle");
                Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
//                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
//                    //Ring but no pickup-  a miss
//                    //onMissedCall(context, savedNumber, callStartTime);
//
//                    Log.v("CallState","Call missed from " + savedNumber);
//                    Toast.makeText(context,"Call missed from " +savedNumber,Toast.LENGTH_LONG).show();
//                } else if (isIncoming) {
//                    stopRecording(context);
//                    Log.v("CallState","Idle");
//                    Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
//                    //onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
//                } else {
//                    stopRecording(context);
//                    Log.v("CallState","Idle");
//                    Toast.makeText(context,"Idle",Toast.LENGTH_LONG).show();
                //onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
//                }
                break;
        }
        lastState = state;
    }

    private void startRecording(Context context) {
        String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String pathDir = basedir + "/Android/data/com.myapp.mt.calldetect/";

        File sampleDir = new File(basedir + File.separator + "TestRecordings");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String out = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());

        String file_name = "Record"+out;
        try {
            audiofile = File.createTempFile(file_name, ".amr", sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }


        recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        Log.v("CallState","Writing file "+audiofile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recordstarted = true;
        Toast.makeText(context,"Recording started",Toast.LENGTH_LONG).show();
        Log.v("CallState","Recording started");
    }

    private void stopRecording(Context context) {
        if (recordstarted) {
            recorder.stop();
            recordstarted = false;

            Toast.makeText(context,"Recording finished",Toast.LENGTH_LONG).show();
            Log.v("CallState","Recording finished");
        }
    }


}
