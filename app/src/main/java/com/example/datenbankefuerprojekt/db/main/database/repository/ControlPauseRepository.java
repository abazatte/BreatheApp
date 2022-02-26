package com.example.datenbankefuerprojekt.db.main.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.datenbankefuerprojekt.db.main.database.UebungDatenbank;
import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPause;
import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPauseDao;

import java.util.List;

public class ControlPauseRepository {
    private ControlPauseDao controlPauseDao;
    private LiveData<List<ControlPause>> alleControlPauseByDate;
    private LiveData<List<ControlPause>> alleControlPauseByLaenge;

    public ControlPauseRepository(Application application){
        UebungDatenbank datenbank = UebungDatenbank.getInstance(application);
        controlPauseDao = datenbank.controlPauseDao();
        alleControlPauseByDate = controlPauseDao.getAllControlPauseByDate();
        alleControlPauseByLaenge = controlPauseDao.getAllControlPauseByLaenge();
    }

    public void insertControlPause(ControlPause controlPause){
        new InsertControlPauseAsyncTask(controlPauseDao).execute(controlPause);
    }

    public LiveData<List<ControlPause>> getAlleControlPauseByDate(){
        return alleControlPauseByDate;
    }

    public LiveData<List<ControlPause>> getAlleControlPauseByLaenge() {
        return alleControlPauseByLaenge;
    }

    private static class InsertControlPauseAsyncTask extends AsyncTask<ControlPause, Void, Void>{
        private ControlPauseDao controlPauseDao;

        private InsertControlPauseAsyncTask(ControlPauseDao controlPauseDao){
            this.controlPauseDao = controlPauseDao;
        }

        @Override
        protected Void doInBackground(ControlPause... controlPauses){
            controlPauseDao.insert(controlPauses[0]);
            return null;
        }
    }
}
