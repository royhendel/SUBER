package com.example.suber_again;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SaniterHomeScreen extends Activity {
    private TextView connected_bool;
    private Button view_history_button, log_out_button;
    private RecyclerView recyclerView;

    private static Socket client;
    private static PrintWriter pw;
    private static BufferedReader input;
    private static String message;

    private FirebaseDatabase database;
    private DatabaseReference RequestsDatabase;

    private static final int SERVERPORT = 8820;
    private static final String SERVER_IP = "10.0.0.16";
    private Boolean connected = false;
    public static connectedvariable cv = new connectedvariable();
    public static connectedvariable lastsanvar = new connectedvariable();
    private RequestAdapter reqadapter;
    private String current_mac;
    private int changecounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saniter_home_screen);

        connected_bool = findViewById(R.id.SanConnectedBool);
        view_history_button = findViewById(R.id.SanReqHistoryButton);
        log_out_button = findViewById(R.id.SanLogOutButton);
        recyclerView = findViewById(R.id.UpdatingSanListview);
        final User current_user = (User)getIntent().getSerializableExtra("Current_User");
        cv = new connectedvariable();
        lastsanvar = new connectedvariable();
        Thread Wifichecker = new Thread(){
            @Override
            public void run() {
                while (true) {
                    while (cv.isconnected()) {
                        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                        String oldbssid = wifiInfo.getBSSID();
                        String newbssid = wifiInfo.getBSSID();
                        Log.d("cv", "on start oldbssid: " + oldbssid + "new bssid: " + newbssid);
                        try {
                            while (oldbssid.equals(newbssid)) {
                                try {
                                    TimeUnit.SECONDS.sleep(5);
                                    Log.d("cv", "waited 5 secs");
                                } catch (InterruptedException e) {
                                    Log.d("connected", "time sleep failed");
                                    e.printStackTrace();
                                }
                                wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                wifiInfo = wifiMgr.getConnectionInfo();
                                newbssid = wifiInfo.getBSSID();
                                Log.d("cv", "in checker oldbssid: " + oldbssid + "new bssid: " + newbssid);
                            }
                        }catch (NullPointerException n){
                            Log.d("cv", "disconnected in null: " + newbssid);
                        }
                        if (newbssid == null) {
                            Log.d("cv", "wifi gone");
                            cv.setNul(true);
                        }
                        /*while(newbssid == null) {
                            SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SaniterHomeScreen.this, "Please Reconnect to the WiFi", Toast.LENGTH_LONG).show();
                                    connected_bool.setText("Not Connected");
                                    connected_bool.setTextColor(Color.RED);
                                }
                            });
                            try {
                                TimeUnit.SECONDS.sleep((5));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiInfo = wifiMgr.getConnectionInfo();
                            newbssid = wifiInfo.getBSSID();
                        }*/
                        cv.setBoo(false);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(10);
                        Log.d("cv", "waited 10 secs 2");
                    } catch (InterruptedException e) {
                        Log.d("connected", "time sleep failed");
                        e.printStackTrace();
                    }
                }
            }
        };
        view_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cv.setListener(new connectedvariable.ChangeListener() {
            @Override
            public void onChange() {
                if (!cv.isconnected()) {
                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SaniterHomeScreen.this, "Wifi was Changed", Toast.LENGTH_LONG).show();
                            Log.d("cv", "wifi was changed: " + cv);
                        }
                    });
                    int waitcheck = disconnect();
                    if (cv.isnul()){
                        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                        String newbssid = wifiInfo.getBSSID();
                        while(newbssid == null) {
                            SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(SaniterHomeScreen.this, "Please Reconnect to the WiFi", Toast.LENGTH_LONG).show();
                                    connected_bool.setText("Not Connected");
                                    connected_bool.setTextColor(Color.RED);
                                }
                            });
                            try {
                                TimeUnit.SECONDS.sleep((4));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiInfo = wifiMgr.getConnectionInfo();
                            newbssid = wifiInfo.getBSSID();
                        }
                    }
                    if (waitcheck == 1) {
                        Thread reconnect = new Thread() {
                            @Override
                            public void run() {
                                async_connect_to_socket(current_user, false);
                            /*try {
                    client = new Socket();
                    SocketAddress server = new InetSocketAddress(SERVER_IP,SERVERPORT);
                    client.connect(server);
                    pw = new PrintWriter(client.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            if (client.isConnected()) {
                                connected_bool.setText("Connected");
                                connected_bool.setTextColor(Color.GREEN);
                            }
                        }
                    });
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    send(current_user.getRole() + " " + current_user.getName() + " " + "wifiInfo.getBSSID()");
                    connected = true;
                } catch (IOException e) {
                    Log.d("socket", "failed");
                    e.printStackTrace();
                }*/
                            }
                        };
                        reconnect.start();
                        try {
                            reconnect.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Log.d("cv", "Connected");
                }
            }
        });
        lastsanvar.setListener(new connectedvariable.ChangeListener() {
            @Override
            public void onChange() {
                if (lastsanvar.isconnected()){
                    Toast.makeText(SaniterHomeScreen.this, "You Are The Last Saniter!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaniterHomeScreen.this, "Logging out", Toast.LENGTH_SHORT).show();
                if (!client.isConnected()) {
                    Intent intent = new Intent(SaniterHomeScreen.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    final Thread disconnectthread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                send("logout");
                                message = receive_message();
                                Log.d("message recv", "in the on click the message is: " + message);
                                if (message.equals("Log Out Successful")) {
                                    Log.d("message recv", "Entered if" + message);
                                    pw.close();
                                    input.close();
                                    client.close();
                                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            //Do your UI operations like dialog opening or Toast here
                                            Log.d("message recv", "in run");
                                            connected_bool.setText("Not Connected");
                                            Log.d("message recv", "in between change");
                                            connected_bool.setTextColor(Color.RED);
                                        }
                                    });
                                    Log.d("message recv", "logout succeeded");
                                    Intent intent = new Intent(SaniterHomeScreen.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            } catch (IOException e) {
                                Log.d("socket", "failed");
                                e.printStackTrace();
                            }
                        }
                    };
                    disconnectthread.start();
                }
            }
        });
        Thread adaptor_thread = new Thread(){
            public void run() {
                Log.d("adaptor", "in connected if");
                database = FirebaseDatabase.getInstance();
                RequestsDatabase = database.getReference("users/" + current_user.getName() + "/Requests");
                RequestAdapter reqadapter = new RequestAdapter(RequestsDatabase, getApplicationContext());
                recyclerView.setAdapter(reqadapter);
                Log.d("adaptor", "adaptor set");

            }
        };
        adaptor_thread.start();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Thread first_connect = new Thread() {
            @Override
            public void run() {
                async_connect_to_socket(current_user, true);
            }
        };
        first_connect.start();
        Log.d("adaptor", "connected is: " + connected);
        Wifichecker.start();
    }
    public void async_adaptor(final User current_user){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                RequestAdapter reqadapter = new RequestAdapter(RequestsDatabase, getApplicationContext());
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("adaptor", "in pre execute");
                database = FirebaseDatabase.getInstance();
                RequestsDatabase = database.getReference("users/" + current_user.getName() + "/Requests");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                recyclerView.setAdapter(reqadapter);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void async_connect_to_socket(final User current_user, final boolean needadap){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    client = new Socket();
                    SocketAddress server = new InetSocketAddress(SERVER_IP, SERVERPORT);
                    client.connect(server);
                    pw = new PrintWriter(client.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            if (client.isConnected()) {
                                connected_bool.setText("Connected");
                                connected_bool.setTextColor(Color.GREEN);
                            }
                        }
                    });
                    WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    send(current_user.getRole() + " " + current_user.getName() + " " + wifiInfo.getBSSID());
                    connected = true;
                    cv.setBoo(true);
                } catch (IOException e) {
                    Log.d("socket", "failed");
                    cv.setBoo(false);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

            }
            @Override
            protected void onPostExecute(Void aVoid) {
                /*if (needadap) {
                    recyclerView.setAdapter(reqadapter);
                }*/
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
    public int disconnect(){
        final Thread disconnectthread = new Thread() {
            @Override
            public void run() {
                try {
                    send("logout");
                    Log.d("message recv", "in the on click the message is: " + message);
                    //if (message.equals("")) {
                        Log.d("message recv", "Entered if" + message);
                        pw.close();
                        input.close();
                        client.close();
                        SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Do your UI operations like dialog opening or Toast here
                                Log.d("message recv", "in run");
                                connected_bool.setText("Not Connected");
                                Log.d("message recv", "in between change");
                                connected_bool.setTextColor(Color.RED);
                            }
                        });
                        Log.d("message recv", "logout succeeded");
                } catch (IOException e) {
                    Log.d("socket", "failed");
                    SaniterHomeScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Do your UI operations like dialog opening or Toast here
                            connected_bool.setText("Not Connected");
                            connected_bool.setTextColor(Color.RED);
                        }
                    });
                    e.printStackTrace();
                }
            }
        };
        disconnectthread.start();
        try {
            disconnectthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
    /*public void Removereq(Request req){
        RequestsDatabase.child(req.getID()).removeValue();
        Log.d("req", "req removed");
    }*/
    public static class sendstrclass implements Runnable {

        private String s;

        public sendstrclass(String s) {
            this.s = s;
        }

        public void run() {
            Log.d("sent string", "the string is " + s);
            pw.write(s);
            pw.flush();

        }
    }

    public String receive_message() {
        getmsg r = new getmsg();
        new Thread(r).start();
        try {
            while (r.getMsg() == null) {
                TimeUnit.SECONDS.sleep(1);
            }
            return r.getMsg();
        } catch (InterruptedException e) {
            Log.d("message recv", "waiting threw exception");
        }
        return "Failed";
    }
    public static void send(String s) {
        Runnable r = new sendstrclass(s);
        new Thread(r).start();
    }

    class getmsg implements Runnable {
        private String msg = "";

        public void run() {
            try {
                String message = input.readLine();
                if (!message.equals("")) {
                    Log.d("message recv", "success! message is: " + message);
                    this.msg = message;
                } else {
                    Log.d("message recv", "message is empty");
                }
                Log.d("message recv", "thread finished");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getMsg() {
            return msg;
        }
    }

}

