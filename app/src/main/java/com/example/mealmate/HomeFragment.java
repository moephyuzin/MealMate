package com.example.mealmate;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Handler;


public class HomeFragment extends Fragment {
    private TextView tvWelcome, tvFeature1, tvFeature2, tvFeature3, tvFeature4, tvFeature5;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvFeature1 = view.findViewById(R.id.tvFeature1);
        tvFeature2 = view.findViewById(R.id.tvFeature2);
        tvFeature3 = view.findViewById(R.id.tvFeature3);
        tvFeature4 = view.findViewById(R.id.tvFeature4);
        tvFeature5 = view.findViewById(R.id.tvFeature5);

        animateTextDrop(tvWelcome, 0);
        animateTextDrop(tvFeature1, 500);
        animateTextDrop(tvFeature2, 1000);
        animateTextDrop(tvFeature3, 1500);
        animateTextDrop(tvFeature4, 2000);
        animateTextDrop(tvFeature5, 2500);

        return view;
    }

    private void animateTextDrop(TextView textView, int delay) {
        handler.postDelayed(() -> {
            textView.setAlpha(1f);
            ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "translationY", -50f, 0f);
            animator.setDuration(600);
            animator.start();
        }, delay);
    }
}