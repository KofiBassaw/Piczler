package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.trncic.library.DottedProgressBar;

import org.json.JSONObject;

/**
 * Created by pk on 4/22/15.
 */
public class PictureViewHolder extends RecyclerView.ViewHolder {


    private final ImageView ivPicture;
    private final ImageView ivVideo;




    public PictureViewHolder(final View parent, ImageView ivPicture, ImageView ivVideo) {
        super(parent);
        this.ivPicture = ivPicture;
        this.ivVideo = ivVideo;

    }

    public static PictureViewHolder newInstance(View parent) {
        ImageView ivPicture = (ImageView) parent.findViewById(R.id.ivPicture);
        ImageView ivVideo = (ImageView) parent.findViewById(R.id.ivVideo);
        return new PictureViewHolder(parent,ivPicture,ivVideo);
    }

    public void setItemText(final GettersAndSetters Details, final Context context) {

    //tvLike



        if(Details.fileType == 1)
        {
            ivVideo.setVisibility(View.VISIBLE);
        }else if(Details.fileType == 2)
        {
            //audio. set the image to the default background
            ivVideo.setVisibility(View.GONE);
          //  odfjglkjdklfgjf
        }else if (Details.fileType == 0)
        {
            ivVideo.setVisibility(View.GONE);
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
