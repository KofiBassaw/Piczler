package com.piczler.piczler;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andexert.library.RippleView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

   // VideoView videoHolder;
   MySurfaceView surfaceViewFrame;
    boolean slide=true;
    boolean mainslide=true;
    boolean direction=false;
    int counter = 0;
    ImageView[] images = new ImageView[4];
    RelativeLayout rlStart;
    RippleView rpStart;
    MediaPlayer player;
    private SurfaceHolder holder;
    boolean hasActiveHolder = false;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =1;
    boolean slideDown = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlStart = (RelativeLayout) findViewById(R.id.rlStart);
        bindData();
        rpStart = (RippleView) findViewById(R.id.rpStart);
        images[0] = (ImageView) findViewById(R.id.ivOne);
        images[1] = (ImageView) findViewById(R.id.ivTwo);
        images[2] = (ImageView) findViewById(R.id.ivThree);
        images[3] = (ImageView) findViewById(R.id.ivFour);

        try{
            surfaceViewFrame = (MySurfaceView) findViewById(R.id.vvSplashVideo);
            // setContentView(videoHolder);
            player = new MediaPlayer();
            holder = surfaceViewFrame.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            player = new MediaPlayer();
            player.setOnPreparedListener(this);
           // player.setOnCompletionListener(this);

/*
            DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoHolder.getLayoutParams();
            params.width =  metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            videoHolder.setLayoutParams(params);

            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.picvideo);
            videoHolder.setVideoURI(video);

            videoHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {

                }

            });
            videoHolder.start();


            */

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

       // getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out).replace(R.id.content_frame, new SliderOne()).commit();

        rlStart.setOnClickListener(this);
       changeFragment(0);
        new timer().execute();



    }


    private void playVideo() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                            + R.raw.picvideo);
                    player.setDataSource(MainActivity.this,video);
                    //player.setDataSource(video);
                    player.prepare();
                   // player.start();
                } catch (Exception e) { // I can split the exceptions to get which error i need.
                   e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        slide =false;
        player.pause();
        super.onPause();
    }



    @Override
    protected void onResume() {
        slide = true;
        if(!player.isPlaying()){
            player.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mainslide = false;
        player.stop();
        player.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlStart:
                rpStart.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        startActivity(new Intent(MainActivity.this,SignIn.class));
                        finish();
                    }
                });
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        System.out.println("------------------------ prepared");
        mp.setLooping(true);
        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = surfaceViewFrame.getLayoutParams();

        System.out.println("+++++++++++++++++++++ 1 Screen width: " + screenWidth + " and Height: " + screenHeight);
        if (videoProportion > screenProportion) {
            System.out.println("+++++++++++++++++++++ 1 Video width: "+videoWidth+" and Height: "+videoHeight);
            lp.width = screenWidth * 2;
            lp.height = videoHeight*2;

        } else {
            System.out.println("+++++++++++++++++++++ 2");
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        surfaceViewFrame.setLayoutParams(lp);




        if (!player.isPlaying()) {
            System.out.println("--------------------------------- start to play");
            player.start();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.setScreenOnWhilePlaying(true);

        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }


    public static class SliderOne extends Fragment {

        TextView tvTitle, tvdescription;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.slider_layout, container, false);
            tvTitle= (TextView) theLayout.findViewById(R.id.tvTitle);
            tvdescription= (TextView) theLayout.findViewById(R.id.tvdescription);
            tvTitle.setText("FOLLOW YOUR INTERESTS");
            tvdescription.setText("Discover pictures, videos and\naudios that you love");
            return  theLayout;
        }

    }




    public static class SliderTwo extends Fragment{

        TextView tvTitle, tvdescription;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.slider_layout, container, false);
            tvTitle= (TextView) theLayout.findViewById(R.id.tvTitle);
            tvdescription= (TextView) theLayout.findViewById(R.id.tvdescription);
            tvTitle.setText("GET FEATURED");
            tvdescription.setText("Share your beautiful pictures and\nand have them appear in our monthly \nmagazine");
            return  theLayout;
        }


    }


    public static class SliderThree extends Fragment{

        TextView tvTitle, tvdescription;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.slider_layout, container, false);
            tvTitle = (TextView) theLayout.findViewById(R.id.tvTitle);
            tvdescription = (TextView) theLayout.findViewById(R.id.tvdescription);
            tvTitle.setText("ORDER PHOTO BOOKS");
            tvdescription.setText("Let us print the cool pictures you\n discover in your own brand \nmagazine");
            return  theLayout;
        }

    }



    public static class SliderFour extends Fragment{

        TextView tvTitle, tvdescription;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.slider_layout, container, false);
            tvTitle = (TextView) theLayout.findViewById(R.id.tvTitle);
            tvdescription = (TextView) theLayout.findViewById(R.id.tvdescription);
            tvTitle.setText("GET DISCOVERED");
            tvdescription.setText("Let the world dicover your amazing\npictures!");
            return  theLayout;
        }

    }




    class timer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        Thread timer;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Creating product
         * */
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters

            timer=new Thread(){
                @Override
                public void run(){
                    try{
                        while(mainslide){
                            sleep(1000*3);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(slide){
                                        if(direction){
                                            //move backward
                                            if(counter<=0){
                                                direction=false;
                                            }else{
                                                counter--;
                                              //  mPager.setCurrentItem(mPager.getCurrentItem()-1);
                                                changeFragment(counter);
                                            }
                                        }else{
                                            //move forward

                                            if(counter>=3){
                                                direction=true;
                                            }else{
                                                counter++;
                                               // mPager.setCurrentItem(mPager.getCurrentItem()+1);
                                                changeFragment(counter);
                                            }
                                        }


                                    }



                                }
                            });
                        }

                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }finally{
                        System.out.println("******** thread end");
                    }

                }

            };

            timer.start();
            //Toast.makeText(MainActivity.this, "Clicked",Toast.LENGTH_LONG).show();

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //countTime
        }

    }


private void changeFragment(final int position){
    Fragment[] frags = {new SliderOne(),new SliderTwo(), new SliderThree(), new SliderFour()};
    System.out.println("--------------------- " + position);
    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left, R.anim.slide_left).replace(R.id.content_frame, frags[position]).commit();









    Thread aa=new Thread(){
        @Override
        public void run() {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {

              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      for (int i=0; i<images.length; i++){
                          if(position == i){
                              images[i].setImageResource(R.drawable.myovalwhite);
                          }else
                          {
                              images[i].setImageResource(R.drawable.myovalgrey);
                          }
                      }
                  }
              });
            }
            super.run();
        }
    };

    aa.start();


}

private void bindData(){
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }else{
        System.out.println("Granted");
    }
}









}
