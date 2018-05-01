package com.myapp.mt.calldetect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 *
 * Created by MT on 2018-01-25.
 */

public class CallDetect extends BroadcastReceiver {

    //static MediaRecorder recorder;
    static File audiofile;
    static boolean recordstarted = false;
    static boolean ringing = false;
    static int lastState = TelephonyManager.CALL_STATE_IDLE;
    static String savedNumber;
    static String myNumber;
    static boolean isIncoming;


    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "TestRecoedings";
    private static final String AUDIO_RECORDER_TEMP_FILE = "Temp_file.raw";
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private static boolean isRecording = false;

    private String SERVER_URL = "http://162.246.156.239/emoDetect/upload.php";
    private String selectedFilePath;

    @Override
    public void onReceive(Context context, Intent intent) {

        bufferSize = AudioRecord.getMinBufferSize(16000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        Log.v("CallState", "OnRecieve");
//        startRecording();
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.v("CallState", "Calling " + savedNumber);
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else {
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            Log.v("CallState", "State=" + Integer.toString(state));

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
                Log.v("CallState", "Ringing: " + number);
                Toast.makeText(context, "Ringing: " + number, Toast.LENGTH_SHORT).show();

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                startRecording(context);

                Log.v("CallState", "Call connected");
                Toast.makeText(context, "Call connected", Toast.LENGTH_SHORT).show();

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
                stopRecording(context);
                Log.v("CallState", "Idle");
                Toast.makeText(context, "Idle", Toast.LENGTH_SHORT).show();
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

//    private void startRecording(Context context) {
//        String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        String pathDir = basedir + "/Android/data/com.myapp.mt.calldetect/";
//
//        //basedir="sdcard/";
//        File sampleDir = new File(basedir + File.separator + "TestRecordings");
//        if (!sampleDir.exists()) {
//            sampleDir.mkdirs();
//        }
//
//        String out = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
//
//        String file_name = "Record"+out;
//        try {
//            audiofile = File.createTempFile(file_name, ".wav", sampleDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        recorder = new MediaRecorder();
////      recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//
//        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
//        recorder.setAudioSamplingRate(16000);
//        recorder.setOutputFile(audiofile.getAbsolutePath());
//        Log.v("CallState","Writing file "+audiofile.getAbsolutePath());
//        try {
//            recorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            recorder.start();
//            recordstarted = true;
//        }
//        catch(Exception e)
//        {;}
//        Toast.makeText(context,"Recording started",Toast.LENGTH_LONG).show();
//        Log.v("CallState","Recording started");
//    }
//
//    private void stopRecording(Context context) {
//
//        if (recordstarted) {
//            try {
//                recorder.stop();
//                recordstarted = false;
//            }
//            catch(Exception e)
//            {
//
//                System.out.print("could not stop");
//            }
//
//            Toast.makeText(context,"Recording finished",Toast.LENGTH_LONG).show();
//            Log.v("CallState","Recording finished");
//        }
//    }

    private String getFilename(Context context) {
        String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(basedir + File.separator + "TestRecordings");

        //File  = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        DataRepo repo = new DataRepo(context);
        Person person = repo.getPersonById(1);

        myNumber=person.phone;

        String out = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());

        String file_name = myNumber+"_"+out+"_";
        Log.v("CallDetect","Saving to: "+file.getAbsolutePath() + "/" + file_name + AUDIO_RECORDER_FILE_EXT_WAV);
        selectedFilePath=file.getAbsolutePath() + "/" + file_name + AUDIO_RECORDER_FILE_EXT_WAV;
        return (file.getAbsolutePath() + "/" + file_name + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename(){
        String basedir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(basedir + File.separator + "TestRecordings");

        //File  = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        //String out = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());

        //String file_name = "Temp_file";
        File tempFile = new File(basedir,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording(Context context){

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1){
            recorder.startRecording();
            Log.v("CallState","Recording started");
            Toast.makeText(context,"Recording Started",Toast.LENGTH_SHORT).show();
        }


        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording(Context context){
        if(recorder != null){
            isRecording = false;
            Log.v("CallState","Recording finished");
            Toast.makeText(context,"Recording finished",Toast.LENGTH_SHORT).show();

            int i = recorder.getState();
            if(i==1)
            {
                recorder.stop();
            }

            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(),getFilename(context));
        deleteTempFile();

        if(selectedFilePath != null){
            final int[] uploadResult = new int[1];

            final Context c = context;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //creating new thread to handle Http Operations
                    uploadResult[0] = uploadFile(c, selectedFilePath);
                }
            }).start();
            Log.v("Upload Result","Server Response Code: "+ uploadResult[0]);
        }else{
            Log.v("File upload", "File path not valid");
        }
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            AppLog.logString("File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    public int uploadFile(Context context, final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        long maxLength = 50*1024*1024;
        if (!selectedFile.isFile()){
            Log.v("CallDetect","File not found");
            return 0;
        }
        else if(selectedFile.length()> maxLength){
            Log.v("CallDetect", "File size too big");
        }
        else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.v(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    Log.v("CallDetect","File " + fileName + " has been successfully uploaded");

                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.v("CallDetect","File Not Found");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.v("File Upload","URL error");

            } catch (IOException e) {
                e.printStackTrace();
                Log.v("File Upload", "Read/Write error");
            }
        }
            return serverResponseCode;


    }

}
