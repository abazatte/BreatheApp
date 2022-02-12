package com.example.datenbankefuerprojekt.db.main.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"fragmentId","uebungId"},unique = true)},tableName = "fragment_table", foreignKeys = {@ForeignKey(entity = Uebung.class, parentColumns = "id", childColumns = "uebungId", onDelete = ForeignKey.CASCADE)})
public class Fragment {

    @PrimaryKey(autoGenerate = true)
    private int fragmentId;

    private String titelFragment;

    private int uebungId;

    private int einAtmenZeit;

    private int einLuftanhaltZeil;

    private int ausAtmenZeit;

    private int ausLuftanhaltZeit;

    private int anzahlWiederholungenFragment;

    private int prioritaetFragment;

    public Fragment(String titelFragment, int uebungId, int einAtmenZeit, int einLuftanhaltZeil, int ausAtmenZeit, int ausLuftanhaltZeit, int anzahlWiederholungenFragment, int prioritaetFragment){
        this.titelFragment = titelFragment;
        this.uebungId = uebungId;
        this.einAtmenZeit = einAtmenZeit;
        this.einLuftanhaltZeil = einLuftanhaltZeil;
        this.ausAtmenZeit = ausAtmenZeit;
        this.ausLuftanhaltZeit = ausLuftanhaltZeit;
        this.anzahlWiederholungenFragment = anzahlWiederholungenFragment;
        this.prioritaetFragment = prioritaetFragment;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public int getUebungId() {
        return uebungId;
    }

    public String getTitelFragment() {
        return titelFragment;
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

    public int getAnzahlWiederholungenFragment() {
        return anzahlWiederholungenFragment;
    }

    public int getPrioritaetFragment() { return prioritaetFragment; }
}
