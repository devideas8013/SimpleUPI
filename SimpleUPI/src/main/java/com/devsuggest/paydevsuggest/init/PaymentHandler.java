package com.devsuggest.paydevsuggest.init;

import static com.devsuggest.paydevsuggest.init.SimpleUPI.simpleUPICallbacks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devsuggest.paydevsuggest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.List;
import java.util.Locale;

public class PaymentHandler extends AppCompatActivity {
    //    private final String SAFE_SERVER_ACCESS = "aHR0cDovL3BnLmRldnN1Z2dlc3QuY29tL3NpbXBsZS11cGkucGhwP2FwaT0=";
    private final String PAYTM_PACKAGE = "net.one97.paytm";
    private final String PHONEPAY_PACKAGE = "com.phonepe.app";
    private final String GPAY_PACKAGE = "com.google.android.apps.nbu.paisa.user";
    private final String WHATSAPP_PACKAGE = "com.whatsapp";
    private final String BHIM_UPI_PACKAGE = "in.org.npci.upiapp";
    private final String JIO_UPI_PACKAGE = "com.jio.myjio";
    private ActivityResultLauncher<Intent> callbacklauncher;
    private TextView pay_amount_tv;
    private boolean isAPIValid = false, isUPIAppsFound = false;
    private String PAY_AMT = "";
    private String PAY_NOTE = "";
    private String API_KEY = "";
    private String TXN_ID = "";
    private String TXN_STATUS = "";
    private String TXN_REF_NO = "";
    private String PAY_UPI_URL = "";
    private String PAY_UPI_URL_2 = "";
    private LinearLayout open_paytm_app_btn, open_phone_pay_app_btn, open_google_pay_app_btn,
            open_bhim_app_btn, open_jio_pay_app_btn, show_all_apps_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.primary_color));
        setContentView(R.layout.activity_payment_handler);

        allFindViewById();
        receiveIntentData();

        setAllViews();
        getAllPackages();
        setAllOnClickListener();

        callbacklauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String paymentResponse = data.getStringExtra("response");
                            processPaymentResponse(paymentResponse);
                        }
                    }
                });
    }

    private void setAllViews() {
        if (!PAY_AMT.equals("")) {
            pay_amount_tv.setText("₹ " + PAY_AMT);
        }
    }

    private void getAllPackages() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(PAYTM_PACKAGE) || packageInfo.packageName.equals(PHONEPAY_PACKAGE)
                    || packageInfo.packageName.equals(GPAY_PACKAGE) || packageInfo.packageName.equals(WHATSAPP_PACKAGE)) {
                isUPIAppsFound = true;
                break;
            }
        }

        if (isUPIAppsFound) {
            generateUPIURLs();
            isAPIValid = true;
        } else {
            simpleUPICallbacks.onPaymentAppNotFound("Sorry, No UPI apps found in your phone!");
            finish();
        }
    }

    private void setAllOnClickListener() {
        open_paytm_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPaytmChooser();
            }
        });

        open_phone_pay_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhonePayChooser();
            }
        });

        open_google_pay_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGooglePayChooser();
            }
        });

        open_bhim_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBhimUpiChooser();
            }
        });

        open_jio_pay_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJioPayChooser();
            }
        });

        show_all_apps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUPIChooser();
            }
        });
    }

    private void processPaymentResponse(String paymentResponse) {
        String[] strArray = paymentResponse.split("&");
        for (String pair : strArray) {
            String[] split = pair.split("=");
            if (split[0].equals("txnId")) {
                try {
                    TXN_ID = split[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    TXN_ID = "NOT_RECEIVED";
                }
            } else if (split[0].equals("Status")) {
                TXN_STATUS = split[1];
            } else if (split[0].equals("ApprovalRefNo")) {
                try {
                    TXN_REF_NO = split[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    TXN_REF_NO = "NOT_RECEIVED";
                }
            } else if (split[0].equals("txnRef")) {
                try {
                    TXN_REF_NO = split[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    TXN_REF_NO = "NOT_RECEIVED";
                }
            }
        }

        if (TXN_STATUS.equalsIgnoreCase("success")) {
            simpleUPICallbacks.onPaymentSuccess(TXN_ID, TXN_STATUS, TXN_REF_NO);
        } else if (TXN_STATUS.equalsIgnoreCase("failure")) {
            simpleUPICallbacks.onPaymentFailure("");
        } else {
            simpleUPICallbacks.onUnknownError(TXN_STATUS);
        }

        finish();
    }

    private void allFindViewById() {
        pay_amount_tv = findViewById(R.id.pay_amount_tv);
        open_paytm_app_btn = findViewById(R.id.open_paytm_app_btn);
        open_phone_pay_app_btn = findViewById(R.id.open_phone_pay_app_btn);
        open_google_pay_app_btn = findViewById(R.id.open_google_pay_app_btn);
        open_bhim_app_btn = findViewById(R.id.open_bhim_app_btn);
        open_jio_pay_app_btn = findViewById(R.id.open_jio_pay_app_btn);
        show_all_apps_btn = findViewById(R.id.show_all_apps_btn);
    }

//    private void requestAPIVerification() {
//        if (!API_KEY.equals("")) {
//            String decoded_server_url = "";
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                decoded_server_url = new String(Base64.getDecoder().decode(SAFE_SERVER_ACCESS));
//            } else {
//                decoded_server_url = new String(android.util.Base64.decode(SAFE_SERVER_ACCESS, android.util.Base64.DEFAULT));
//            }
//
//            if (API_KEY.equals("SIMPLE_UPI_PAYMENT_GATEWAY")) {
//                generateUPIURLs();
//                isAPIValid = true;
//            } else {
//                decoded_server_url = decoded_server_url + API_KEY + "&amt=" + PAY_AMT + "&tnote=" + PAY_NOTE;
//                verifyAPIKey(decoded_server_url);
//            }
//        } else {
//            simpleUPICallbacks.onPaymentFailure("MISSING_API_KEY");
//        }
//    }

    private void generateUPIURLs() {
        PAY_UPI_URL = PAY_UPI_URL + "&tn=" + PAY_NOTE + "&am=" + PAY_AMT;
        PAY_UPI_URL_2 = PAY_UPI_URL_2 + "&tr=" + getRandId(15) + "&tn=" + PAY_NOTE + "&am=" + PAY_AMT;
    }

    private void ShowConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentHandler.this);
        builder.setMessage("Click confirm to dismiss the transaction!");
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                simpleUPICallbacks.onPaymentDismiss();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void receiveIntentData() {
        Intent intent = getIntent();
        API_KEY = intent.getStringExtra("API_KEY");
        PAY_AMT = intent.getStringExtra("PAY_AMT");
        PAY_NOTE = intent.getStringExtra("PAY_NOTE");
        PAY_UPI_URL = intent.getStringExtra("PAY_UPI_URL");
        PAY_UPI_URL_2 = intent.getStringExtra("PAY_UPI_URL_2");
    }

    private void showUPIChooser() {
        if (isAPIValid) {
            Intent upiIntent = new Intent(Intent.ACTION_VIEW);
            upiIntent.setData(Uri.parse(PAY_UPI_URL));
            Intent chooser = Intent.createChooser(upiIntent, "Pay with...");
            try {
                callbacklauncher.launch(chooser);
            } catch (Exception e) {
                Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPaytmChooser() {
        if (isAPIValid) {
            if (isAppInstalled(PaymentHandler.this, PAYTM_PACKAGE)) {
                Intent upiIntent = new Intent(Intent.ACTION_VIEW);
                upiIntent.setPackage(PAYTM_PACKAGE);
                upiIntent.setData(Uri.parse(PAY_UPI_URL));
                try {
                    callbacklauncher.launch(upiIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
                }
            } else {
                simpleUPICallbacks.onPaymentAppNotFound("Paytm not Installed!");
            }
        }
    }

    private void showPhonePayChooser() {
        if (isAPIValid) {
            if (isAppInstalled(PaymentHandler.this, PHONEPAY_PACKAGE)) {
                Intent upiIntent = new Intent(Intent.ACTION_VIEW);
                upiIntent.setPackage(PHONEPAY_PACKAGE);
                upiIntent.setData(Uri.parse(PAY_UPI_URL_2));
                try {
                    callbacklauncher.launch(upiIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
                }
            } else {
                simpleUPICallbacks.onPaymentAppNotFound("PhonePe not Installed!");
            }
        }
    }

    private void showGooglePayChooser() {
        if (isAPIValid) {
            if (isAppInstalled(PaymentHandler.this, GPAY_PACKAGE)) {
                Intent upiIntent = new Intent(Intent.ACTION_VIEW);
                upiIntent.setPackage(GPAY_PACKAGE);
                upiIntent.setData(Uri.parse(PAY_UPI_URL_2));
                try {
                    callbacklauncher.launch(upiIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
                }
            } else {
                simpleUPICallbacks.onPaymentAppNotFound("GooglePay not Installed!");
            }
        }
    }

    private void showBhimUpiChooser() {
        if (isAPIValid) {
            if (isAppInstalled(PaymentHandler.this, BHIM_UPI_PACKAGE)) {
                Intent upiIntent = new Intent(Intent.ACTION_VIEW);
                upiIntent.setPackage(BHIM_UPI_PACKAGE);
                upiIntent.setData(Uri.parse(PAY_UPI_URL));
                try {
                    callbacklauncher.launch(upiIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
                }
            } else {
                simpleUPICallbacks.onPaymentAppNotFound("BhimUPI not Installed!");
            }
        }
    }

    private void showJioPayChooser() {
        if (isAPIValid) {
            if (isAppInstalled(PaymentHandler.this, JIO_UPI_PACKAGE)) {
                Intent upiIntent = new Intent(Intent.ACTION_VIEW);
                upiIntent.setPackage(JIO_UPI_PACKAGE);
                upiIntent.setData(Uri.parse(PAY_UPI_URL));
                try {
                    callbacklauncher.launch(upiIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Oops! there is an unknown error! please try different app!", Toast.LENGTH_SHORT).show();
                }
            } else {
                simpleUPICallbacks.onPaymentAppNotFound("Jio Payments bank not Installed!");
            }
        }
    }

    @Override
    public void onBackPressed() {
        ShowConfirmDialog();
    }

//    private void verifyAPIKey(String decoded_server_url) {
//        StringRequest request = new StringRequest(Request.Method.GET, decoded_server_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String RESPONSE_CODE = jsonObject.getString("RESPONSE_CODE");
//                    PAY_UPI_URL = jsonObject.getString("PAY_UPI_URL");
//                    PAY_UPI_URL_2 = jsonObject.getString("PAY_UPI_URL_2");
//
//                    if (RESPONSE_CODE.equals("true")) {
//                        isAPIValid = true;
//                        showUPIChooser();
//                    } else if (RESPONSE_CODE.equals("exp")) {
//                        isAPIValid = false;
//                        simpleUPICallbacks.onPaymentFailure("API_KEY_EXPIRE");
//                        finish();
//                    } else {
//                        isAPIValid = false;
//                        simpleUPICallbacks.onPaymentFailure("INVALID_API_KEY");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                simpleUPICallbacks.onPaymentFailure("SERVER_NOT_RESPOND");
//            }
//        });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(PaymentHandler.this);
//        requestQueue.add(request);
//    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRandId(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}