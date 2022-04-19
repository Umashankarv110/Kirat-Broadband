package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.umashankar.kiratbroadbanduser.AdapterClass.HelpAdapter;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Help;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelpActivity extends AppCompatActivity {

    ListView helpListView;
    public static ArrayList<Help> helpArrayList = new ArrayList<>();
    HelpAdapter helpAdapter;
    Help helpDetails;

    ImageView closeLayout;
    Dialog dialog;
    private ProgressDialog progressDialog;

    ListView moduleListView;
    public static ArrayList<Help> moduleArrayList = new ArrayList<>();
    HelpAdapter moduleAdapter;
    Help moduleDetails;
    String mQuestion="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.user_help_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        helpListView = findViewById(R.id.faqListView);
        getAllQuestion();
        helpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getAllModules(helpArrayList.get(i).getQuestionId());
                mQuestion = helpArrayList.get(i).getQuestion();
            }
        });


        dialog = new Dialog(HelpActivity.this);
        dialog.setContentView(R.layout.layout_module);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        closeLayout = dialog.findViewById(R.id.layoutClose);
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        moduleListView = dialog.findViewById(R.id.moduleListView);
        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HelpActivity.this, FAQ_AnswerActivity.class);
                intent.putExtra("questionId",""+moduleArrayList.get(i).getQuestionId());
                intent.putExtra("module",""+moduleArrayList.get(i).getQuestion());
                intent.putExtra("question",mQuestion);
                startActivity(intent);
                dialog.dismiss();
            }
        });


    }
    private void getAllModules(int questionId) {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_FAQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        moduleArrayList.clear();
                        Log.i("FaQModule", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("Module");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    int QID = object.getInt("questionId");
                                    String Module = object.getString("moduleName");

                                    if(!Module.equalsIgnoreCase("")){
                                        dialog.show();
                                        moduleDetails = new Help(QID,Module);
                                        moduleArrayList.add(moduleDetails);
                                        moduleAdapter = new HelpAdapter(HelpActivity.this, moduleArrayList);
                                        moduleListView.setAdapter(moduleAdapter);
                                        moduleAdapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }else{
                                        Intent intent = new Intent(HelpActivity.this, FAQ_AnswerActivity.class);
                                        intent.putExtra("questionId",""+QID);
                                        intent.putExtra("question",questionId);
                                        intent.putExtra("module",Module);
                                        startActivity(intent);
                                    }

                                }
                            } else if (obj.optString("status").equals("false")) {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
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
                map.put("queryType","Module");
                map.put("questionId",""+questionId);
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(HelpActivity.this);
        rQeue.add(request);

    }
    private void getAllQuestion() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_FAQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        helpArrayList.clear();
                        Log.i("FaQDetails", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("FaQDetails");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    int Id = object.getInt("id");
                                    String Question = object.getString("question");

                                    helpDetails = new Help(Id, Question);
                                    helpArrayList.add(helpDetails);
                                    helpAdapter = new HelpAdapter(HelpActivity.this, helpArrayList);
                                    helpListView.setAdapter(helpAdapter);
                                    helpAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            } else if (obj.optString("status").equals("false")) {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
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
                map.put("queryType","Questions");
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(HelpActivity.this);
        rQeue.add(request);

    }
}