package com.umashankar.kiratbroadbanduser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FAQ_AnswerActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    String questionId,module, question, pdfUrl;
    TextView tvQuestion,tvSolution;
    LinearLayout tvPdf,layoutSolution;

    PDFView pdfView;
    Dialog dialog;
    ImageView closeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_answer);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.user_fnq_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        questionId =getIntent().getStringExtra("questionId");
        question =getIntent().getStringExtra("question");
        module =getIntent().getStringExtra("module");

        tvQuestion = findViewById(R.id.tvQuestion);
        tvSolution = findViewById(R.id.tvSolution);
        tvPdf = findViewById(R.id.tvPdf);
        layoutSolution = findViewById(R.id.layoutSolution);

        tvQuestion.setText("Q. "+question);

        layoutSolution.setVisibility(View.GONE);

        getFAQ();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_pdfview);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        pdfView = dialog.findViewById(R.id.pdfView);
        closeLayout = dialog.findViewById(R.id.layoutClose);
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                progressDialog.show();
                new RetrivePdfStream().execute(pdfUrl);
            }
        });
    }

    private void getFAQ() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_FAQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("FaQDetails", response);
                        if (response.equalsIgnoreCase("0")){
                            progressDialog.dismiss();
                            tvPdf.setVisibility(View.GONE);
                            Toast.makeText(FAQ_AnswerActivity.this, "Try Again....", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                JSONObject obj = new JSONObject(response);

                                Log.i("fileName",""+obj.getString("fileName"));

                                tvSolution.setText(""+obj.getString("answerDetails"));
                                if (obj.getString("fileName").equalsIgnoreCase("")){
                                    tvPdf.setVisibility(View.GONE);
                                }else {
                                    pdfUrl = "https://kiratcommunications.com/kiratbroadband/uploads/faq/" + obj.getString("fileName");
                                }

                                if (obj.getString("answerDetails").equalsIgnoreCase("")){
                                    layoutSolution.setVisibility(View.GONE);
                                }else{
                                    layoutSolution.setVisibility(View.VISIBLE);
                                }

                                progressDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                map.put("queryType","Answer");
                map.put("questionId",questionId);
                map.put("module",module);
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(FAQ_AnswerActivity.this);
        rQeue.add(request);

    }

    class RetrivePdfStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {

                // adding url
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // if url connection response code is 200 means ok the execute
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            // if error return null
            catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        // Here load the pdf and dismiss the dialog box
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressDialog.dismiss();
        }
    }
}