package com.prm392.knowva_mobile.repository;

import android.content.Context;

import com.prm392.knowva_mobile.factory.APIClient;
import com.prm392.knowva_mobile.model.request.PaymentRequest;
import com.prm392.knowva_mobile.model.response.PaymentResponse;
import com.prm392.knowva_mobile.service.PaymentService;

import retrofit2.Call;
import retrofit2.Retrofit;

public class PaymentRepository {
    private PaymentService paymentService;

    public PaymentRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        this.paymentService = retrofit.create(PaymentService.class);
    }

    public Call<PaymentResponse> createPaymentLink(long userId) {
        PaymentRequest request = new PaymentRequest(userId);
        return paymentService.createPaymentLink(request);
    }
}