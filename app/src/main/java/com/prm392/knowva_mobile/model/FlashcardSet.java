package com.prm392.knowva_mobile.model;

public class FlashcardSet {
    private String id;
    private String title;
    private String author;
    private int cardCount;

    public FlashcardSet(String id, String title, String author, int cardCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.cardCount = cardCount;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getCardCount() { return cardCount; }
}