package com.example.datenbankefuerprojekt.animation;

import static com.example.datenbankefuerprojekt.animation.AnimationViewModel.BASE_ANIMATION_DURATION;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;

public class AnimationFragment extends Fragment {



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
     *
     * */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //this viewmodel should be owned by "this" so it gets destroyed when we have a different uebung
        mViewModel = new ViewModelProvider(this).get(AnimationViewModel.class);
        binding = AnimationFragmentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        initMemberVariables();


        return root;
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

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setSavedFrame(lottieAnimationView.getFrame());
        mViewModel.setPlaying(lottieAnimationView.isAnimating());
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