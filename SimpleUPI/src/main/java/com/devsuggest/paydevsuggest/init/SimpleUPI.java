package com.devsuggest.paydevsuggest.init;

import android.content.Context;
import android.content.Intent;

public class SimpleUPI {

    static SimpleUPICallbacks simpleUPICallbacks;
    static String DS_PG_API_KEY = "";

    public void init(SimpleUPICallbacks callbackscontext, String apikey) {
        simpleUPICallbacks = callbackscontext;
        DS_PG_API_KEY = apikey;
    }

    public void pay(Context context, String amount, String note) {
        if (!amount.equals("") && !amount.equals("0")) {
            Intent intent = new Intent(context, PaymentHandler.class);
            intent.putExtra("API_KEY", DS_PG_API_KEY);
            intent.putExtra("PAY_AMT", amount);
            intent.putExtra("PAY_NOTE", note);
            context.startActivity(intent);
        } else {
            simpleUPICallbacks.onPaymentFailure("INVALID_AMOUNT");
        }
    }

    public interface SimpleUPICallbacks {
        void onPaymentSuccess(String transaction_id, String transaction_status, String transaction_ref_no);

        void onPaymentFailure(String error_message);

        void onUnknownError(String transaction_status);

        void onPaymentAppNotFound(String error_message);

        void onPaymentDismiss();
    }
}
