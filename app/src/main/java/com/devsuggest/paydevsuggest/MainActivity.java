package com.devsuggest.paydevsuggest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devsuggest.paydevsuggest.init.SimpleUPI;

public class MainActivity extends AppCompatActivity implements SimpleUPI.SimpleUPICallbacks {

    Button pay_now_btn;
    TextView response_tv, response_status_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pay_now_btn = findViewById(R.id.pay_now_btn);
        response_tv = findViewById(R.id.response_tv);
        response_status_tv = findViewById(R.id.response_status_tv);

        SimpleUPI simpleUPI = new SimpleUPI();
        simpleUPI.init(MainActivity.this, "SIMPLE_UPI_PAYMENT_GATEWAY", "", "");

        pay_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleUPI.pay(MainActivity.this, "1.00", "");
            }
        });
    }


    @Override
    public void onPaymentSuccess(String transaction_id, String transaction_status, String transaction_ref_no) {
        response_status_tv.setText("Status: SUCCESS");
        response_tv.setText("Transaction ID: " + transaction_id + "\n" + "Transaction Status: " + transaction_status + "\n" + "Reference Num: " + transaction_ref_no + "\n");
    }

    @Override
    public void onPaymentFailure(String error_message) {
        response_status_tv.setText("Status: FAILED");
        Toast.makeText(this, "Failed: " + error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnknownError(String transaction_status) {
        response_status_tv.setText("Status: UNKNOWN ERROR");
        Toast.makeText(this, "There is an unknown error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentAppNotFound(String error_message) {
        response_status_tv.setText("Status: APPS NOT FOUND");
        Toast.makeText(this, error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentDismiss() {
        response_status_tv.setText("Status: DISMISS");
        Toast.makeText(this, "Dismiss", Toast.LENGTH_SHORT).show();
    }
}