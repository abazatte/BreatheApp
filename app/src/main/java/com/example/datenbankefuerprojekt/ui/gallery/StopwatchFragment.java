package com.example.datenbankefuerprojekt.ui.gallery;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.example.datenbankefuerprojekt.databinding.StopwatchFragment2Binding;
import com.example.datenbankefuerprojekt.db.main.database.ControlPause;

import java.util.Calendar;


public class StopwatchFragment extends Fragment {
    private StopwatchFragment2Binding binding;
    private GalleryViewModel mViewModel;

    private Chronometer chronometer;

    public static StopwatchFragment newInstance() {
        return new StopwatchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = StopwatchFragment2Binding.inflate(inflater,container,false);
        View root = binding.getRoot();

        mViewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        chronometer = binding.chronometer;
        //chronometer.setFormat("Luft Anhalten: %s");
        restartChronometer();


        binding.buttonStart.setOnClickListener(view -> startChronometer());
        binding.buttonPause.setOnClickListener(view -> pauseChronometer());
        binding.buttonReset.setOnClickListener(view -> resetChronometer());

        mViewModel.getSavedTime().observe(this, savedTime -> binding.textViewSaveTime.setText(Long.toString(savedTime) + "s"));

        binding.buttonSaveToDatabase.setOnClickListener(view -> saveTimeToDatabase());

        return root;
    }

    public void restartChronometer(){
        if(mViewModel.getState() == StopwatchState.running){
            //weiter
            chronometer.setBase(mViewModel.getStartTime());
            chronometer.start();
        } else if (mViewModel.getState() == StopwatchState.paused){
            //weiter von saved state
            mViewModel.setStartTime(SystemClock.elapsedRealtime() - mViewModel.getPauseOffset());
            chronometer.setBase(mViewModel.getStartTime());
        }
        //do nothing if its stopped!
    }



    public void startChronometer(){
        if (mViewModel.getState() == StopwatchState.stopped){
            mViewModel.setStartTime(SystemClock.elapsedRealtime());
            chronometer.setBase(mViewModel.getStartTime());
            chronometer.start();
            mViewModel.setState(StopwatchState.running);
        } else if(mViewModel.getState() == StopwatchState.paused){
            //pauseOffset sind die vergangenen sekunden x
            //wenn wir die startzeit auf jetzt - x stellen, dann wird hat die Stoppuhr bei start schon
            //x sekunden
            mViewModel.setStartTime(SystemClock.elapsedRealtime() - mViewModel.getPauseOffset());
            chronometer.setBase(mViewModel.getStartTime());
            chronometer.start();
            mViewModel.setState(StopwatchState.running);
        }
    }



    public void pauseChronometer(){
        if(mViewModel.getState() == StopwatchState.running){
            chronometer.stop();

            //wir speichern die schon vergangenen sekunden in PauseOffset
            mViewModel.setPauseOffset(SystemClock.elapsedRealtime() - chronometer.getBase());
            mViewModel.setState(StopwatchState.paused);
        }
    }


    public void resetChronometer(){
        mViewModel.setSavedTime((SystemClock.elapsedRealtime() - chronometer.getBase())/1000L);
        if(mViewModel.getState() == StopwatchState.running){
            mViewModel.setStartTime(SystemClock.elapsedRealtime());
            chronometer.setBase(mViewModel.getStartTime());
        } else if(mViewModel.getState() == StopwatchState.paused){
            mViewModel.setStartTime(SystemClock.elapsedRealtime());
            chronometer.setBase(mViewModel.getStartTime());
            mViewModel.setState(StopwatchState.stopped);
        }

    }

    public void saveTimeToDatabase(){
        //kann kein nullpointerexception bei getSavedTime geben, da es mit dem 0L wert initialisiert ist
        ControlPause controlPause = new ControlPause(new java.sql.Date(Calendar.getInstance().getTime().getTime()), mViewModel.getSavedTime().getValue());

        mViewModel.insert(controlPause);
    }

}