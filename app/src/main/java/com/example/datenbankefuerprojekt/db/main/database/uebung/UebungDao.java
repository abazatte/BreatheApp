package com.example.datenbankefuerprojekt.db.main.database.uebung;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;

import java.util.List;
/**
 * @author Abdurrahman Azattemür
 * <p>Dies ist die Dao für die Uebung Entität.</p>
 */
@Dao
public interface UebungDao {
    @Insert
    void insert(Uebung note);

    @Update
    void update(Uebung note);

    @Delete
    void delete(Uebung note);

    @Query("DELETE FROM uebung_table")
    void deleteAllUebung();

    @Query("SELECT * FROM uebung_table ORDER BY prioritaet ASC")
    LiveData<List<Uebung>> getAllNotes();

    @Query("Select * from uebung_table where id=:id")
    LiveData<Uebung> getUebungById(int id);
}
