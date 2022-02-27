package com.example.datenbankefuerprojekt.animation;

import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_COUNT;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_DESC;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_ID;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_PRIO;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_SECONDS;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_SPINNER_POSITION;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_TITEL;
import static com.example.datenbankefuerprojekt.ui.home.HomeFragment.EXTRA_USE_SECONDS;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;
import com.example.datenbankefuerprojekt.ui.home.HomeViewModel;

import java.util.List;
import java.util.Locale;

/**
 * @author Maximilian Jaesch, Abdurrahman Azattemür
 *
 * diese Klasse setzt die Animation und Uebungsdurchführung um*/
public class AnimationFragment extends Fragment {
    public static final String TAG = "AnimationFragment";

    private HomeViewModel homeViewModel;

    private AnimationViewModel mViewModel;
    private AnimationFragmentBinding binding;

    private LottieAnimationView lottieAnimationView;
    private Button buttonStartPause;
    private Button resetButton;

    private TextView textViewCountDown;
    private TextView textViewTitle;
    private TextView textViewFragmentTitle;

    private boolean alreadyInitialised;
    //Textview timer??

    /*DONE:
     * animation um viewmodel erweitern
     * -> egal nur um sachen zu speichern, keine lifecycle sachen das das weiterläuft!
     * keep it simple, i need to finish rather than perfect!
     * animation rüberholen
     * timer rüberholen
     * bundle abholen -> auf die einzelnen werte packenn????? fuck idk
     *
     * fragment liste abholen -> gucken das die nicht null ist und
     * dann gucken das das zu der id der übung passt, dann erst activate buttons to start
     *
     * neue idee für fragment liste -> die query iwie beim recyclerview schon machen bevor geklickt wurde, damit viel zeit is zwischen init und
     * start der übung
     *
     * */



    /**
     * @author Maximilian Jaesch, Abdurrahman Azattemür(debugging)
     *
     * diese Methode initialisiert die Viewmodel, holt die Übung aus dem Bundle
     * und extrahiert alle informationen aus dem fragment, wenn dieses von der SQL query eingetroffen ist
     * */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //this viewmodel should be owned by "this" so it gets destroyed when we have a different uebung
        mViewModel = new ViewModelProvider(this).get(AnimationViewModel.class);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = AnimationFragmentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        Log.i(TAG, "onCreateView: wird doppler?");
        initMemberVariables();

        //das kann nicht null sein idk
        Bundle bundle = getArguments();
        if(bundle == null){
            Toast.makeText(getContext(), "bundleFehler", Toast.LENGTH_SHORT).show();
        }
        Uebung receivedUebung = new Uebung(bundle.getString(EXTRA_TITEL,"bundle error"),bundle.getString(EXTRA_DESC,"default"), bundle.getInt(EXTRA_COUNT,0), bundle.getInt(EXTRA_PRIO,0), bundle.getBoolean(EXTRA_USE_SECONDS,false),bundle.getInt(EXTRA_SECONDS,0), bundle.getInt(EXTRA_SPINNER_POSITION,1) );
        receivedUebung.setId(bundle.getInt(EXTRA_ID, 0));
        mViewModel.setCurrentUebung(receivedUebung);

        textViewTitle.setText(receivedUebung.getTitel());


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
                                mViewModel.initUebung();
                                timerInitObservers();
                                setupAnimation();
                                lottieInitObservers();
                                setListeners();
                            }
                            alreadyInitialised = true;
                        }
                        //init die ganze animations shit!!!
                    }
                }
            }
        });
        //mViewModel.setFragmentsOfCurrentUebung(homeViewModel.getAllFragmentsOfCurrentUebung());


        return root;
    }

    /*
    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setSavedFrame(lottieAnimationView.getFrame());
        //mViewModel.setPlaying(lottieAnimationView.isAnimating());
    }*/

    /**
     * @author Maximilian Jaesch
     *
     * diese hilfsmethode weist die referenzen aus dem Databinding den Objektvariablen zu
     * */

    private void initMemberVariables(){
        lottieAnimationView = binding.lottiePolygon;
        buttonStartPause = binding.buttonStartPause;
        resetButton = binding.resetButton;
        textViewCountDown = binding.textViewTimer;
        textViewTitle = binding.textViewAnimationTitle;
        textViewFragmentTitle = binding.textViewFragmentTitle;
        //ich brauch noch ne reset methode
    }





    //hilfsmethoden ab hier



    /**
     * @author Maximilian Jaesch
     *
     * diese hilfsmethode setted einen Observer, der dafür sorgt, das die animation stoppt wenn die zeit abgelaufen ist*/
    private void lottieInitObservers(){
        mViewModel.isUebungFinished().observe(getViewLifecycleOwner(), finished -> {
            lottieAnimationView.pauseAnimation();
        });
    }


    /**
     * @author Maximilian Jaesch
     * <p>
     *
     * </p>
     * diese Methode setzt Observer auf MutableLiveData von mViewModel, damit die Buttons richtig deaktiviert und aktiviert werden
     * Wenn die uebung vorbei ist soll z. B. der Start Button deaktiviert sein
     * <p>
     *
     * </p>
     * greift auf TimeLeftInMillis und UebungDurationZu, also erst aufrufen wenn diese initialisiert wurden
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
    }

    /**
     * @author Maximilian Jaesch
     * <p></p>
     * OnClickListener für steuerung über Buttons werden gesetzt.
     * Es wird ein OnAnimationEndListener gesetzt, damit die nächste Animation geladen werden kann.
     *
     * */
    private void setListeners(){

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel.isUebungRunning().getValue()){
                    mViewModel.pauseTimer();
                    lottieAnimationView.pauseAnimation();
                    mViewModel.setSavedFrame(lottieAnimationView.getFrame());
                } else {
                    mViewModel.startTimer();
                    lottieAnimationView.setMinFrame(mViewModel.getSavedFrame());
                    lottieAnimationView.playAnimation();
                }
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.resetTimer();
                mViewModel.resetUebung();
                resetButton.setEnabled(false);
                lottieAnimationView.setMinFrame(0);
                lottieAnimationView.setProgress(0);
                setupAnimation();
            }
        });

        resetButton.setEnabled(false);
        //buttonStartPause.setEnabled(true);

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
        });
    }


    /**
     * @author Maximilian Jaesch
     * <p></p>
     * Es werden die werte des lottieAnimationView vorbereitet
     * */
    private void setupAnimation(){
        Log.i(TAG, "setupAnimation: speed:" + mViewModel.getSpeed());
        Log.i(TAG, "setupAnimation: Progress" + lottieAnimationView.getProgress());
        lottieAnimationView.setSpeed(mViewModel.getSpeed());


        textViewFragmentTitle.setText(mViewModel.getCurrentFragment().getTitelFragment());

        if(mViewModel.getState() == BreatheAnimationState.breathe_in){
            lottieAnimationView.setAnimation(R.raw.breathe_in);
        }
        if(mViewModel.getState() == BreatheAnimationState.hold_up) {
            lottieAnimationView.setAnimation(R.raw.hold_up);
        }
        if(mViewModel.getState() == BreatheAnimationState.breathe_out) {
            lottieAnimationView.setAnimation(R.raw.breathe_out);
        }
        if(mViewModel.getState() == BreatheAnimationState.hold_down){
            lottieAnimationView.setAnimation(R.raw.hold_down);
        }
    }
}
