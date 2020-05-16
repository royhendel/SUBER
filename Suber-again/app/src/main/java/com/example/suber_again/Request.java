package com.example.suber_again;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class Request implements Serializable {
    private String Status;
    private String Doctor;
    private String TimeRequested;
    private String Patients_Room;
    private String Next_Room;
    private String Patient;
    private String ID;
    private String Saniter;


    public Request(){}

    public Request(String Doctor, String Patients_Room, String Next_Room, String Patient, String ID) {
        this.Status = "Unfinished";
        this.Doctor = Doctor;
        this.TimeRequested = new Date().toString();
        this.Patients_Room = Patients_Room;
        this.Next_Room = Next_Room;
        this.Patient = Patient;
        this.ID = ID;
    }

    public Request(DataSnapshot snap){
        HashMap<String, String> mymap = snap.getValue(HashMap.class);
        this.Status = mymap.getOrDefault("Status", "Fuck me");
        this.Doctor = mymap.getOrDefault("Doctor", "Duck me");
        this.Next_Room = mymap.getOrDefault("Next_Room", "Luck me");
        this.TimeRequested = mymap.getOrDefault("TimeRequested", "Uck me");
        this.Patient = mymap.getOrDefault("Patient", "Muck Me");
        this.ID = mymap.getOrDefault("ID", "Cuck Me");
        this.Patients_Room = mymap.getOrDefault("Patients_Room", "Puck Me");
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {this.Status = Status;
    }

    public String getDoctor() {
        return Doctor;
    }

    public void setDoctor(String doctor) {
        Doctor = doctor;
    }

    public String getTimeRequested() {
        return TimeRequested;
    }

    public void setTimeRequested(String timeRequested) {
        TimeRequested = timeRequested;
    }

    public String getPatients_Room() {
        return Patients_Room;
    }

    public void setPatients_Room(String Patients_Room) {
        Patients_Room = Patients_Room;
    }

    public String getNext_Room() {
        return Next_Room;
    }

    public void setNext_Room(String to) {
        Next_Room = to;
    }

    public String getPatient() {
        return Patient;
    }

    public void setPatient(String Patient) {
        Patient = Patient;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {this.ID = ID;
    }
}
