package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;
import com.umashankar.kiratbroadbanduser.TempCustomer.DashboardTempUserActivity;

public class AuthOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_options);

        if (SharedPrefUserLogin.getInstance(this).isUserLoggedIn()) {
            Intent i = new Intent(AuthOptionsActivity.this, CustomerHomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }else if (SharedPrefTempUserLogin.getInstance(this).isUserLoggedIn()) {
            Intent i = new Intent(AuthOptionsActivity.this, DashboardTempUserActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }
    }

    public void bsnlAction(View view) {
        startActivity(new Intent(AuthOptionsActivity.this,AuthBSNLActivity.class));
    }

    public void railWireAction(View view) {
        startActivity(new Intent(AuthOptionsActivity.this,AuthRailWireActivity.class));
    }

    public void registeredAction(View view) {
        startActivity(new Intent(AuthOptionsActivity.this,AuthNewCustomerActivity.class));
    }
}