package com.example.suber_again;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class DoctorHomeScreen extends Activity {

    private TextView connected_bool;
    private EditText current_room, new_room;
    private Button send_req_button, view_history_button, log_out_button;

    private static Socket client;
    private static PrintWriter pw;

    private static final int SERVERPORT = 8820;
    private static final String SERVER_IP = "10.0.0.13";
    private Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_screen);

        connected_bool = findViewById(R.id.ConnectedBool);

        current_room = findViewById(R.id.ReqCurrentLocation);
        new_room = findViewById(R.id.ReqFutureLocationText);

        send_req_button = findViewById(R.id.Send_request_button);
        view_history_button = findViewById(R.id.ReqHistoryButton);
        log_out_button = findViewById(R.id.LogOutButton);

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    client = new Socket();
                    SocketAddress server = new InetSocketAddress(SERVER_IP,SERVERPORT);
                    client.connect(server);

                    DoctorHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            if(client.isConnected()) {
                                connected_bool.setText("Connected");
                                connected_bool.setTextColor(Color.GREEN);

                            }

                        }
                    });
                    connected = true;
                    send_req_button.setOnClickListener(new View.OnClickListener()  {
                        @Override
                        public void onClick(View v){


                            String old_room = current_room.getText().toString();
                            String next_room = new_room.getText().toString();

                            if(next_room.isEmpty()){
                                Toast.makeText(DoctorHomeScreen.this, "Please Enter The Destination", Toast.LENGTH_SHORT).show();
                            }
                            else if(old_room.isEmpty()){
                                Toast.makeText(DoctorHomeScreen.this, "Please Enter the Current Location", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                SendRequest(old_room, next_room);
                            }

                        }
                    });
                } catch (IOException e) {
                    Log.d("socket", "failed");
                    e.printStackTrace();
                }
            }
        };
        t.start();

        }
    public void send(String s){
        try {
            pw = new PrintWriter(client.getOutputStream());
            pw.write(s);
            pw.flush();
            pw.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public void SendRequest(String oldroom, String newroom){

    }
}







    /*public void connect() {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {

                    Socket client = new Socket(SERVER_IP, SERVERPORT);

                    PrintWriter printwriter = new PrintWriter(client.getOutputStream(), true);
                    printwriter.write("HI "); // write the message to output stream
                    printwriter.flush();
                    printwriter.close();
                    Boolean connection = true;
                    Log.d("socket", "connected" + connection);

                    // Toast in background becauase Toast cannnot be in main thread you have to create runOnuithread.
                    // this is run on ui thread where dialogs and all other GUI will run.
                    if (client.isConnected()) {
                        DoctorHomeScreen.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Do your UI operations like dialog opening or Toast here
                                Toast.makeText(getApplicationContext(), "Messege send", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (UnknownHostException e2){
                    DoctorHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Unknown host please make sure IP address", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (IOException e1) {
                    Log.d("socket", "IOException");
                    DoctorHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }
}*/
