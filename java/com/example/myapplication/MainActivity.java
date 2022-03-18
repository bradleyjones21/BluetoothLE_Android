package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.le.BluetoothLeScanner;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.os.HandlerThread;

@RequiresApi(api = 23)
public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    private final int BLUETOOTH_PERMISSION_CODE = 1;
    public final boolean PAIRING = false;
    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private static final String TAG = "MyActivity";
    private static final String SLAVE_NAME = "JDY-16";
    private static final String SLAVE_PW = "123456";
    Thread thread;
    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

    };
    String[] PermissionsNeeded = new String[PERMISSIONS.length];
    public boolean isFirstScan = true;
    public int debug = 0;


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
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
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

    //Broadcast receiver for devices that are not yet paired
    private final BroadcastReceiver BroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            final String action = intent.getAction();
            Log.d(TAG, "Action found\n");
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onReceive: " + device.getName() + ":" + device.getAddress());
                    if (device.getName() != null) {
                        if (device.getName().equals(SLAVE_NAME)) {//JDY-16
                            btDevices.add(device);
                            if (PAIRING) {
                                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                                    if (btAdapter.isDiscovering()) {
                                        btAdapter.cancelDiscovery();
                                        Log.d(TAG, "Cancelled discovery");
                                    }
                                    device.createBond();
                                    Log.d(TAG, "Creating bond");
                                }
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Do not have permission");
                    closeApp();
                }
            }

            if (isFirstScan) {

                thread = new Thread(new Runnable() {//62747f5
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            btAdapter.cancelDiscovery();
                            debug = 1;
                        }
                    }
                });
                thread.start();
                thread.run();
                isFirstScan = false;
            }

        }
    };


        //For receiving pairing requests
        private final BroadcastReceiver PairingRequest = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                //the pin in case you need to accept for an specific pin
                Log.d("PIN", " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));
                //maybe you look for a name or address
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Bonded", device.getName());
                    byte[] pinBytes;
                    try {
                        pinBytes = ("" + pin).getBytes("UTF-8");
                        device.setPin(pinBytes);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                //loop checks every 100ms if the device is connected with a timeout of 10sec until it keeps looking
                thread = new Thread(new Runnable() {//62747f5
                    @Override
                    public void run() {
                        int x = 0;
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                            for (int ii = 0; ii < 100; ii++) {
                                x = device.getBondState(); //just for debugging
                                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                    Log.d(TAG, "Break device connected, from thread");

                                    break;
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        enableBT();
                        if (!btAdapter.isDiscovering()) {
                            btAdapter.startDiscovery();
                            Log.d(TAG, "Starting discovery, from thread");
                        }
                    }
                });
                thread.start();
                thread.run();
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
                if(PAIRING) {
                    btAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(BroadcastReceiver2, discoverDevicesIntent);
                }
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
                        btDevicesPaired[0]
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
