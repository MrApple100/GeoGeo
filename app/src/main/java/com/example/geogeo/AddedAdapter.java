package com.example.geogeo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddedAdapter extends RecyclerView.Adapter<AddedAdapter.ViewHolder>{
    private int staticTag=0;
    private  final LayoutInflater inflater;
    private final List<AddedCity> cities;
    private  static boolean timeforselect=false;
    private ArrayList<Boolean> checkarrayvis=new ArrayList<Boolean>();
    private ArrayList<Boolean> checkarraycheck=new ArrayList<Boolean>();


    AddedAdapter(Context context, List<AddedCity> cities) {
        this.cities = cities;
        this.inflater = LayoutInflater.from(context);

        for(int i=0;i<cities.size();i++){
            checkarrayvis.add(false);
            checkarraycheck.add(false);
        }
    }

    public ArrayList<Boolean> getCheckarrayvis() {
        return checkarrayvis;
    }

    public void setCheckarrayvisByPos(int pos,Boolean state) {
        this.checkarrayvis.set(pos,state);
    }

    public ArrayList<Boolean> getCheckarraycheck() {
        return checkarraycheck;
    }

    public void setCheckarraycheckByPos(int pos,Boolean state) {
        this.checkarraycheck.set(pos,state);
    }

    @Override
    public AddedAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.oneaddedcityelement, parent, false) ;
        view.setTag(staticTag);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!AddedAdapter.getTimeforSelect()) {
                    AddedAdapter.setTimeforselect(true);
                    RecyclerView recyclerView = (RecyclerView) v.getParent();
                    ((ViewHolder) recyclerView.getChildViewHolder(v)).checkBox.setChecked(true);
                    int kolchildonview=6;
                    if(recyclerView.getAdapter().getItemCount()<6){
                        kolchildonview=recyclerView.getAdapter().getItemCount();
                    }
                    for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                        checkarrayvis.set(i,true);
                        notifyItemChanged(i);
                    }

                    return true;
                }
                else{

                    return false;
                }
            }
        });
        return new AddedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( AddedAdapter.ViewHolder holder, int position) {
        AddedCity city = cities.get(position);
        holder.NameCityView.setText(city.getNameCity());
        holder.NameCountryView.setText(city.getCountry());
        holder.Longitude.setText("Долгота :"+city.getLon());
        holder.Latitude.setText("Широта :"+city.getLat());
        holder.Degree.setText(city.degree);
        if(checkarrayvis.get(position)){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
        if(checkarraycheck.get(position)){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setTag("{\"coord\":"+"{\"idtag\":\""+city.getId()+"\",\"name\":\""+city.getNameCity()+"\",\"country\":\""+city.getCountry()+"\",\"lon\":\""+city.getLon()+"\",\"lat\":\""+city.getLat()+"\"}}");

    }


    @Override
    public int getItemCount() {
        return cities.size();
    }
    public static boolean getTimeforSelect(){
        return timeforselect;
    }
    public static void setTimeforselect(boolean state){
        timeforselect=state;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView NameCityView, NameCountryView, Longitude, Latitude;
        final TextView Degree;
        final CheckBox checkBox;
        ViewHolder(View view){
            super(view);
            NameCityView = (TextView) view.findViewById(R.id.NameCity);
            NameCountryView = (TextView) view.findViewById(R.id.NameCountry);
            Longitude = (TextView) view.findViewById(R.id.longitude);
            Latitude = (TextView) view.findViewById(R.id.latitude);
            Degree=(TextView) view.findViewById(R.id.currentdegree);
            checkBox=(CheckBox) view.findViewById(R.id.checkdelete);


        }
    }
}