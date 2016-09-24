package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

/**
 * Created by pk on 4/22/15.
 */
public class PostCatViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvName;




    public PostCatViewHolder(final View parent, TextView tvName
    ) {
        super(parent);
        this.tvName = tvName;

    }

    public static PostCatViewHolder newInstance(View parent) {
        TextView tvName = (TextView) parent.findViewById(R.id.tvTitle);
        return new PostCatViewHolder(parent, tvName);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {

        tvName.setText(Details.name);

    }

}
