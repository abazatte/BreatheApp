package com.example.datenbankefuerprojekt.db.main.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Uebung.class, Fragment.class, ControlPause.class}, version = 7)
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

        private PopulateDbAsyncTask(UebungDatenbank database){
            this.uebungDao = database.uebungDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uebungDao.insert(new Uebung("Title 1", "Description 1", 1,1,false,0,1));
            uebungDao.insert(new Uebung("Title 2", "Description 2", 1,1,false,0,1));
            uebungDao.insert(new Uebung("Title 3", "Description 3", 1,1,false,0,1));
            return null;
        }
    }
}
