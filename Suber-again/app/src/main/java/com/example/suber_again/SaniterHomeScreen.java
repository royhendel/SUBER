package com.example.suber_again;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SaniterHomeScreen extends Activity {
    private TextView connected_bool;
    private Button view_history_button, log_out_button;

    private static Socket client;
    private static PrintWriter pw;

    private static final int SERVERPORT = 8820;
    private static final String SERVER_IP = "10.0.54.56";
    private Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saniter_home_screen);

        connected_bool = findViewById(R.id.SanConnectedBool);
        view_history_button = findViewById(R.id.SanReqHistoryButton);
        log_out_button = findViewById(R.id.SanLogOutButton);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    client = new Socket();
                    SocketAddress server = new InetSocketAddress(SERVER_IP, SERVERPORT);
                    client.connect(server);

                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            if (client.isConnected()) {
                                connected_bool.setText("Connected");
                                connected_bool.setTextColor(Color.GREEN);

                            }

                        }
                    });
                    connected = true;
                    send(current_user.getrole() + currentuser.getname() + "has connected");
                } catch (IOException e) {
                    Log.d("socket", "failed");
                    e.printStackTrace();
                }
            }
        };
        t.start();


    }

    public void send(String s) {
        try {
            pw = new PrintWriter(client.getOutputStream());
            pw.write(s);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


