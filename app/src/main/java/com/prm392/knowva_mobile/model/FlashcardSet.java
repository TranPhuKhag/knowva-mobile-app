package com.prm392.knowva_mobile.model;

import java.io.Serializable;
import java.util.List;

public class FlashcardSet implements Serializable {
    private long id;
    private String title;
    private String author;
    private String username;
    private int cardCount;
    private List<Flashcard> flashcards;

    public FlashcardSet() {}

    public FlashcardSet(String id, String title, String author, int cardCount) {
        this.id = Long.parseLong(id);
        this.title = title;
        this.author = author;
        this.cardCount = cardCount;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getUsername() { return username; }
    public int getCardCount() { return cardCount; }
    public List<Flashcard> getFlashcards() { return flashcards; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setUsername(String username) { this.username = username; }
    public void setCardCount(int cardCount) { this.cardCount = cardCount; }
    public void setFlashcards(List<Flashcard> flashcards) { this.flashcards = flashcards; }
}