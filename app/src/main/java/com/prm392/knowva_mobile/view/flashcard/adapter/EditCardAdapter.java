package com.prm392.knowva_mobile.view.flashcard.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.view.flashcard.model.EditableCard;
import java.util.List;

public class EditCardAdapter extends RecyclerView.Adapter<EditCardAdapter.VH> {

    private final List<EditableCard> data;
    private final OnCardRemoveListener removeListener;

    public interface OnCardRemoveListener {
        void onRemove(int position);
    }

    public EditCardAdapter(List<EditableCard> data, OnCardRemoveListener listener) {
        this.data = data;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_editor, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EditableCard card = data.get(position);
        holder.bind(card, removeListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextInputEditText edtFront, edtBack;
        ImageButton btnRemove;

        VH(@NonNull View v) {
            super(v);
            edtFront = v.findViewById(R.id.edt_term);
            edtBack = v.findViewById(R.id.edt_definition);
            btnRemove = v.findViewById(R.id.btn_remove_card);
        }

        void bind(EditableCard card, OnCardRemoveListener listener) {
            // Remove previous listeners
            edtFront.setTag(null);
            edtBack.setTag(null);

            // Set text
            edtFront.setText(card.front);
            edtBack.setText(card.back);

            // Add TextWatcher for front
            edtFront.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    card.front = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Add TextWatcher for back
            edtBack.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    card.back = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Remove button click
            btnRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRemove(pos);
                }
            });
        }
    }
}
