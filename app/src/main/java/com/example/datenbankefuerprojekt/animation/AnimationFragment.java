package com.example.datenbankefuerprojekt.animation;

import static com.example.datenbankefuerprojekt.animation.AnimationViewModel.BASE_ANIMATION_DURATION;
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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;
import com.example.datenbankefuerprojekt.db.main.database.Uebung;
import com.example.datenbankefuerprojekt.ui.gallery.GalleryFragment;
import com.example.datenbankefuerprojekt.ui.home.HomeViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

public class AnimationFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private AnimationViewModel mViewModel;
    private AnimationFragmentBinding binding;

    private LottieAnimationView lottieAnimationView;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    //Textview timer??

    /*TODO:
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



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //this viewmodel should be owned by "this" so it gets destroyed when we have a different uebung
        mViewModel = new ViewModelProvider(this).get(AnimationViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = AnimationFragmentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        initMemberVariables();

        //das kann nicht null sein idk
        Bundle bundle = getArguments();
        if(bundle == null){
            Toast.makeText(getContext(), "bundleFehler", Toast.LENGTH_SHORT).show();
        }
        Uebung receivedUebung = new Uebung(bundle.getString(EXTRA_TITEL,"bundle error"),bundle.getString(EXTRA_DESC,"default"), bundle.getInt(EXTRA_COUNT,0), bundle.getInt(EXTRA_PRIO,0), bundle.getBoolean(EXTRA_USE_SECONDS,false),bundle.getInt(EXTRA_SECONDS,0), bundle.getInt(EXTRA_SPINNER_POSITION,1) );
        receivedUebung.setId(bundle.getInt(EXTRA_ID, 0));
        mViewModel.setCurrentUebung(receivedUebung);

        //hier noch iwie vor warten
        homeViewModel.getAllFragmentsOfCurrentUebung().observe(this, new Observer<List<com.example.datenbankefuerprojekt.db.main.database.Fragment>>() {
            @Override
            public void onChanged(List<com.example.datenbankefuerprojekt.db.main.database.Fragment> fragments) {
                if(fragments != null){
                    if(fragments.isEmpty()){
                        Toast.makeText(getContext(), "Fragmente leer!!!!", Toast.LENGTH_SHORT).show();
                    }else{
                        mViewModel.setFragmentsOfCurrentUebung(fragments);
                        com.example.datenbankefuerprojekt.db.main.database.Fragment test = mViewModel.getFragmentsOfCurrentUebung().get(0);
                        if(test.getUebungId() == mViewModel.getCurrentUebung().getId()){
                            Toast.makeText(getContext(), "Es geht lmaooooooo", Toast.LENGTH_SHORT).show();
                        }
                        //init die ganze animations shit!!!
                    }
                }
            }
        });
        //mViewModel.setFragmentsOfCurrentUebung(homeViewModel.getAllFragmentsOfCurrentUebung());


        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setSavedFrame(lottieAnimationView.getFrame());
        mViewModel.setPlaying(lottieAnimationView.isAnimating());
    }

    private void changeAnimation(){
        //check if it is a certain state
        //move to the next state in the chain

        lottieAnimationView.setMinFrame(0);
        lottieAnimationView.setProgress(0);

        if(mViewModel.getState() == BreatheAnimationState.breathe_in){
            lottieAnimationView.setAnimation(R.raw.hold_up);
            mViewModel.setState(BreatheAnimationState.hold_up);

            //calcAndSetSpeed(TIME_HOLD_UP);

        } else if(mViewModel.getState() == BreatheAnimationState.hold_up){
            lottieAnimationView.setAnimation(R.raw.breathe_out);
            mViewModel.setState(BreatheAnimationState.breathe_out);

            //calcAndSetSpeed(TIME_BREATHE_OUT);

        } else if(mViewModel.getState() == BreatheAnimationState.breathe_out) {
            lottieAnimationView.setAnimation(R.raw.hold_down);
            mViewModel.setState(BreatheAnimationState.hold_down);

            //calcAndSetSpeed(TIME_HOLD_DOWN);

        } else if (mViewModel.getState() == BreatheAnimationState.hold_down){
            lottieAnimationView.setAnimation(R.raw.breathe_in);
            mViewModel.setState(BreatheAnimationState.breathe_in);

            //calcAndSetSpeed(TIME_BREATHE_IN);

        }
    }

    /**
     *
     * entnimmt den {@link BreatheAnimationState} der animation und setted die passende animation bei {@link LottieAnimationView}
     * */
    private void initAnimationView(){
        lottieAnimationView.setSpeed(mViewModel.getSpeed());
        lottieAnimationView.setMinFrame(mViewModel.getSavedFrame());
        if(mViewModel.getState() == BreatheAnimationState.breathe_in){
            lottieAnimationView.setAnimation(R.raw.breathe_in);
            //hier muss ich wohl iwie ne mehtode haben bei changeanimation das ich im viewmodel was hochzähle oder so???????
            //und dann ne abfrage??
            //und eine methode, wo der timer das aufruft wenns fertig ist, zb iwie end methode oder so
            //calcAndSetSpeed(TIME_BREATHE_IN);

        } else if(mViewModel.getState() == BreatheAnimationState.hold_up){
            lottieAnimationView.setAnimation(R.raw.hold_up);
            //calcAndSetSpeed(TIME_HOLD_UP);

        } else if(mViewModel.getState() == BreatheAnimationState.breathe_out) {
            lottieAnimationView.setAnimation(R.raw.breathe_out);
            //calcAndSetSpeed(TIME_BREATHE_OUT);

        } else if (mViewModel.getState() == BreatheAnimationState.hold_down){
            lottieAnimationView.setAnimation(R.raw.breathe_out);
            //calcAndSetSpeed(TIME_HOLD_DOWN);
        }

    }

    private void calcAndSetSpeed(float duration){
        //if 0 then set speed to 10000f so the part of the animation gets skipped
        float speed = 10000f;

        if(duration != 0f){
            speed = BASE_ANIMATION_DURATION / duration;
        }

        mViewModel.setSpeed(speed);
        lottieAnimationView.setSpeed(speed);

    }



    //hilfsmethoden ab hier

    private void initMemberVariables(){
        lottieAnimationView = binding.lottiePolygon;
        startButton = binding.startAnimationButton;
        stopButton = binding.stopAnimationButton;
        resetButton = binding.resetButton;
        //ich brauch noch ne reset methode
    }
}

/*
*  private final MyHandler myHandler = new MyHandler();
    private MyRunnable myRunnable;


    //https://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android
private static class MyHandler extends Handler {
}

private static class MyRunnable implements Runnable {
    private final WeakReference<AnimationFragment> animationFragmentWeakReference;

    public MyRunnable(AnimationFragment fragment) {
        animationFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public void run() {
        GalleryFragment fragment = galleryFragmentWeakReference.get();
        if (fragment != null) {
            //fragment.returnToUebungEditor();
            fragment.startDiagrammCreation();
        }
    }
}
*
* */