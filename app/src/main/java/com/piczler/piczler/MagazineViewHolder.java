package com.piczler.piczler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

/**
 * Created by pk on 4/22/15.
 */
public class MagazineViewHolder extends RecyclerView.ViewHolder {


    private final ImageView ivPicture;
    private final CheckBox cbChecked;




    public MagazineViewHolder(final View parent, ImageView ivPicture, CheckBox cbChecked) {
        super(parent);
        this.ivPicture = ivPicture;
        this.cbChecked = cbChecked;

    }

    public static MagazineViewHolder newInstance(View parent) {
        ImageView ivPicture = (ImageView) parent.findViewById(R.id.ivPicture);
        CheckBox cbChecked = (CheckBox) parent.findViewById(R.id.cbChecked);
        return new MagazineViewHolder(parent,ivPicture,cbChecked);
    }

    public void setItemText(final GettersAndSetters Details, final Context context) {

    //tvLike
        cbChecked.setChecked(Details.selected);


        if(Details.fileType == 0)
        {
            cbChecked.setVisibility(View.VISIBLE);
        }else if(Details.fileType == 2)
        {
            //audio. set the image to the default background
            cbChecked.setVisibility(View.GONE);
          //  odfjglkjdklfgjf
        }else if (Details.fileType == 1)
        {
            cbChecked.setVisibility(View.GONE);
        }


        if(Details.fileType == 1 || Details.fileType == 0)
        {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.placeholder);
            AQuery aq = new AQuery(context);
            ImageOptions op=new ImageOptions();
            op.fileCache = true;
            op.memCache=true;
            op.targetWidth = 0;
            op.preset = icon;
            op.fallback = R.drawable.placeholder;
            aq.id(ivPicture).image(Details.cover, op);
        }

    }

}
