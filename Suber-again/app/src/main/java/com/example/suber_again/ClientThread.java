package com.example.suber_again;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientThread implements Runnable{
    private static final int SERVERPORT = 8820;
    private static final String SERVER_IP = "10.0.0.13";

    @Override
    public void run() {

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Socket socket = new Socket(serverAddr, SERVERPORT);

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}

