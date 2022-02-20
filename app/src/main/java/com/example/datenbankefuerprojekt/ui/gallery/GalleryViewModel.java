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

    private ControlPauseRepository repository;
    private LiveData<List<ControlPause>> allControlPause;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ControlPauseRepository(application);
        this.allControlPause = repository.getAlleControlPause();
    }

    public void insert(ControlPause controlPause) {
        repository.insertControlPause(controlPause);
    }

    public LiveData<List<ControlPause>> getAllControlPause() {
        return allControlPause;
    }
}