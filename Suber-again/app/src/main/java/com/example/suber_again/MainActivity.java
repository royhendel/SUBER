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
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbutton);


        /*Intent intent = new Intent(MainActivity.this, SaniterHomeScreen.class);
        startActivity(new );*/

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Verifying User Information", Toast.LENGTH_SHORT).show();

                /*progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage("Just a couple seconds...");
                progress.setTitle("Logging you in...");
                progress.setProgress(ProgressDialog.STYLE_SPINNER);
                progress.show();

                username_input = username.getText().toString();
                String pass = editpass.getText().toString();
                mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            StaticObjects.useruid = mAuth.getUid();
                            myRef = database.getReference("eztravel").child(mAuth.getUid());
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    StaticObjects.user = dataSnapshot.getValue(eztravel.class);
                                    progress.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progress.dismiss();
                                }
                            });


                        }

                    }
                });
                */

            }
        });


    }
}
