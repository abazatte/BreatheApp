package com.example.datenbankefuerprojekt.db.main.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ControlPauseRepository {
    private ControlPauseDao controlPauseDao;
    private LiveData<List<ControlPause>> alleControlPause;

    public ControlPauseRepository(Application application){
        UebungDatenbank datenbank = UebungDatenbank.getInstance(application);
        controlPauseDao = datenbank.controlPauseDao();
        alleControlPause = controlPauseDao.getAllControlPause();
    }

    public void insertControlPause(ControlPause controlPause){
        new InsertControlPauseAsyncTask(controlPauseDao).execute(controlPause);
    }

    public LiveData<List<ControlPause>> getAlleControlPause(){
        return alleControlPause;
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
