package com.example.geogeo;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
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

import java.util.ArrayList;
import java.util.List;

public class MyGeoPosAdapter extends RecyclerView.Adapter<MyGeoPosAdapter.ViewHolder>{
    private int staticTag=0;
    private Context context;
    private  final LayoutInflater inflater;
    private final MyGeoPosition myGeoPosition;


    MyGeoPosAdapter(Context context, MyGeoPosition myGeoPosition) {
        this.context=context;
        this.myGeoPosition=myGeoPosition;
        this.inflater = LayoutInflater.from(context);

    }




    @Override
    public MyGeoPosAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.oneaddedcityelement, parent, false) ;
        return new MyGeoPosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MyGeoPosAdapter.ViewHolder holder, int position) {
        holder.Myposition.setText("Погода рядом");
        holder.NameCountryView.setText("");
        holder.Longitude.setText("Долгота :"+myGeoPosition.lon);
        holder.Latitude.setText("Широта :"+myGeoPosition.lat);
        holder.Degree.setText(myGeoPosition.getDegree());
        holder.checkBox.setVisibility(View.GONE);

        holder.checkBox.setTag("{\"coord\":"+"{\"name\":\"Ятут\",\"lon\":\""+myGeoPosition.getLon()+"\",\"lat\":\""+myGeoPosition.getLat()+"\",\"degree\":\""+myGeoPosition.getDegree()+"\"}}");
        holder.itemView.setTag(-1);
        //меняю цвет в зависимости от темп
        if(Integer.parseInt(holder.Degree.getText()+"")>4){
            if(Integer.parseInt(holder.Degree.getText()+"")>15){
                holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.backoneaddedcity_warm));
            }else{
                holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.backoneaddedcity_neitral_warm));
            }
        }else{
            if(Integer.parseInt(holder.Degree.getText()+"")<-15){
                holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.backoneaddedcity_cold));
            }else{
                if(Integer.parseInt(holder.Degree.getText()+"")<-4) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.backoneaddedcity_neitral_cold));
                }else{
                    holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.backoneaddedcity_neitral));
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return 1;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Myposition, NameCountryView, Longitude, Latitude;
        final TextView Degree;
        final CheckBox checkBox;
        ViewHolder(View view){
            super(view);
            Myposition = (TextView) view.findViewById(R.id.NameCity);
            NameCountryView = (TextView) view.findViewById(R.id.NameCountry);
            Longitude = (TextView) view.findViewById(R.id.longitude);
            Latitude = (TextView) view.findViewById(R.id.latitude);
            Degree=(TextView) view.findViewById(R.id.currentdegree);
            checkBox=(CheckBox) view.findViewById(R.id.checkdelete);


        }
    }
}
