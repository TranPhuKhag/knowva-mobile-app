package com.prm392.knowva_mobile.model.request;

public class PaymentRequest {
    private long userId;

    public PaymentRequest(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}