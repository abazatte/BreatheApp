package com.example.datenbankefuerprojekt.animation;


import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;

import java.util.List;

public class ProgressBarViewModel extends ViewModel {
    public static final String TAG = "ProgressBarViewModel";

    /* TODO: iwie durch uebung iterieren
             von test projekt rüberholen

     */

    //hier muss ich wohl iwie ne mehtode haben bei changeanimation das ich im viewmodel was hochzähle oder so???????
    //und dann ne abfrage??
    //und eine methode, wo der timer das aufruft wenns fertig ist, zb iwie end methode oder so

    //Abfangen: wenn breathe_in 0 ist

    //länge der animation, wird bei erstellung der animation in AfterEffects festgelegt
    //bei uns 10sekunden
    //public static final float BASE_ANIMATION_DURATION = 10f;

    private Uebung currentUebung;


    //time oder repetitions of uebung
    private int uebungDurationInSeconds;

    private List<Fragment> fragmentsOfCurrentUebung;
    private Fragment currentFragment;
    private int currentFragmentRepetitions;
    private int currentFragmentListPosition;
    //hier iwie methode - go to next Fragment
    //da auch arrayindexoutofbounds abfangen unsooooo


    private MutableLiveData<BreatheAnimationState> state;
    private MutableLiveData<Integer> progressPbar;
    private MutableLiveData<Integer> maxPbar;

    private CountDownTimer countDownTimer;

    private MutableLiveData<Boolean> uebungRunning;
    private MutableLiveData<Boolean> uebungFinished;
    private MutableLiveData<Long> timeLeftInMillis;

    private MutableLiveData<Long> timeLeftBreatheIn;
    private MutableLiveData<Long> timeLeftUp;
    private MutableLiveData<Long> timeLeftDown;
    private MutableLiveData<Long> timeLeftBreatheOut;

    private CountDownTimer progTimer;



    /**
     * Dieser Konstruktor sollte nur aufgerufen werden wenn das Fragment neu geöffnet wird, da das Fragment der Owner des Viewmodels ist
     * Dieser Aufruf passiert nur wenn der User eine Übung von vorne beginnen möchte, also werden alle werte auf 0 gesetzt
     *
     * keine von den Variablen die mit uebung oder Fragment zu tun haben werden hier abgefragt um racecondition
     * (Konstruktoraufruf vs SQL abfrage der Uebung und fragmente)
     *
     * */
    public ProgressBarViewModel(){
        currentFragmentListPosition = 0;
        currentFragmentRepetitions = 0;

        state = new MutableLiveData<>(BreatheAnimationState.breathe_in);
        progressPbar = new MutableLiveData<>(0);
        maxPbar = new MutableLiveData<>(0);
        uebungRunning = new MutableLiveData<>(false);
        uebungFinished = new MutableLiveData<>(false);
        //8000 ist ein dummy wert der überschrieben wird, nicht 0 da observer gucken ob dies 0 ist
        timeLeftInMillis = new MutableLiveData<>(8000L);
        timeLeftBreatheIn = new MutableLiveData<>(8000L);;
        timeLeftUp = new MutableLiveData<>(8000L);;
        timeLeftDown = new MutableLiveData<>(8000L);;
        timeLeftBreatheOut = new MutableLiveData<>(8000L);;

    }

    /**
     * <p>wird aufgerufen wenn currentUebung und fragmentsOfCurrentUebung schon initialisiert sind
     * </p>
     *
     * <p>Berechnung für den fall das wiederholungen verwendet werden: </p>
     *
     *  <p>((breatheIn + holdIn + breatheOut + holdOut)*wiederholungenFragment) für alle fragmente</p>
     *    ergebnis * wiederholungenUebung
     * */
    public void initUebung(){
        if(currentUebung.getUseTimed()){
            uebungDurationInSeconds = currentUebung.getTimeInSeconds();
        } else {
            int durationHelp = 0;
            for(Fragment f: fragmentsOfCurrentUebung){
                durationHelp += f.getAnzahlWiederholungenFragment()*(f.getEinAtmenZeit()+f.getEinLuftanhaltZeit()+f.getAusAtmenZeit()+f.getAusLuftanhaltZeit());
            }
            uebungDurationInSeconds = durationHelp * currentUebung.getAnzahlDerWiederholungen();

        }
        resetUebung();
    }


    /**
     * wird vom Reset button aufgerufen, alles in anfangszustand
     * */
    public void resetUebung(){
        timeLeftInMillis.setValue(uebungDurationInSeconds * 1000L);
        currentFragmentListPosition = 0;
        currentFragmentRepetitions = 0;
        currentFragment = fragmentsOfCurrentUebung.get(currentFragmentListPosition);


        state.setValue( BreatheAnimationState.breathe_in);
        progressPbar.setValue(0);
        calcAndSetMax();
        timeLeftBreatheIn.setValue((long)getMaxPbar().getValue());
    }
    //diese Methode in startUebung aufrufen
    public void startTimer(){
        resumeBreatheProgress();
        countDownTimer = new CountDownTimer(timeLeftInMillis.getValue(), 1000) {
            @Override
            public void onTick(long millisLeftUntilFinished) {
                timeLeftInMillis.setValue(millisLeftUntilFinished);

            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish von timer aufgerufen");

                uebungRunning.setValue(false);
                uebungFinished.setValue(true);

                progTimer.cancel();
            }
        }.start();
        uebungRunning.setValue(true);
        uebungFinished.setValue(false);

    }



    public void pauseTimer(){
        countDownTimer.cancel();
        progTimer.cancel();
        uebungRunning.setValue(false);

    }

    public void resetTimer(){
        timeLeftInMillis.setValue((long)uebungDurationInSeconds);
        uebungFinished.setValue(false);
    }

    private void resumeBreatheProgress(){
        if(state.getValue() == BreatheAnimationState.breathe_in){
            breatheInTimer();
        }else if(state.getValue() == BreatheAnimationState.hold_up) {
            holdUpTimer();
        }else if(state.getValue() == BreatheAnimationState.breathe_out) {
            breatheOutTimer();
        }else if(state.getValue() == BreatheAnimationState.hold_down){
            holdDownTimer();
        }
    }

    private void changeToNextFragment(){
        if(currentFragmentListPosition+1 < fragmentsOfCurrentUebung.size()){
            currentFragmentListPosition++;
        } else {
            //ist Uebung duration schon vorbei oder die anzahl an wiederholungen schon durch? dann ende
            // das wird im timer abgefragt, also hier nicht!

            currentFragmentListPosition = 0;
        }
        currentFragment = fragmentsOfCurrentUebung.get(currentFragmentListPosition);
    }

    public void increaseCurrentFragmentRepetitions(){
        if(currentFragmentRepetitions+1 < currentFragment.getAnzahlWiederholungenFragment()){
            currentFragmentRepetitions++;
        }else{
            currentFragmentRepetitions = 0;
            changeToNextFragment();
        }
    }

    private void breatheInTimer(){
        if(!uebungFinished.getValue()) {
            Log.i(TAG, "breatheInTimer: nein");

            progTimer = new CountDownTimer(timeLeftBreatheIn.getValue(), 10) {
                @Override
                public void onTick(long l) {
                    progressPbar.setValue((int) (getMaxPbar().getValue() - l));
                    timeLeftBreatheIn.setValue(l);
                    Log.i(TAG, "onTick: breatheIn");
                }

                @Override
                public void onFinish() {
                    changeToNextBreatheAnimationState();
                    timeLeftUp.setValue(currentFragment.getEinLuftanhaltZeit() * 1000L);
                    holdUpTimer();
                }
            }.start();
        }
    }

    private void holdUpTimer(){
        if(!uebungFinished.getValue()) {
            Log.i(TAG, "holdUpTimer: start");

            progTimer = new CountDownTimer(timeLeftUp.getValue(), 10) {
                @Override
                public void onTick(long l) {
                    Log.i(TAG, "onTick: hold");
                    timeLeftUp.setValue(l);
                }

                @Override
                public void onFinish() {
                    changeToNextBreatheAnimationState();
                    calcAndSetMax();
                    timeLeftBreatheOut.setValue((long)getMaxPbar().getValue());
                    breatheOutTimer();
                }
            }.start();
        }
    }

    private void breatheOutTimer(){
        if(!uebungFinished.getValue()) {
            Log.i(TAG, "breatheOutTimer: start");

            calcAndSetMax();
            progTimer = new CountDownTimer(timeLeftBreatheOut.getValue(), 10) {
                @Override
                public void onTick(long l) {
                    progressPbar.setValue((int) l);
                    timeLeftBreatheOut.setValue(l);
                    Log.i(TAG, "onTick: breatheOut");
                }

                @Override
                public void onFinish() {
                    changeToNextBreatheAnimationState();
                    timeLeftDown.setValue(currentFragment.getAusLuftanhaltZeit() * 1000L);
                    holdDownTimer();
                }
            }.start();
        }
    }

    private void holdDownTimer(){
        if(!uebungFinished.getValue()) {
            Log.i(TAG, "holdUpTimer: start");

            progTimer = new CountDownTimer(timeLeftDown.getValue(), 10) {
                @Override
                public void onTick(long l) {
                    timeLeftDown.setValue(l);
                    Log.i(TAG, "onTick: hold");
                }

                @Override
                public void onFinish() {
                    changeToNextBreatheAnimationState();
                    calcAndSetMax();
                    timeLeftBreatheIn.setValue((long)getMaxPbar().getValue());
                    breatheInTimer();
                }
            }.start();
        }
    }


    //könnte raceconditions hevorrufen
    public void changeToNextBreatheAnimationState(){

        if(state.getValue() == BreatheAnimationState.breathe_in){
            Log.i(TAG, "changeToNextBreatheAnimationState: breathe in ");
            state.setValue(BreatheAnimationState.hold_up);

        }else if(state.getValue() == BreatheAnimationState.hold_up) {
            Log.i(TAG, "changeToNextBreatheAnimationState: holdup ");
            state.setValue(BreatheAnimationState.breathe_out);

        }else if(state.getValue() == BreatheAnimationState.breathe_out) {
            Log.i(TAG, "changeToNextBreatheAnimationState: breathe out ");
            state.setValue(BreatheAnimationState.hold_down);

        }else if(state.getValue() == BreatheAnimationState.hold_down){
            state.setValue(BreatheAnimationState.breathe_in);
            increaseCurrentFragmentRepetitions();
            Log.i(TAG, "changeToNextBreatheAnimationState: holddown ");

        }
    }


    /**
     * currentFragment wird abgerufen, also erst nach dem diese Aus datenbank gekommen sind aufrufen
     * */
    public void calcAndSetMax(){

        if(state.getValue() == BreatheAnimationState.breathe_in){
            this.maxPbar.setValue(currentFragment.getEinAtmenZeit()*1000);
        }
        if(state.getValue() == BreatheAnimationState.breathe_out) {
            this.maxPbar.setValue(currentFragment.getAusAtmenZeit()*1000);        }

    }

    public Uebung getCurrentUebung() {
        return currentUebung;
    }

    public void setCurrentUebung(Uebung currentUebung) {
        this.currentUebung = currentUebung;
    }


    public LiveData<BreatheAnimationState> getState() {
        return state;
    }


    public List<Fragment> getFragmentsOfCurrentUebung() {
        return fragmentsOfCurrentUebung;
    }

    public void setFragmentsOfCurrentUebung(List<Fragment> fragmentsOfCurrentUebung) {
        this.fragmentsOfCurrentUebung = fragmentsOfCurrentUebung;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public int getUebungDurationInSeconds() {
        return uebungDurationInSeconds;
    }

    public LiveData<Boolean> isUebungRunning() {
        return uebungRunning;
    }
    public LiveData<Boolean> isUebungFinished() {
        return uebungFinished;
    }
    public LiveData<Long> getTimeLeftInMillis(){
        return timeLeftInMillis;
    }

    public LiveData<Integer> getProgressPbar() {
        return progressPbar;
    }

    public void setProgressPbar(int progressPbar) {
        this.progressPbar.setValue(progressPbar);
    }

    public LiveData<Integer> getMaxPbar() {
        return maxPbar;
    }

    public void setMaxPbar(int maxPbar) {
        this.progressPbar.setValue(maxPbar);
    }
}
