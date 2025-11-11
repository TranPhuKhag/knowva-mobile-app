package com.prm392.knowva_mobile.model.response;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("checkoutUrl")
    private String checkoutUrl;

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}