package com.prm392.knowva_mobile.model;

import java.io.Serializable;

public class Flashcard implements Serializable {
    private long id;
    private String front;
    private String back;
    private String imageUrl;
    private int order;

    public Flashcard() {}

    public Flashcard(long id, String front, String back, String imageUrl, int order) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.imageUrl = imageUrl;
        this.order = order;
    }

    // Getters
    public long getId() { return id; }
    public String getFront() { return front; }
    public String getBack() { return back; }
    public String getImageUrl() { return imageUrl; }
    public int getOrder() { return order; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setFront(String front) { this.front = front; }
    public void setBack(String back) { this.back = back; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setOrder(int order) { this.order = order; }
}

