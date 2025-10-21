package com.prm392.knowva_mobile.view.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.view.flashcard.adapter.CardEditorAdapter;
import com.prm392.knowva_mobile.view.flashcard.model.CardDraft;

import java.util.ArrayList;
import java.util.List;

public class CreateSetActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etDesc;
    private CardEditorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                onClickDone();
                return true;
            }
            return false;
        });

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);

        RecyclerView rv = findViewById(R.id.rv_cards);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardEditorAdapter();
        rv.setAdapter(adapter);

        // Default 2 empty cards
        adapter.addEmpty();
        adapter.addEmpty();

        findViewById(R.id.fab_add).setOnClickListener(v -> adapter.addEmpty());
    }

    private boolean hasAnyInput() {
        if (!TextUtils.isEmpty(etTitle.getText()) || !TextUtils.isEmpty(etDesc.getText())) {
            return true;
        }
        for (CardDraft d : adapter.getData()) {
            if (!TextUtils.isEmpty(d.front) || !TextUtils.isEmpty(d.back)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!hasAnyInput()) {
            super.onBackPressed();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure?\nLeaving your set will discard this draft.")
                .setNegativeButton("Keep editing", null)
                .setPositiveButton("Discard draft", (d, w) -> finish())
                .show();
    }

    private void onClickDone() {
        // If no input, just exit
        if (!hasAnyInput()) {
            finish();
            return;
        }

        // Filter valid cards
        List<CardDraft> valid = new ArrayList<>();
        for (CardDraft d : adapter.getData()) {
            if (d.isValid()) {
                valid.add(d);
            }
        }

        if (valid.size() < 2) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("You must add at least two terms to save your set.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Package data and move to metadata screen
        Intent intent = new Intent(this, CreateSetMetaActivity.class);
        intent.putExtra("title", valueOf(etTitle));
        intent.putExtra("desc", valueOf(etDesc));
        intent.putExtra("cards_json", new Gson().toJson(valid));
        startActivity(intent);
    }

    private String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}

