package com.umashankar.kiratbroadbanduser;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;
import com.umashankar.kiratbroadbanduser.databinding.ActivityCustomerHomeBinding;

import java.util.HashMap;
import java.util.Map;

public class CustomerHomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCustomerHomeBinding binding;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    private ProgressDialog progressDialog;

    Customers customers;
    String customerId;
    private ConstraintLayout notifCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarCustomerHome.toolbar);
        binding.appBarCustomerHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareAppLink();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_status, R.id.nav_contactus)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        customers = SharedPrefUserLogin.getInstance(this).getUser();
        customerId = String.valueOf(customers.getLandline());

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        TextView navUserEmail = (TextView) headerView.findViewById(R.id.navUserEmail);
        navUsername.setText(customers.getName());
        navUserEmail.setText("Landline: "+customers.getLandline());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        //Popup Notification
        createNotificationChannel();
        getToken();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifyChannel";
            String description = "Receive Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    //Token
                    String token = task.getResult();
                    Log.i("tokens___", token);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_INSERT_FCM,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("tokenResponse", response);
                                    progressDialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(CustomerHomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Error", error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("customerId", customerId);
                            map.put("Token", token);
                            return map;
                        }

                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(CustomerHomeActivity.this);
                    requestQueue.add(stringRequest);

                }else{
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem item = menu.findItem(R.id.notify_menu);
        MenuItemCompat.setActionView(item, R.layout.badge_layout);
        notifCount = (ConstraintLayout)   MenuItemCompat.getActionView(item);

        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NotificationActivity.class);
                intent.putExtra("cartCount",String.valueOf("10"));
                intent.putExtra("msgId","");
                startActivity(intent);
            }
        });
        return true;
    }

    public void Profile(MenuItem item) {
        Intent intent = new Intent(CustomerHomeActivity.this, ProfileActivity.class);
        intent.putExtra("type", "RegUser");
        startActivity(intent);
    }

    public void ComplaintStatus(MenuItem item){
        Intent i = new Intent(CustomerHomeActivity.this, ViewAllReportActivity.class);
        i.putExtra("reportView", "userView");
        startActivity(i);
    }
    public void ContactUs(MenuItem item){
        startActivity(new Intent(CustomerHomeActivity.this, ContactUsActivity.class));
    }
    public void AboutUs(MenuItem item){
        startActivity(new Intent(CustomerHomeActivity.this, AboutUsActivity.class));
    }
    public void Help(MenuItem item){
        startActivity(new Intent(CustomerHomeActivity.this, HelpActivity.class));
    }
    public void Share(MenuItem item){
        ShareAppLink();
    }

    private void ShareAppLink() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.umashankar.kiratbroadbanduser";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share with"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public void Logout(MenuItem item){
        new AlertDialog.Builder(CustomerHomeActivity.this)
                .setMessage("Are you sure you want to Logout? \n(क्या आप लॉग आउट करना चाहते हैं?)")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPrefUserLogin.getInstance(getApplicationContext()).logout();
                Intent intent = new Intent(CustomerHomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("LOGOUT", true);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("No",null).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}