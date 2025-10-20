package com.prm392.knowva_mobile.view.flashcard.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.view.flashcard.model.CardDraft;

import java.util.ArrayList;
import java.util.List;

public class CardEditorAdapter extends RecyclerView.Adapter<CardEditorAdapter.VH> {
    public final List<CardDraft> data = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_editor, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CardDraft draft = data.get(position);

        // Clear old listeners to prevent issues
        holder.etTerm.removeTextChangedListener(holder.termWatcher);
        holder.etDef.removeTextChangedListener(holder.defWatcher);

        holder.etTerm.setText(draft.front);
        holder.etDef.setText(draft.back);

        holder.termWatcher = new SimpleTextWatcher(s -> draft.front = s);
        holder.defWatcher = new SimpleTextWatcher(s -> draft.back = s);

        holder.etTerm.addTextChangedListener(holder.termWatcher);
        holder.etDef.addTextChangedListener(holder.defWatcher);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextInputEditText etTerm, etDef;
        TextWatcher termWatcher, defWatcher;

        VH(@NonNull View view) {
            super(view);
            etTerm = view.findViewById(R.id.et_term);
            etDef = view.findViewById(R.id.et_definition);
        }
    }

    // TextWatcher simplified
    static class SimpleTextWatcher implements TextWatcher {
        private final Consumer<String> callback;

        SimpleTextWatcher(Consumer<String> callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.accept(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        interface Consumer<T> {
            void accept(T t);
        }
    }
}

