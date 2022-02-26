package com.example.datenbankefuerprojekt.db.main.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPause;
import com.example.datenbankefuerprojekt.db.main.database.controlpause.ControlPauseDao;
import com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.fragment.FragmentDao;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.db.main.database.uebung.UebungDao;

import java.sql.Date;
import java.util.Random;


@Database(entities = {Uebung.class, Fragment.class, ControlPause.class}, version = 12)
public abstract class UebungDatenbank extends RoomDatabase {
    private static UebungDatenbank INSTANCE;
    public abstract UebungDao uebungDao();
    public abstract FragmentDao fragmentDao();
    public abstract ControlPauseDao controlPauseDao();

    public static synchronized UebungDatenbank getInstance(Context context){
       if (INSTANCE == null){
           INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                   UebungDatenbank.class, "uebung_datenbank")
                   .fallbackToDestructiveMigration()
                   .addCallback(roomCallBack)
                   .build();
       }
       return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private UebungDao uebungDao;
        private ControlPauseDao controlPauseDao;
        private FragmentDao fragmentDao;

        private PopulateDbAsyncTask(UebungDatenbank database){
            this.uebungDao = database.uebungDao();
            this.controlPauseDao = database.controlPauseDao();
            this.fragmentDao = database.fragmentDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uebungDao.insert(new Uebung("Title 1", "Description 1", 1,1,false,0,1));
            uebungDao.insert(new Uebung("Title 2", "Description 2", 1,1,false,0,1));
            uebungDao.insert(new Uebung("Title 3", "Description 3", 1,1,false,0,1));

            for (int i = 1; i <= 3; i++) {
                fragmentDao.insert(new Fragment("Fragment 1", i, 2,3,4,5,2,4));
                fragmentDao.insert(new Fragment("Fragment 2", i, 3,4,4,5,2,3));
            }

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
                controlPauseDao.insert(controlPause);
                controlPauseDao.insert(controlPause1);
            }
            return null;
        }
    }


}
