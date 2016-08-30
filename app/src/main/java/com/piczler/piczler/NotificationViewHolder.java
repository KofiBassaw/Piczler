package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pk on 4/22/15.
 */
public class NotificationViewHolder extends RecyclerView.ViewHolder {


    private final TextView tvName;




    public NotificationViewHolder(final View parent, TextView tvName) {
        super(parent);
        this.tvName = tvName;

    }

    public static NotificationViewHolder newInstance(View parent) {
        TextView tvName = (TextView) parent.findViewById(R.id.tvName);
        return new NotificationViewHolder(parent,tvName);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {



        tvName.setText(Details.name);






    }

}
