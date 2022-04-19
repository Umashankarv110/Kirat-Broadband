package com.umashankar.kiratbroadbanduser.TempCustomer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.*;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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

import com.google.android.material.snackbar.Snackbar;
import com.umashankar.kiratbroadbanduser.AdapterClass.PlanAdapter;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Connection;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.ModelClass.Document;
import com.umashankar.kiratbroadbanduser.ModelClass.PlanDetails;
import com.umashankar.kiratbroadbanduser.R;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefTempUserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentCollectionActivity extends AppCompatActivity {

    private Spinner connectionSpinner, documentSpinner, documentSpinner2;
    private TextView customerName,planTextView,otherDocTextView,tv_pdfPath;
    private Customers customers;

    private ConstraintLayout otherDocLayout;

    //Connection
    private Connection connection;
    private List<Connection> connectionList;
    private ArrayAdapter<Connection> connectionAdapter;
    //Plan
    ListView planListView;
    public static ArrayList<PlanDetails> planArrayList = new ArrayList<>();
    PlanAdapter planAdapter;
    PlanDetails planDetails;
    //Document
    private Document document;
    private List<Document> documentList1,documentList2;
    private ArrayAdapter<Document> documentAdapter;
    private ImageView docImage,back_iv,front_iv;
    private Button docBtnSubmit, docBtn2;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PDF_REQUEST = 11;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private int imageFlag,customerId,selectedPlanId;
    private String mCurrentPhotoPath="", secureDeposit="";
    private String PdfPathb64img="",otherDoc_b64img="",aadharFront_b64img="",aadharBack_b64img="",selected="", docCollected="";
    private String connectionText="",aadharDocText="",otherDocText="", otherDocExtension="";
    private ConstraintLayout otherImageLinearLayout1;
    private ProgressDialog progressDialog;
    Dialog dialog;
    ImageView closeLayout;

    Uri path;
    private Bitmap bMap;

    RadioGroup radioGroupDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_collection);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.doc_collect_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Document Collection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");

        customerName = findViewById(R.id.tvCustomerName);
        docImage = findViewById(R.id.docImage2);
        docBtnSubmit= findViewById(R.id.doc_btn);
        connectionSpinner = findViewById(R.id.spinConnection);
        documentSpinner = findViewById(R.id.spinDocument);
        documentSpinner2 = findViewById(R.id.spinDocument2);
        planTextView= findViewById(R.id.spinReqPlan);
        front_iv= findViewById(R.id.front_iv);
        back_iv= findViewById(R.id.back_iv);
        otherDocLayout = findViewById(R.id.otherDocLayout);
        otherDocTextView= findViewById(R.id.otherDocText);
        otherImageLinearLayout1 = findViewById(R.id.otherImageLinearLayout1);
        tv_pdfPath = findViewById(R.id.tv_pdfPath);
        radioGroupDoc = findViewById(R.id.radioGroupDoc);


        radioGroupDoc.setVisibility(View.GONE);
        otherDocLayout.setVisibility(View.GONE);
        otherImageLinearLayout1.setVisibility(View.GONE);

        customers = SharedPrefTempUserLogin.getInstance(this).getUser();
        customerName.setText(customers.getName());

        //Connection Type
        connectionList = new ArrayList<>();
        retrieveConnection();
        connectionAdapter = new ArrayAdapter<Connection>(DocumentCollectionActivity.this, android.R.layout.simple_spinner_item, connectionList);
        connectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        connectionSpinner.setAdapter(connectionAdapter);
        connectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                connection = (Connection) parent.getSelectedItem();
                connectionText  = connection.getConnectionType();
                if (connectionText.equalsIgnoreCase("Business")){
                    otherDocTextView.setText("* Other Doc Type :");
                    otherDocLayout.setVisibility(View.VISIBLE);
                }else {
                    otherDocTextView.setText("Other Doc Type :");
                    otherDocLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Plans
        dialog = new Dialog(DocumentCollectionActivity.this);
        dialog.setContentView(R.layout.layout_all_plan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        planListView = dialog.findViewById(R.id.planListView);
        closeLayout = dialog.findViewById(R.id.layoutClose);
        getAllPlans();
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        planListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                planTextView.setText(planArrayList.get(i).getFinalAmountRound());
                selectedPlanId = planArrayList.get(i).getPlanId();
                secureDeposit = planArrayList.get(i).getSecurityDeposit();
                dialog.dismiss();
            }
        });

        //Document Type
        documentList1 = new ArrayList<>();
        document = new Document(0,"Aadhar");
        documentList1.add(document);
        documentAdapter = new ArrayAdapter<Document>(DocumentCollectionActivity.this, android.R.layout.simple_spinner_item, documentList1);
        documentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentSpinner.setAdapter(documentAdapter);
        documentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                document = (Document) parent.getSelectedItem();
                aadharDocText  = document.getDocType();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        documentList2 = new ArrayList<>();
        retrieveDocument1();
        documentAdapter = new ArrayAdapter<Document>(DocumentCollectionActivity.this, android.R.layout.simple_spinner_item, documentList2);
        documentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentSpinner2.setAdapter(documentAdapter);
        documentSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                document = (Document) parent.getSelectedItem();
                otherDocText  = document.getDocType();
                if(otherDocText.equalsIgnoreCase("Select Document")) {
                    otherImageLinearLayout1.setVisibility(View.GONE);
                    otherDoc_b64img ="";
                }else{
                    otherImageLinearLayout1.setVisibility(View.VISIBLE);
                    otherDocAction();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        documentSpinner2.setAdapter(documentAdapter);

        docBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String planText = planTextView.getText().toString();
                if (connectionText.equalsIgnoreCase("Select Connection Type")){
                    Toast.makeText(DocumentCollectionActivity.this, "Select Connection Type", Toast.LENGTH_SHORT).show();
                } else if (planText.equalsIgnoreCase("Select Plan")){
                    Toast.makeText(DocumentCollectionActivity.this, "Select Plan", Toast.LENGTH_SHORT).show();
                } else if (aadharDocText.equalsIgnoreCase("Aadhar") && aadharFront_b64img.equalsIgnoreCase("")){
                    Toast.makeText(DocumentCollectionActivity.this, "Capture Front Image", Toast.LENGTH_SHORT).show();
                } else if (aadharDocText.equalsIgnoreCase("Aadhar") && aadharBack_b64img.equalsIgnoreCase("")){
                    Toast.makeText(DocumentCollectionActivity.this, "Capture Back Image", Toast.LENGTH_SHORT).show();
                } else  if (connectionText.equalsIgnoreCase("Business") && otherDocText.equalsIgnoreCase("Select Document")){
                    Toast.makeText(DocumentCollectionActivity.this, "Select Other Document", Toast.LENGTH_SHORT).show();
                }else if (otherDocExtension.equalsIgnoreCase("img") && otherDocText.equalsIgnoreCase("Electricity Bill") && otherDoc_b64img.equalsIgnoreCase("")){
                    Toast.makeText(DocumentCollectionActivity.this, "Capture Other Document", Toast.LENGTH_SHORT).show();
                }else if (otherDocExtension.equalsIgnoreCase("img") && otherDocText.equalsIgnoreCase("GST Certificate") && otherDoc_b64img.equalsIgnoreCase("")){
                    Toast.makeText(DocumentCollectionActivity.this, "Capture Other Document", Toast.LENGTH_SHORT).show();
                }else if (otherDocExtension.equalsIgnoreCase("pdf") && otherDocText.equalsIgnoreCase("GST Certificate") && PdfPathb64img.equalsIgnoreCase("")){
                    Toast.makeText(DocumentCollectionActivity.this, "Select PDF Document", Toast.LENGTH_SHORT).show();
                } else{
                    Log.i("planText", planText);
                    Log.i("connectionText", connectionText);
                    Log.i("aFront", aadharFront_b64img.substring(0,50));
                    Log.i("aBack", aadharBack_b64img.substring(0,50));
                    Log.i("otherDocText", otherDocText);
                    Log.i("docCollected", docCollected);
                    Log.i("secureDeposit", secureDeposit);
                    if(otherDoc_b64img.length() != 0){
                        Log.i("otherDoc_b64img", otherDoc_b64img.substring(0,50));
                    }
                    if(PdfPathb64img.length() != 0){
                        Log.i("PdfPathb64img", PdfPathb64img.substring(0,50));
                    }
                    Log.i("otherDocExtension", otherDocExtension);

                    progressDialog.show();
                    StringRequest sr = new StringRequest(Request.Method.POST, PhpLink.URL_DOCS_COLLECT,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("InsertResponse:  ", response);
                                    if (response.contains("Inserted")) {
                                        progressDialog.dismiss();
                                        Dialog d1 = new Dialog(DocumentCollectionActivity.this);
                                        d1.setContentView(R.layout.layout_msg);
                                        d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        d1.setCancelable(false);
                                        TextView txtMsg1 = d1.findViewById(R.id.textMsgTitle);
                                        Button btnOk = d1.findViewById(R.id.msgOk);
                                        Button btnCancel = d1.findViewById(R.id.msgCancel);
                                        d1.show();
                                        btnCancel.setVisibility(View.GONE);

                                        txtMsg1.setText("Your Documents has been Submitted successfully. Kindly Proceed towards security payment of ₹"+secureDeposit+" via Paytm/PhonePe/Google Pay to 9896389883 or Deposit Cash.\n\n" +
                                                "आपके दस्तावेज़ सफलतापूर्वक सबमिट कर दिए गए हैं। कृपया पेटीएम/फोनपे/गूगल पे के माध्यम से 9896389883 या जमा नकद के माध्यम से ₹"+secureDeposit+" के सुरक्षा भुगतान के लिए आगे बढ़ें।");

                                        btnOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String stringNodeCopied= "9896389883";
                                                ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = android.content.ClipData.newPlainText("Copied", stringNodeCopied);
                                                clipboard.setPrimaryClip(clip);
                                                Toast.makeText(getBaseContext(), stringNodeCopied+"\nCopied to clipboard!", Toast.LENGTH_SHORT).show();
                                                d1.dismiss();
                                                startActivity(new Intent(getApplicationContext(), DashboardTempUserActivity.class));
                                                finish();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(DocumentCollectionActivity.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(DocumentCollectionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Error", error.toString());
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("customerId", "" + customers.getMobile());
                            map.put("connectionType", connectionText);
                            map.put("planId", "" + selectedPlanId);
                            map.put("documentType", aadharDocText);
                            map.put("aadharFront", aadharFront_b64img);
                            map.put("aadharBack", aadharBack_b64img);
                            map.put("documentType2", otherDocText);
                            map.put("docType2Img", otherDoc_b64img);
                            map.put("docType2Pdf", PdfPathb64img);
                            map.put("otherDocExtension", otherDocExtension);
                            return map;
                        }
                    };
                    RequestQueue rQeue = Volley.newRequestQueue(DocumentCollectionActivity.this);
                    rQeue.add(sr);

                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changePdf(View view) {
        otherDocAction();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickFrontImage(View view) {
        if(checkAndRequestPermissions()) {
            imageFlag = 1;
            if(checkAndRequestPermissions()) {
                CaptureImage();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickBackImage(View view) {
        if(checkAndRequestPermissions()) {
            imageFlag = 2;
            if(checkAndRequestPermissions()) {
                CaptureImage();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void otherDocAction() {
        if(otherDocText.equalsIgnoreCase("GST Certificate")) {
            CharSequence options[] = new CharSequence[]
                    {
                            "Click Image",
                            "Upload Pdf"
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(DocumentCollectionActivity.this);
            builder.setTitle("Document Options: ");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    if(i == 0){
                        imageFlag = 3;
                        otherDocExtension = "img";
                        if(checkAndRequestPermissions()) {
                            CaptureImage();
                        }
                    }if(i == 1){
                        otherDocExtension = "pdf";
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_REQUEST);
                    }
                }
            });
            builder.show();

        }else{
            imageFlag = 3;
            otherDocExtension = "img";
            if(checkAndRequestPermissions()) {
                CaptureImage();
            }
        }
    }

    private void retrieveConnection() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_REQ_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("ConnectionResponse",response);
                        connectionList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("true")){
                                JSONArray jsonArray = jsonObject.getJSONArray("Details");
                                for (int i=0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    int Id = object.getInt("connectionId");
                                    String Type = object.getString("connectionType");
                                    connection = new Connection(Id,Type);
                                    connectionList.add(connection);
                                    connectionAdapter.notifyDataSetChanged();
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
                Toast.makeText(DocumentCollectionActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("type","Connection");
                return map;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
    private void getAllPlans() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_REQ_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        planArrayList.clear();
                        Log.i("planDetails", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("Details");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    int Id = object.getInt("planId");
                                    String planName = object.getString("planName");
                                    String planAmt = object.getString("planAmt");
                                    String gst = object.getString("gst");
                                    String discount = object.getString("discount");
                                    String finalAmount = object.getString("finalAmount");
                                    String finalAmountRound = object.getString("finalAmountRound");
                                    String securityDeposit = object.getString("securityDeposit");
                                    String bSpeed = object.getString("bandwithDownloadSpeed");
                                    String fupSpeed = object.getString("fupSpeed");

                                    planDetails = new PlanDetails(Id, planName, planAmt, gst, discount, finalAmount, securityDeposit,finalAmountRound,bSpeed);
                                    planArrayList.add(planDetails);
                                    planAdapter = new PlanAdapter(DocumentCollectionActivity.this, planArrayList);
                                    planListView.setAdapter(planAdapter);
                                    planAdapter.notifyDataSetChanged();
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
                map.put("type","PlanDetails");
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(DocumentCollectionActivity.this);
        rQeue.add(request);

    }
    private void retrieveDocument1() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_REQ_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("DocResponse",response);
                        documentList2.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equals("true")){
                                JSONArray jsonArray = jsonObject.getJSONArray("Details");
                                for (int i=0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    int Id = object.getInt("docId");
                                    String Type = object.getString("docType");
                                    if (!Type.equalsIgnoreCase("Aadhar")){
                                        document = new Document(Id, Type);
                                        documentList2.add(document);
                                    }
                                    documentAdapter.notifyDataSetChanged();
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
                Toast.makeText(DocumentCollectionActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("type","Document");
                return map;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkAndRequestPermissions()){}
    }
    private boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }

    //CaptureImage
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CaptureImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
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
            if(imageFlag == 1) {
                front_iv.setImageBitmap(bMapRotate);
                aadharFront_b64img= Base64.encodeToString(b, Base64.DEFAULT);
                Log.i("Front", aadharFront_b64img);
            }
            if(imageFlag == 2) {
                back_iv.setImageBitmap(bMapRotate);
                aadharBack_b64img= Base64.encodeToString(b, Base64.DEFAULT);
                Log.i("Back", aadharBack_b64img);
            }
            if(imageFlag == 3) {
                docCollected = "Collected";
                docImage.setImageBitmap(bMapRotate);
                otherDoc_b64img= Base64.encodeToString(b, Base64.DEFAULT);
                Log.i("otherDoc", otherDoc_b64img);
                PdfPathb64img = "";
                docImage.setVisibility(View.VISIBLE);
                tv_pdfPath.setVisibility(View.GONE);
                otherImageLinearLayout1.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == PDF_REQUEST && resultCode == Activity.RESULT_OK){
            Log.i("ABBBBBBBC","Geeeeeeeeeeet "+data);
            Uri uri = data.getData();
            try {
                InputStream inputStream = DocumentCollectionActivity.this.getContentResolver().openInputStream(uri);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                PdfPathb64img = Base64.encodeToString(pdfInBytes,Base64.DEFAULT);

                otherDoc_b64img = "";
                tv_pdfPath.setVisibility(View.VISIBLE);
                tv_pdfPath.setText("PDF Selected");
                docImage.setVisibility(View.GONE);
                otherImageLinearLayout1.setVisibility(View.VISIBLE);
                docCollected = "Collected";
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("TAG", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                ||ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Snackbar.make(
                                    findViewById(R.id.docCollection),
                                    R.string.permission_rationale,
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            checkAndRequestPermissions();
                                        }
                                    })
                                    .show();
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Snackbar.make(
                                    findViewById(R.id.docCollection),
                                    R.string.permission_denied_explanation,
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.settings, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // Build intent that displays the App settings screen.
                                            Intent intent = new Intent();
                                            intent.setAction(
                                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package",
                                                    getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

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

    public void selectPlans(View view) {
        dialog.show();
    }

}