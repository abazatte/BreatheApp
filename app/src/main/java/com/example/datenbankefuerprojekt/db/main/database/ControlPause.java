package com.example.datenbankefuerprojekt.db.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "control_pause_table")
public class ControlPause {

    @PrimaryKey(autoGenerate = true)
    private int controlPauseId;

    private String date;

    private int laenge;

    public ControlPause(String date, int laenge){
        this.date = date;
        this.laenge = laenge;
    }

    public int getControlPauseId() {
        return controlPauseId;
    }

    public String getDate() {
        return date;
    }

    public int getLaenge() {
        return laenge;
    }
}
