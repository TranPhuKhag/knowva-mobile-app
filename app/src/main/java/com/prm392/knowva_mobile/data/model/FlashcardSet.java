package com.prm392.knowva_mobile.data.model;

public class FlashcardSet {
    public final String id;
    public final String title;
    public final int termCount;
    public final String authorName;
    public final int progress; // ví dụ: 75 nghĩa là 75%

    public FlashcardSet(String id, String title, int termCount, String authorName, int progress) {
        this.id = id;
        this.title = title;
        this.termCount = termCount;
        this.authorName = authorName;
        this.progress = progress;
    }
}