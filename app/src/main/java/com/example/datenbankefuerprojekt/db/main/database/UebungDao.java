package com.example.datenbankefuerprojekt.db.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.datenbankefuerprojekt.db.main.database.Uebung;

import java.util.List;

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

    @Query("SELECT * FROM uebung_table ORDER BY prioritaet DESC")
    LiveData<List<Uebung>> getAllNotes();

    @Query("Select * from uebung_table where id=:id")
    LiveData<Uebung> getUebungById(int id);
}
