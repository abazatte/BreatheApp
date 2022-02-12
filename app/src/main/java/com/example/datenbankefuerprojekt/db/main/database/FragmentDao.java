package com.example.datenbankefuerprojekt.db.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FragmentDao {

    @Insert
    void insert(Fragment fragment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Fragment fragment);

    @Delete
    void delete(Fragment fragment);

    @Query("DELETE  FROM fragment_table")
    void deleteAllFragments();

    @Query("SELECT * FROM fragment_table ORDER BY fragmentId ASC")
    LiveData<List<Fragment>> getAllFragments();

    @Query("Select * From fragment_table join uebung_table on uebung_table.id = fragment_table.uebungId where uebungId = :uebungId ORder by prioritaetFragment aSc")
    LiveData<List<Fragment>> getAllFragmentsOfUebung(int uebungId);
}
