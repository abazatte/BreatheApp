package com.example.datenbankefuerprojekt.db.main.database.controlpause;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPause;

import java.util.List;

@Dao
public interface ControlPauseDao {
    @Insert
    void insert(ControlPause controlPause);

    @Query("SELECT * FROM control_pause_table ORDER BY laenge DESC")
    LiveData<List<ControlPause>> getAllControlPauseByLaenge();

    @Query("SELECT * FROM control_pause_table ORDER BY date DESC")
    LiveData<List<ControlPause>> getAllControlPauseByDate();
}
