package com.example.bolor.marbotcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends ActionBarActivity implements BluetoothListener{

    private BluetoothThread btThread;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            System.out.println("Bluetooth not supported");
        } else {
            System.out.println("Bluetooth supported");

            if (!mBluetoothAdapter.isEnabled()) {
                System.out.println("Bluetooth is OFF");
            } else {
                System.out.println("Bluetooth is ON");
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    System.out.format("Found %d Devices\n", pairedDevices.size());
                    BluetoothDevice hc06 = null;
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        String info = device.getName() + " " + device.getAddress();
                        System.out.println(info);
                        if (device.getAddress().equals("98:D3:37:00:83:11")) {
                            hc06 = device;
                        }
                    }
                    if (hc06 == null) {
                        System.out.println("HC06 not found.");
                    } else {
                        System.out.println("Found HC06.");
                        BluetoothSocket socket = null;
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        try {
                            // MY_UUID is the app's UUID string, also used by the server code
                            socket = hc06.createRfcommSocketToServiceRecord(uuid);
                            System.out.println("Socket created.");
                            btThread = new BluetoothThread(socket);
                            btThread.setListener(this);
                            btThread.setDaemon(true);
                            btThread.start();

                        } catch (IOException e) {
                            System.out.println("IOException during socket creation.");
                        }

                    }

                }

            }

        }
        status = (TextView) findViewById(R.id.statusinfo);

    }

    public void onDown(View view){
        if(btThread!=null)
            btThread.write("w");
    }

    public void onUp(View view){
        if(btThread!=null)
            btThread.write("s");
    }

    public void onLeft(View view){
        if(btThread!=null)
            btThread.write("a");
    }

    public void onRight(View view){
        if(btThread!=null)
            btThread.write("d");
    }

    public void onStop(View view){
        if(btThread!=null)
            btThread.write("o");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(msg);
            }
        });
    }
}
