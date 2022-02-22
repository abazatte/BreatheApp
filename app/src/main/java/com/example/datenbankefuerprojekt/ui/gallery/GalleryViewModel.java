package com.example.datenbankefuerprojekt.ui.gallery;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datenbankefuerprojekt.db.main.database.ControlPause;
import com.example.datenbankefuerprojekt.db.main.database.ControlPauseRepository;

import java.util.List;

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

}