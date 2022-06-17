package com.umashankar.kiratbroadbanduser.AdapterClass;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umashankar.kiratbroadbanduser.ModelClass.Report;
import com.umashankar.kiratbroadbanduser.R;

import java.util.List;

public class ReportAdapter extends ArrayAdapter<Report> {
    Context context;
    List<Report> arrayListReport;

    public ReportAdapter(@NonNull Context context, List<Report> arrayListReport) {
        super(context, R.layout.layout_view_report, arrayListReport);
        this.context = context;
        this.arrayListReport = arrayListReport;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_report,null,true);
        TextView tv_report_id = view.findViewById(R.id.report_id);
        TextView tv_report_name = view.findViewById(R.id.report_name);
        TextView tv_report_status = view.findViewById(R.id.report_status);
        TextView tv_report_date = view.findViewById(R.id.report_date);
        TextView tv_report_time = view.findViewById(R.id.report_time);
        TextView tv_report_reason = view.findViewById(R.id.report_reason);
        TextView tv_report_datetime = view.findViewById(R.id.report_resolve_dt);
        TextView tv_report_assign = view.findViewById(R.id.report_assign);
        TextView textView2 = view.findViewById(R.id.textView2);
        TextView tv_resolveReason = view.findViewById(R.id.report_ResolveReason);
        TextView tvAlias = view.findViewById(R.id.tvAlias);

        if (arrayListReport.get(position).getReportStatus().equalsIgnoreCase("Pending")){
            tv_report_status.setTextColor(Color.RED);
            tv_report_assign.setVisibility(View.GONE);
            tv_report_datetime.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            tv_resolveReason.setVisibility(View.GONE);
        }if (arrayListReport.get(position).getReportStatus().equalsIgnoreCase("In Progress")){
            tv_report_status.setTextColor(Color.rgb(255,140,0));
            tv_report_assign.setVisibility(View.VISIBLE);
            tv_report_datetime.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            if (arrayListReport.get(position).getInProgressReason().equalsIgnoreCase("")){
                tv_resolveReason.setVisibility(View.GONE);
            }else{
                tv_resolveReason.setVisibility(View.VISIBLE);
                tv_resolveReason.setText("In-Progress Reason: "+arrayListReport.get(position).getInProgressReason());
            }
            if (arrayListReport.get(position).getReportAssignTo().equalsIgnoreCase("Unassigned")){
                tv_report_assign.setVisibility(View.GONE);
            }else{
                tv_report_assign.setVisibility(View.VISIBLE);
            }
        }if (arrayListReport.get(position).getReportStatus().equalsIgnoreCase("Resolved")){
            tv_report_status.setTextColor(Color.rgb(0,153,0));
            tv_report_assign.setVisibility(View.VISIBLE);
            tv_report_datetime.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            if (arrayListReport.get(position).getReportAssignTo().equalsIgnoreCase("Unassigned")){
                tv_report_assign.setVisibility(View.GONE);
            }else{
                tv_report_assign.setVisibility(View.VISIBLE);
            }
            if (arrayListReport.get(position).getResolveReason().equalsIgnoreCase("")){
                tv_resolveReason.setVisibility(View.GONE);
            }else{
                tv_resolveReason.setVisibility(View.VISIBLE);
                tv_resolveReason.setText("Resolve Reason: "+arrayListReport.get(position).getResolveReason());
            }
        }
        tv_report_id.setText("KB_"+arrayListReport.get(position).getReportId());
        tv_report_status.setText(""+arrayListReport.get(position).getReportStatus());
        tv_report_name.setText(": "+arrayListReport.get(position).getReportName());
        tv_report_date.setText(": "+arrayListReport.get(position).getReportDate());
        tv_report_time.setText(": "+arrayListReport.get(position).getReportTime());
        tv_report_reason.setText(": "+arrayListReport.get(position).getReportReason());
        tv_report_datetime.setText(arrayListReport.get(position).getResolveDate()+" | "+arrayListReport.get(position).getResolveTime());
        tv_report_assign.setText("Complaint Assigned to : "+arrayListReport.get(position).getReportAssignTo()+" | "+arrayListReport.get(position).getAssignUserContact());


        return view;
    }
}
