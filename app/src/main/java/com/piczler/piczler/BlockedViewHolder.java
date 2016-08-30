package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

/**
 * Created by pk on 4/22/15.
 */
public class BlockedViewHolder extends RecyclerView.ViewHolder {


    private final ImageView ivPicture;
    private final ImageView ivOverFlow;
    private final TextView tvName;
    private final LinearLayout llMenu;
    private final RippleView rpMenu;




    public BlockedViewHolder(final View parent, ImageView ivPicture, TextView tvName, LinearLayout llMenu, RippleView rpMenu,ImageView ivOverFlow) {
        super(parent);
        this.ivPicture = ivPicture;
        this.tvName = tvName;
        this.llMenu = llMenu;
        this.rpMenu = rpMenu;
        this.ivOverFlow = ivOverFlow;

    }

    public static BlockedViewHolder newInstance(View parent) {
        ImageView ivPicture = (ImageView) parent.findViewById(R.id.ivProfile);
        ImageView ivOverFlow = (ImageView) parent.findViewById(R.id.ivOverFlow);
        TextView tvName = (TextView) parent.findViewById(R.id.tvName);
        LinearLayout llMenu= (LinearLayout) parent.findViewById(R.id.llMenu);
        RippleView rpMenu= (RippleView) parent.findViewById(R.id.rpMenu);
        return new BlockedViewHolder(parent,ivPicture,tvName,llMenu,rpMenu,ivOverFlow);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {

    //tvLike
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.placeholder);
            AQuery aq = new AQuery(context);
            ImageOptions op=new ImageOptions();
            op.fileCache = true;
            op.memCache=true;
            op.targetWidth = 0;
            op.preset = icon;
            //op.fallback = R.drawable.placeholder;
            aq.id(ivPicture).image(Details.cover, op);

        tvName.setText(Details.name);

        llMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                        PopupMenu menu=new PopupMenu(context,ivOverFlow);
                        menu.inflate(R.menu.unblockpopup);
                        final MenuItem unBlock=menu.getMenu().findItem(R.id.action_Unblock);

                        unBlock.setTitle("Unblock @ " + Details.name);
                        menu.show();

                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.action_Unblock:
                                        Intent it = new Intent(StaticVariables.UNBLOCK);
                                        it.putExtra("pos", position);
                                         context.sendBroadcast(it);
                                        break;
                                }

                                return false;
                            }
                        });



                    }
                });
            }
        });

    }

}
