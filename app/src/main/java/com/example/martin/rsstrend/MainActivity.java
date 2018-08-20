package com.example.martin.rsstrend;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private boolean isRanging = false;

    private FileOutputStream fileOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(this);

        rangingListener = new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if(!list.isEmpty()){
                    Beacon gotBeacon = list.get(0);
                    String fileName = fileNameEditText.getText().toString().trim() + ".txt";
                    String address = gotBeacon.getMacAddress().toString();
                    int rssi = gotBeacon.getRssi();

                    String fileContents = address+"\t"+rssi+"\n";

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
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if(fileNameEditText.getText().toString().trim() == null || fileNameEditText.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"Enter file name",Toast.LENGTH_SHORT).show();
                        return false;
                    }else{
                        startRanging();
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
                    startRanging();
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
        startRanging();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
    }
}
