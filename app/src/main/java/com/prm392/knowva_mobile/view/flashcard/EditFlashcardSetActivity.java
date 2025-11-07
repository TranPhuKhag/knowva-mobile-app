package com.prm392.knowva_mobile.view.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.Flashcard;
import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.repository.FlashcardRepository;
import com.prm392.knowva_mobile.view.flashcard.adapter.EditCardAdapter;
import com.prm392.knowva_mobile.view.flashcard.model.EditableCard;
import com.prm392.knowva_mobile.view.flashcard.model.UpdateSetRequest;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFlashcardSetActivity extends AppCompatActivity {

    private TextInputEditText edtTitle, edtDesc;
    private RecyclerView rv;
    private FloatingActionButton fabAdd;
    private EditCardAdapter adapter;

    private FlashcardRepository repo;
    private long setId;
    private FlashcardSet original;
    private final List<EditableCard> cards = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set);

        setupToolbar();
        bindViews();

        repo = new FlashcardRepository(this);
        setId = getIntent().getLongExtra("set_id", -1);

        String json = getIntent().getStringExtra("set_json");
        if (json != null && !json.isEmpty()) {
            original = new Gson().fromJson(json, FlashcardSet.class);
            prefill(original);
        } else {
            repo.getSetById(setId, new Callback<FlashcardSet>() {
                @Override
                public void onResponse(Call<FlashcardSet> call, Response<FlashcardSet> res) {
                    if (res.isSuccessful() && res.body() != null) {
                        original = res.body();
                        prefill(original);
                    } else {
                        finishWithError("Failed to load set");
                    }
                }

                @Override
                public void onFailure(Call<FlashcardSet> call, Throwable t) {
                    finishWithError(t.getMessage());
                }
            });
        }
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Flashcard Set");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void bindViews() {
        edtTitle = findViewById(R.id.et_title);
        edtDesc = findViewById(R.id.et_desc);
        rv = findViewById(R.id.rv_cards);
        fabAdd = findViewById(R.id.fab_add);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditCardAdapter(cards, this::onCardRemoved);
        rv.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            EditableCard e = new EditableCard();
            e.id = 0;
            e.order = cards.size() + 1;
            cards.add(e);
            adapter.notifyItemInserted(cards.size() - 1);
            rv.smoothScrollToPosition(cards.size() - 1);
        });
    }

    private void onCardRemoved(int position) {
        if (position >= 0 && position < cards.size()) {
            cards.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, cards.size());
        }
    }

    private void prefill(FlashcardSet set) {
        edtTitle.setText(set.getTitle());
        edtDesc.setText(set.getDescription());

        cards.clear();
        if (set.getFlashcards() != null) {
            int idx = 1;
            for (Flashcard fc : set.getFlashcards()) {
                EditableCard e = new EditableCard();
                e.id = fc.getId();
                e.front = fc.getFront();
                e.back = fc.getBack();
                e.imageUrl = fc.getImageUrl();
                e.order = idx++;
                cards.add(e);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void finishWithError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_set_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save || item.getItemId() == R.id.action_done) {
            doSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        if (TextUtils.isEmpty(edtTitle.getText())) {
            edtTitle.setError("Title is required");
            return false;
        }

        boolean hasValidCard = false;
        for (EditableCard c : cards) {
            if (!TextUtils.isEmpty(c.front) && !TextUtils.isEmpty(c.back)) {
                hasValidCard = true;
                break;
            }
        }

        if (!hasValidCard) {
            Toast.makeText(this, "At least 1 term required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void doSave() {
        if (!validate()) return;

        UpdateSetRequest body = new UpdateSetRequest();
        body.title = edtTitle.getText().toString().trim();
        body.description = edtDesc.getText() != null ? edtDesc.getText().toString().trim() : "";

        body.sourceType = orEmpty(original.getSourceType());
        body.language = orEmpty(original.getLanguage());
        body.cardType = orEmpty(original.getCardType());
        body.visibility = orEmpty(original.getVisibility());
        body.category = orEmpty(original.getCategory());

        body.flashcards = new ArrayList<>();
        int order = 1;
        for (EditableCard e : cards) {
            if (isBlank(e.front) && isBlank(e.back)) continue;

            UpdateSetRequest.Card cc = new UpdateSetRequest.Card();
            cc.id = e.id;
            cc.front = safe(e.front);
            cc.back = safe(e.back);
            cc.imageUrl = e.imageUrl;
            cc.order = order++;
            body.flashcards.add(cc);
        }

        Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();

        repo.updateSet(setId, body).enqueue(new Callback<FlashcardSet>() {
            @Override
            public void onResponse(Call<FlashcardSet> call, Response<FlashcardSet> res) {
                if (res.isSuccessful() && res.body() != null) {
                    Toast.makeText(EditFlashcardSetActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent().putExtra("updated_json", new Gson().toJson(res.body()));
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    String msg = "Update failed (" + res.code() + ")";
                    if (res.code() == 401) msg = "Unauthorized - Please login again";
                    else if (res.code() == 403) msg = "Forbidden - You don't have permission";
                    else if (res.code() == 404) msg = "Set not found";
                    Toast.makeText(EditFlashcardSetActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FlashcardSet> call, Throwable t) {
                Toast.makeText(EditFlashcardSetActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String orEmpty(String s) {
        return s != null ? s : "";
    }

    private String safe(String s) {
        return s != null ? s.trim() : "";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
