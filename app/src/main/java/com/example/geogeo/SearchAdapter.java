package com.example.geogeo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView NameCityView, NameCountryView;

        ViewHolder(View view){
            super(view);
            NameCityView = (TextView) view.findViewById(R.id.NameCity);
            NameCountryView = (TextView) view.findViewById(R.id.NameCountry);
        }
    }
}
