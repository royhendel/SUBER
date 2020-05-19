package com.example.suber_again;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PastRequestScreen extends Activity {

    private PastRequestAdapter pastreqadapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference RequestsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_requests);

        final User current_user = (User)getIntent().getSerializableExtra("Current_User");
        recyclerView = findViewById(R.id.pastreqrecycler);

        Thread adaptor_thread = new Thread(){
            public void run() {
                Log.d("adaptor", "in connected if");
                database = FirebaseDatabase.getInstance();
                if(current_user.getRole().startsWith("D")){
                    RequestsDatabase = database.getReference("users/" + current_user.getName() + "/Requests");
                }
                else{
                    RequestsDatabase = database.getReference("users/" + current_user.getName() + "/Requests_completed");
                }
                PastRequestAdapter reqadapter = new PastRequestAdapter(RequestsDatabase);
                recyclerView.setAdapter(reqadapter);
                Log.d("adaptor", "adaptor set");


            }
        };
        adaptor_thread.start();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}
