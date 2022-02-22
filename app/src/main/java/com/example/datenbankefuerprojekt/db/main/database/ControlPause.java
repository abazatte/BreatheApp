package com.example.datenbankefuerprojekt.db.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Date;

@Entity(tableName = "control_pause_table")
@TypeConverters({Converter.class})
public class ControlPause {

    @PrimaryKey(autoGenerate = true)
    private int controlPauseId;

    //hier mit date und typeconverter
    private Date date;

    private int laenge;

    public ControlPause(Date date, int laenge){
        this.date = date;
        this.laenge = laenge;
    }

    public void setControlPauseId(int controlPauseId) {
        this.controlPauseId = controlPauseId;
    }

    public int getControlPauseId() {
        return controlPauseId;
    }

    public Date getDate() {
        return date;
    }

    public int getLaenge() {
        return laenge;
    }
}
