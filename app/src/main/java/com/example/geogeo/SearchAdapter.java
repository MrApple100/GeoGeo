package com.example.geogeo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private int staticTag=0;
    private  final LayoutInflater inflater;
    private final ArrayList<City> cities;

    SearchAdapter(Context context, ArrayList<City> cities) {
        this.cities = cities;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.onecityelement, parent, false);
        view.setTag(staticTag);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( SearchAdapter.ViewHolder holder, int position) {
        City city = cities.get(position);
        holder.NameCityView.setText(city.getNameCity());
        holder.NameCountryView.setText(city.getNameCountry());
        holder.Longitude.setText("Долгота :"+city.getLongitude());
        holder.Latitude.setText("Широта :"+city.getLatitude());
        holder.Add.setTag("{\"coord\":"+"{\"name\":\""+city.getNameCity()+"\",\"country\":\""+city.getNameCountry()+"\",\"lon\":\""+city.getLongitude()+"\",\"lat\":\""+city.getLatitude()+"\"}}");
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
