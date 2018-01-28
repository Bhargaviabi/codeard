package com.jansen.sander.arduinorgb;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sander on 20/12/2017.
 */

public class CustomColorDataAdapter extends RecyclerView.Adapter<CustomColorDataAdapter.CustomColorViewHolder> {

    private  List<CustomColor> customColors;

    public class CustomColorViewHolder extends RecyclerView.ViewHolder{
        private TextView title, color, colorbar;

        public CustomColorViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rgb_color_title);
            color = itemView.findViewById(R.id.custom_red);
            colorbar = itemView.findViewById(R.id.color_bar);
        }
    }

    public CustomColorDataAdapter(List<CustomColor> customColors){
        this.customColors = customColors;
    }

    public List<CustomColor> getCustomColors(){
        return customColors;
    }

    @Override
    public CustomColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_row, parent, false);
        return new CustomColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomColorViewHolder holder, int position) {
        CustomColor customColor = customColors.get(position);
        holder.title.setText(R.string.rgbColor);
        //Log.d("COLOR", String.format(getResources().getString(R.string.color_value), red, green, blue));
        holder.color.setText(String.format(MainActivity.getContext().getResources().getString(R.string.rgbValues),customColor.getRed(),customColor.getGreen(), customColor.getBlue()));
        holder.colorbar.setBackgroundColor(Color.rgb(customColor.getRed(), customColor.getGreen(), customColor.getBlue()));

    }

    @Override
    public int getItemCount() {
        return customColors.size();
    }

    public int getCid(int position){
        CustomColor customColor = customColors.get(position);
        return customColor.getCid();
    }
}
