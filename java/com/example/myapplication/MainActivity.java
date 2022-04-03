package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = 23)
public class MainActivity extends AppCompatActivity {
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    BluetoothAdapter btAdapter;
    List<BluetoothGattService> testServices;
    BluetoothGatt btGatt0;
    BluetoothGatt btGatt1;
    BluetoothGatt btGatt2;
    BluetoothGatt btGatt3;
    BluetoothGatt btGatt4;
    BluetoothGatt btGatt5;
    BluetoothDevice btDevice0;
    BluetoothDevice btDevice1;
    BluetoothDevice btDevice2;
    BluetoothDevice btDevice3;
    BluetoothDevice btDevice4;
    BluetoothDevice btDevice5;
    BluetoothServerSocket btServerSocket0;
    BluetoothServerSocket btServerSocket1;
    BluetoothServerSocket btServerSocket2;
    BluetoothServerSocket btServerSocket3;
    BluetoothServerSocket btServerSocket4;
    BluetoothServerSocket btServerSocket5;
    BluetoothSocket btSocket0;
    BluetoothSocket btSocket1;
    BluetoothSocket btSocket2;
    BluetoothSocket btSocket3;
    BluetoothSocket btSocket4;
    BluetoothSocket btSocket5;
    BluetoothSocket btSocket01;
    BluetoothSocket btSocket11;
    BluetoothSocket btSocket21;
    BluetoothSocket btSocket31;
    BluetoothSocket btSocket41;
    BluetoothSocket btSocket51;
    private BluetoothGatt btGatt;
    int gattCounter = 0;
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
    UUID MY_UUID_INSECURE0;
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final int READ_DATA = 1;
    String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    private String receiveBuffer = "";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    //threads for comms
    private AcceptThread InsecureAcceptThread0;
    private ConnectThread ConnectThread0;
    private ConnectedThread mConnectedThread0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thread = null;
        int Counter; //used to index an array to copy over the permissions that need approval
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
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

        //Log.d(TAG, "===> Start Server !");
        //Log.d(TAG, toString(btDevicesPaired));
        //Intent intent = getIntent();
        //String address = intent.getStringExtra("3CA5518AD35C");
        //startActivity(intent);
        try {
            // This will connect the device with address as passed
            Set<BluetoothDevice> btDevices = btAdapter.getBondedDevices();
            for(BluetoothDevice btDevice : btDevices) {
                Log.d(TAG, "socket 0");
                switch(gattCounter){
                    case(0):
                        btDevice0 = btDevice;
                        btGatt0 = btDevice.connectGatt(MainActivity.this, false, btGattCallback);
                        Log.d(TAG, "Connecting device 0");
                        debug++;
                        break;
                    case(1):
                        btDevice1 = btDevice;
                        btGatt1 = btDevice.connectGatt(MainActivity.this, true, btGattCallback);
                        break;
                    case(2):
                        btDevice2 = btDevice;
                        btGatt2 = btDevice.connectGatt(MainActivity.this, true, btGattCallback);
                        break;
                    case(3):
                        btDevice3 = btDevice;
                        btGatt3 = btDevice.connectGatt(MainActivity.this, true, btGattCallback);
                        break;
                    case(4):
                        btDevice4 = btDevice;
                        btGatt4 = btDevice.connectGatt(MainActivity.this, true, btGattCallback);
                        break;
                    case(5):
                        btDevice5 = btDevice;
                        btGatt5 = btDevice.connectGatt(MainActivity.this, true, btGattCallback);
                        break;
                }
                gattCounter++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "===> ERROR!");
        }
    }

    public BluetoothGattCallback btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange (BluetoothGatt gatt,int status, int newState){
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d("GattCallback", "connected");

                    gatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d("GattCallback", "Disconnected");
                    break;
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                testServices = gatt.getServices();
                debug++;
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicRead SUCCESS");
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            } else {
                Log.d(TAG, "onCharacteristicRead FAILED");
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

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
        String uuid = null;
        if ((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            //Set<BluetoothDevice> btDevicesPaired = btAdapter.getBondedDevices();
            switch (view.getId()) {
                case (R.id.toggleButton):
                    HashMap<String, String> currentCharaData =
                            new HashMap<String, String>();
                    String unknownCharaString = getResources().
                            getString(R.string.unknown_characteristic);
                    Log.d(TAG, "Button LED1 Pressed\n");

                    writeCharacteristic("hello");


                    //send toggle message to connection 1
                    //BluetoothGattService Service = testServices.get(0);
                    //uuid = Service.getUuid().toString();
                    //Log.d(TAG, "uuid: " + uuid);
                    //MY_UUID_INSECURE0 = UUID.fromString(uuid);
                    //List<BluetoothGattCharacteristic> gattCharacteristics = Service.getCharacteristics();
                    //btGatt0.readCharacteristic(gattCharacteristics.get(2));
                    //Log.d(TAG, "chara: " + gattCharacteristics.get(2));
                    debug++;
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

    public void writeCharacteristic(String input)
    {
        input = "test message\n";
        try
        {
            BluetoothGatt mBluetoothGatt = btGatt0;
            BluetoothGattService Service = mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            BluetoothGattCharacteristic charac = Service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            charac.setValue(input);
            mBluetoothGatt.writeCharacteristic(charac);
        }catch (IllegalStateException | NullPointerException e)
        {
            Log.e(TAG, "Wrong device, does not contain service/characteristic 0000fee1-0000-1000-8000-00805f9b34fb");
        }
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        String string;
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA, new String(data));
            debug++;
        }
        Log.d(TAG, "Running broadcast update\n");
        IntentFilter filter = new IntentFilter(action);
        this.registerReceiver(broadcastUpdateReceiver, filter);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver broadcastUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DATA_AVAILABLE.equals(action)) {
                receiveBuffer += intent.getStringExtra(EXTRA_DATA);
                if (receiveBuffer.contains("\n")) {
                    receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                    receiveBuffer = "";
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_DATA) {
            Log.d(TAG, "Got activity result from read data\n");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                debug++;
                // Do something with the contact here (bigger example below)
            }
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
