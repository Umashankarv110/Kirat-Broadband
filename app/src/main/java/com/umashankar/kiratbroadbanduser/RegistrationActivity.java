package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.umashankar.kiratbroadbanduser.TempCustomer.PersonalDetailsActivity;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    ConstraintLayout railWireBtn, bsnlBtn;
    TextInputLayout txtName, txtDigit, txtContact;
    String Name, FourDigit, Contact;
    String selectConnectionType="";
    TextView selectCType;
    ImageView ctypeEdit,layoutClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtName = findViewById(R.id.nameET);
        txtDigit = findViewById(R.id.digitET);
        txtContact = findViewById(R.id.mobileET);
        selectCType = findViewById(R.id.selectCType);
        ctypeEdit = findViewById(R.id.ctypeEdit);

        ctypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionType();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionType();
    }

    private void connectionType() {
        Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.setContentView(R.layout.layout_connection_type);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        railWireBtn = dialog.findViewById(R.id.btn_railwire);
        bsnlBtn = dialog.findViewById(R.id.btn_bsnl);
        layoutClose = dialog.findViewById(R.id.layoutClose);

        railWireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectConnectionType = "RailWire";
                selectCType.setText("Connection Type: "+selectConnectionType);
                dialog.dismiss();
            }
        });

        bsnlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectConnectionType = "BSNL";
                selectCType.setText("Connection Type: "+selectConnectionType);
                dialog.dismiss();
            }
        });

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectConnectionType = "";
                dialog.dismiss();
            }
        });
    }

    public void userRegistration(View view) {
        Name = txtName.getEditText().getText().toString().trim();
        FourDigit = txtDigit.getEditText().getText().toString().trim();
        Contact = txtContact.getEditText().getText().toString().trim();

        if (selectConnectionType.isEmpty()){
            Toast.makeText(this, "Select ConnectionType", Toast.LENGTH_SHORT).show();
        }else if (Name.isEmpty()){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
        }else if (Contact.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Mobile Number", Toast.LENGTH_SHORT).show();
        }else if (Contact.length()!=10){
            Toast.makeText(getApplicationContext(),"Invalid Number", Toast.LENGTH_SHORT).show();
        }else if (FourDigit.length()!=4){
            Toast.makeText(getApplicationContext(),"Enter 4 Digit Number", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_TempCustomer,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("SignUpResponce", response);
                            if (response.equalsIgnoreCase("Customer Inserted")) {
                                Customers user = new Customers(001, Name,Contact,FourDigit, selectConnectionType.toLowerCase());
                                SharedPrefTempUserLogin.getInstance(getApplicationContext()).userLogin(user);
                                Intent i = new Intent(RegistrationActivity.this, PersonalDetailsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                Toast.makeText(RegistrationActivity.this, "Successfully Registered...", Toast.LENGTH_SHORT).show();
                            }else if (response.equalsIgnoreCase("Customer exist")) {
                                Toast.makeText(RegistrationActivity.this, "Customer Already Registered", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegistrationActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", Name);
                    map.put("mobile", Contact);
                    map.put("connectionFor", selectConnectionType.toLowerCase());
                    map.put("loginPin", FourDigit);
                    map.put("queryType", "Registration");
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
            requestQueue.add(stringRequest);

        }

    }

    public void userLogin(View view) {
        startActivity(new Intent(getApplicationContext(), AuthBSNLActivity.class));
    }
}