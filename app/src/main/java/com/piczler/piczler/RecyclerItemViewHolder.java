package com.piczler.piczler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.trncic.library.DottedProgressBar;

import org.json.JSONObject;

/**
 * Created by pk on 4/22/15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvName;
    private final TextView tvGreen;
    private final TextView tvTransparent;
    private final ImageView ivBackground;
    private final RelativeLayout rlTransparent;
    private final RelativeLayout rlGreen;
    private final RelativeLayout rlFollow;
    private final RelativeLayout rlmain;
    private final RippleView rpFollow;
    private final RippleView rpMain;
    private final DottedProgressBar progressBar;




    public RecyclerItemViewHolder(final View parent, TextView tvName, ImageView ivBackground,
                                  RelativeLayout rlTransparent,
                                  RelativeLayout rlGreen,
                                  RelativeLayout rlFollow,
                                  RippleView rpFollow,DottedProgressBar progressBar,TextView tvGreen,TextView tvTransparent,
                                  RippleView rpMain, RelativeLayout rlmain
    ) {
        super(parent);
        this.tvName = tvName;
        this.ivBackground = ivBackground;
        this.rlTransparent = rlTransparent;
        this.rlGreen = rlGreen;
        this.rlFollow = rlFollow;
        this.rpFollow = rpFollow;
        this.progressBar = progressBar;
        this.tvGreen = tvGreen;
        this.tvTransparent = tvTransparent;
        this.rpMain = rpMain;
        this.rlmain = rlmain;

    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView tvName = (TextView) parent.findViewById(R.id.tvName);
        TextView tvGreen = (TextView) parent.findViewById(R.id.tvGreen);
        TextView tvTransparent = (TextView) parent.findViewById(R.id.tvTransparent);
        ImageView ivBackground = (ImageView) parent.findViewById(R.id.ivBackground);
        RelativeLayout rlTransparent = (RelativeLayout) parent.findViewById(R.id.rlTransparent);
        RelativeLayout rlGreen = (RelativeLayout) parent.findViewById(R.id.rlGreen);
        RelativeLayout rlFollow = (RelativeLayout) parent.findViewById(R.id.rlFollow);
        RelativeLayout rlmain = (RelativeLayout) parent.findViewById(R.id.rlmain);
        RippleView rpFollow = (RippleView) parent.findViewById(R.id.rpFollow);
        RippleView rpMain = (RippleView) parent.findViewById(R.id.rpMain);
        DottedProgressBar progressBar = (DottedProgressBar) parent.findViewById(R.id.progress);
        return new RecyclerItemViewHolder(parent, tvName,ivBackground,rlTransparent,rlGreen,rlFollow,rpFollow,progressBar,tvGreen,tvTransparent,rpMain,rlmain);
    }

    public void setItemText(final GettersAndSetters Details, final Context context, final int position) {


        tvName.setText(Details.name.toUpperCase());
        AQuery aq = new AQuery(context);
        ImageOptions op=new ImageOptions();
        op.fileCache = true;
        op.memCache=true;
        op.targetWidth = 0;
        aq.id(ivBackground).image(Details.cover, op);

        if(Details.from == null){
            rpMain.setVisibility(View.GONE);
        }else{
            rpMain.setVisibility(View.VISIBLE);
        }
        if(Details.isFollowed()){
            rlTransparent.setVisibility(View.GONE);
            rlGreen.setVisibility(View.VISIBLE);
        }else{
            rlTransparent.setVisibility(View.VISIBLE);
            rlGreen.setVisibility(View.GONE);
        }
        if (Details.loading){
            progressBar.setVisibility(View.VISIBLE);
            tvGreen.setVisibility(View.GONE);
            tvTransparent.setVisibility(View.GONE);
            progressBar.startProgress();

            //progressBar.stopProgress();
        }else{
            progressBar.setVisibility(View.GONE);
            tvGreen.setVisibility(View.VISIBLE);
            tvTransparent.setVisibility(View.VISIBLE);
            progressBar.stopProgress();
        }

        rlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpFollow.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(StaticVariables.FOLLOWNOTIFY);
                        it.putExtra(StaticVariables.POSITION,position);

                        //progressBar.setVisibility(View.VISIBLE);
                        //tvGreen.setVisibility(View.GONE);
                       // tvTransparent.setVisibility(View.GONE);
                        //progressBar.startProgress();

                        if(Details.isFollowed()){
                          it.putExtra(StaticVariables.TYPE,StaticVariables.UNFOLLOW);
                        }else{
                            it.putExtra(StaticVariables.TYPE,StaticVariables.FOLLOW);
                        }
                        context.sendBroadcast(it);
                    }
                });
            }
        });


        rlmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rpMain.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        System.out.println("------------------------------- " + Details.id);
                        Intent it = new Intent(context,Pictures.class);
                        it.putExtra(StaticVariables.FROM,StaticVariables.FEED);
                        it.putExtra(StaticVariables.ID,String.valueOf(Details.id));
                        context.startActivity(it);



                        try
                        {
                            MixpanelAPI mixpanel =
                                    MixpanelAPI.getInstance(context, StaticVariables.MIXPANEL_TOKEN);
                            JSONObject onj = new JSONObject();
                            onj.put("Category",Details.name);
                                mixpanel.track("Viewed Category Feed",onj);
                        }catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

    }

}
