package com.prm392.knowva_mobile.view.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.FlashcardSet;

import java.util.List;

public class HorizontalSetAdapter extends RecyclerView.Adapter<HorizontalSetAdapter.ViewHolder> {

    private final List<FlashcardSet> sets;

    public HorizontalSetAdapter(List<FlashcardSet> sets) {
        this.sets = sets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard_set_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlashcardSet set = sets.get(position);
        holder.title.setText(set.getTitle());
        holder.author.setText(set.getAuthor());
    }

    @Override
    public int getItemCount() {
        return sets != null ? sets.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_set_title_horizontal);
            author = itemView.findViewById(R.id.tv_set_author_horizontal);
        }
    }
}