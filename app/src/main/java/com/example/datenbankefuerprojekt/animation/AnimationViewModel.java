package com.example.datenbankefuerprojekt.animation;

import androidx.lifecycle.ViewModel;

import com.example.datenbankefuerprojekt.db.main.database.Fragment;
import com.example.datenbankefuerprojekt.db.main.database.Uebung;

public class AnimationViewModel extends ViewModel {

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

    private Fragment currentFragment;
    //hier iwie methode - go to next Fragment
    //da auch arrayindexoutofbounds abfangen unsooooo

    private int savedFrame;
    private BreatheAnimationState state;
    private float speed;
    private boolean playing;

    public AnimationViewModel(){
        savedFrame = 0;
        state = BreatheAnimationState.breathe_in;
        speed = 4f;
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

    public void setState(BreatheAnimationState state) {
        this.state = state;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}