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

    public Uebung(String titel, String beschreibung, int anzahlDerWiederholungen, int prioritaet){
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.anzahlDerWiederholungen = anzahlDerWiederholungen;
        this.prioritaet = prioritaet;
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
}
