package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    BluetoothManager btManager;
    BluetoothLeScanner btScanner;
    private final int BLUETOOTH_PERMISSION_CODE = 1;

    String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
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
            }
        }
        requestPermissions();


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

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionsNeeded, BLUETOOTH_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults.length > 0){
                if (grantResults.length == 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission has been granted\n");
                    }else{
                        closeApp();
                    }
                }else if (grantResults.length == 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                     && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission has been granted\n");
                    }else{
                        closeApp();
                    }
                }
            } else {
                closeApp();
            }
    }

    public void closeApp() {
        System.out.println("Permission has been denied\n");
        MainActivity.this.finish();
    }
}