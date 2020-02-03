package com.example.suber_again;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private EditText uid, password, role;

    private Button signupbtn, loginbtn;
    //FireBase

    FirebaseDatabase database;
    DatabaseReference UsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        uid = findViewById(R.id.SignUpUsername);
        password = findViewById(R.id.SignUpPassword);
        role = findViewById(R.id.SignUpRole);
        signupbtn = findViewById(R.id.SignUpButton);
        loginbtn = findViewById(R.id.Signuppageloginbutton);

        //FireBase
        database = FirebaseDatabase.getInstance();
        UsersDatabase = database.getReference("users");

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username_input = uid.getText().toString();
                String password_input = password.getText().toString();
                String role_input = role.getText().toString();
                Toast.makeText(SignUpActivity.this, role_input, Toast.LENGTH_SHORT).show();
                if (role_input.equals("Doctor") || role_input.equals("Saniter")) {
                    final User user = new User(username_input, password_input, role_input);

                    UsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(user.getName()).exists()){
                                Toast.makeText(SignUpActivity.this, "That UID Exists!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                UsersDatabase.child(username_input).setValue(user);
                                Toast.makeText(SignUpActivity.this, "New User Created", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Toast.makeText(SignUpActivity.this, "Please Enter Legitimate Role!", Toast.LENGTH_SHORT).show();
                    role.setText("");
                }

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            }
    });
}}