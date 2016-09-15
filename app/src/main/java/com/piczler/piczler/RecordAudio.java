package com.piczler.piczler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matiyas on 6/13/16.
 */
public class RecordAudio extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnCompletionListener {



    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    boolean isPlaying = true;
    boolean isRecording = true;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    ImageView ivCancel,ivAccept,ivRecord,ivPlay;
    RippleView rpPlay,rpRecord;
    RelativeLayout rlRecord,rlPlay;
    TextView tvTitle;
    Timer timer;

    int applicationStatus = 0;
    int counter = 0;
    private static final int SENDPOST = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/"+System.currentTimeMillis()+".3gp";
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivAccept = (ImageView) findViewById(R.id.ivAccept);
        ivRecord = (ImageView) findViewById(R.id.ivRecord);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        rpPlay = (RippleView) findViewById(R.id.rpPlay);
        rpRecord = (RippleView) findViewById(R.id.rpRecord);
        rlRecord = (RelativeLayout) findViewById(R.id.rlRecord);
        rlPlay = (RelativeLayout) findViewById(R.id.rlPlay);
        tvTitle = (TextView) findViewById(R.id.tvTitle);


        ivCancel.setOnClickListener(this);
        rlPlay.setOnClickListener(this);
        rlRecord.setOnClickListener(this);
        ivAccept.setOnClickListener(this);


        /*
        application status
        0 - new entry can quit when cancel is pressed
        1 - recording can quit when clicked
        2 - finished recording when clicked prepare unterface as new entry




         */
    }

    private void startTimer()
    {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                tvTitle.post(new Runnable() {
                                    @Override
                                    public void run() {
                                      //  tvTitle.setText(""+(((double)mPlayer.getCurrentPosition()/(double)mPlayer.getDuration())*100));
                                        String display = ""+counter;
                                        if(counter<10)
                                            display = "0"+counter;
                                        tvTitle.setText("00:"+display);
                                       // System.out.println((double)mPlayer.getCurrentPosition()/(double)mPlayer.getDuration());

                                        if(!isRecording && counter>29)
                                        {
                                            //stop here now
                                            swapRecordStatus();
                                        }
                                       counter++;

                                    }
                                });

                        }
                    });
                }
            }, 0, 1000);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.rlRecord:

                rpRecord.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        swapRecordStatus();
                    }
                });







                break;

            case R.id.rlPlay:
                rpPlay.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        onPlay(isPlaying);
                        if (isPlaying) {
                           // btnPlay.setText("Stop playing");
                            ivPlay.setImageResource(R.drawable.audio_pause);
                        } else {
                            //btnPlay.setText("Start playing");
                            ivPlay.setImageResource(R.drawable.audio_play);
                        }
                        isPlaying = !isPlaying;


                    }
                });



                break;
            case R.id.ivCancel:
                switch (applicationStatus)
                {
                    case 0:
                        stopPlaying();
                        onBackPressed();
                        break;
                    case 1:
                        stopPlaying();
                        onBackPressed();
                        break;
                    case 2:
                        isRecording = true;
                        isPlaying = true;
                        applicationStatus = 0;
                        stopPlaying();
                        //prepare the inetrface as new entry
                        ivRecord.setVisibility(View.VISIBLE);
                        rlRecord.setBackgroundResource(R.drawable.myovalrecord);
                        rpPlay.setVisibility(View.GONE);
                        ivAccept.setVisibility(View.GONE);
                        rpRecord.setVisibility(View.VISIBLE);
                        ivPlay.setImageResource(R.drawable.audio_play);
                        ivRecord.setImageResource(R.drawable.audio_record);
                        tvTitle.setText("tap to record");

                        break;
                }
                break;

            case R.id.ivAccept:
                stopPlaying();
                Intent it = new Intent(RecordAudio.this, Post.class);
                it.putExtra(StaticVariables.PROFILE_PICTURE, mFileName);
                it.putExtra(StaticVariables.TYPE, "audio");
                it.putExtra(StaticVariables.POSITION, 0);
                startActivityForResult(it, SENDPOST);
                break;
        }
    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }


    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            counter = 0;
            startTimer();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mPlayer.setOnCompletionListener(this);
    }

    private void stopPlaying() {
        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }

        if(mPlayer!=null)
        {
            mPlayer.release();
            mPlayer = null;
        }

    }



    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
      if(mRecorder!=null)
      {
          mRecorder.stop();
          mRecorder.release();
          mRecorder = null;
      }
    }



    @Override
    public void onCompletion(MediaPlayer arg0) {

        timer.cancel();
        timer.purge();
        ivPlay.setImageResource(R.drawable.audio_play);
        isPlaying =true;
    }


    @Override
    protected void onDestroy() {

        if(timer != null)
        {
            timer.cancel();
            timer.purge();
        }
        if(mRecorder != null)
        {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SENDPOST){
            Bundle busket = data.getExtras();
            Intent it = new Intent();
            it.putExtras(busket);
            setResult(RESULT_OK,it);
            finish();
        }
    }

    private void swapRecordStatus()
    {
        onRecord(isRecording);
        if (isRecording) {
            ivAccept.setVisibility(View.GONE);
            rpPlay.setVisibility(View.GONE);
            ivRecord.setVisibility(View.GONE);
            rpRecord.setVisibility(View.VISIBLE);
            applicationStatus = 1;
            //btnRecord.setText("Stop recording");
            rlRecord.setBackgroundResource(R.drawable.myovalstoprecord);
            tvTitle.setText("00:00");
            counter = 0;
            startTimer();
        } else {
            ivAccept.setVisibility(View.VISIBLE);
            rpRecord.setVisibility(View.GONE);
            rpPlay.setVisibility(View.VISIBLE);
            ivRecord.setVisibility(View.VISIBLE);
            applicationStatus = 2;
            // btnRecord.setText("Start recording");
            rlRecord.setBackgroundResource(R.drawable.myovalrecord);
            String display = ""+counter;
            if(counter<10)
                display="0"+counter;
            tvTitle.setText("00:"+display);
            timer.cancel();
            timer.purge();

        }
        isRecording = !isRecording;
    }
}
