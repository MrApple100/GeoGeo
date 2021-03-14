package com.example.geogeo;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SimpleTimeZone;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder>{
    private int staticTag=0;
    private Context context;
    private  final LayoutInflater inflater;
    private final List<Day> days;


    DayAdapter(Context context, List<Day> days) {
        this.context=context;
        this.days = days;
        this.inflater = LayoutInflater.from(context);

    }



    @Override
    public DayAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.onedayelement, parent, false) ;
        view.setTag(staticTag);
        staticTag++;

        return new DayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( DayAdapter.ViewHolder holder, int position) {
        Day day = days.get(position);
        int weekday;
        String monthday;
        int month;
        SimpleDateFormat df;
        df = new SimpleDateFormat("dd.MM");
        String strDate = df.format(new Date(Long.parseLong(day.When)*1000));
        //monthday = new SimpleDateFormat("dd mm").format("1616317200");;
        month = new Time("1616317200").month;



        holder.WhenName.setText(strDate);
        holder.Wheather.setText(day.getWheather());
        holder.MinDegree.setText(day.getMinDegree());
        holder.MaxDegree.setText(day.getMaxDegree());

    }


    @Override
    public int getItemCount() {
        return days.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView WhenName,Wheather;
        final TextView MinDegree,MaxDegree;
        ViewHolder(View view){
            super(view);
            WhenName = (TextView) view.findViewById(R.id.WhenName);
            Wheather = (TextView) view.findViewById(R.id.wheather);
            MinDegree=(TextView) view.findViewById(R.id.mindegree);
            MaxDegree=(TextView) view.findViewById(R.id.maxdegree);


        }
    }
}
