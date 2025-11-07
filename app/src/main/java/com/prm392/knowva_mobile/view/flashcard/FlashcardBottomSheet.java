package com.prm392.knowva_mobile.view.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prm392.knowva_mobile.R;

public class FlashcardBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_flashcard, container, false);

        TextView tvFlashcardSet = view.findViewById(R.id.tv_flashcard_set);
        TextView tvMyFlashcard = view.findViewById(R.id.tv_my_flashcard);

        tvFlashcardSet.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CreateSetActivity.class));
            dismiss();
        });

        tvMyFlashcard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MyFlashcardsActivity.class);
            startActivity(intent);
            dismiss();
        });

        return view;
    }
}
