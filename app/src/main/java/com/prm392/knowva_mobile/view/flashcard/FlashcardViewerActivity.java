package com.prm392.knowva_mobile.view.flashcard;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.Flashcard;
import com.prm392.knowva_mobile.model.FlashcardSet;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.repository.FlashcardRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashcardViewerActivity extends AppCompatActivity {

    private ViewPager2 vp;
    private TabLayout tabDots;
    private TextView tvSetTitle, tvUsername, tvTerms;
    private RecyclerView rvTerms;
    private TermsAdapter termsAdapter;
    private FlashcardRepository repo;
    private long setId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_flashcard_viewer);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> onBackPressed());

        vp = findViewById(R.id.vpCards);
        tabDots = findViewById(R.id.tabDots);
        tvSetTitle = findViewById(R.id.tvSetTitle);
        tvUsername = findViewById(R.id.tvUsername);
        tvTerms = findViewById(R.id.tvTerms);
        rvTerms = findViewById(R.id.rvTerms);
        rvTerms.setLayoutManager(new LinearLayoutManager(this));
        rvTerms.setItemAnimator(null); // tránh flicker khi highlight

        repo = new FlashcardRepository(this);
        setId = getIntent().getLongExtra("set_id", -1);

        // Nhận meta từ Intent
        String title = getIntent().getStringExtra("set_title");
        String username = getIntent().getStringExtra("set_username");
        int terms = getIntent().getIntExtra("set_terms", -1);

        if (title != null) tvSetTitle.setText(title);
        if (username != null) tvUsername.setText(username);
        if (terms >= 0) tvTerms.setText(terms + " terms");

        // 1) Nếu có truyền kèm JSON cards -> dùng luôn
        String json = getIntent().getStringExtra("cards_json");
        if (json != null && !json.isEmpty()) {
            List<MyFlashcardSetResponse.Card> cards = new Gson().fromJson(json,
                new TypeToken<List<MyFlashcardSetResponse.Card>>(){}.getType());
            bindCards(convertToFlashcards(cards));
            return;
        }

        // 2) Nếu không -> gọi API theo id
        if (setId > 0) {
            repo.getSetById(setId, new Callback<FlashcardSet>() {
                @Override
                public void onResponse(Call<FlashcardSet> call, Response<FlashcardSet> res) {
                    if (res.isSuccessful() && res.body() != null) {
                        FlashcardSet set = res.body();
                        tvSetTitle.setText(set.getTitle());
                        tvUsername.setText(set.getUsername() != null ? set.getUsername() : "");
                        int count = (set.getFlashcards() != null ? set.getFlashcards().size() : 0);
                        tvTerms.setText(count + " terms");
                        bindCards(set.getFlashcards());
                    } else {
                        Toast.makeText(FlashcardViewerActivity.this, "Failed to load set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<FlashcardSet> call, Throwable t) {
                    Toast.makeText(FlashcardViewerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    private List<Flashcard> convertToFlashcards(List<MyFlashcardSetResponse.Card> cards) {
        List<Flashcard> result = new ArrayList<>();
        if (cards != null) {
            for (MyFlashcardSetResponse.Card c : cards) {
                Flashcard f = new Flashcard();
                f.setId(c.id);
                f.setFront(c.front);
                f.setBack(c.back);
                f.setImageUrl(c.imageUrl);
                f.setOrder(c.order);
                result.add(f);
            }
        }
        return result;
    }

    private void bindCards(List<Flashcard> cards) {
        if (cards == null || cards.isEmpty()) {
            Toast.makeText(this, "No flashcards", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ViewPager
        FlashcardPagerAdapter pagerAdapter = new FlashcardPagerAdapter(cards);
        vp.setAdapter(pagerAdapter);
        vp.setOffscreenPageLimit(1);
        new TabLayoutMediator(tabDots, vp, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@androidx.annotation.NonNull TabLayout.Tab tab, int position) {
                // Empty - chỉ dùng dots
            }
        }).attach();

        // RecyclerView "Terms"
        termsAdapter = new TermsAdapter(cards, new TermsAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                vp.setCurrentItem(position, true);
            }
        });
        rvTerms.setAdapter(termsAdapter);

        // Đồng bộ highlight & auto-scroll
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                termsAdapter.setSelected(position);
                rvTerms.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
