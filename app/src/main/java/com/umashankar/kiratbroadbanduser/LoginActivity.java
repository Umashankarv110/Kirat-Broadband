package com.umashankar.kiratbroadbanduser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;
import com.umashankar.kiratbroadbanduser.TempCustomer.DashboardTempUserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    ImageView iv_logo;
    TextView sub_title,forgotpawd;

    private Button login;
    private TextInputLayout email,password, contact;
    private ProgressDialog pd;

    String Email,Pass, Mobile, mMobile, mLandLine;
    String selectedRB="";

    int otpCode;
    private Dialog dialog;
    ImageView mCloseIv;
    TextInputLayout mContact;
    Button mVerify,mSendAgain;
    LinearLayout mlinearOtp;
    TextView mSuccessText, mCounter;
    EditText mCode1, mCode2, mCode3, mCode4;

    CountDownTimer countDownTimer;
    long timeLeftInMilliSecond = 180000;    //3Min: 180000, 5Min: 300000

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iv_logo = findViewById(R.id.imageView);
        sub_title = findViewById(R.id.textView);

        if (SharedPrefUserLogin.getInstance(this).isUserLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, CustomerHomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }else if (SharedPrefTempUserLogin.getInstance(this).isUserLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, DashboardTempUserActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Checking Credentials \nPlease Wait.... ");

        email = findViewById(R.id.landline2);
        password = findViewById(R.id.password);
        login = findViewById(R.id.button);
        forgotpawd = findViewById(R.id.forgotPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNetworkConnectionStatus()) {
//                    UserLoginNow();
                    UserContactLogin();
                }
            }

        });
    }

    private void UserContactLogin() {
        mLandLine = email.getEditText().getText().toString().trim();
        mMobile = password.getEditText().getText().toString().trim();

        if (mMobile.isEmpty()) {
            email.setError("Enter Mobile Number");
            email.requestFocus();
            return;
        } else if (mLandLine.isEmpty()) {
            password.setError("Enter Landline Number");
            password.requestFocus();
            return;
        }else if (mMobile.length()!=10){
            Toast.makeText(getApplicationContext(),"Invalid Mobile Number", Toast.LENGTH_SHORT).show();
        }else if (mLandLine.length()!=6){
            Toast.makeText(getApplicationContext(),"Invalid Landline Number", Toast.LENGTH_SHORT).show();
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            password.setError(null);
            password.setErrorEnabled(false);
            pd.show();

            Log.e("Mobile", mMobile);
            Log.e("Landline", mLandLine);
            StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_USER_AUTH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response:  ", response);
                            if (response.equalsIgnoreCase("0")){
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }else {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Customers user = new Customers(
                                            obj.getInt("id"),
                                            obj.getString("name"),
                                            obj.getString("mobile"),
                                            obj.getString("phone")
                                    );
                                    SharedPrefUserLogin.getInstance(getApplicationContext()).userLogin(user);
                                    Intent i = new Intent(LoginActivity.this, CustomerHomeActivity.class);
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
                    Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("mobile", mMobile);
                    map.put("landline", "01686-"+mLandLine);
                    return map;
                }

            };
            RequestQueue rQeue = Volley.newRequestQueue(LoginActivity.this);
            rQeue.add(sr);

        }
    }

    private void randomOtp(){
        Random random = new Random();
        otpCode = random.nextInt(8999)+1000;
    }

    private boolean checkNetworkConnectionStatus() {
        final Dialog dialog = new Dialog(LoginActivity.this);
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

    public void newUserLogin(View view) {
        Dialog dialog = new Dialog(LoginActivity.this);
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

                String mobText = tvMobNumber.getEditText().getText().toString().trim();
                String pinText = tvLoginPin.getEditText().getText().toString().trim();

                if (mobText.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }else if (mobText.length()!=10){
                    Toast.makeText(getApplicationContext(),"Invalid Number", Toast.LENGTH_SHORT).show();
                }else if (pinText.length()!=4){
                    Toast.makeText(getApplicationContext(),"Enter 4 Digit Number", Toast.LENGTH_SHORT).show();
                }else{
                    pd.show();

                    Log.e("Mobile", mobText);
                    Log.e("Landline", pinText);
                    StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("Response:  ", response);
                                    if (response.equalsIgnoreCase("0")){
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }else {
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            Customers user = new Customers(
                                                    obj.getInt("id"),
                                                    obj.getString("name"),
                                                    obj.getString("mobile"),
                                                    obj.getString("loginPin")
                                            );
                                            SharedPrefTempUserLogin.getInstance(getApplicationContext()).userLogin(user);
                                            Intent i = new Intent(LoginActivity.this, DashboardTempUserActivity.class);
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
                            Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                    RequestQueue rQeue = Volley.newRequestQueue(LoginActivity.this);
                    rQeue.add(sr);

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

    public void newUserRegistration(View view) {
        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
    }
}