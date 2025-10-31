package com.prm392.knowva_mobile.view.Home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.quiz.MyQuizSetResponse;
import com.prm392.knowva_mobile.view.quiz.QuizDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuggestedQuizAdapter extends RecyclerView.Adapter<SuggestedQuizAdapter.VH> {

    private final List<MyQuizSetResponse> data = new ArrayList<>();

    public void submit(List<MyQuizSetResponse> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard_set_horizontal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MyQuizSetResponse s = data.get(pos);
        h.tvTitle.setText(s.title);
        h.tvAuthor.setText(s.username);

        h.itemView.setOnClickListener(v -> {
            // Mở QuizDetailActivity khi nhấn vào
            Intent intent = new Intent(v.getContext(), QuizDetailActivity.class);
            intent.putExtra(QuizDetailActivity.QUIZ_ID_KEY, s.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor;

        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_set_title_horizontal);
            tvAuthor = v.findViewById(R.id.tv_set_author_horizontal);
        }
    }
}