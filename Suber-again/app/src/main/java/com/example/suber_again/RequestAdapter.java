package com.example.suber_again;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private ArrayList<Request> reqs;
    private Context context;
    private static String lastID;
    private static DatabaseReference RequestsDatabaseref;
    private static ChildEventListener mChildListener;

    public RequestAdapter(DatabaseReference ref, Context context) {
        this.context = context;
        RequestsDatabaseref = ref;
        reqs = new ArrayList<>();
        mChildListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Request req = dataSnapshot.getValue(Request.class);
                reqs.add(req);
                notifyItemInserted(reqs.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Request req = dataSnapshot.getValue(Request.class);
                int deleteIndex = 0;
                lastID = req.getID();
                ArrayList<Request> reqs2 = new ArrayList<>(reqs);
                for (Request req2:reqs2){
                    if(req.getID().equals(req2.getID())){
                        deleteIndex = reqs.indexOf(req2);
                        reqs.remove(deleteIndex);
                    }
                }
                notifyItemRemoved(deleteIndex);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        Log.d("Click", "add listener");
        RequestsDatabaseref.addChildEventListener(mChildListener);
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View contactView = LayoutInflater.from(context).inflate(R.layout.request_listing, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request request = reqs.get(position);
        holder.bind(request, RequestsDatabaseref);
        // Set item views based on your views and data model

    }

    @Override
    public int getItemCount() {
        return reqs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String Patient_Name;
        Boolean accept;
        TextView nameFrom;
        TextView NextRoom;
        Button AcceptButton;
        Button DeclineButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NextRoom = itemView.findViewById(R.id.Next_Room);
            nameFrom = itemView.findViewById(R.id.Patients_Room);
            AcceptButton = itemView.findViewById(R.id.Accept_button);
            DeclineButton = itemView.findViewById(R.id.DeclineButton);
        }
        public void bind(final Request req, final DatabaseReference ref){
            Patient_Name = req.getPatient();
            if(req.getID().equals(lastID)){
                SaniterHomeScreen.lastsanvar.setBoo(true);
            }
            nameFrom.setText("From: " + req.getPatients_Room());
            NextRoom.setText("To: " + req.getNext_Room());
            AcceptButton.setText("Accept");
            final twentysectimer tsectimer = new twentysectimer(AcceptButton, DeclineButton, ref, req.getID());
            AcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaniterHomeScreen.send("im doing it");
                    inflateConfirm(v, context, Patient_Name, nameFrom.getText().toString(), NextRoom.getText().toString());
                    tsectimer.Cancelthread();
                }
            });
            DeclineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaniterHomeScreen.send("im not doing it");
                    ref.child(req.getID()).removeValue();
                    Log.d("req", "req removed");
                    tsectimer.Cancelthread();
                }
            });
            /*SaniterHomeScreen.cv.setListener2(new connectedvariable.ChangeListener() {
                @Override
                public void onChange() {
                    if (!SaniterHomeScreen.cv.isconnected()) {
                        Log.d("Rip", "onChange: actually worked, deleted the ting");
                        SaniterHomeScreen.send("im not doing it");
                        ref.child(req.getID()).removeValue();
                        DeclineButton.setOnClickListener(null);
                        AcceptButton.setOnClickListener(null);
                        tsectimer.Cancelthread();
                    }
                }
            });*/
            Log.d("Rip", "Starting Timer");
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));

        }
        private void inflateConfirm(View view, Context context, String pat_name, String from, String To){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.pop_up_req, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.setElevation(20);
            //popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_resend_back));
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            TextView Frombox = popupView.findViewById(R.id.From);
            TextView ToBox = popupView.findViewById(R.id.popupto);
            TextView NameBox = popupView.findViewById(R.id.popupname);
            Frombox.setText(from);
            ToBox.setText(To);
            NameBox.setText(pat_name);
            final Button conf_btn = popupView.findViewById(R.id.Finished);
            conf_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaniterHomeScreen.send("done");
                    popupWindow.dismiss();
                }
            });

        }
    }
}



