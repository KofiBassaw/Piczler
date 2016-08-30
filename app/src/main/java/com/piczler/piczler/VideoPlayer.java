package com.piczler.piczler;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

/**
 * Created by matiyas on 6/18/16.
 */
public class VideoPlayer extends AppCompatActivity {
    String TAG = "com.piczler.piczler.videoplayer";
    ProgressBar pbBar;
    ImageView ivImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        ivImage = (ImageView) findViewById(R.id.ivImage);


        String url = getIntent().getStringExtra(StaticVariables.IMAGES);
        AQuery aq = new AQuery(this);
        ImageOptions op=new ImageOptions();
        op.fileCache = true;
        op.memCache=true;
        op.targetWidth = 2000;

        aq.id(ivImage).image(url, op);

        final VideoView videoView =
                (VideoView) findViewById(R.id.videoView1);

        videoView.setVideoPath(
                getIntent().getStringExtra(StaticVariables.URL));



        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(new
                                                MediaPlayer.OnPreparedListener() {
                                                    @Override
                                                    public void onPrepared(MediaPlayer mp) {
                                                        Log.i(TAG, "Duration = " +
                                                                videoView.getDuration());
                                                        pbBar.setVisibility(View.GONE);
                                                        ivImage.setVisibility(View.GONE);

                                                    }
                                                });
        videoView.start();
    }
}
