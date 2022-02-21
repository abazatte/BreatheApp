package com.example.datenbankefuerprojekt.animation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datenbankefuerprojekt.R;
import com.example.datenbankefuerprojekt.databinding.AnimationFragmentBinding;

public class AnimationFragment extends Fragment {

    private AnimationViewModel mViewModel;
    private AnimationFragmentBinding binding;


    public static AnimationFragment newInstance() {
        return new AnimationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AnimationViewModel.class);
        binding = AnimationFragmentBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        return root;
    }


}