package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {
    Button send;

    Customers customers;
    private ProgressDialog progressDialog;

    String PayNote = "";
    String PayAmount= "";
    String userEmail= "";
    String userMobile= "";
    TextView TvCustomernName,TvConnType,tvEmail,tvContact,tvPValue,tvPayFor,pname,pvalue,billAmt;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.upiPayment_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        customers = SharedPrefTempUserLogin.getInstance(this).getUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait.... ");

        send = (Button) findViewById(R.id.send);
        TvCustomernName = findViewById(R.id.tvCName);
        TvConnType = findViewById(R.id.tvConnType);
        tvPValue = findViewById(R.id.tvPValue);
        pname = findViewById(R.id.pname);
        pvalue = findViewById(R.id.pvalue);
        billAmt = findViewById(R.id.billAmt);
        pname = findViewById(R.id.pname);
        tvPayFor = findViewById(R.id.tvPayFor);
        tvEmail = findViewById(R.id.tvEmail);
        tvContact = findViewById(R.id.tvContact);

        getAllTempUserDetails();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getAllTempUserDetails() {
        progressDialog.show();
        StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response:  ", response);
                        if (response.equalsIgnoreCase("0")){
                            progressDialog.dismiss();
                            Toast.makeText(PaymentActivity.this, "Something went wrong\n Try Again...", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                JSONObject obj = new JSONObject(response);
                                TvCustomernName.setText("Customer Name: "+obj.getString("name"));
                                TvConnType.setText("Connection Type: "+obj.getString("connectionType"));
                                tvEmail.setText("Email Id: "+obj.getString("email"));
                                tvContact.setText("Mobile : "+obj.getString("mobile"));

                                userEmail = obj.getString("email");
                                userMobile = obj.getString("mobile");

                                String planId = obj.getString("planId");
                                getPlanDetails(planId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", customers.getMobile());
                map.put("queryType", "AllInfo");
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(PaymentActivity.this);
        rQeue.add(sr);

    }
    private void getPlanDetails(String planId) {
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_REQ_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("planDetails", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("Details");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    String Id = object.getString("planId");
                                    String planName = object.getString("planName");
                                    String planAmt = object.getString("planAmt");
                                    String finalAmountRound = object.getString("finalAmountRound");
                                    if (planId.equalsIgnoreCase(Id)){
                                        tvPayFor.setText("Payment For: Plan "+planName);
                                        tvPValue.setText("Selected Plan: ₹"+finalAmountRound);
                                        pname.setText("Plan: "+planName);
                                        pvalue.setText("Plan Amount: ₹"+planAmt);
                                        billAmt.setText("Payable Amount: ₹"+finalAmountRound);
                                        PayAmount = finalAmountRound;
                                        PayNote = "Payment For Plan "+planName;
                                    }

                                    progressDialog.dismiss();
                                }
                            } else if (obj.optString("status").equals("false")) {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                String message = null;
                if (volleyError instanceof NetworkError || volleyError instanceof AuthFailureError || volleyError instanceof NoConnectionError || volleyError instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again later";
                    Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("type","PlanDetails");
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(PaymentActivity.this);
        rQeue.add(request);

    }


    public void copyUpi(View view) {
        String stringNodeCopied= "9896389883-2@okbizaxis";
        ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText("Copied", stringNodeCopied);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}