package com.prm392.knowva_mobile.service;

import com.prm392.knowva_mobile.model.request.PaymentRequest;
import com.prm392.knowva_mobile.model.response.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentService {
    @POST("payment/create-payment-link")
    Call<PaymentResponse> createPaymentLink(@Body PaymentRequest body);
}