package com.example.suber_again;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ChildEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginbtn, registerbtn;

    FirebaseDatabase database;
    DatabaseReference UsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbutton);
        registerbtn = findViewById(R.id.Register);
        //FireBase
        database = FirebaseDatabase.getInstance();
        UsersDatabase = database.getReference("users");

        /*Intent intent = new Intent(MainActivity.this, SaniterHomeScreen.class);
        startActivity(new );*/
        registerbtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Verifying User Information", Toast.LENGTH_SHORT).show();

                String username_input = username.getText().toString();
                String password_input = password.getText().toString();

                Log_in(username_input, password_input);

            }
        });


    }

    public void Log_in(final String username_input, final String password_input) {
        UsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(username_input).exists()){
                    if(!username_input.isEmpty()){
                        User Current_Attempt = dataSnapshot.child(username_input).getValue(User.class);
                        if(Current_Attempt.getPassword().equals(password_input)){
                            Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                            switch (Current_Attempt.getRole()){
                                case "Doctor":
                                    Intent intent = new Intent(MainActivity.this, DoctorHomeScreen.class);
                                    startActivity(intent);

                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please Enter a Username", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Username is Not Registered", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
