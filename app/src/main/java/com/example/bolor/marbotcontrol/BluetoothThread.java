package com.example.bolor.marbotcontrol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by bolor on 02/04/16.
 */
public class BluetoothThread extends Thread {

    private BluetoothSocket socket;
    private BluetoothListener listener;
    private OutputStream out;
    private InputStream in;

    public BluetoothThread(BluetoothSocket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            socket.connect();
            System.out.println("Connected.");
            try{
                out = socket.getOutputStream();
                in = socket.getInputStream();
                System.out.println("Ready to receive.");
                BufferedReader brIn = new BufferedReader(new InputStreamReader(in));
                while(!isInterrupted()) {
                    //String msg = "Hello=)\n";
                    //


                    String msg = brIn.readLine();
                    listener.onNewMessage(msg);
                    //System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Unable to send.");
            }

        } catch (IOException connectException) {
            System.out.println("Unable to connect; close the socket and get out.");

            try {
                socket.close();
            } catch (IOException closeException) {
                System.out.println("Unable to close the socket.");
            }
            return;
        }
    }

    public void write(String msg) {
        if(out!=null) {
            try {
                out.write(msg.getBytes());
                System.out.println(msg);
            } catch(IOException e) {
            }
        }

    }

    public void setListener(BluetoothListener listener){
        this.listener = listener;
    }

}
