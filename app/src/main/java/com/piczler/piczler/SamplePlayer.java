package com.piczler.piczler;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by matiyas on 5/11/16.
 */
public class SamplePlayer extends AppCompatActivity {

    ImageView ivSample;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_player);
        ivSample = (ImageView) findViewById(R.id.ivSample);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.picvideo);

        Glide
                .with(this)
                .load(video)
                .into( ivSample );
    }
}
