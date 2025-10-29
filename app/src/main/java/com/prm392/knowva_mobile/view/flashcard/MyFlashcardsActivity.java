package com.prm392.knowva_mobile.view.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.repository.FlashcardRepository;
import com.prm392.knowva_mobile.view.HomeActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFlashcardsActivity extends AppCompatActivity {

    private final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private final SimpleDateFormat monthFmt = new SimpleDateFormat("'In' MMMM yyyy", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_flashcards);
        setTitle("Your library");

        RecyclerView rv = findViewById(R.id.rv_my_sets);
        rv.setLayoutManager(new LinearLayoutManager(this));
        LibraryAdapter adapter = new LibraryAdapter();
        rv.setAdapter(adapter);

        // Thiết lập click listener để mở FlashcardViewerActivity
        adapter.setOnItemClickListener(set -> {
            Intent intent = new Intent(this, FlashcardViewerActivity.class);
            intent.putExtra("set_id", set.id);
            intent.putExtra("set_title", set.title);
            intent.putExtra("set_username", set.username);
            int terms = (set.flashcards == null) ? 0 : set.flashcards.size();
            intent.putExtra("set_terms", terms);
            // Truyền kèm flashcards nếu có (tránh gọi API lần nữa)
            if (set.flashcards != null && !set.flashcards.isEmpty()) {
                intent.putExtra("cards_json", new com.google.gson.Gson().toJson(set.flashcards));
            }
            startActivity(intent);
        });

        // Thiết lập Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.menu_bottom_flashcard);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_bottom_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            if (id == R.id.menu_bottom_flashcard) {
                new FlashcardBottomSheet().show(getSupportFragmentManager(), "FlashcardBottomSheet");
                return false;
            }
            if (id == R.id.menu_bottom_quiz) {
                Toast.makeText(this, "Quiz", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Gọi API
        new FlashcardRepository(this).getMySets().enqueue(new Callback<List<MyFlashcardSetResponse>>() {
            @Override
            public void onResponse(Call<List<MyFlashcardSetResponse>> call, Response<List<MyFlashcardSetResponse>> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    String errorMsg = "Lỗi tải dữ liệu - Code: " + res.code();
                    if (res.code() == 401) {
                        errorMsg += " (Unauthorized - Token không hợp lệ)";
                    }
                    // Thêm chi tiết error body từ server
                    try {
                        if (res.errorBody() != null) {
                            String errorBody = res.errorBody().string();
                            Log.e("MyFlashcardsActivity", "Error body: " + errorBody);
                            errorMsg += "\n" + errorBody;
                        }
                    } catch (Exception e) {
                        Log.e("MyFlashcardsActivity", "Cannot read error body", e);
                    }
                    Log.e("MyFlashcardsActivity", errorMsg);
                    Toast.makeText(MyFlashcardsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    return;
                }
                List<Object> items = groupByMonth(res.body());
                adapter.submit(items);
                Toast.makeText(MyFlashcardsActivity.this, "Tải thành công " + res.body().size() + " flashcard sets", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<MyFlashcardSetResponse>> call, Throwable t) {
                Log.e("MyFlashcardsActivity", "Network error: " + t.getMessage(), t);
                Toast.makeText(MyFlashcardsActivity.this, "Mạng lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Object> groupByMonth(List<MyFlashcardSetResponse> src) {
        Collections.sort(src, (a, b) -> b.createdAt.compareTo(a.createdAt));
        Map<String, List<MyFlashcardSetResponse>> map = new LinkedHashMap<>();
        for (MyFlashcardSetResponse s : src) {
            String key = "Unknown";
            try {
                key = monthFmt.format(iso.parse(s.createdAt));
            } catch (ParseException ignored) {
            }
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        List<Object> out = new ArrayList<>();
        for (Map.Entry<String, List<MyFlashcardSetResponse>> e : map.entrySet()) {
            out.add(e.getKey());
            out.addAll(e.getValue());
        }
        return out;
    }

    static class LibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<Object> data = new ArrayList<>();
        private static final int T_HEADER = 0, T_ITEM = 1;
        private OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(MyFlashcardSetResponse set);
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        void submit(List<Object> items) {
            data.clear();
            data.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int pos) {
            return (data.get(pos) instanceof String) ? T_HEADER : T_ITEM;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            if (vt == T_HEADER) {
                View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_month_header, p, false);
                return new VHHeader(v);
            } else {
                View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_my_flashcard_set, p, false);
                return new VHItem(v);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
            if (getItemViewType(pos) == T_HEADER) {
                ((VHHeader) h).tv.setText((String) data.get(pos));
            } else {
                MyFlashcardSetResponse s = (MyFlashcardSetResponse) data.get(pos);
                VHItem vh = (VHItem) h;
                vh.title.setText(s.title);
                int terms = (s.flashcards == null) ? 0 : s.flashcards.size();
                vh.terms.setText(terms + " terms");
                vh.username.setText(s.username);

                // Thêm click listener
                vh.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(s);
                    }
                });
            }
        }

        static class VHHeader extends RecyclerView.ViewHolder {
            TextView tv;
            VHHeader(View v) {
                super(v);
                tv = v.findViewById(R.id.tv_month);
            }
        }

        static class VHItem extends RecyclerView.ViewHolder {
            TextView title, terms, username;
            VHItem(View v) {
                super(v);
                title = v.findViewById(R.id.tv_title);
                terms = v.findViewById(R.id.tv_terms);
                username = v.findViewById(R.id.tv_username);
            }
        }
    }
}
