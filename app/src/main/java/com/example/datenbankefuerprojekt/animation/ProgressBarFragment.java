package com.example.datenbankefuerprojekt.animation;

import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_COUNT;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_DESC;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_ID;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_PRIO;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_SECONDS;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_SPINNER_POSITION;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_TITEL;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_USE_SECONDS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;
import com.example.datenbankefuerprojekt.databinding.FragmentProgressBarBinding;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.ui.home.HomeViewModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;


/**
 * @author Maximilian Jaesch, Abdurrahman Azattemür
 *
 * diese Klasse setzt die Animation mit Progressbarund Uebungsdurchführung um
 * <p></p>
 * kopie und Ausbau von {@link AnimationFragment} */

public class ProgressBarFragment extends Fragment {
    public static final String TAG = "ProgressBarFragment";

    private HomeViewModel homeViewModel;

    private ProgressBarViewModel mViewModel;
    private FragmentProgressBarBinding binding;

    private ProgressBar progressBar;
    private Button buttonStartPause;
    private Button resetButton;

    private TextView textViewHold;

    private TextView textViewCountDown;
    private TextView textViewTitle;
    private TextView textViewFragmentTitle;

    private boolean alreadyInitialised;




    /**
     * @author Maximilian Jaesch
     *
     * standard onCreateView Methode, die auch die restlichen Methoden aufruft.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ProgressBarViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentProgressBarBinding.inflate(inflater,container,false);

        Log.i(TAG, "onCreateView: wird doppler?");
        initMemberVariables();
        receiveUebungAndSetViewModelAndTitle();
        receiveFragmentListAndSetToViewmodel();

        return binding.getRoot();
    }


    /**
     * @author Maximilian Jaesch
     *
     * diese hilfsmethode weist die referenzen aus dem Databinding den Objektvariablen zu
     * */
    private void initMemberVariables(){
        progressBar = binding.pbar;
        buttonStartPause = binding.buttonStartPauseProg;
        resetButton = binding.resetButtonProg;

        textViewHold = binding.textViewHold;

        textViewCountDown = binding.textViewTimerProg;
        textViewTitle = binding.textViewAnimationTitleProg;
        textViewFragmentTitle = binding.textViewFragmentTitleProg;
        //ich brauch noch ne reset methode
    }



    private void receiveUebungAndSetViewModelAndTitle(){
        //das kann nicht null sein idk
        Bundle bundle = getArguments();
        if(bundle == null){
            Toast.makeText(getContext(), "bundleFehler", Toast.LENGTH_SHORT).show();
        }
        Uebung receivedUebung = new Uebung(bundle.getString(EXTRA_TITEL,"bundle error"),bundle.getString(EXTRA_DESC,"default"), bundle.getInt(EXTRA_COUNT,0), bundle.getInt(EXTRA_PRIO,0), bundle.getBoolean(EXTRA_USE_SECONDS,false),bundle.getInt(EXTRA_SECONDS,0), bundle.getInt(EXTRA_SPINNER_POSITION,1) );
        receivedUebung.setId(bundle.getInt(EXTRA_ID, 0));
        mViewModel.setCurrentUebung(receivedUebung);
        textViewTitle.setText(receivedUebung.getTitel());
    }


    private void receiveFragmentListAndSetToViewmodel(){
        //um vor den zugriff auf List<Fragment> fragments sicherzugehen, das es nicht null ist
        //wird observed und geguckt ob die fragments null sind und ob sie leer sind.
        //wenn dies nicht der fall ist dann wird begonnen mit dem Zugriff.
        homeViewModel.getAllFragmentsOfCurrentUebung().observe(getViewLifecycleOwner(), new Observer<List<com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment>>() {
            @Override
            public void onChanged(List<com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment> fragments) {
                if(fragments != null){
                    if(fragments.isEmpty()){
                        Toast.makeText(getContext(), "Fragmente leer!!!!", Toast.LENGTH_SHORT).show();
                    }else{
                        mViewModel.setFragmentsOfCurrentUebung(fragments);
                        com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment test = mViewModel.getFragmentsOfCurrentUebung().get(0);
                        //wenn diese if bedingung True ist, dann ist alles gucci gang
                        if(test.getUebungId() == mViewModel.getCurrentUebung().getId()){
                            Log.i(TAG, "onChanged: ¿dolepe?");

                            Toast.makeText(getContext(), "Bereit", Toast.LENGTH_SHORT).show();
                            /*Fehler: wenn das Fragment geschlossen und wieder aufgerufen wird, dann wird diese onchanged methode doppelt aufrufen
                              und das zerstört die animation, sie überspringt die luftAnhaltTeile

                              mit der alreadyInitialised boolean variable wird dieser fall abgefangen!
                             */
                            if(!alreadyInitialised) {
                                Log.i(TAG, "onChanged: superdollo");
                                mViewModel.initUebung();
                                timerInitObservers();
                                //setupAnimation(); -> setup pbar
                                setListeners();
                                //lottieInitObservers(); -> init observerPbar?
                                //setListeners();
                            }
                            alreadyInitialised = true;
                        }
                        //init die ganze animations shit!!!
                    }
                }
            }
        });
    }



    /**
     *
     * @author Maximilian Jaesch
     * <p>
     * greift auf TimeLeftInMillis und UebungDurationZu, also erst aufrufen wenn diese initialisiert wurden
     * gleiche Methode wie in AnimationFragment</p>
     * <p>
     * der Fragment titel wird bei jedem BreatheAnimationState change geupdated!
     * </p>
     * */
    private void timerInitObservers(){
        mViewModel.isUebungRunning().observe(getViewLifecycleOwner(), timerRunning -> {
            if(timerRunning){
                buttonStartPause.setText(R.string.pause);
                resetButton.setEnabled(false);
            } else {
                buttonStartPause.setText(R.string.start);
                //das hier is blöd aber die resources gehen nicht
                //die antwort is glaub ich ne benutzerdefinierte value!!!
                if(mViewModel.getTimeLeftInMillis().getValue() != mViewModel.getUebungDurationInSeconds())
                    resetButton.setEnabled(true);

            }
        });


        mViewModel.isUebungFinished().observe(getViewLifecycleOwner(), timerFinished -> {
            if(timerFinished){
                buttonStartPause.setEnabled(false);
                Log.i(TAG, "onChanged: buttonstartpauseinvvvv");
            } else {
                buttonStartPause.setEnabled(true);
            }
        });

        mViewModel.getTimeLeftInMillis().observe(getViewLifecycleOwner(), timeLeftInMillis -> {
            int minutes = (int) (timeLeftInMillis / 1000) / 60; //divide by 1000 for seconds, then by 60 for minutes
            int seconds = (int) (timeLeftInMillis / 1000) % 60; // modulus for getting the leftover seconds after extracting minutes

            String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

            textViewCountDown.setText(timeLeftFormatted);
        });

        mViewModel.getProgressPbar().observe(getViewLifecycleOwner(), progress -> {
            progressBar.setProgress(progress);
            Log.i(TAG, "timerInitObservers: progress" + progress);
        });

        mViewModel.getMaxPbar().observe(getViewLifecycleOwner(), max -> progressBar.setMax(max));

        mViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            textViewFragmentTitle.setText(mViewModel.getCurrentFragment().getTitelFragment());
            if (state == BreatheAnimationState.hold_up || state == BreatheAnimationState.hold_down){
                textViewHold.setVisibility(View.VISIBLE);
            } else {
                textViewHold.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setListeners(){

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel.isUebungRunning().getValue()){
                    mViewModel.pauseTimer();
                   // lottieAnimationView.pauseAnimation();
                   // mViewModel.setSavedFrame(lottieAnimationView.getFrame());
                } else {
                    mViewModel.startTimer();
                   // lottieAnimationView.setMinFrame(mViewModel.getSavedFrame());
                  ////  lottieAnimationView.playAnimation();
                }
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.resetTimer();
                mViewModel.resetUebung();
                resetButton.setEnabled(false);
                //lottieAnimationView.setMinFrame(0);
                //lottieAnimationView.setProgress(0);
                //setupAnimation();
            }
        });

        resetButton.setEnabled(false);
        //buttonStartPause.setEnabled(true);

        /*
        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lottieAnimationView.setMinFrame(0);
                lottieAnimationView.setProgress(0);
                //mViewModel.increaseCurrentFragmentRepetitions();
                mViewModel.changeToNextBreatheAnimationState();
                mViewModel.calcAndSetSpeed();

                setupAnimation();
                lottieAnimationView.playAnimation();
            }
        });*/
    }
}
/*

    private final MyHandler myHandler = new MyHandler();
    private MyRunnable myRunnable;
*/

/*Da wir wait auf den Thread callen möchten, und nicht den UI-Thread pausieren möchten
 * weil es dazu führen würde, dass die applikation einfriert, wird eine Runnable erstellt, die auf einem seperaten Thread läuft.
 *
 *
 * */
//https://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android
/*
private static class MyHandler extends Handler {
}

private static class MyRunnable implements Runnable {
    private final WeakReference<ProgressBarFragment> progressBarFragmentWeakReference;

    public MyRunnable(ProgressBarFragment fragment) {
        progressBarFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void run() {
        ProgressBarFragment fragment = progressBarFragmentWeakReference.get();
        if (fragment != null) {

        }
    }
}

class PbarRunnable implements Runnable {
    @Override
    public void run() {
        //mHandler.post(()->textView.setText("oof"));


        Log.i(TAG, "run: forschleifffe");
        if(mViewModel.getState() == BreatheAnimationState.breathe_in){

            myHandler.post(() -> progressBar.setProgress(0));
            //progressBar.setProgress(j);
            Log.i(TAG,"up");
            android.os.SystemClock.sleep(50);
        }else if(mViewModel.getState() == BreatheAnimationState.hold_up){
            Log.i(TAG,"waitup");
            android.os.SystemClock.sleep(50);

        }else if(mViewModel.getState() == BreatheAnimationState.breathe_out){
            Log.i(TAG,"down");
            int finalJ2 = j;
            myHandler.post(() -> progressBar.setProgress(200- finalJ2));                            //progressBar.setProgress(200-j);
            android.os.SystemClock.sleep(50);

        }else if(mViewModel.getState() == BreatheAnimationState.hold_down){
            Log.i(TAG,"waitdown");
            android.os.SystemClock.sleep(50);

        }


    }
}
*/