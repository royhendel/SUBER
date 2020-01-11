package com.example.login_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var Login_Button = findViewById(R.id.Login_Button) as CardView
        var Password_card = findViewById(R.id.Password) as TextView
        var Username = findViewById(R.id.Username) as TextView

        var db = FirebaseFirestore.getInstance()
        val San1Data = hashMapOf(
            "UID" to "uid",
            "password" to "plz?",
            "Role" to "Saniter"
        )

        db.document("San1").set(San1Data)
        val users = db.collection("users")
        val query = users.whereEqualTo("UID", "uid")

        Login_Button.setOnClickListener{
            Toast.makeText(this@MainActivity,"query.toString()", Toast.LENGTH_LONG).show()

        }

    }
}
