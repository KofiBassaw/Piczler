package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by pk on 4/22/15.
 */
public class LocationViewHolder extends RecyclerView.ViewHolder {


    private final TextView tvName;
    private final SwitchCompat cbChecked;




    public LocationViewHolder(final View parent, TextView tvName, SwitchCompat cbChecked) {
        super(parent);
        this.tvName = tvName;
        this.cbChecked = cbChecked;

    }

    public static LocationViewHolder newInstance(View parent) {
        TextView tvName = (TextView) parent.findViewById(R.id.tvName);
        SwitchCompat cbChecked = (SwitchCompat) parent.findViewById(R.id.switch_compat);
        return new LocationViewHolder(parent,tvName,cbChecked);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {



        tvName.setText(Details.name);
        cbChecked.setChecked(Details.selected);



        cbChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(StaticVariables.COUNTRIES);
                it.putExtra("pos", position);
                it.putExtra("check", cbChecked.isChecked());
                context.sendBroadcast(it);
            }
        });






    }

}
