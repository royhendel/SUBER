package com.example.login_page

import android.text.style.TtsSpan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList

class FirebaseDatabaseHelper {
    private val mDatabase: FirebaseDatabase
    private val mReferenceUsers: DatabaseReference
    private val users = ArrayList<User>()

    interface DataStatus {
        fun DataIsLoaded(users: List<User>, keys: List<String>)
        fun DataIsInserted()
        fun DataIsUpdated()
        fun DataIsDeleted()
    }

    init {
        mDatabase = FirebaseDatabase.getInstance()
        mReferenceUsers = mDatabase.getReference("users")
    }

    fun readUsers(dataStatus: DataStatus) {
        mReferenceUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                val keys = ArrayList<String>()
                for (keyNode in dataSnapshot.children) {
                    keys.add(keyNode.key)
                    val User = keyNode.getValue(User::class.java)
                    users.add(User)
                }
                dataStatus.DataIsLoaded(users, keys)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}