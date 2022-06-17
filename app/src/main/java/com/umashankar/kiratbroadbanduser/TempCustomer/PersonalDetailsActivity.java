package com.umashankar.kiratbroadbanduser.TempCustomer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalDetailsActivity extends AppCompatActivity {
    private TextInputLayout nameTextInput, mobileTextInput, emailTextInput, addressTextInput;
    private ProgressDialog progressDialog;
    private Spinner selectLocation;
    private ImageView profileImage;

    Customers customers;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;

    String profileb64img="";

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

        nameTextInput = findViewById(R.id.name);
        mobileTextInput = findViewById(R.id.mobile);
        emailTextInput = findViewById(R.id.email);
        selectLocation = findViewById(R.id.spinLocation);
        profileImage = findViewById(R.id.profileImage);

        customers = SharedPrefTempUserLogin.getInstance(this).getUser();
        if (customers.getConnectionFor().equalsIgnoreCase("bsnl")){
            profileImage.setVisibility(View.GONE);
            BSNLAction();
        }if (customers.getConnectionFor().equalsIgnoreCase("railwire")){
            profileImage.setVisibility(View.VISIBLE);
            RailWireAction();
        }

        nameTextInput.getEditText().setText(""+customers.getName());
        mobileTextInput.getEditText().setText(""+customers.getMobile());

        locationsList = new ArrayList<>();
        retrieveLocationData();
        locationsAdapter = new ArrayAdapter<Locations>(PersonalDetailsActivity.this, android.R.layout.simple_spinner_item, locationsList);
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

    private void RailWireAction() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    private void BSNLAction() {
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
                Toast.makeText(PersonalDetailsActivity.this, ""+message, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bMap = (Bitmap) data.getExtras().get("data");
            Matrix mat = new Matrix();
            mat.postRotate(0);
            Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0,bMap.getWidth(),bMap.getHeight(), mat, true);

            int reqw= (int) ((int) bMapRotate.getWidth()* 0.3);
            int reqh= (int) ((int) bMapRotate.getHeight()* 0.3);
            Bitmap bMapReduced = Bitmap.createScaledBitmap(bMapRotate,reqw,reqh,true);

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bMapReduced.compress(Bitmap.CompressFormat.JPEG,10, baos);
            byte [] b=baos.toByteArray();
            profileImage.setImageBitmap(bMapRotate);
            profileb64img= Base64.encodeToString(b, Base64.DEFAULT);
            Log.i("Front", profileb64img);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Permission callback called-------");
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

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
                            Toast.makeText(PersonalDetailsActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PersonalDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        RequestQueue rQeue = Volley.newRequestQueue(PersonalDetailsActivity.this);
        rQeue.add(sr);

    }

    public void updateTempDetails(View view) {
        String Email = emailTextInput.getEditText().getText().toString();
        if (Email.equalsIgnoreCase("")){
            Toast.makeText(PersonalDetailsActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        } else if (locationName.equalsIgnoreCase("Select City / Village")){
            Toast.makeText(PersonalDetailsActivity.this, "Select City / Village", Toast.LENGTH_SHORT).show();
        }else if (profileb64img.equalsIgnoreCase("")) {
            Toast.makeText(PersonalDetailsActivity.this, "Capture Image For Profile ", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response:  ", response);
                            progressDialog.dismiss();
                            if (response.equalsIgnoreCase("Error")) {
                                Toast.makeText(PersonalDetailsActivity.this, "Try Again...", Toast.LENGTH_SHORT).show();
                            }else if (response.equalsIgnoreCase("Updated")) {
                                Toast.makeText(PersonalDetailsActivity.this, "Customer Details Updated", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),DocumentCollectionActivity.class));
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PersonalDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", Email);
                    map.put("address", "");
                    map.put("customerId", "" + customers.getMobile());
                    map.put("cityVillage", locationName);
                    map.put("mobile", customers.getMobile());
                    map.put("profileImage", profileb64img);
                    map.put("queryType", "UpdateInfo");
                    return map;
                }

            };
            RequestQueue rQeue = Volley.newRequestQueue(PersonalDetailsActivity.this);
            rQeue.add(sr);
        }
    }
}