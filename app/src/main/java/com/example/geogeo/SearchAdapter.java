package com.example.geogeo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static int staticTag=-1;
    private  final LayoutInflater inflater;
    private final ArrayList<City> cities;
    private final ArrayList<Integer> arrayTorFadded;

    SearchAdapter(Context context, ArrayList<City> cities,ArrayList<Integer> arrayTorFadded) {
        this.cities = cities;
        this.inflater = LayoutInflater.from(context);
        this.arrayTorFadded=arrayTorFadded;

    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.onecityelement, parent, false);
        staticTag++;
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( SearchAdapter.ViewHolder holder, int position) {
        City city = cities.get(position);
        holder.NameCityView.setText(city.getNameCity());
        holder.NameCountryView.setText(city.getNameCountry());
        holder.Longitude.setText("Долгота :"+city.getLongitude());
        holder.Latitude.setText("Широта :"+city.getLatitude());
        int idcity = (city.getLongitude() + city.getLatitude()).hashCode();
        Boolean TorF=false;
        for(int i=0;i<arrayTorFadded.size();i++){
            if(arrayTorFadded.get(i)==idcity) {
                TorF=true;
            }
        }
        if(TorF){
            holder.Add.setBackground(ContextCompat.getDrawable(holder.itemView.getRootView().getContext(),R.drawable.nullbackground));
        }
        holder.Add.setTag("{\"coord\":"+"{\"idtag\":\""+idcity+"\",\"name\":\""+city.getNameCity()+"\",\"country\":\""+city.getNameCountry()+"\",\"lon\":\""+city.getLongitude()+"\",\"lat\":\""+city.getLatitude()+"\"}}");
    }


    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView NameCityView, NameCountryView, Longitude, Latitude;
        final TextView Add;
        ViewHolder(View view){
            super(view);
            NameCityView = (TextView) view.findViewById(R.id.NameCity);
            NameCountryView = (TextView) view.findViewById(R.id.NameCountry);
            Longitude = (TextView) view.findViewById(R.id.longitude);
            Latitude = (TextView) view.findViewById(R.id.latitude);
            Add=(TextView) view.findViewById(R.id.added);
        }
    }
}
