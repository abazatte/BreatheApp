package com.example.datenbankefuerprojekt.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.db.main.database.repository.UebungRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static String TAG = "HomeViewModel:";

    private UebungRepository repository;
    private LiveData<List<Uebung>> allUebung;
    //private LiveData<Uebung> returnUebung;
    private MutableLiveData<List<Fragment>> allFragmentsOfCurrentUebung;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.repository = new UebungRepository(application);
        this.allUebung = repository.getAlleUebungen();
        allFragmentsOfCurrentUebung = new MutableLiveData<>(null);
    }

    public void insert(Uebung uebung) {
        repository.insertUebung(uebung);
    }

    public void update(Uebung uebung) {
        repository.updateUebung(uebung);
    }
    public void delete(Uebung uebung) {
        repository.deleteUebung(uebung);
    }
    public void deleteAllUebung() {
        repository.deleteAllUebungen();
    }

    public LiveData<List<Uebung>> getAllUebung() {return allUebung;}

    public void insertFragment(Fragment fragment){
        repository.insertFragment(fragment);
    }
    public void updateFragment(Fragment fragment){
        repository.updateFragment(fragment);
    }
    public void deleteFragment(Fragment fragment){
        repository.deleteFragment(fragment);
    }

    public LiveData<List<Fragment>> getAllFragmentsOfUebung(int uebungID){
        return repository.getAlleFragmenteOfUebung(uebungID);
    }

    /*
    public void initReturnUebung(int uebungId){
        Log.i(TAG, "initReturnUebung: uebungID" + uebungId);
        this.returnUebung = repository.getUebungById(uebungId);
    }

    public LiveData<Uebung> getReturnUebung(){
        return returnUebung;
    }*/


    public LiveData<Uebung> getUebungById(int uebungID){
        return repository.getUebungById(uebungID);
    }


    public LiveData<List<Fragment>> getAllFragmentsOfCurrentUebung() {
        return allFragmentsOfCurrentUebung;
    }

    public void setAllFragmentsOfCurrentUebung(List<Fragment> allFragmentsOfCurrentUebung) {
        this.allFragmentsOfCurrentUebung.postValue(allFragmentsOfCurrentUebung);
    }
}

/*
CODE GRAVEYARD:

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }



    public Uebung getUebungById(int uebungID) throws UebungNotPresentInDatabaseException {
        List<Uebung> uebungList = allUebung.getValue();
        Uebung result = null;
        for(Uebung u: uebungList){
            if(u.getId() == uebungID){
                result = u;
            }
        }
        if(result == null){
            throw new UebungNotPresentInDatabaseException("getUebungByID failed");
        }
        return result;
    }
 */