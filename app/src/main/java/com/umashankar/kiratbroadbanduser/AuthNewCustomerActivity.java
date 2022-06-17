package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;
import com.umashankar.kiratbroadbanduser.TempCustomer.DashboardTempUserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthNewCustomerActivity extends AppCompatActivity {

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_new_customer);

        pd = new ProgressDialog(this);
        pd.setMessage("Checking Credentials \nPlease Wait.... ");
    }

    public void newUserRegistration(View view) {
        startActivity(new Intent(AuthNewCustomerActivity.this,RegistrationActivity.class));
    }

    public void RegisteredCustomer(View view) {
        Dialog dialog = new Dialog(AuthNewCustomerActivity.this);
        dialog.setContentView(R.layout.layout_user_login);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        TextInputLayout tvMobNumber = dialog.findViewById(R.id.tvMobNumber);
        TextInputLayout tvLoginPin = dialog.findViewById(R.id.tvLoginPin);
        Button btnLogin = dialog.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkNetworkConnectionStatus()) {
                    String mobText = tvMobNumber.getEditText().getText().toString().trim();
                    String pinText = tvLoginPin.getEditText().getText().toString().trim();

                    if (mobText.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    } else if (mobText.length() != 10) {
                        Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
                    } else if (pinText.length() != 4) {
                        Toast.makeText(getApplicationContext(), "Enter 4 Digit Number", Toast.LENGTH_SHORT).show();
                    } else {
                        pd.show();

                        Log.e("Mobile", mobText);
                        Log.e("Landline", pinText);
                        StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.i("Response:  ", response);
                                        if (response.equalsIgnoreCase("0")) {
                                            pd.dismiss();
                                            Toast.makeText(AuthNewCustomerActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                Customers user = new Customers(
                                                        obj.getInt("id"),
                                                        obj.getString("name"),
                                                        obj.getString("mobile"),
                                                        obj.getString("loginPin"),
                                                        obj.getString("connectionFor")
                                                );
                                                SharedPrefTempUserLogin.getInstance(getApplicationContext()).userLogin(user);
                                                Intent i = new Intent(AuthNewCustomerActivity.this, DashboardTempUserActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                pd.dismiss();
                                                dialog.dismiss();
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(AuthNewCustomerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("mobile", mobText);
                                map.put("loginPin", pinText);
                                map.put("queryType", "Login");
                                return map;
                            }

                        };
                        RequestQueue rQeue = Volley.newRequestQueue(AuthNewCustomerActivity.this);
                        rQeue.add(sr);

                    }

                }
            }
        });

        ImageView otpClose = dialog.findViewById(R.id.otpClose);
        otpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
    private boolean checkNetworkConnectionStatus() {
        final Dialog dialog = new Dialog(AuthNewCustomerActivity.this);
        dialog.setContentView(R.layout.layout_connectivity);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        ImageView mConStatusIv = dialog.findViewById(R.id.conStatusIv);
        TextView mConStatusTv = dialog.findViewById(R.id.conStatusTv);
        TextView mConStatusCloseBtn = dialog.findViewById(R.id.close);
        TextView mConStatusDataBtn = dialog.findViewById(R.id.setting_btn);

        mConStatusCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        boolean wifiConnected;
        boolean mobileConnected;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()){ //connected with either mobile or wifi
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected){ //wifi connected
                Log.i("Network: ","Connected with Wifi");
                dialog.dismiss();
                return true;
            }
            else if (mobileConnected){ //mobile data connected
                Log.i("Network: ","Connected with Mobile Data");
                dialog.dismiss();
                return true;
            }
        }
        else { //no internet connection
            mConStatusIv.setImageResource(R.drawable.ic_wifi_off_24);
            Log.i("Network: ","Not Connected");
            mConStatusDataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                }
            });
            return false;
        }
        return false;
    }
}