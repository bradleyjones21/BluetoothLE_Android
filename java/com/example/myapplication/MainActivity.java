package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private final int BLUETOOTH_CONNECT_PERMISSION_CODE = 1;
    private final int BLUETOOTH_PERMISSION_CODE         = 2;
    private final int BLUETOOTH_SCAN_PERMISSION_CODE    = 3;
    private volatile boolean isPermissionInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission is already granted.\n");
        }else{
            isPermissionInProgress = true;
            requestBluetoothConnectPermission();
            wait();
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission is already granted.\n");
        }else{
            requestBluetoothPermission();
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission is already granted.\n");
        }else{
            requestBluetoothScanPermission();
        }
    }

    public void ScanBLE(View view) {
        System.out.println("Button ScanBLE Pressed\n");
        //here we need to scan for the JDY16's and connect them
    }

    public void ToggleLED(View view) {
        int buttonNumber = 0;
        switch (view.getId()) {
            case (R.id.toggleButton):
                System.out.println("Button LED1 Pressed\n");
                //send toggle message to connection 1
                buttonNumber = 1;
                break;
            case (R.id.toggleButton2):
                System.out.println("Button LED2 Pressed\n");
                //send toggle message to connection 2
                buttonNumber = 2;
                break;
            case (R.id.toggleButton3):
                System.out.println("Button LED3 Pressed\n");
                //send toggle message to connection 3
                buttonNumber = 3;
                break;
        }
        System.out.println(buttonNumber);

    }

    private void requestBluetoothConnectPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION_CODE);
    }

    private void requestBluetoothPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSION_CODE);
    }

    private void requestBluetoothScanPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(grantResults);
                System.out.println("Permission has been granted\n");
            } else {
                System.out.println("Permission has been denied\n");
                MainActivity.this.finish();
            }
        isPermissionInProgress = false;
    }
}