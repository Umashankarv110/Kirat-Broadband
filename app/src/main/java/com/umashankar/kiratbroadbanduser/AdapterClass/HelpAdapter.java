package com.umashankar.kiratbroadbanduser.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.umashankar.kiratbroadbanduser.ModelClass.Help;
import com.umashankar.kiratbroadbanduser.R;

import java.util.List;

public class HelpAdapter extends ArrayAdapter<Help> {
    Context context;
    List<Help> arrayListHelp;

    public HelpAdapter(@NonNull Context context, List<Help> arrayListHelp) {
        super(context, R.layout.layout_faq, arrayListHelp);
        this.context = context;
        this.arrayListHelp = arrayListHelp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_faq, null, true);
        TextView tv_index = view.findViewById(R.id.questionIndex);
        TextView tv_Title = view.findViewById(R.id.questionTitle);

        int serialNo = position+1;
        tv_index.setText(""+serialNo);
        tv_Title.setText(""+arrayListHelp.get(position).getQuestion());
        return view;
    }
}