package com.example.martin.rsstrend;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private BeaconManager beaconManager;
    BeaconManager.RangingListener rangingListener;
    private Region region;

    private EditText fileNameEditText;
    private Button startButton;
    private TextView rssiTextView;

    private boolean isRanging = false;

    private FileOutputStream fileOutputStream;

    private  Calendar calendar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        beaconManager = new BeaconManager(this);

        rssiTextView = findViewById(R.id.rssi_textview);

        rangingListener = new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if(!list.isEmpty()){
                    Beacon gotBeacon = list.get(0);
                    String fileName = fileNameEditText.getText().toString().trim() + ".txt";
                    String address = gotBeacon.getMacAddress().toString();
                    int rssi = gotBeacon.getRssi();
                    rssiTextView.setText("RSSI: "+rssi);

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int seconds = calendar.get(Calendar.SECOND);
                    long current_time = System.currentTimeMillis();

                    // String time = year+"/"+month+"/"+day+"-"+hour+":"+minute+":"+seconds;
                    String time = current_time + "";

                    String fileContents = address+"\t"+rssi+"\t"+time+"\n";

                    try{
                        fileOutputStream = openFileOutput(fileName,Context.MODE_APPEND);
                        fileOutputStream.write(fileContents.getBytes());
                        fileOutputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        beaconManager.setRangingListener(rangingListener);

        fileNameEditText = findViewById(R.id.fileName_editText);

        fileNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    if(i == KeyEvent.KEYCODE_ENTER) {
                        if (fileNameEditText.getText().toString().trim() == null || fileNameEditText.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Enter file name", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            startRanging();
                        }
                    }
                }
                return true;
            }
        });

        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fileNameEditText.getText().toString().trim() == null || fileNameEditText.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter file name",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(isRanging){
                        stopRanging();
                    }else{
                        startRanging();
                    }
                }

            }
        });

        region = new Region("ranged region",UUID.fromString(BeaconContract.Beacon3.UUID), BeaconContract.Beacon3.major, BeaconContract.Beacon3.minor);


    }

    private void startRanging(){
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
                fileNameEditText.setEnabled(false);
                startButton.setText("STOP");
                isRanging = true;
            }
        });
    }



    private void stopRanging(){
        if(isRanging){
            beaconManager.stopRanging(region);
            fileNameEditText.setEnabled(true);
            startButton.setText("START");
            isRanging = false;
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
    }
}
