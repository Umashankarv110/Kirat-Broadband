package com.umashankar.kiratbroadbanduser.ui.home;

import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.textfield.TextInputLayout;
import com.umashankar.kiratbroadbanduser.AdapterClass.NotificationAdapter;
import com.umashankar.kiratbroadbanduser.ContactUsActivity;
import com.umashankar.kiratbroadbanduser.HelpActivity;
import com.umashankar.kiratbroadbanduser.HelperClass.PhpLink;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;
import com.umashankar.kiratbroadbanduser.ModelClass.Notifications;
import com.umashankar.kiratbroadbanduser.R;
import com.umashankar.kiratbroadbanduser.SharedPreferencesClass.SharedPrefUserLogin;
import com.umashankar.kiratbroadbanduser.ViewAllReportActivity;
import com.umashankar.kiratbroadbanduser.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ConstraintLayout clRaiseComplain, clComplainStatus, clContactUs, clHelp;

    Customers user;
    TextView text_home;
    String selectedRB="",selectedRB1="",selectedRB2="", complaintText="", radioButtonText="", currentDate="", currentTime="";
    private ProgressDialog progressDialog;
    Dialog dialog, dialogNo, dialogConfirmation;

    String detailMsg1="", detailMsg2="", oltIp="";

    Customers customers;
    String customerId;
    public static ArrayList<Notifications> notificationArrayList = new ArrayList<>();
    NotificationAdapter notificationAdapter;
    Notifications notification;

    NotificationManagerCompat notificationManagerCompat;
    Notification noti;
    Dialog d1;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        user = SharedPrefUserLogin.getInstance(getActivity()).getUser();

        customers = SharedPrefUserLogin.getInstance(getActivity()).getUser();
        customerId = String.valueOf(customers.getLandline());

        text_home = root.findViewById(R.id.text_home);
        text_home.setText("Welcome "+ user.getName());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        currentTime = time.format(calendar.getTime());

        d1 = new Dialog(getActivity());
        d1.setContentView(R.layout.layout_msg);
        d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        d1.setCancelable(false);


        clRaiseComplain = root.findViewById(R.id.cl_RaiseComplain);
        clComplainStatus = root.findViewById(R.id.cl_ComplainStatus);
        clContactUs = root.findViewById(R.id.cl_ContactUs);
        clHelp = root.findViewById(R.id.cl_Help);

        clRaiseComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                getDetails();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_CStatus,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("StatusResponse", response);
                                progressDialog.dismiss();
                                if (response.equalsIgnoreCase("Not Resolved")){
                                    //null
                                    ComplaintAction("Not Resolved");
                                }else {
                                    ComplaintAction("Resolved");
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("customerId", customerId);
                        return map;
                    }

                };
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);

            }
        });
        clComplainStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ViewAllReportActivity.class);
                i.putExtra("reportView", "userView");
                startActivity(i);
            }
        });
        clContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
            }
        });
        clHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });


        return root;
    }


    private void getDetails() {
        Log.i("UserReport","Getting.........."+user.getCustomerId());
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, PhpLink.URL_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("userReport", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")) {
                                JSONArray dataArray = obj.getJSONArray("CustomersDetails");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject object = dataArray.getJSONObject(i);
                                    String oltipid = object.getString("oltipid");
                                    String alias = object.getString("alias");
                                    oltIp = object.getString("oltIp");
                                    Log.i("OLTI>>",oltIp);
                                    progressDialog.dismiss();
                                }
                            } else if (obj.optString("status").equals("false")) {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                String message = null;
                if (volleyError instanceof NetworkError || volleyError instanceof AuthFailureError || volleyError instanceof NoConnectionError || volleyError instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again later";
                    Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("customerId",""+user.getLandline());
                return map;
            }

        };
        RequestQueue rQeue = Volley.newRequestQueue(getActivity());
        rQeue.add(request);

    }
    private void ComplaintAction(String statusResponse){

        if(statusResponse.equalsIgnoreCase("Not Resolved")) {
            Dialog d1 = new Dialog(getActivity());
            d1.setContentView(R.layout.layout_msg);
            d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            d1.setCancelable(true);
            TextView txtMsg1 = d1.findViewById(R.id.textMsgTitle);
            Button btnOk = d1.findViewById(R.id.msgOk);
            Button btnCancel = d1.findViewById(R.id.msgCancel);
            d1.show();
            btnCancel.setVisibility(View.GONE);

            txtMsg1.setText("Your Previous Raised Complaint Still In Progress.\nPlease Wait Till Resolved.\n\n" +
                    "(आपकी पिछली उठाई गई शिकायत अभी भी प्रगति पर है। \nकृपया समाधान होने तक प्रतीक्षा करें।)");

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d1.dismiss();
                }
            });
        }
        else {
            dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.layout_report);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.show();

            TextInputLayout reportUserName = dialog.findViewById(R.id.username);
            reportUserName.getEditText().setText("" + user.getName());

            Button buttonCancel = dialog.findViewById(R.id.reportCancel);
            Button buttonReport = dialog.findViewById(R.id.reportButton);
            RadioButton rbReportNo = dialog.findViewById(R.id.rbReportNo);
            RadioGroup groupOption = dialog.findViewById(R.id.radioGroupReport);

            groupOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    selectedRB = "" + radioButton.getText();

                    if (selectedRB.equalsIgnoreCase("Yes")) {
                        radioButtonText = "Red light Active";
                    }
                    else if (selectedRB.equalsIgnoreCase("No")) {
                        radioButtonText = "Red light Not Active";
                        dialogNo = new Dialog(getActivity());
                        dialogNo.setContentView(R.layout.layout_popup_msg);
                        dialogNo.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialogNo.setCancelable(false);
                        dialogNo.show();

                        TextView cb1 = dialogNo.findViewById(R.id.cb1);
                        TextView cb2 = dialogNo.findViewById(R.id.cb2);
                        CheckBox cb3 = (CheckBox) dialogNo.findViewById(R.id.cb3);
                        RadioGroup grp1 = dialogNo.findViewById(R.id.radioGroupOption1);
                        RadioGroup grp2 = dialogNo.findViewById(R.id.radioGroupOption2);
                        RadioButton rb1 = dialogNo.findViewById(R.id.rb1);
                        RadioButton rb2 = dialogNo.findViewById(R.id.rb2);
                        RadioButton rb3 = dialogNo.findViewById(R.id.rb3);
                        RadioButton rb4 = dialogNo.findViewById(R.id.rb4);
                        Button cancelBtn1 = dialogNo.findViewById(R.id.noBtn);
                        Button buttonComplaint = dialogNo.findViewById(R.id.mComplaint);

                        EditText et1 = dialogNo.findViewById(R.id.et_Reason);
                        TextView detail1 = dialogNo.findViewById(R.id.details1);
                        TextView detail2 = dialogNo.findViewById(R.id.details2);

                        cancelBtn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogNo.dismiss();
                                radioButtonText="no reason";
                                rbReportNo.setChecked(false);
                            }
                        });

                        buttonComplaint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                complaintText = et1.getText().toString().trim();
                                if (cb3.isChecked() && complaintText.equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), "Enter Complaint Details", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("OLTI_________",oltIp);
                                    String details = radioButtonText + ": " + detailMsg1 + "\n" + detailMsg2 + "\nReason :" + complaintText;
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("Are you sure you want to Complaint?\n(क्या आप वाकई शिकायत करना चाहते हैं?)")
                                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog1, int which) {
                                            progressDialog.show();
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_INSERT_USER_REPORT,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.i("JoiningUserResponce", response);
                                                            progressDialog.dismiss();
                                                            dialog.dismiss();
                                                            dialogNo.dismiss();

                                                            adminNotification();
                                                            Dialog d1 = new Dialog(getActivity());
                                                            d1.setContentView(R.layout.layout_msg);
                                                            d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                            d1.setCancelable(true);
                                                            TextView txtMsg1 = d1.findViewById(R.id.textMsgTitle);
                                                            Button btnOk = d1.findViewById(R.id.msgOk);
                                                            Button btnCancel = d1.findViewById(R.id.msgCancel);
                                                            d1.show();
                                                            btnCancel.setVisibility(View.GONE);

                                                            txtMsg1.setText("Your complaint has been registered. We are trying to solve as soon as possible\n" +
                                                                    "\n(आपकी शिकायत दर्ज कर ली गई है। हम जल्द से जल्द समाधान करने की कोशिश कर रहे हैं)");

                                                            btnOk.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    d1.dismiss();
                                                                    radioButtonText = "";
                                                                }
                                                            });

                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                                                    Log.i("Error", error.toString());
                                                }
                                            }) {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                    Map<String, String> map = new HashMap<>();
                                                    map.put("reportStatus", "Pending");
                                                    map.put("customerId", customerId);
                                                    map.put("reportName", "" + user.getName()+" | "+user.getMobile());
                                                    map.put("reportDate", currentDate);
                                                    map.put("reportTime", currentTime);
                                                    map.put("reportReason", details);
                                                    map.put("oltIp", oltIp);
                                                    return map;
                                                }

                                            };
                                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                            requestQueue.add(stringRequest);

                                        }
                                    }).setNegativeButton("No", null).show();
                                }

                            }
                        });

                        grp1.setVisibility(View.VISIBLE);
                        grp1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                                selectedRB1 = "" + radioButton.getText();

                                dialogConfirmation = new Dialog(getActivity());
                                dialogConfirmation.setContentView(R.layout.layout_msg);
                                dialogConfirmation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialogConfirmation.setCancelable(true);
                                TextView txtMsg1 = dialogConfirmation.findViewById(R.id.textMsgTitle);
                                Button btnOk = dialogConfirmation.findViewById(R.id.msgOk);
                                Button btnCancel = dialogConfirmation.findViewById(R.id.msgCancel);

                                if (selectedRB1.equalsIgnoreCase("Yes")) {
                                    cb2.setVisibility(View.VISIBLE);
                                    detail1.setVisibility(View.GONE);
                                    detailMsg1 = "Last Month Bill Paid: Yes";
                                    detail1.setText(detailMsg1);

                                    grp2.setVisibility(View.VISIBLE);
                                    grp2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                                            selectedRB2 = "" + radioButton.getText();
                                            if (selectedRB2.equalsIgnoreCase("Yes")) {
                                                dialogConfirmation.show();
                                                btnCancel.setVisibility(View.GONE);
                                                txtMsg1.setText("BSNL Link is Down, Please Wait for sometime \n Or \nKindly Restart Your Device" +
                                                        "\n\n(बीएसएनएल लिंक डाउन है, कृपया कुछ देर प्रतीक्षा करें\n या \nकृपया अपने डिवाइस को पुनः आरंभ करें)");
                                                btnOk.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogConfirmation.dismiss();
                                                        cb3.setVisibility(View.VISIBLE);
                                                        detail2.setVisibility(View.GONE);
                                                        detailMsg2 = "Power & PON Light Green: Yes";
                                                        detail2.setText(detailMsg2);
                                                        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                if (isChecked) {
                                                                    et1.setVisibility(View.VISIBLE);
                                                                    buttonComplaint.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    et1.setVisibility(View.GONE);
                                                                    buttonComplaint.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            } else if (selectedRB2.equalsIgnoreCase("No")) {
                                                btnCancel.setVisibility(View.GONE);
                                                dialogConfirmation.show();
                                                txtMsg1.setText("Check Adaptor is giving Power or Not \n\n(चेक एडॉप्टर पावर दे रहा है या नहीं)");
                                                btnOk.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogConfirmation.dismiss();
                                                        cb3.setVisibility(View.VISIBLE);
                                                        detail2.setVisibility(View.GONE);
                                                        detailMsg2 = "Power & PON Light Green: No";
                                                        detail2.setText(detailMsg2);
                                                        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                if (isChecked) {
                                                                    et1.setVisibility(View.VISIBLE);
                                                                    buttonComplaint.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    et1.setVisibility(View.GONE);
                                                                    buttonComplaint.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });

                                } else if (selectedRB1.equalsIgnoreCase("No")) {
                                    btnCancel.setVisibility(View.GONE);

                                    detail1.setVisibility(View.GONE);
                                    detailMsg1 = "";
                                    detail1.setText(detailMsg1);

                                    rb3.setChecked(false);
                                    rb4.setChecked(false);

                                    cb2.setVisibility(View.GONE);
                                    cb2.setVisibility(View.GONE);
                                    et1.setVisibility(View.GONE);
                                    buttonComplaint.setVisibility(View.GONE);
                                    et1.setText("");
                                    grp2.setVisibility(View.GONE);

                                    dialogConfirmation.show();
                                    txtMsg1.setText("Kindly Pay the Bill.\n\nPayments using:\nPhonePay/Paytm/Google Pay to 9896389883\n" +
                                            "\nकृपया बिल का भुगतान करें।\nभुगतान के लिए: फोनपे/पेटीएम/गूगलपे 9896389883 का उपयोग करे");
                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirmation.dismiss();
                                            dialogNo.dismiss();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            });

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendReport();
                }
            });

        }
    }
    private void adminNotification() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_PushNotification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("pushNotify", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("title", "New complaint");
                map.put("message", "New complaint raised, Please check it now");
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
    private void SendReport() {
        if (radioButtonText.equalsIgnoreCase("")){
            Toast.makeText(getActivity(), "Select Complaint Option", Toast.LENGTH_SHORT).show();
        } else if (radioButtonText.equalsIgnoreCase("no reason")){
            Toast.makeText(getActivity(), "Select Complaint Reason", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            Log.i("OLTI_________",oltIp);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpLink.URL_INSERT_USER_REPORT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("JoiningUserResponce", response);
                            progressDialog.dismiss();
                            dialog.dismiss();
                            adminNotification();

                            Dialog d1 = new Dialog(getActivity());
                            d1.setContentView(R.layout.layout_msg);
                            d1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            d1.setCancelable(true);
                            TextView txtMsg1 = d1.findViewById(R.id.textMsgTitle);
                            Button btnOk = d1.findViewById(R.id.msgOk);
                            Button btnCancel = d1.findViewById(R.id.msgCancel);
                            d1.show();
                            btnCancel.setVisibility(View.GONE);

                            txtMsg1.setText("Your complaint has been registered. We are trying to solve as soon as possible\n" +
                                    "(आपकी शिकायत दर्ज कर ली गई है। हम जल्द से जल्द समाधान करने की कोशिश कर रहे हैं)");

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d1.dismiss();
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Error", error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("reportStatus", "Pending");
                    map.put("customerId", customerId);
                    map.put("reportName", "" + user.getName()+" | "+user.getMobile());
                    map.put("reportDate", currentDate);
                    map.put("reportTime", currentTime);
                    map.put("reportReason", radioButtonText);
                    map.put("oltIp", oltIp);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}