package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = 23)
public class MainActivity extends AppCompatActivity {
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    BluetoothSocket btSocket0;
    BluetoothSocket btSocket1;
    BluetoothSocket btSocket2;
    BluetoothSocket btSocket3;
    BluetoothSocket btSocket4;
    BluetoothSocket btSocket5;
    int socketCounter = 0;
    private final int BLUETOOTH_PERMISSION_CODE = 1;
    public final boolean PAIRING = false;
    public boolean PERMISSION = false;
    //public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private static final String TAG = "MyActivity";
    private static final String SLAVE_NAME = "JDY-16";
    private static final String SLAVE_PW = "123456";
    Thread thread;
    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };
    String[] PermissionsNeeded = new String[PERMISSIONS.length];
    public boolean isFirstScan = true;
    public int debug = 0; //just to use as a variable to add breakpoints
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_SECURE = UUID.randomUUID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thread = null;
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
            } else {
                Log.d(TAG, "Permission already satisfied\n");
            }
        }
        if (!(PermissionsNeeded.length == 0)) {
            ActivityCompat.requestPermissions(MainActivity.this, PermissionsNeeded, BLUETOOTH_PERMISSION_CODE);
        } else {
            enableBT();
        }
        ///////////////////////////////////
        //connect devices
        Log.d(TAG, "Checking permissions\n");
        if (!btAdapter.isEnabled()) {
            enableBT();
        }
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            PERMISSION = true; //Permission is now available for all other tasks
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
                Log.d(TAG, "Cancelling discovery\n");
            }
        } else {
            Log.d(TAG, "No permission exiting app\n");
            closeApp();
        }

        Log.d(TAG, "===> Start Server !");
        //Log.d(TAG, toString(btDevicesPaired));
        Intent intent = getIntent();
        String address = intent.getStringExtra("3CA5518AD35C");
        startActivity(intent);
        try {
            // This will connect the device with address as passed
            Set<BluetoothDevice> btDevices = btAdapter.getBondedDevices();
            for(BluetoothDevice btDevice : btDevices)
                switch(socketCounter){
                    case 0:
                        Log.d(TAG, "socket 0");
                        btSocket0 = btDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                        btAdapter.listenUsingRfcommWithServiceRecord(MY_UUID_SECURE.fromString());
                        //btSocket0 = btDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                        //btSocket0 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket0.connect();
                        break;
                    case 1:
                        Log.d(TAG, "socket 1");
                        btSocket1 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket1.connect();
                        break;
                    case 2:
                        Log.d(TAG, "socket 2");
                        btSocket2 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket2.connect();
                        break;
                    case 3:
                        Log.d(TAG, "socket 3");
                        btSocket3 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket3.connect();
                        break;
                    case 4:
                        Log.d(TAG, "socket 4");
                        btSocket4 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket4.connect();
                        break;
                    case 5:
                        Log.d(TAG, "socket 5");
                        btSocket5 =(BluetoothSocket) btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class}).invoke(btDevice,1);
                        btAdapter.cancelDiscovery();
                        btSocket5.connect();
                        break;
                }
                socketCounter++;


                Log.d(TAG, "===> Device Connected !");

        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e ){
            e.printStackTrace();
            Log.d(TAG, "===> ERROR!");
        }
    }

    public void enableBT() {
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth not available\n");
        } else {
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

    public void disableBT() {
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth not available\n");
        } else {
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


    public void ScanBLE(View view) {
        isFirstScan = true;

        Log.d(TAG, "Button ScanBLE Pressed\n");
        if (!btAdapter.isEnabled()) {
            enableBT();
        }
        Log.d(TAG, "Looking for unpaired devices\n");
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
                Log.d(TAG, "Cancelling discovery\n");
            }
            Set<BluetoothDevice> btDevicesPaired = btAdapter.getBondedDevices();
            Log.d(TAG, "Starting discovery\n");

            //IntentFilter pairDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
            //registerReceiver(PairingRequest, pairDeviceIntent);
        } else {
            Log.d(TAG, "No scanning permission\n");
        }


        //here we need to scan for the JDY16's and connect them
    }

    public void ToggleLED(View view) {
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            Set<BluetoothDevice> btDevicesPaired = btAdapter.getBondedDevices();
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
        }else{
            Log.d(TAG, "No BLE permission in toggle LED\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults.length == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission has been granted\n");
                    enableBT();
                } else {
                    //closeApp();
                }
            } else if (grantResults.length == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission has been granted\n");
                    enableBT();
                } else {
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(BroadcastReceiver1);
        } catch (Exception e) {
            Log.d(TAG, "Receiver does not exist\n");
        }
    }
}
