package com.example.datenbankefuerprojekt.animation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;
import com.example.datenbankefuerprojekt.databinding.FragmentProgressBarBinding;
import com.example.datenbankefuerprojekt.ui.home.HomeViewModel;

public class ProgressBarFragment extends Fragment {
    public static final String TAG = "ProgressBarFragment";

    private HomeViewModel homeViewModel;

    private AnimationViewModel mViewModel;
    private FragmentProgressBarBinding binding;

    private ProgressBar progressBar;
    private Button buttonStartPause;
    private Button resetButton;

    private TextView textViewHold;

    private TextView textViewCountDown;
    private TextView textViewTitle;
    private TextView textViewFragmentTitle;

    private boolean alreadyInitialised;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AnimationViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentProgressBarBinding.inflate(inflater,container,false);


        return binding.getRoot();
    }

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
}