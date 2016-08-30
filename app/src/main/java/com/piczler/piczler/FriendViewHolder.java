package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

/**
 * Created by pk on 4/22/15.
 */
public class FriendViewHolder extends RecyclerView.ViewHolder {


    private final TextView tvName;
    private final TextView tvNumber;
    private final CheckBox cbChecked;




    public FriendViewHolder(final View parent, TextView tvNumber, TextView tvName, CheckBox cbChecked) {
        super(parent);
        this.tvNumber = tvNumber;
        this.tvName = tvName;
        this.cbChecked = cbChecked;

    }

    public static FriendViewHolder newInstance(View parent) {
        TextView tvName = (TextView) parent.findViewById(R.id.tvName);
        TextView tvNumber = (TextView) parent.findViewById(R.id.tvNumber);
        CheckBox cbChecked = (CheckBox) parent.findViewById(R.id.cbChecked);
        return new FriendViewHolder(parent,tvNumber,tvName,cbChecked);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {



        tvName.setText(Details.name);
        tvNumber.setText(Details.phoneNum);
        cbChecked.setChecked(Details.selected);



        cbChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(StaticVariables.FRIEND);
                it.putExtra("pos", position);
                it.putExtra("check", cbChecked.isChecked());
                context.sendBroadcast(it);
            }
        });






    }

}
