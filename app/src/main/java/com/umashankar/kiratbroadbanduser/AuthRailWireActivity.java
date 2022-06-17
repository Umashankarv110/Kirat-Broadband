package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Authentication;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthRailWireActivity extends AppCompatActivity {

    TextInputLayout railUsername, railMobNumber;
    Button railSignInBtn;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_railwire);

        pd = new ProgressDialog(this);
        pd.setMessage("Checking Credentials \nPlease Wait.... ");

        railUsername = findViewById(R.id.railUsername);
        railMobNumber = findViewById(R.id.railMobNumber);
        railSignInBtn = findViewById(R.id.railSignInBtn);

        railSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mUsername = railUsername.getEditText().getText().toString().trim();
                String mMobNumber = railMobNumber.getEditText().getText().toString().trim();

                if (mUsername.isEmpty()) {
                    railUsername.setError("Enter Username");
                    railUsername.requestFocus();
                    return;
                } else if (mMobNumber.isEmpty()) {
                    railMobNumber.setError("Enter Mobile Number");
                    railMobNumber.requestFocus();
                    return;
                }else if (mMobNumber.length()!=10){
                    Toast.makeText(getApplicationContext(),"Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    railUsername.setError(null);
                    railUsername.setErrorEnabled(false);
                    railMobNumber.setError(null);
                    railMobNumber.setErrorEnabled(false);
                    pd.show();

                    Log.e("Username", ""+mUsername);
                    Log.e("Mobile", mMobNumber);
                    StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_RailWire_USER,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("Response:  ", response);
                                    if (response.equalsIgnoreCase("0")){
                                        pd.dismiss();
                                        Toast.makeText(AuthRailWireActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }else {
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            Authentication user = new Authentication(
                                                    obj.getInt("id"),
                                                    obj.getString("customerName"),
                                                    obj.getString("mobileNumber"),
                                                    obj.getString("email"),
                                                    obj.getString("username"),
                                                    "railwire"
                                            );

                                            Log.i("uUsername",user.getCustomerLandline());
                                            SharedPrefUserLogin.getInstance(getApplicationContext()).userLogin(user);
                                            Intent i = new Intent(AuthRailWireActivity.this, CustomerHomeActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            pd.dismiss();
                                            finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AuthRailWireActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Error", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("username", ""+mUsername);
                            map.put("mobile", mMobNumber);
                            map.put("fetchType","authentication");
                            return map;
                        }

                    };
                    RequestQueue rQeue = Volley.newRequestQueue(AuthRailWireActivity.this);
                    rQeue.add(sr);

                }
            }
        });

    }
}