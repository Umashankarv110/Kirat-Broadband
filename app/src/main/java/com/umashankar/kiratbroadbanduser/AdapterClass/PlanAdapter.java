package com.umashankar.kiratbroadbanduser.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umashankar.kiratbroadbanduser.ModelClass.PlanDetails;
import com.umashankar.kiratbroadbanduser.R;

import java.util.List;

public class PlanAdapter  extends ArrayAdapter<PlanDetails> {
    Context context;
    List<PlanDetails> arrayListPlanDetails;

    public PlanAdapter(@NonNull Context context, List<PlanDetails> arrayListPlanDetails) {
        super(context, R.layout.layout_plan_details, arrayListPlanDetails);
        this.context = context;
        this.arrayListPlanDetails = arrayListPlanDetails;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_plan_details, null, true);
        TextView tvPlanName = view.findViewById(R.id.tvPlanName);
        TextView tvPlanValue = view.findViewById(R.id.tvPlanValue);
        TextView tv_gst = view.findViewById(R.id.tv_gst);
        TextView tvIncludeAmt = view.findViewById(R.id.tvIncludeAmt);
        TextView tv_finalAmt = view.findViewById(R.id.tv_finalAmt);
        TextView tvPlanDiscount = view.findViewById(R.id.tvPlanDiscount);
        TextView tvPayAmt = view.findViewById(R.id.tvPayAmt);
        TextView tvSecureAmt = view.findViewById(R.id.tvSecureAmt);
        TextView tvPlanSpeed = view.findViewById(R.id.tvPlanSpeed);

        tvPlanName.setText("Plan Name: "+arrayListPlanDetails.get(position).getPlanName());
        tvPlanValue.setText("Plan Amount: ₹"+arrayListPlanDetails.get(position).getFixedMonthlyCharges());
        tv_gst.setText(arrayListPlanDetails.get(position).getGst()+"% GST: ");
        tvIncludeAmt.setText("₹"+arrayListPlanDetails.get(position).getFinalAmount());
        tv_finalAmt.setText("₹"+arrayListPlanDetails.get(position).getFinalAmount());
        tvPlanDiscount.setText("(Discount ₹"+arrayListPlanDetails.get(position).getDiscount()+")");
        tvSecureAmt.setText("Security Deposit: ₹"+arrayListPlanDetails.get(position).getSecurityDeposit());
        tvPayAmt.setText("₹"+arrayListPlanDetails.get(position).getFinalAmountRound());
        tvPlanSpeed.setText("Plan Speed: "+arrayListPlanDetails.get(position).getBandwithDownloadSpeed());
        return view;
    }
}