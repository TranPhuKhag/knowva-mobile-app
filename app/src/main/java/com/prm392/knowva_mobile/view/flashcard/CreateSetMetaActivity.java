package com.prm392.knowva_mobile.view.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.repository.FlashcardRepository;
import com.prm392.knowva_mobile.view.HomeActivity;
import com.prm392.knowva_mobile.view.flashcard.model.CardDraft;
import com.prm392.knowva_mobile.view.flashcard.model.CreateSetRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateSetMetaActivity extends AppCompatActivity {
    private TextInputEditText etCategory, etLanguage, etCardType, etSourceType;
    private RadioButton rbPublic;
    private List<CardDraft> cardsDraft;
    private String title, desc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set_meta);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                submit();
                return true;
            }
            return false;
        });

        // Receive data from previous screen
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        Type listType = new TypeToken<List<CardDraft>>() {}.getType();
        cardsDraft = new Gson().fromJson(getIntent().getStringExtra("cards_json"), listType);

        // Views
        rbPublic = findViewById(R.id.rb_public);
        etCategory = findViewById(R.id.et_category);
        etLanguage = findViewById(R.id.et_language);
        etCardType = findViewById(R.id.et_card_type);
        etSourceType = findViewById(R.id.et_source_type);
    }

    private void submit() {
        CreateSetRequest request = new CreateSetRequest();
        request.title = title;
        request.description = desc;
        request.visibility = rbPublic.isChecked() ? "PUBLIC" : "PRIVATE";
        request.category = textOf(etCategory, "HISTORY");
        request.language = textOf(etLanguage, "VIETNAMESE");
        request.cardType = textOf(etCardType, "STANDARD");
        request.sourceType = textOf(etSourceType, "PDF");

        request.flashcards = new ArrayList<>();
        int order = 1;
        for (CardDraft draft : cardsDraft) {
            CreateSetRequest.Card card = new CreateSetRequest.Card();
            card.front = draft.front;
            card.back = draft.back;
            card.order = order++;
            request.flashcards.add(card);
        }

        // Call API
        new FlashcardRepository(this).createSet(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateSetMetaActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    // Go back to Home and clear history
                    Intent intent = new Intent(CreateSetMetaActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Log chi tiết lỗi từ server
                    String errorMsg = "Save failed (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e("CreateSetMeta", "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        }
                    } catch (Exception e) {
                        Log.e("CreateSetMeta", "Cannot read error body", e);
                    }
                    Toast.makeText(CreateSetMetaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CreateSetMeta", "Network error: " + t.getMessage(), t);
                Toast.makeText(CreateSetMetaActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String textOf(EditText editText, String defaultValue) {
        String text = editText.getText() == null ? "" : editText.getText().toString().trim();
        return text.isEmpty() ? defaultValue : text;
    }
}
