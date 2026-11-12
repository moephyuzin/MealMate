package com.example.mealmate;

import android.animation.AnimatorSet;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AboutFragment extends Fragment {

    private TextView tvAppPurpose, tvAppPurposeDesc, tvFeatures, tvFeaturesDesc, tvTargetUsers, tvTargetUsersDesc,
            tvHowItWorks, tvHowItWorksDesc, tvDeveloperinfo, tvDeveloperinfoDesc,
            tvPrivacy, tvPrivacyDesc, tvAcknowledgments, tvAcknowledgmentsDesc;


    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Initialize TextViews
        tvAppPurpose = view.findViewById(R.id.tvAppPurpose);
        tvAppPurposeDesc = view.findViewById(R.id.tvAppPurposeDesc);
        tvFeatures = view.findViewById(R.id.tvFeatures);
        tvFeaturesDesc = view.findViewById(R.id.tvFeaturesDesc);
        tvTargetUsers = view.findViewById(R.id.tvTargetUsers);
        tvTargetUsersDesc = view.findViewById(R.id.tvTargetUsersDesc);
        tvHowItWorks = view.findViewById(R.id.tvHowItWorks);
        tvHowItWorksDesc = view.findViewById(R.id.tvHowItWorksDesc);
        tvDeveloperinfo = view.findViewById(R.id.tvDeveloperinfo);
        tvDeveloperinfoDesc = view.findViewById(R.id.tvDeveloperinfoDesc);
        tvPrivacy = view.findViewById(R.id.tvPrivacy);
        tvPrivacyDesc = view.findViewById(R.id.tvPrivacyDesc);
        tvAcknowledgments = view.findViewById(R.id.tvAcknowledgments);
        tvAcknowledgmentsDesc = view.findViewById(R.id.tvAcknowledgmentsDesc);


        // Apply Slide-In Animations from Left
        animateTextSlideIn(tvAppPurpose, 0);
        animateTextSlideIn(tvAppPurposeDesc, 300);
        animateTextSlideIn(tvFeatures, 600);
        animateTextSlideIn(tvFeaturesDesc, 900);
        animateTextSlideIn(tvTargetUsers, 1200);
        animateTextSlideIn(tvTargetUsersDesc, 1500);
        animateTextSlideIn(tvHowItWorks, 1800);
        animateTextSlideIn(tvHowItWorksDesc, 2100);
        animateTextSlideIn(tvDeveloperinfo, 2400);
        animateTextSlideIn(tvDeveloperinfoDesc, 2700);
        animateTextSlideIn(tvPrivacy, 3000);
        animateTextSlideIn(tvPrivacyDesc, 3300);
        animateTextSlideIn(tvAcknowledgments, 3600);
        animateTextSlideIn(tvAcknowledgmentsDesc, 3900);

        return view;
    }

    private void animateTextSlideIn(TextView textView, int delay) {
        textView.setAlpha(0f); // Initially hidden
        handler.postDelayed(() -> {
            ObjectAnimator slideIn = ObjectAnimator.ofFloat(textView, "translationX", -300f, 0f); // Slide from left
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f); // Fade in
            slideIn.setDuration(600);
            fadeIn.setDuration(600);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(slideIn, fadeIn);
            animatorSet.start();
        }, delay);
    }
}