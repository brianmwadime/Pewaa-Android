package com.fortunekidew.pewaa.api;

import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.models.payments.ConfirmPaymentResponse;
import com.fortunekidew.pewaa.models.payments.EditPayments;
import com.fortunekidew.pewaa.models.payments.PaymentRequest;
import com.fortunekidew.pewaa.models.payments.PaymentResponse;
import com.fortunekidew.pewaa.models.payments.RequestPaymentResponse;
import com.fortunekidew.pewaa.models.wishlists.GiftResponse;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by mwakima on 10/13/16.
 */

public interface APIPayments {
    /**
     * method to get all gifts
     *
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.Payments)
    Call<PaymentResponse> createPayment(@Field("amount") double amount,
                                        @Field("wishlist_item_id") String giftId,
                                        @Field("reference") String payment_reference,
                                        @Field("status") String status,
                                        @Field("description") String description);


    @POST(EndPoints.PAYMENT_REQUEST)
     Call<RequestPaymentResponse> requestPayment(@Body EditPayments payment);

    @GET(EndPoints.PAYMENT_CONFIRM)
    Call<ConfirmPaymentResponse> confirmPayment(@Path("trx_id") String trxID);
}
