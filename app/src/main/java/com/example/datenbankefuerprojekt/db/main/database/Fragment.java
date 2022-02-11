package com.example.datenbankefuerprojekt.db.main.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.datenbankefuerprojekt.db.main.database.Uebung;

import org.jetbrains.annotations.PropertyKey;

@Entity(indices = {@Index(value = {"id","uebungId"},unique = true)},tableName = "fragment_table", foreignKeys = {@ForeignKey(entity = Uebung.class, parentColumns = "id", childColumns = "uebungId", onDelete = ForeignKey.CASCADE)})
public class Fragment {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titel;

    private int uebungId;

    private int einAtmenZeit;

    private int einLuftanhaltZeil;

    private int ausAtmenZeit;

    private int ausLuftanhaltZeit;

    private int anzahlWiederholungen;

    private int prioritaet;

    public Fragment(String titel, int uebungId, int einAtmenZeit, int einLuftanhaltZeil, int ausAtmenZeit, int ausLuftanhaltZeit, int anzahlWiederholungen, int prioritaet){
        this.titel = titel;
        this.uebungId = uebungId;
        this.einAtmenZeit = einAtmenZeit;
        this.einLuftanhaltZeil = einLuftanhaltZeil;
        this.ausAtmenZeit = ausAtmenZeit;
        this.ausLuftanhaltZeit = ausLuftanhaltZeit;
        this.anzahlWiederholungen = anzahlWiederholungen;
        this.prioritaet = prioritaet;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUebungId() {
        return uebungId;
    }

    public String getTitel() {
        return titel;
    }

    public int getEinAtmenZeit() {
        return einAtmenZeit;
    }

    public int getEinLuftanhaltZeil() {
        return einLuftanhaltZeil;
    }

    public int getAusAtmenZeit() {
        return ausAtmenZeit;
    }

    public int getAusLuftanhaltZeit() {
        return ausLuftanhaltZeit;
    }

    public int getAnzahlWiederholungen() {
        return anzahlWiederholungen;
    }

    public int getPrioritaet() { return prioritaet; }
}
