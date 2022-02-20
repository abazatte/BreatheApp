package com.example.datenbankefuerprojekt.db.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface ControlPauseDao {
    @Insert
    void insert(ControlPause controlPause);

    @Query("SELECT * FROM control_pause_table ORDER BY laenge DESC")
    LiveData<List<ControlPause>> getAllControlPause();
}
