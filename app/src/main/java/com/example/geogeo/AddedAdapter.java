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

public class AddedAdapter extends RecyclerView.Adapter<AddedAdapter.ViewHolder>{
    private int staticTag=0;
    private Context context;
    private  final LayoutInflater inflater;
    private final List<AddedCity> cities;
    private  static boolean timeforselect=false;
    private ArrayList<Boolean> checkarrayvis=new ArrayList<Boolean>();
    private ArrayList<Boolean> checkarraycheck=new ArrayList<Boolean>();


    AddedAdapter(Context context, List<AddedCity> cities) {
        this.context=context;
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
        //view.setTag(staticTag);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!AddedAdapter.getTimeforSelect()) {
                    AddedAdapter.setTimeforselect(true);
                    RecyclerView recyclerView = (RecyclerView) v.getParent();
                    setCheckarraycheckByPos(recyclerView.getChildAdapterPosition(v),true);
                    for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                        checkarrayvis.set(i,true);
                        notifyItemChanged(i);
                    }
                    LinearLayout linearLayout=(LinearLayout) view.getRootView().findViewById(R.id.celldeleteadded);
                    linearLayout.setVisibility(View.VISIBLE);
                    Button button=(Button) view.getRootView().findViewById(R.id.exit);
                    button.setBackground(ContextCompat.getDrawable(view.getRootView().getContext(),R.drawable.baseline_clear_24));
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
