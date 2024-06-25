package com.example.notespro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView day,date,month,title,content,time;
    ImageView options;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
       day=itemView.findViewById(R.id.my_day);
       date=itemView.findViewById(R.id.my_date);
       month=itemView.findViewById(R.id.my_month);
       title=itemView.findViewById(R.id.my_title);
       content=itemView.findViewById(R.id.my_content);
       time=itemView.findViewById(R.id.my_time);
       options=itemView.findViewById(R.id.options);
    }
}
