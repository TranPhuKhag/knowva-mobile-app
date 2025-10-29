package com.prm392.knowva_mobile.view.Home;

import android.widget.VideoView;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.prm392.knowva_mobile.R;
import com.prm392.knowva_mobile.model.response.MyFlashcardSetResponse;
import com.prm392.knowva_mobile.view.Home.HomeScreenItem;
import com.prm392.knowva_mobile.view.flashcard.FlashcardViewerActivity;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Các hằng số để xác định loại view
    private static final int TYPE_BANNER = 0;
    private static final int TYPE_CONTINUE_LEARNING = 1;
    private static final int TYPE_AUTHORS = 2;
    private static final int TYPE_HEADER = 3;
    private static final int TYPE_RECOMMENDED = 4;
    private static final int TYPE_SUGGESTED_SETS = 5;

    private List<HomeScreenItem> items;

    public void setItems(List<HomeScreenItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        HomeScreenItem item = items.get(position);
        if (item instanceof HomeScreenItem.Banner) return TYPE_BANNER;
        if (item instanceof HomeScreenItem.ContinueLearning) return TYPE_CONTINUE_LEARNING;
        if (item instanceof HomeScreenItem.Authors) return TYPE_AUTHORS;
        if (item instanceof HomeScreenItem.Header) return TYPE_HEADER;
        if (item instanceof HomeScreenItem.RecommendedSet) return TYPE_RECOMMENDED;
        if (item instanceof HomeScreenItem.SuggestedSets) return TYPE_SUGGESTED_SETS;
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
            case TYPE_AUTHORS:
                // Sử dụng layout chung cho cả hai loại danh sách ngang
                return new HorizontalCarouselViewHolder(inflater.inflate(R.layout.item_horizontal_carousel, parent, false));
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_home_header, parent, false));
            case TYPE_RECOMMENDED:
                return new RecommendedSetViewHolder(inflater.inflate(R.layout.item_recommended_set, parent, false));
            case TYPE_SUGGESTED_SETS:
                return new SuggestedSetsViewHolder(inflater.inflate(R.layout.item_suggested_sets_container, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeScreenItem item = items.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).bind((HomeScreenItem.Banner) item);
                break;
            case TYPE_CONTINUE_LEARNING:
                // Ép kiểu về ViewHolder chung và truyền dữ liệu tương ứng
                ((HorizontalCarouselViewHolder) holder).bindContinueLearning((HomeScreenItem.ContinueLearning) item);
                break;
            case TYPE_AUTHORS:
                // Ép kiểu về ViewHolder chung và truyền dữ liệu tương ứng
                ((HorizontalCarouselViewHolder) holder).bindAuthors((HomeScreenItem.Authors) item);
                break;
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind((HomeScreenItem.Header) item);
                break;
            case TYPE_RECOMMENDED:
                ((RecommendedSetViewHolder) holder).bind((HomeScreenItem.RecommendedSet) item);
                break;
            case TYPE_SUGGESTED_SETS:
                ((SuggestedSetsViewHolder) holder).bind((HomeScreenItem.SuggestedSets) item);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // --- ViewHolder Classes ---

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        TextView tvGreeting;
        VideoView videoView; // Thêm VideoView

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGreeting = itemView.findViewById(R.id.tv_greeting);
            videoView = itemView.findViewById(R.id.video_view_banner); // Tìm VideoView bằng ID
        }

        void bind(HomeScreenItem.Banner bannerItem) {
            // 1. Gán text chào mừng
            String greetingText = "Chào bạn, " + bannerItem.userName + "!";
            tvGreeting.setText(greetingText);

            // 2. Thiết lập và phát video
            try {
                android.content.Context context = itemView.getContext();

                // Tạo đường dẫn Uri đến tệp trong res/raw
                String path = "android.resource://" + context.getPackageName() + "/" + R.raw.hello;
                Uri uri = Uri.parse(path);

                videoView.setVideoURI(uri);

                // Xóa các nút điều khiển mặc định
                videoView.setMediaController(null);

                // Tắt tiếng và lặp lại video khi nó đã sẵn sàng
                videoView.setOnPreparedListener(mp -> {
                    mp.setVolume(0f, 0f); // Tắt tiếng
                    mp.setLooping(true);    // Tự động lặp lại
                });

                // Bắt đầu phát
                videoView.start();

            } catch (Exception e) {
                // Ghi log nếu có lỗi (ví dụ: không tìm thấy tệp video)
                Log.e("BannerViewHolder", "Lỗi khi phát video", e);
            }
        }
    }

    // ViewHolder chung cho các danh sách cuộn ngang
    static class HorizontalCarouselViewHolder extends RecyclerView.ViewHolder {
        RecyclerView horizontalRecyclerView;
        HorizontalCarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalRecyclerView = itemView.findViewById(R.id.rv_horizontal_list);
        }

        // Phương thức để bind dữ liệu "Continue Learning"
        void bindContinueLearning(HomeScreenItem.ContinueLearning item) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            // SỬ DỤNG ADAPTER MỚI
            HorizontalSetAdapter adapter = new HorizontalSetAdapter(item.sets);
            horizontalRecyclerView.setAdapter(adapter);
        }

        // Phương thức để bind dữ liệu "Authors"
        void bindAuthors(HomeScreenItem.Authors item) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            // SỬ DỤNG ADAPTER MỚI
            HorizontalSetAdapter adapter = new HorizontalSetAdapter(item.sets);
            horizontalRecyclerView.setAdapter(adapter);
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
        TextView tvTitle, tvSetDetails;
        RecommendedSetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_set_title);
            tvSetDetails = itemView.findViewById(R.id.tv_set_details);
        }
        void bind(HomeScreenItem.RecommendedSet item) {
            // SỬA LẠI PHẦN BIND DỮ LIỆU
            tvTitle.setText(item.set.getTitle());
            String details = item.set.getCardCount() + " thuật ngữ";
            tvSetDetails.setText(details);
        }
    }

    // ViewHolder cho danh sách gợi ý từ API
    static class SuggestedSetsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvSuggested;

        SuggestedSetsViewHolder(@NonNull View itemView) {
            super(itemView);
            rvSuggested = itemView.findViewById(R.id.rv_suggested_sets);
        }

        void bind(HomeScreenItem.SuggestedSets item) {
            rvSuggested.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            SuggestedSetAdapter adapter = new SuggestedSetAdapter(set -> openSet(itemView.getContext(), set));
            rvSuggested.setAdapter(adapter);
            adapter.submit(item.sets);
        }

        private void openSet(android.content.Context context, MyFlashcardSetResponse set) {
            Intent i = new Intent(context, FlashcardViewerActivity.class);
            i.putExtra("set_id", set.id);
            i.putExtra("set_title", set.title);
            i.putExtra("set_username", set.username);
            int terms = (set.flashcards == null) ? 0 : set.flashcards.size();
            i.putExtra("set_terms", terms);

            // Truyền sẵn danh sách thẻ để Viewer không cần gọi lại API
            if (set.flashcards != null && !set.flashcards.isEmpty()) {
                i.putExtra("cards_json", new Gson().toJson(set.flashcards));
            }

            context.startActivity(i);
        }
    }
}