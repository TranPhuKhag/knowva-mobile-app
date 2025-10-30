package com.prm392.knowva_mobile.view.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prm392.knowva_mobile.R;

public class QuizBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout mới bạn vừa tạo
        View view = inflater.inflate(R.layout.bottom_sheet_quiz, container, false);

        ImageView imgShiba = view.findViewById(R.id.img_quiz_shiba);

//        Animation bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.shiba_bounce);
//        imgShiba.startAnimation(bounce);
        Animation swing = AnimationUtils.loadAnimation(requireContext(), R.anim.shiba_swing);
        imgShiba.startAnimation(swing);

        TextView tvQuizSet = view.findViewById(R.id.tv_quiz_set);
        TextView tvMyQuiz = view.findViewById(R.id.tv_my_quiz);

        // Tạm thời hiển thị Toast (bạn có thể thay bằng Intent sau)
        tvQuizSet.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GenerateQuizActivity.class);
            startActivity(intent);
            dismiss();
        });

        // Tạm thời hiển thị Toast
        tvMyQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyQuizzesActivity.class);
            startActivity(intent);
            // Ví dụ: Intent intent = new Intent(requireContext(), MyQuizzesActivity.class);
            // startActivity(intent);
            dismiss();
        });

        return view;
    }
}