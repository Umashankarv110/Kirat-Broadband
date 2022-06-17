package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Authentication;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;
import com.umashankar.kiratbroadbanduser.TempCustomer.RailWirePaymentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BSNLPaymentActivity extends AppCompatActivity  implements PaymentResultListener {
    private ProgressDialog progressDialog;

    Authentication regUser;
    String customerUsername;

    String payAmount= "", userEmail= "", userMobile= "";
    String payNote = "RailWire Recharge";
    String name = "Kirat Broadbands";
    String razorpayId="";

    int finalAmount;
    String orderStatus = "";

    TextView tvPayAmt;
    Button payBtn;

    TextInputLayout customAmt;
    private Dialog d1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsnlpayment);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.BsnlPay_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        d1 = new Dialog(BSNLPaymentActivity.this);
        d1.setContentView(R.layout.layout_msg);
        d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        d1.setCancelable(false);

        regUser = SharedPrefUserLogin.getInstance(this).getUser();
        userMobile = ""+regUser.getCustomerMobNumber();
        userEmail = ""+regUser.getCustomerEmail();

        customAmt = findViewById(R.id.customAmt);
        payBtn = findViewById(R.id.payBSNLBtn);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amt = customAmt.getEditText().getText().toString().trim();
                if (amt.equalsIgnoreCase("")){
                    Toast.makeText(BSNLPaymentActivity.this, "Enter Recharge Amount", Toast.LENGTH_SHORT).show();
                }else {
                    payRechargeAmt(amt);
                }
            }
        });

    }

    private void payRechargeAmt(String amt) {
        payAmount = amt;

        final Checkout co = new Checkout();
        finalAmount = Math.round(Float.parseFloat(payAmount) * 100);
        try {
            JSONObject options = new JSONObject();
            options.put("name", name);
            options.put("description", payNote);
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://cdn.razorpay.com/logos/IJnwt4TchafuXp_medium.png");
            options.put("currency", "INR");
            options.put("amount", finalAmount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userEmail);
            preFill.put("contact", userMobile);

            options.put("prefill", preFill);

            co.open(BSNLPaymentActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(BSNLPaymentActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String response) {
        try {
            Log.e("PaymentSuccessResponse",  response);
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            orderStatus = "Confirm";
            int successCode = 12345678;
            razorpayResponse(response, successCode);
        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.e("PaymentfailedResponse",  code+""+response);
            if (code == 0){
                orderStatus = "Payment cancelled by user";
                razorpayResponse(response, code);
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Payment Status Pending \nPlease wait for the confirmation", Toast.LENGTH_SHORT).show();
                orderStatus = "Pending";
                razorpayResponse(response, code);
            }
        } catch (Exception e) {
            Log.e("PaymentResponse", "Exception in onPaymentError", e);
        }
    }

    private void razorpayResponse(String payResponse, int responseCode) {

        Log.i("username", ""+regUser.getCustomerLandline());
        Log.i("rechargeAmount", payAmount);
        Log.i("razorpayId", razorpayId);
        Log.i("response", payResponse);
        Log.i("responseCode", ""+responseCode);
        Log.i("status", orderStatus);


        TextView txtTitle = d1.findViewById(R.id.textView11);
        TextView txtMsg1 = d1.findViewById(R.id.textMsgTitle);
        Button btnOk = d1.findViewById(R.id.msgOk);
        Button btnCancel = d1.findViewById(R.id.msgCancel);
        txtTitle.setText("Payment Response (भुगतान प्रतिक्रिया)");
        d1.show();
        if (orderStatus.equalsIgnoreCase("Confirm")){
            razorpayId = payResponse;
//            txtMsg1.setText("Payment Processed.. \nPlease wait for confirmation");
            txtMsg1.setText("Thank You.. \nYour payment has been confirmed.");
        }else if (orderStatus.equalsIgnoreCase("Pending")){
            txtMsg1.setText("Payment Status Pending \nPlease wait for the confirmation");
        }else {
            Log.i("ResponseStatus",orderStatus);
            txtMsg1.setText("Your Payment Has Been Confirmed.");
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_InsertRechargeResponse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("razorpayResponse", response);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d1.dismiss();
                                finish();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BSNLPaymentActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", ""+regUser.getCustomerLandline());
                map.put("rechargeAmount", ""+payAmount);
                map.put("razorpayId", razorpayId);
                map.put("status", orderStatus);
                map.put("response", payResponse);
                map.put("responseCode", ""+responseCode);
                map.put("payFor", "BSNL");
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(BSNLPaymentActivity.this);
        requestQueue.add(stringRequest);

    }

}