package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.le.BluetoothLeScanner;

import java.util.ArrayList;

@RequiresApi(api = 23)
public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    private final int BLUETOOTH_PERMISSION_CODE = 1;
    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private static final String TAG = "MyActivity";
    private static final String SLAVE_NAME = "JDY-16";
    private static final String SLAVE_PW = "123456";

    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

    };
    String[] PermissionsNeeded = new String[PERMISSIONS.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int Counter; //used to index an array to copy over the permissions that need approval
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        Counter = 0;
        for (String permission : PERMISSIONS) {
            if (!(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)) {
                PermissionsNeeded[Counter] = permission;
                Counter++;
            }else{
                Log.d(TAG, "Permission already satisfied\n");
            }
        }
        if (!(PermissionsNeeded.length == 0)) {
            ActivityCompat.requestPermissions(MainActivity.this, PermissionsNeeded, BLUETOOTH_PERMISSION_CODE);
        } else {
            enableBT();
        }
    }

    public void enableBT() {
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth not available\n");
        }else{
            if (!btAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    closeApp();
                }
                startActivity(enableBTIntent);
                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(BroadcastReceiver1, BTIntent);
            }
        }
    }

    public void disableBT(){
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth not available\n");
        }else {
            if (btAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    closeApp();
                }
                btAdapter.disable();
                IntentFilter BTIntent = new IntentFilter(btAdapter.ACTION_STATE_CHANGED);
                registerReceiver(BroadcastReceiver1, BTIntent);
            }
        }
    }

    private final BroadcastReceiver BroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, btAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "STATE_OFF\n");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "STATE_TURNING_OFF\n");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "STATE_ON\n");

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "STATE_TURNING_ON\n");
                        break;

                }
            }
        }
    };

    //Broadcast receiver for devices that are not yet paired
    private final BroadcastReceiver BroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG, "Action found\n");
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onReceive: " + device.getName() + ":" + device.getAddress());
                    if (device.getName() == SLAVE_NAME){//JDY-16
                        
                    }
                }else{
                    Log.d(TAG, "Do not have permission");
                    closeApp();
                }
            }
        }
    };

    public void ScanBLE(View view) {
        Log.d(TAG, "Button ScanBLE Pressed\n");
        if (!btAdapter.isEnabled()) {
            enableBT();
        }
        Log.d(TAG, "Looking for unpaired devices\n");
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH)== PackageManager.PERMISSION_GRANTED)&&
           (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)&&
           (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)){
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
                Log.d(TAG, "Cancelling discovery\n");
            }
            Log.d(TAG, "Starting discovery\n");
            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(BroadcastReceiver2, discoverDevicesIntent);
        }else{
            Log.d(TAG, "No scanning permission\n");
        }



        //here we need to scan for the JDY16's and connect them
    }

    public void ToggleLED(View view) {
        switch (view.getId()) {
            case (R.id.toggleButton):
                Log.d(TAG, "Button LED1 Pressed\n");
                //send toggle message to connection 1
                break;
            case (R.id.toggleButton2):
                Log.d(TAG, "Button LED2 Pressed\n");
                //send toggle message to connection 2
                break;
            case (R.id.toggleButton3):
                Log.d(TAG, "Button LED3 Pressed\n");
                //send toggle message to connection 3
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults.length > 0){
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Permission has been granted\n");
                        enableBT();
                    }else{
                        //closeApp();
                    }
                }else if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                     && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Permission has been granted\n");
                        enableBT();
                    }else{
                        //closeApp();
                    }
                }
            } else {
                //closeApp();
            }
    }

    public void closeApp() {
        Log.d(TAG, "Permission has been denied\n");
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            unregisterReceiver(BroadcastReceiver1);
        }catch(Exception e){
            Log.d(TAG, "Receiver does not exist\n");
        }
    }

}