package com.umashankar.kiratbroadbanduser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import com.umashankar.kiratbroadbanduser.AdapterClass.ReportAdapter;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Authentication;
import com.umashankar.kiratbroadbanduser.ModelClass.Report;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAllReportActivity extends AppCompatActivity {

    ListView reportListView;
    public static ArrayList<Report> reportArrayList = new ArrayList<>();
    ReportAdapter reportAdapter;
    Report report;
    TextView complaintRefresh;

    Authentication user;
    ProgressDialog pd;
    String intentView="", selectedRB="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_report);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.all_report_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users Complaint");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intentView = getIntent().getStringExtra("reportView");

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait.. ");
        pd.setCanceledOnTouchOutside(false);

        complaintRefresh = findViewById(R.id.complaintRefresh);
        reportListView = findViewById(R.id.report_ListView);
        user = SharedPrefUserLogin.getInstance(this).getUser();


        complaintRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllUserReport();
            }
        });

        getAllUserReport();
        reportAdapter = new ReportAdapter(this, reportArrayList);
        reportListView.setAdapter(reportAdapter);

    }

    private void getAllUserReport() {
        Log.i("UserReport","Getting details for id:"+user.getId());
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_USER_REPORT_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reportArrayList.clear();
                        Log.i("userReport", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("UserReportDetails");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    int Id = object.getInt("reportId");
                                    String status = object.getString("reportStatus");
                                    String name = object.getString("reportName");
                                    String date = object.getString("reportDate");
                                    String time = object.getString("reportTime");
                                    String reason = object.getString("reportReason");
                                    if(!status.equalsIgnoreCase("Pending")) {
                                        String reportAssignTo = object.getString("name");
                                        String assignUserContact = object.getString("contact");
                                        String resolveDate = object.getString("resolveDate");
                                        String resolveTime = object.getString("resolveTime");
                                        String assignUserId = object.getString("reportAssignId");
                                        String resolveReason = object.getString("resolveReason");
                                        String inProgressReason = object.getString("inProgressReason");
                                        report = new Report(Id, status, name, date, time, reason, assignUserId, reportAssignTo, assignUserContact, resolveDate, resolveTime, resolveReason, inProgressReason);
                                    }else {
                                        report = new Report(Id, status, name, date, time, reason);
                                    }
                                    reportArrayList.add(report);
                                    reportAdapter.notifyDataSetChanged();
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
                map.put("customerId",""+user.getCustomerLandline());
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(ViewAllReportActivity.this);
        rQeue.add(request);

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