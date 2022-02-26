package com.example.datenbankefuerprojekt.db.main.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.datenbankefuerprojekt.db.main.database.UebungDatenbank;
import com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.fragment.FragmentDao;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.db.main.database.uebung.UebungDao;

import java.util.List;

public class UebungRepository {
    private UebungDao uebungDao;
    private FragmentDao fragmentDao;
    private LiveData<List<Uebung>> alleUebungen;
    private LiveData<List<Fragment>> alleFragmente;

    public UebungRepository(Application application) {
        UebungDatenbank datenbank = UebungDatenbank.getInstance(application);
        uebungDao = datenbank.uebungDao();
        fragmentDao = datenbank.fragmentDao();
        alleUebungen = uebungDao.getAllNotes();
        alleFragmente = fragmentDao.getAllFragments();
    }



    public void insertUebung(Uebung uebung) {
        new InsertUebungAsyncTask(uebungDao).execute(uebung);
    }

    public void updateUebung(Uebung uebung) {
        new UpdateUebungAsyncTask(uebungDao).execute(uebung);
    }

    public void deleteUebung(Uebung uebung) {
        new DeleteUebungAsyncTask(uebungDao).execute(uebung);
    }

    public void deleteAllUebungen() {
        new DeleteAllUebungAsyncTask(uebungDao).execute();
    }



    public void insertFragment(Fragment fragment) {
        new InsertFragmentAsyncTask(fragmentDao).execute(fragment);
    }

    public void updateFragment(Fragment fragment) {
        new UpdateFragmentAsyncTask(fragmentDao).execute(fragment);
    }

    public void deleteFragment(Fragment fragment) {
        new DeleteFragmentAsyncTask(fragmentDao).execute(fragment);
    }

    public void deleteAllFragment() {
        new DeleteAllFragmentAsyncTask(fragmentDao).execute();
    }



    public LiveData<List<Fragment>> getAlleFragment() {
        return alleFragmente;
    }

    public LiveData<List<Fragment>> getAlleFragmenteOfUebung(int id){
        return fragmentDao.getAllFragmentsOfUebung(id);
    }

    public LiveData<List<Uebung>> getAlleUebungen() {
        return alleUebungen;
    }

    /*
    public AsyncTask<Integer, Void, LiveData<Uebung>> getUebungById(int uebungId){
        return new getUebungByIdAsyncTask(uebungDao).execute(uebungId);
    }*/


    public LiveData<Uebung> getUebungById(int uebungId){
        return uebungDao.getUebungById(uebungId);
    }

    /*
    private static class getUebungByIdAsyncTask extends AsyncTask<Integer, Void, LiveData<Uebung>> {
        private UebungDao uebungDao;

        private getUebungByIdAsyncTask(UebungDao uebungDao) {
            this.uebungDao = uebungDao;
        }

        @Override
        protected LiveData<Uebung> doInBackground(Integer... uebungId) {
            return uebungDao.getUebungById(uebungId[0]);
        }
    }
*/
    private static class InsertUebungAsyncTask extends AsyncTask<Uebung, Void, Void> {
        private UebungDao uebungDao;

        private InsertUebungAsyncTask(UebungDao uebungDao) {
            this.uebungDao = uebungDao;
        }

        @Override
        protected Void doInBackground(Uebung... uebung) {
            uebungDao.insert(uebung[0]);
            return null;
        }
    }

    private static class InsertFragmentAsyncTask extends AsyncTask<Fragment, Void, Void> {
        private FragmentDao fragmentDao;

        private InsertFragmentAsyncTask(FragmentDao fragmentDao) {
            this.fragmentDao = fragmentDao;
        }

        @Override
        protected Void doInBackground(Fragment... fragment) {
            fragmentDao.insert(fragment[0]);
            return null;
        }
    }

    private static class UpdateUebungAsyncTask extends AsyncTask<Uebung, Void, Void> {
        private UebungDao uebungDao;

        private UpdateUebungAsyncTask(UebungDao uebungDao) {
            this.uebungDao = uebungDao;
        }

        @Override
        protected Void doInBackground(Uebung... uebung) {
            uebungDao.update(uebung[0]);
            return null;
        }
    }

    private static class UpdateFragmentAsyncTask extends AsyncTask<Fragment, Void, Void> {
        private FragmentDao fragmentDao;

        private UpdateFragmentAsyncTask(FragmentDao fragmentDao) {
            this.fragmentDao = fragmentDao;
        }

        @Override
        protected Void doInBackground(Fragment... fragment) {
            fragmentDao.update(fragment[0]);
            return null;
        }
    }

    private static class DeleteUebungAsyncTask extends AsyncTask<Uebung, Void, Void> {
        private UebungDao uebungDao;

        private DeleteUebungAsyncTask(UebungDao uebungDao) {
            this.uebungDao = uebungDao;
        }

        @Override
        protected Void doInBackground(Uebung... uebung) {
            uebungDao.delete(uebung[0]);
            return null;
        }
    }

    private static class DeleteFragmentAsyncTask extends AsyncTask<Fragment, Void, Void> {
        private FragmentDao fragmentDao;

        private DeleteFragmentAsyncTask(FragmentDao fragmentDao) {
            this.fragmentDao = fragmentDao;
        }

        @Override
        protected Void doInBackground(Fragment... fragment) {
            fragmentDao.delete(fragment[0]);
            return null;
        }
    }

    private static class DeleteAllUebungAsyncTask extends AsyncTask<Void, Void, Void> {
        private UebungDao uebungDao;

        private DeleteAllUebungAsyncTask(UebungDao uebungDao) {
            this.uebungDao = uebungDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uebungDao.deleteAllUebung();
            return null;
        }
    }

    private static class DeleteAllFragmentAsyncTask extends AsyncTask<Void, Void, Void> {
        private FragmentDao fragmentDao;

        private DeleteAllFragmentAsyncTask(FragmentDao fragmentDao) {
            this.fragmentDao = fragmentDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fragmentDao.deleteAllFragments();
            return null;
        }
    }


}
