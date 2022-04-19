package com.umashankar.kiratbroadbanduser.TempCustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.umashankar.kiratbroadbanduser.ModelClass.Locations;
import com.umashankar.kiratbroadbanduser.R;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCustomerActivity extends AppCompatActivity {
    private TextInputLayout nameTextInput, mobileTextInput, emailTextInput, addressTextInput;
    private ProgressDialog progressDialog;
    private Spinner selectLocation;

    Customers customers;

    //Model Class
    Locations locations;
    private List<Locations> locationsList;
    private ArrayAdapter<Locations> locationsAdapter;
    private String locationName="";
    private int locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.new_customer_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personal Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait.... ");

        customers = SharedPrefTempUserLogin.getInstance(this).getUser();

        nameTextInput = findViewById(R.id.name);
        mobileTextInput = findViewById(R.id.mobile);
        emailTextInput = findViewById(R.id.email);
        selectLocation = findViewById(R.id.spinLocation);

        nameTextInput.getEditText().setText(""+customers.getName());
        mobileTextInput.getEditText().setText(""+customers.getMobile());

        locationsList = new ArrayList<>();
        retrieveLocationData();
        locationsAdapter = new ArrayAdapter<Locations>(NewCustomerActivity.this, android.R.layout.simple_spinner_item, locationsList);
        locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectLocation.setAdapter(locationsAdapter);
        selectLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locations = (Locations) parent.getSelectedItem();
                locationId = locations.getLocationId();
                locationName  = locations.getLocationName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void retrieveLocationData() {
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_REQ_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("locationsResponce",response);
                        locationsList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("true")){
                                JSONArray jsonArray = jsonObject.getJSONArray("Details");
                                for (int i=0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    int locId = object.getInt("locationId");
                                    String locName = object.getString("locationName");
                                    locations = new Locations(locId,locName);
                                    locationsList.add(locations);
                                    locationsAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();

                                }
                            }else if (jsonObject.optString("status").equals("false")){
                                progressDialog.dismiss();
                            }
                        }catch (JSONException e){
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
                    message = "No Internet Connection";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again later";
                }
                Toast.makeText(NewCustomerActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("type","Location");
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

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
                            Toast.makeText(NewCustomerActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                JSONObject obj = new JSONObject(response);
                                Customers user = new Customers(
                                        obj.getInt("id"),
                                        obj.getString("name"),
                                        obj.getString("mobile"),
                                        obj.getString("mobile")
                                );

                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewCustomerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        RequestQueue rQeue = Volley.newRequestQueue(NewCustomerActivity.this);
        rQeue.add(sr);

    }

    public void updateTempDetails(View view) {

        String Email = emailTextInput.getEditText().getText().toString();

        if (Email.equalsIgnoreCase("")){
            Toast.makeText(NewCustomerActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if (locationName.equalsIgnoreCase("Select City / Village")){
            Toast.makeText(NewCustomerActivity.this, "Select City / Village", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.show();
            StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response:  ", response);
                            progressDialog.dismiss();
                            if (response.equalsIgnoreCase("Error")) {
                                Toast.makeText(NewCustomerActivity.this, "Try Again...", Toast.LENGTH_SHORT).show();
                            }else if (response.equalsIgnoreCase("Updated")) {
                                Toast.makeText(NewCustomerActivity.this, "Customer Details Updated", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),DocumentCollectionActivity.class));
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(NewCustomerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", Email);
                    map.put("address", "");
                    map.put("cityVillage", locationName);
                    map.put("mobile", customers.getMobile());
                    map.put("queryType", "UpdateInfo");
                    return map;
                }

            };
            RequestQueue rQeue = Volley.newRequestQueue(NewCustomerActivity.this);
            rQeue.add(sr);
        }
    }
}