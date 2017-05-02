package com.fortunekidew.pewaad.models.payments;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PaymentRequest {
    public String return_code;
    public int  status_code;
    public String message;
    public String description;
    public String trx_id;
    public String cust_msg;
    public String reference_id;
    public String merchant_transaction_id;
    public String amount_in_double_float;
    public String client_phone_number;
    public String callback_url;
}
