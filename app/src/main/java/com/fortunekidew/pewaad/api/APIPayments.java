package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.DefaultResponse;
import com.fortunekidew.pewaad.models.payments.ConfirmPaymentResponse;
import com.fortunekidew.pewaad.models.payments.EditPayments;
import com.fortunekidew.pewaad.models.payments.RequestPaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public interface APIPayments {
    /**
     * method to get all gifts
     *
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.Payments)
    Call<DefaultResponse> createPayment(@Field("amount") double amount,
                                        @Field("wishlist_item_id") String giftId,
                                        @Field("reference") String payment_reference,
                                        @Field("status") String status,
                                        @Field("description") String description,
                                        @Field("trx_id") String transaction_id,
                                        @Field("is_anonymous") Boolean is_anonymous);


    @POST(EndPoints.PAYMENT_REQUEST)
     Call<RequestPaymentResponse> requestPayment(@Body EditPayments payment);

    @GET(EndPoints.PAYMENT_CONFIRM)
    Call<ConfirmPaymentResponse> confirmPayment(@Path("trx_id") String trxID);
}
