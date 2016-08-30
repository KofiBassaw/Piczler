package com.piczler.piczler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by pk on 4/22/15.
 */
public class RecyclerItemViewHolderImages extends RecyclerView.ViewHolder {

    private final ImageView picture;
    private final ImageView ivCheck;
    private final TextView tvName;

    public RecyclerItemViewHolderImages(final View parent, ImageView picture,TextView tvName, ImageView ivCheck) {
        super(parent);
        this.picture = picture;
        this.ivCheck = ivCheck;
        this.tvName = tvName;
    }

    public static RecyclerItemViewHolderImages newInstance(View parent) {
        ImageView picture= (ImageView) parent.findViewById(R.id.ivImage);
        ImageView ivCheck= (ImageView) parent.findViewById(R.id.ivCheck);
        TextView tvName= (TextView) parent.findViewById(R.id.tvName);
        return new RecyclerItemViewHolderImages(parent,picture,tvName,ivCheck);
    }

    public void setItemText(GettersAndSetters item, int position, Context context) {


            ImageSatffs staffs=new ImageSatffs(80,80,item.filter,context);
            Bitmap bitmap=null;
            staffs.loadBitmap(item.imageUrl, picture, bitmap, "" + position);


        /*
         Bitmap bitmap = (Bitmap)Cache.getInstance(context).getLru().get(position+item.imageUrl);

        if(bitmap==null)
        {
            bitmap = decodeFile(item.imageUrl);
            if(position !=0)
            {
                bitmap =   StaticVariables.getFilter(position).renderImage(bitmap);
            }

            Cache.getInstance(context).getLru().put(position + item.imageUrl, bitmap);
        }
        picture.setImageBitmap(bitmap);
        */




        tvName.setText(item.name);






 if(item.showIcon)
 {
     ivCheck.setVisibility(View.VISIBLE);
 }else
 {
     ivCheck.setVisibility(View.GONE);
 }



    }




    public Bitmap decodeFile(String path) {
        try {
            // Decode deal_image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap  map = BitmapFactory.decodeFile(path, o2);
            int imageHeigt = map.getHeight();
            int imageWidth = map.getWidth();

            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
