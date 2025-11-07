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
import com.prm392.knowva_mobile.view.flashcard.model.CardDraft;
import java.util.ArrayList;
import java.util.List;

public class CardEditorAdapter extends RecyclerView.Adapter<CardEditorAdapter.VH> {

    private final List<CardDraft> data = new ArrayList<>();

    public CardEditorAdapter() {
    }

    public void addEmpty() {
        data.add(new CardDraft());
        notifyItemInserted(data.size() - 1);
    }

    public List<CardDraft> getData() {
        return data;
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
        CardDraft card = data.get(position);
        holder.bind(card, position, this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void removeAt(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
        }
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

        void bind(CardDraft card, int position, CardEditorAdapter adapter) {
            // Clear previous listeners to avoid memory leaks
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
                if (pos != RecyclerView.NO_POSITION) {
                    adapter.removeAt(pos);
                }
            });
        }
    }
}
