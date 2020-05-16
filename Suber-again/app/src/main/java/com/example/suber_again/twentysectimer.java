package com.example.suber_again;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

import java.util.Timer;
import java.util.TimerTask;

public class twentysectimer {
    Timer timer;

    public twentysectimer(Button dec, Button acc, DatabaseReference reference, String id) {
        timer = new Timer();
        timer.schedule(new twentysectimertask(dec, acc, reference, id), 20 * 1000);
    }
    public void Cancelthread(){
        timer.cancel();
    }
    class twentysectimertask extends TimerTask{
        private Button dec;
        private Button acc;
        private DatabaseReference ref;
        private String Id;

        protected twentysectimertask(Button decline, Button accept, DatabaseReference reference, String id) {
            super();
            this.dec = decline;
            this.acc = accept;
            this.ref = reference;
            this.Id = id;
        }

        @Override
        public void run() {
            Log.d("Rip", "req died");
            this.dec.setOnClickListener(null);
            this.dec.setOnClickListener(null);
            SaniterHomeScreen.send("im not doing it");
            ref.child(this.Id).removeValue();
            timer.cancel();
        }

        @Override
        public boolean cancel() {
            Log.d("Rip", "req saved!!");
            return super.cancel();
        }
    }

    }
