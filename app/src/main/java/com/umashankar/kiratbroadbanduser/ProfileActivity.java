package com.umashankar.kiratbroadbanduser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.material.textfield.TextInputLayout;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    Button btnSubmit;
    TextInputLayout txtName, txtEmail, txtContact, txtLandline;
    String Name, Email, Contact, LandLine, Address, intentUserType;

    TextView txtPlanName,txtPlanValue, txtBillAmt, txtWStatus,txtInstallDate;

    Customers user;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.user_main_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait.. ");
        pd.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        intentUserType = intent.getStringExtra("type");

        txtName = findViewById(R.id.etFullName);
        txtEmail = findViewById(R.id.etEmail);
        txtContact = findViewById(R.id.etMobile);
        txtLandline = findViewById(R.id.etLandline);
        btnSubmit = findViewById(R.id.saveProfileBtn);

        txtPlanName = findViewById(R.id.pname);
        txtPlanValue = findViewById(R.id.pvalue);
        txtBillAmt = findViewById(R.id.billAmt);
        txtWStatus = findViewById(R.id.wStatus);
        txtInstallDate = findViewById(R.id.idate);

//        txtName.getEditText().setFocusable(false);
//        txtEmail.getEditText().setFocusable(false);
//        txtContact.getEditText().setFocusable(false);
//        txtLandline.getEditText().setFocusable(false);

        txtPlanValue.setVisibility(View.GONE);
        txtBillAmt.setVisibility(View.GONE);
        txtWStatus.setVisibility(View.GONE);
        txtInstallDate.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        if (intentUserType.equalsIgnoreCase("TempUser")){
            user = SharedPrefTempUserLogin.getInstance(this).getUser();
            getTempProfileDetails();
        }else {
            user = SharedPrefUserLogin.getInstance(this).getUser();
            getProfileDetails();
        }

    }

    private void getTempProfileDetails() {
        pd.show();
        StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response:  ", response);
                        if (response.equalsIgnoreCase("0")){
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, "Something went wrong\n Try Again...", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                JSONObject obj = new JSONObject(response);
                                txtName.getEditText().setText(""+obj.getString("name"));
                                txtContact.getEditText().setText(""+obj.getString("mobile"));

                                txtLandline.setVisibility(View.GONE);
                                String Email = ""+obj.getString("email");
                                if(obj.getString("email").equalsIgnoreCase("")){
                                    txtEmail.setVisibility(View.GONE);
                                }else{
                                    txtEmail.getEditText().setText(Email);
                                    txtEmail.setVisibility(View.VISIBLE);
                                }
                                if(obj.getString("isPaid").equalsIgnoreCase("0")){
                                    txtWStatus.setText("Payment Status: Pending");
                                    txtWStatus.setVisibility(View.VISIBLE);
                                }else{
                                    txtWStatus.setText("Payment Status: Paid");
                                    txtWStatus.setVisibility(View.VISIBLE);
                                }

                                String planId = obj.getString("planId");
                                getTempPlanDetails(planId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", user.getMobile());
                map.put("queryType", "AllInfo");
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(ProfileActivity.this);
        rQeue.add(sr);

    }
    private void getTempPlanDetails(String planId) {
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
                                        txtPlanName.setText("Plan Name: "+planName);
                                        txtPlanValue.setText("Plan Value: "+planAmt+"/-");
                                        txtBillAmt.setText("Bill Amount: "+finalAmountRound+"/-");
                                        txtPlanValue.setVisibility(View.VISIBLE);
                                        txtBillAmt.setVisibility(View.VISIBLE);
                                    }

                                    pd.dismiss();
                                }
                            } else if (obj.optString("status").equals("false")) {
                                pd.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
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
        RequestQueue rQeue = Volley.newRequestQueue(ProfileActivity.this);
        rQeue.add(request);

    }

    private void getProfileDetails() {
        Log.i("UserReport","Getting.........."+user.getCustomerId());
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("userReport", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("CustomersDetails");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    int Id = object.getInt("customerId");
                                    String name = object.getString("name");
                                    String mobile = object.getString("mobile");
                                    String email = object.getString("email");
                                    String landline = object.getString("landline");
                                    String planName = object.getString("planName");
                                    String planValue = object.getString("planValue");
                                    String billAmt = object.getString("billAmt");
                                    String workingStatus = object.getString("workingStatus");
                                    String installDate = object.getString("installDate");

                                    txtName.getEditText().setText(name);
                                    txtContact.getEditText().setText(mobile);
                                    txtEmail.getEditText().setText(email);
                                    txtLandline.getEditText().setText(landline);

                                    pd.dismiss();
                                    if(!planName.equalsIgnoreCase("")){
                                        txtPlanName.setText("Plan Name: "+planName);
                                        txtPlanValue.setText("Plan Value: "+planValue+"/-");
                                        txtBillAmt.setText("Bill Amount: "+billAmt+"/-");
                                        txtWStatus.setText("Working Status: "+workingStatus);
                                        txtInstallDate.setText("Installation Date: "+installDate);
                                        txtPlanValue.setVisibility(View.VISIBLE);
                                        txtBillAmt.setVisibility(View.VISIBLE);
                                        txtWStatus.setVisibility(View.VISIBLE);
                                        txtInstallDate.setVisibility(View.VISIBLE);
                                    }else {
                                        txtPlanName.setText("No Active Plan");
                                        txtPlanValue.setVisibility(View.GONE);
                                        txtBillAmt.setVisibility(View.GONE);
                                        txtWStatus.setVisibility(View.GONE);
                                        txtInstallDate.setVisibility(View.GONE);
                                    }

                                }
                            } else if (obj.optString("status").equals("false")) {
                                pd.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
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
                map.put("customerId",""+user.getLandline());
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(ProfileActivity.this);
        rQeue.add(request);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.profile, menu);
//        return true;
//    }

    public void EditProfile(MenuItem item) {
        txtName.getEditText().setFocusable(true);
        txtEmail.getEditText().setFocusable(true);
        txtContact.getEditText().setFocusable(true);
        txtLandline.getEditText().setFocusable(true);
        btnSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}