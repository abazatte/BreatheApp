package com.example.datenbankefuerprojekt.animation;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datenbankefuerprojekt.db.main.database.fragment.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.uebung.Uebung;

import java.util.List;

/**
 * @author Maximilian Jaesch, Abdurrahman Azattemür
 *
 * dieses Viewmodel hält die Übung und die Liste an Fragmentetn und
 * erlaubt eine gute Steuerung des AnimationFragment über seine MutableLiveData
 *
 * <p></p>
 * <a>Quelle: https://www.youtube.com/watch?v=MDuGwI6P-X8</a>
 * der Timer wurde mithilfe von diesem Tutorial erstellt, aber in eigenarbeit auf Viewmodel und MutableLiveData ausgebaut
 *
 * */
public class AnimationViewModel extends ViewModel {
    public static final String TAG = "AnimationViewModel";

    /* TODO: iwie durch uebung iterieren
             von test projekt rüberholen

     */

    //hier muss ich wohl iwie ne mehtode haben bei changeanimation das ich im viewmodel was hochzähle oder so???????
    //und dann ne abfrage??
    //und eine methode, wo der timer das aufruft wenns fertig ist, zb iwie end methode oder so

    //Abfangen: wenn breathe_in 0 ist

    //länge der animation, wird bei erstellung der animation in AfterEffects festgelegt
    //bei uns 10sekunden
    public static final float BASE_ANIMATION_DURATION = 10f;

    private Uebung currentUebung;


    //time oder repetitions of uebung
    private int uebungDurationInSeconds;

    private List<Fragment> fragmentsOfCurrentUebung;
    private Fragment currentFragment;
    private int currentFragmentRepetitions;
    private int currentFragmentListPosition;
    //hier iwie methode - go to next Fragment
    //da auch arrayindexoutofbounds abfangen unsooooo


    private int savedFrame;
    private BreatheAnimationState state;
    //lottie soll das observen
    private float speed;

    private CountDownTimer countDownTimer;

    private MutableLiveData<Boolean> uebungRunning;
    private MutableLiveData<Boolean> uebungFinished;
    private MutableLiveData<Long> timeLeftInMillis;

    /**
     * @author Maximilian Jaesch
     *
     * Dieser Konstruktor sollte nur aufgerufen werden wenn das Fragment neu geöffnet wird, da das Fragment der Owner des Viewmodels ist
     * Dieser Aufruf passiert nur wenn der User eine Übung von vorne beginnen möchte, also werden alle werte auf 0 gesetzt
     *
     * keine von den Variablen die mit uebung oder Fragment zu tun haben werden hier abgefragt um racecondition
     * (Konstruktoraufruf vs SQL abfrage der Uebung und fragmente)
     *
     * */
    public AnimationViewModel(){


        currentFragmentListPosition = 0;
        currentFragmentRepetitions = 0;

        savedFrame = 0;
        state = BreatheAnimationState.breathe_in;
        speed = 4f;

        uebungRunning = new MutableLiveData<>(false);
        uebungFinished = new MutableLiveData<>(false);
        //8000 ist ein dummy wert der überschrieben wird, nicht 0 da observer gucken ob dies 0 ist
        timeLeftInMillis = new MutableLiveData<>(8000L);

    }

    /**@author Maximilian Jaesch
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
     * @author Maximilian Jaesch
     * wird vom Reset button aufgerufen, alles wird in den Anfangszustand gesetzt
     * */
    public void resetUebung(){
        timeLeftInMillis.setValue(uebungDurationInSeconds * 1000L);
        currentFragmentListPosition = 0;
        currentFragmentRepetitions = 0;
        currentFragment = fragmentsOfCurrentUebung.get(currentFragmentListPosition);


        savedFrame = 0;
        state = BreatheAnimationState.breathe_in;
        calcAndSetSpeed();
    }



    /**
     * @author Maximilian Jaesch
     *
     * startet den Timer, der die Uebung anfängt und beendet
     * */

    //diese Methode in startUebung aufrufen
    public void startTimer(){
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
            }
        }.start();
        uebungRunning.setValue(true);
        uebungFinished.setValue(false);

    }

    public void pauseTimer(){
        countDownTimer.cancel();
        uebungRunning.setValue(false);

    }

    public void resetTimer(){
        timeLeftInMillis.setValue((long)uebungDurationInSeconds);
        uebungFinished.setValue(false);
    }


    /**
     * @author Maximilian Jaesch
     * <p>
     *    wechselt zum nächsten Fragment der Übung, wenn es durch ist, dann fängt es von vorne an
     *    wird von increaseCurrentFragmentRepetitions alleinig aufgerufen
     * </p>
     * */
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

    /**
     * @author Maximilian Jaesch
     * <p>
     *    incrementiert die Durchläufe des Fragments
     *    wird von changeToNextBreatheAnimationState alleinig aufgerufen
     * </p>
     * */
    public void increaseCurrentFragmentRepetitions(){
        if(currentFragmentRepetitions+1 < currentFragment.getAnzahlWiederholungenFragment()){
            currentFragmentRepetitions++;
        }else{
            currentFragmentRepetitions = 0;
            changeToNextFragment();
        }
    }




    //könnte raceconditions hevorrufen
    public void changeToNextBreatheAnimationState(){

        if(state == BreatheAnimationState.breathe_in){
            Log.i(TAG, "changeToNextBreatheAnimationState: breathe in ");
            state = BreatheAnimationState.hold_up;

        }else if(state == BreatheAnimationState.hold_up) {
            Log.i(TAG, "changeToNextBreatheAnimationState: holdup ");
            state = BreatheAnimationState.breathe_out;

        }else if(state == BreatheAnimationState.breathe_out) {
            Log.i(TAG, "changeToNextBreatheAnimationState: breathe out ");
            state = BreatheAnimationState.hold_down;

        }else if(state == BreatheAnimationState.hold_down){
            state = BreatheAnimationState.breathe_in;
            increaseCurrentFragmentRepetitions();
            Log.i(TAG, "changeToNextBreatheAnimationState: holddown ");

        }
    }


    /**
     * currentFragment wird abgerufen, also erst nach dem diese Aus datenbank gekommen sind aufrufen
     * */
    public void calcAndSetSpeed(){
        float duration = 0f;
        if(state == BreatheAnimationState.breathe_in){
            duration = (float) currentFragment.getEinAtmenZeit();
        }
        if(state == BreatheAnimationState.hold_up) {
            duration = (float) currentFragment.getEinLuftanhaltZeit();

        }
        if(state == BreatheAnimationState.breathe_out) {
            duration = (float) currentFragment.getAusAtmenZeit();

        }
        if(state == BreatheAnimationState.hold_down){
            duration = (float) currentFragment.getAusLuftanhaltZeit();

        }
        //if 0 then set speed to 10000f so the part of the animation gets skipped
        if(duration == 0f) {
            this.speed = 10000f;
        }else{
            this.speed = BASE_ANIMATION_DURATION / duration;
        }
    }

    public Uebung getCurrentUebung() {
        return currentUebung;
    }

    public void setCurrentUebung(Uebung currentUebung) {
        this.currentUebung = currentUebung;
    }

    public int getSavedFrame() {
        return savedFrame;
    }

    public void setSavedFrame(int savedFrame) {
        this.savedFrame = savedFrame;
    }

    public BreatheAnimationState getState() {
        return state;
    }

    public float getSpeed() {
        return speed;
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

}