package com.example.datenbankefuerprojekt.ui.gallery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datenbankefuerprojekt.db.main.database.ControlPause;
import com.example.datenbankefuerprojekt.db.main.database.ControlPauseRepository;

import java.sql.Date;
import java.util.List;
import java.util.Random;

public class GalleryViewModel extends AndroidViewModel {

    private Long pauseOffset;
    private Long startTime;
    private StopwatchState state;

    private MutableLiveData<Long> savedTime;

    private ControlPauseRepository repository;
    private LiveData<List<ControlPause>> allControlPauseByDate;
    private LiveData<List<ControlPause>> allControlPauseByLaenge;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ControlPauseRepository(application);
        this.allControlPauseByDate = repository.getAlleControlPauseByDate();
        this.allControlPauseByLaenge = repository.getAlleControlPauseByLaenge();
        savedTime = new MutableLiveData<>(0L);

        pauseOffset = (long) 0;
        state = StopwatchState.stopped;
    }


    public void insert(ControlPause controlPause) {
        repository.insertControlPause(controlPause);
    }

    public LiveData<List<ControlPause>> getAllControlPauseByDate() {
        return allControlPauseByDate;
    }

    public LiveData<List<ControlPause>> getAllControlPauseByLaenge() {
        return allControlPauseByLaenge;
    }

    public void setSavedTime(Long savedTime) {
        this.savedTime.setValue(savedTime);
    }

    public void setPauseOffset(Long pauseOffset) {
        this.pauseOffset = pauseOffset;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public StopwatchState getState() {
        return state;
    }

    public void setState(StopwatchState state) {
        this.state = state;
    }

    public Long getStartTime() {
        return startTime;
    }


    public long getPauseOffset() {
        return pauseOffset;
    }

    public LiveData<Long> getSavedTime(){
        return savedTime;
    }

    public void testMonth(){
        final int MAX_Y_VALUE = 50;
        final int MIN_Y_VALUE = 5;
        float y;
        for (int i = 0; i < 12; i++) {
            y = MIN_Y_VALUE + new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE);
            Date date = new Date(2022, i, 12);
            ControlPause controlPause = new ControlPause(date, (long) y);
            y = MIN_Y_VALUE + new Random().nextFloat() * (MAX_Y_VALUE - MIN_Y_VALUE);
            Date date1 = new Date(2022, i, 25);
            ControlPause controlPause1 = new ControlPause(date1, (long) y);
            insert(controlPause);
            insert(controlPause1);
        }
    }

}