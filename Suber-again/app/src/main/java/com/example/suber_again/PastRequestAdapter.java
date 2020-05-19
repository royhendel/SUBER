package com.example.suber_again;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class PastRequestAdapter extends RecyclerView.Adapter<PastRequestAdapter.PastReqHolder> {
    private ArrayList<Request> reqs;
    private static DatabaseReference RequestsDatabaseref;
    private static ChildEventListener mChildListener;

    public PastRequestAdapter(DatabaseReference ref) {
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
        Log.d("Past Click", "add listener");
        RequestsDatabaseref.addChildEventListener(mChildListener);
    }
    @Override
    public PastReqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View contactView = LayoutInflater.from(context).inflate(R.layout.past_request_listing, parent, false);

        // Return a new holder instance
        return new PastRequestAdapter.PastReqHolder(contactView);

    }

    @Override
    public void onBindViewHolder(@NonNull PastReqHolder holder, int position) {
        Request request = reqs.get(position);
        holder.bind(request, RequestsDatabaseref);
    }

    @Override
    public int getItemCount() {
        return reqs.size();
    }


    public class PastReqHolder extends RecyclerView.ViewHolder{
        String Patient_Name;
        TextView nameFrom;
        TextView NextRoom;
        TextView Doctor;
        TextView Date;
        TextView ID;

        public PastReqHolder(@NonNull View itemView){
            super(itemView);
            NextRoom = itemView.findViewById(R.id.Past_Next_Room);
            nameFrom = itemView.findViewById(R.id.Past_Patients_Room);
            Doctor = itemView.findViewById(R.id.Past_Doctor);
            Date = itemView.findViewById(R.id.Past_Date);
            ID = itemView.findViewById(R.id.Past_Request_ID);
        }
        public void bind(final Request req, final DatabaseReference ref){
            Patient_Name = req.getPatient();
            nameFrom.setText("From: " + req.getPatients_Room());
            NextRoom.setText("To: " + req.getNext_Room());
            Doctor.setText("Doctor: " + req.getDoctor());
            Date.setText("Date: " + req.getTimeRequested());
            ID.setText("ID: " + req.getID());
        }

    }

}
