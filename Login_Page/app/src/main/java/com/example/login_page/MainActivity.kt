package com.example.login_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var Login_Button = findViewById(R.id.Login_Button) as CardView
        var Password_card = findViewById(R.id.Password) as TextView
        var Username = findViewById(R.id.Username) as TextView

        var LoginInfoDatabase =

        Login_Button.setOnClickListener{
            Toast.makeText(this@MainActivity, "Verifying User info", Toast.LENGTH_LONG).show()
        }

    }
}
