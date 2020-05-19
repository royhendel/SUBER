package com.example.suber_again;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class DoctorHomeScreen extends Activity {

    private TextView connected_bool, current_room, new_room;
    private EditText Patient_name;
    private Button send_req_button, view_history_button, log_out_button;
    private Spinner current_room_spinner, next_room_spinner;

    private static Socket client;
    private static PrintWriter pw;
    private static BufferedReader input;
    private static String message;
    private static String[] rooms;
    private static List<String> roomslist;
    private static final int SERVERPORT = 8820;
    private static final String SERVER_IP = "10.0.0.24";
    private Boolean connected = false;


    public String From = "";
    public String To = "";

    FirebaseDatabase database;
    DatabaseReference RoomsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_screen);

        connected_bool = findViewById(R.id.ConnectedBool);
        current_room = findViewById(R.id.ReqCurrentLocation);
        new_room = findViewById(R.id.ReqFutureLocationText);
        current_room_spinner = findViewById(R.id.current_room_spinner);
        next_room_spinner = findViewById(R.id.next_room_spinner);

        Patient_name = findViewById(R.id.Patient_name);

        send_req_button = findViewById(R.id.Send_request_button);
        view_history_button = findViewById(R.id.ReqHistoryButton);
        log_out_button = findViewById(R.id.LogOutButton);

        final User current_user = (User)getIntent().getSerializableExtra("Current_User");

        roomslist = new ArrayList<String>();
        database = FirebaseDatabase.getInstance();
        RoomsDatabase = database.getReference("Rooms");
        connected_bool.setText("");
        RoomsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d("roomsdatabase", "value: " + postSnapshot.getValue().toString());
                    roomslist.add(postSnapshot.getValue().toString());
                }
                Log.d("roomslist", roomslist.toString());
                rooms = roomslist.toArray(new String[0]);
                Log.d("rooms", rooms.toString());
                Log.d("roomslist","fuck: "+ roomslist.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, rooms);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                current_room_spinner.setAdapter(adapter);
                next_room_spinner.setAdapter(adapter);

                current_room_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        From = String.valueOf(parent.getItemAtPosition(position));
                        Log.d("new spinner", "from spinner fine" + From);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                next_room_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        To = String.valueOf(parent.getItemAtPosition(position));
                        Log.d("new spinner", "to spinner fine" + To);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("roomsdatabase", "loadPost:onCancelled", databaseError.toException());
            }
        });
        view_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorHomeScreen.this, PastRequestScreen.class);
                intent.putExtra("Current_User", current_user);
                startActivity(intent);
            }
        });

        send_req_button.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v){
                String Patient = Patient_name.getText().toString();
                String ID = UUID.randomUUID().toString();
                if(To.isEmpty()){
                    Toast.makeText(DoctorHomeScreen.this, "Please Enter The Destination", Toast.LENGTH_SHORT).show();
                }
                else if(From.isEmpty()){
                    Toast.makeText(DoctorHomeScreen.this, "Please Enter the Current Location", Toast.LENGTH_SHORT).show();
                }
                else if(Patient.isEmpty()){
                    Toast.makeText(DoctorHomeScreen.this, "Please Enter the Current Location", Toast.LENGTH_SHORT).show();
                }
                else{
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            try {
                                client = new Socket();
                                SocketAddress server = new InetSocketAddress(SERVER_IP,SERVERPORT);
                                client.connect(server);
                                pw = new PrintWriter(client.getOutputStream());
                                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                connected = true;
                                final User current_user = (User) getIntent().getSerializableExtra("Current_User");
                                send(current_user.getRole() + " " + current_user.getName() + " has connected");
                            } catch (IOException e) {
                                Log.d("socket", "failed");
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                    String Doctor = current_user.getName();
                    Toast.makeText(DoctorHomeScreen.this, "Request Sent", Toast.LENGTH_LONG).show();
                    SendRequest(Doctor, From, To, Patient, ID);
                    /*String Doctor = current_user.getName();
                    final Request request = new Request(Doctor, From, To, Patient, ID);
                    database = FirebaseDatabase.getInstance();
                    RequestDatabase = database.getReference("Requests");
                    RequestDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            RequestDatabase.child(request.getID()).setValue(request);
                            Toast.makeText(DoctorHomeScreen.this, "New User Created", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });*/
                }
            }
        });

        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DoctorHomeScreen.this, "Logging out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DoctorHomeScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            });
        }

    public JSONObject serialize_request(Request req) {
        JSONObject obj = new JSONObject();
        try{
        obj.put("Doctor", req.getDoctor());
        obj.put("Patients_Room", req.getPatients_Room());
        obj.put("Next_Room", req.getNext_Room());
        obj.put("Patient", req.getPatient());
        obj.put("ID", req.getID());
        obj.put("TimeRequested", req.getTimeRequested());
        obj.put("Status", req.getStatus());

        Log.d("Json", "succeeded" + obj);
        return obj;
        }catch(org.json.JSONException e){
            Log.d("Json", "Failed");
            return null;
        }
    }
    public void SendRequest(String current_user, String oldroom, String newroom, String patient, String Id) {
        String Doctor = current_user;
        String From = oldroom;
        String To = newroom;
        String PatientName = patient;
        String ID = Id;

        final Request request = new Request(Doctor, From, To, PatientName, ID);
        JSONObject serialized_request = serialize_request(request);
        sendobj(serialized_request);
        /*RequestDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(request.toString()).exists()) {
                    Toast.makeText(DoctorHomeScreen.this, "That request Exists!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestDatabase.child(Integer.toString(new Random().nextInt(500000000))).setValue(request);
                    Toast.makeText(DoctorHomeScreen.this, "New User Created", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    public void sendobj(JSONObject s){
        Runnable r = new sendobjclass(s);
        new Thread(r).start();

    }
    public void send(String s) {
        Runnable r = new sendstrclass(s);
        new Thread(r).start();
    }
    public String receive_message(){
            getmsg r = new getmsg();
            new Thread(r).start();
            try {
                while (r.getMsg() == null) {
                    TimeUnit.SECONDS.sleep(1);
                }
                return r.getMsg();
            } catch(InterruptedException e){
                Log.d("message recv", "waiting threw exception");
            }
            return "Failed";
    }

    class getmsg implements Runnable {
        private String msg = null;
        public void run() {
            try {
                String message = input.readLine();
                if (message != null) {
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
    public class sendobjclass implements Runnable {

        private JSONObject s;

        public sendobjclass(JSONObject s) {
            this.s = s;
        }

        public void run() {
            while(!connected) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("sent obj", "the string is " + s);
            pw.write(s.toString());
            pw.flush();
            disconnect();
        }
    }
    public class sendstrclass implements Runnable {

        private String s;

        public sendstrclass(String s) {
            this.s = s;
        }

        public void run() {
            Log.d("sent string", "the string is " + s);
            pw.write(s);
            pw.flush();
            connected = true;
        }
    }
    public int disconnect(){
        final Thread disconnectthread = new Thread() {
            @Override
            public void run() {
                try {
                    pw.close();
                    input.close();
                    client.close();
                    Log.d("message recv", "logout succeeded");
                } catch (IOException e) {
                    Log.d("socket", "failed");
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
        connected = false;
        return 1;
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
