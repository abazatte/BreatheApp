package com.example.datenbankefuerprojekt.db.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "uebung_table")
public class Uebung {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titel;

    private String beschreibung;

    private int anzahlDerWiederholungen;

    private int prioritaet;

    private boolean useTimed;

    private int timeInSeconds;

    private int animationSpinnerPosition;

    public Uebung(String titel, String beschreibung, int anzahlDerWiederholungen, int prioritaet, boolean useTimed, int timeInSeconds, int animationSpinnerPosition){
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.anzahlDerWiederholungen = anzahlDerWiederholungen;
        this.prioritaet = prioritaet;
        this.useTimed = useTimed;
        this.timeInSeconds = timeInSeconds; //hier default value 0?
        this.animationSpinnerPosition = animationSpinnerPosition;
    }

    public void setUseTimed(boolean useTimed) {
        this.useTimed = useTimed;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public int getAnzahlDerWiederholungen() {
        return anzahlDerWiederholungen;
    }

    public int getPrioritaet() {
        return prioritaet;
    }

    public boolean getUseTimed() {
        return useTimed;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public int getAnimationSpinnerPosition() {
        return animationSpinnerPosition;
    }
}
