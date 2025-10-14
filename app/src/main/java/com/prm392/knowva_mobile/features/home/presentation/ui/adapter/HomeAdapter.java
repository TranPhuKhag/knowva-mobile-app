package com.prm392.knowva_mobile.features.home.presentation.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.features.home.presentation.state.HomeScreenItem;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BANNER = 0;
    private static final int TYPE_CONTINUE_LEARNING = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_AUTHORS = 3;
    private static final int TYPE_RECOMMENDED = 4;


    private List<HomeScreenItem> items;

    public void setItems(List<HomeScreenItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged(); // Nên dùng DiffUtil để hiệu năng tốt hơn
    }

    @Override
    public int getItemViewType(int position) {
        HomeScreenItem item = items.get(position);
        if (item instanceof HomeScreenItem.Banner) {
            return TYPE_BANNER;
        } else if (item instanceof HomeScreenItem.ContinueLearning) {
            return TYPE_CONTINUE_LEARNING;
        } else if (item instanceof HomeScreenItem.Header) {
            return TYPE_HEADER;
        } else if (item instanceof HomeScreenItem.Authors) {
            return TYPE_AUTHORS;
        } else if (item instanceof HomeScreenItem.RecommendedSet) {
            return TYPE_RECOMMENDED;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_BANNER:
                return new BannerViewHolder(inflater.inflate(R.layout.item_home_banner, parent, false));
            case TYPE_CONTINUE_LEARNING:
                return new ContinueLearningCarouselViewHolder(inflater.inflate(R.layout.item_home_carousel, parent, false));
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_home_header, parent, false));
            case TYPE_AUTHORS:
                return  new AuthorsViewHolder(inflater.inflate(R.layout.item_home_authors, parent, false));
            case TYPE_RECOMMENDED:
                return new RecommendedSetViewHolder(inflater.inflate(R.layout.item_recommended_set, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeScreenItem item = items.get(position);
        if (holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).bind((HomeScreenItem.Banner) item);
        } else if (holder instanceof ContinueLearningCarouselViewHolder) {
            ((ContinueLearningCarouselViewHolder) holder).bind((HomeScreenItem.ContinueLearning) item);
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((HomeScreenItem.Header) item);
        } else if (holder instanceof AuthorsViewHolder) {
            ((AuthorsViewHolder) holder).bind((HomeScreenItem.Authors) item);
        } else if (holder instanceof RecommendedSetViewHolder) {
            ((RecommendedSetViewHolder) holder).bind((HomeScreenItem.RecommendedSet) item);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // --- CÁC VIEWHOLDER (Nên tách ra các file riêng trong package `viewholder`) ---

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        // BƯỚC 1: Khai báo các biến cho View
        TextView tvGreeting, tvStreak, tvProgressText;
        ProgressBar pbDailyGoal;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            // BƯỚC 2: Tìm và gán các View từ layout bằng ID
            tvGreeting = itemView.findViewById(R.id.tv_greeting);
            tvStreak = itemView.findViewById(R.id.tv_streak);
            tvProgressText = itemView.findViewById(R.id.tv_progress_text);
            pbDailyGoal = itemView.findViewById(R.id.pb_daily_goal);
        }

        void bind(HomeScreenItem.Banner bannerItem) {
            // BƯỚC 3: Gán dữ liệu từ item vào các View tương ứng
            String greetingText = "Chào buổi tối, " + bannerItem.userName + "!";
            String streakText = "Chuỗi " + bannerItem.streak + " ngày học 🔥";
            String progressText = bannerItem.dailyProgress + "/" + bannerItem.dailyGoal + " thẻ";

            tvGreeting.setText(greetingText);
            tvStreak.setText(streakText);
            tvProgressText.setText(progressText);
            pbDailyGoal.setMax(bannerItem.dailyGoal);
            pbDailyGoal.setProgress(bannerItem.dailyProgress);
        }
    }

    static class ContinueLearningCarouselViewHolder extends RecyclerView.ViewHolder {
        RecyclerView horizontalRecyclerView;
        ContinueLearningCarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalRecyclerView = itemView.findViewById(R.id.rv_horizontal_sets);
        }
        void bind(HomeScreenItem.ContinueLearning item) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            // TODO: Tạo một adapter mới (ví dụ: `HorizontalSetAdapter`) cho RecyclerView này
            // HorizontalSetAdapter adapter = new HorizontalSetAdapter(item.sets);
            // horizontalRecyclerView.setAdapter(adapter);
        }
    }

    static class AuthorsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView horizontalRecyclerView;
        AuthorsViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalRecyclerView = itemView.findViewById(R.id.rv_horizontal_sets);
        }
        void bind(HomeScreenItem.Authors item) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            // TODO: Tạo một adapter mới (ví dụ: `HorizontalSetAdapter`) cho RecyclerView này
            // HorizontalSetAdapter adapter = new HorizontalSetAdapter(item.sets);
            // horizontalRecyclerView.setAdapter(adapter);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_header_title);
        }
        void bind(HomeScreenItem.Header item) {
            tvTitle.setText(item.title);
        }
    }

    static class RecommendedSetViewHolder extends RecyclerView.ViewHolder {
        // ... Khai báo view
        RecommendedSetViewHolder(@NonNull View itemView) { super(itemView); }
        void bind(HomeScreenItem.RecommendedSet item) { /* ... Gán dữ liệu */ }
    }
}
