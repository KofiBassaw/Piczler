package com.piczler.piczler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matiyas on 12/13/15.
 */
public class SocialPictures extends AppCompatActivity implements View.OnClickListener {

    static ArrayList<GettersAndSetters> details;
    int pos = 0;
    int selectedPos = 0;
    private Toolbar toolbar;
    CustomViewPager vpPager;
    UserFunctions functions;
    private PagerAdapter mPagerAdapter;
    RelativeLayout rlDownload;
    RippleView rpDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_pic_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rlDownload = (RelativeLayout) findViewById(R.id.rlDownload);
        rpDownload = (RippleView) findViewById(R.id.rpDownload);
        rlDownload.setOnClickListener(this);

        //set the max number of images (image width > 50) to be cached in memory, default is 20
        BitmapAjaxCallback.setCacheLimit(200);

        //set the max size of an image to be cached in memory, default is 1600 pixels (ie. 400x400)
        BitmapAjaxCallback.setPixelLimit(8000 * 8000);

        //set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(8000000);
        vpPager = (CustomViewPager) findViewById(R.id.vpPager);
        vpPager.setPageMargin(25);
        vpPager.setPageMarginDrawable(R.color.black);
        functions = new UserFunctions(this);

        pos = getIntent().getExtras().getInt(StaticVariables.POSITION);
        new BindAsync().execute(getIntent().getStringExtra(StaticVariables.MEDIA));




        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("------------------ onPageSelected: "+position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("------------------ onPageScrollStateChanged: "+state);
            }
        });


        vpPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtStart() {
            }

            @Override
            public void onSwipeOutAtEnd() {
                System.out.println("------------------------- swipe to end");


            }
        });



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
        } else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindData(String jsonArray){
        details = new ArrayList<>();
        GettersAndSetters Details;
        try{
            JSONArray images = new JSONArray(jsonArray);
            for(int i=0; i< images.length(); i++){
                JSONObject json = images.getJSONObject(i);
                Details = new GettersAndSetters();
                Details.setJsonString(functions.getJsonString(json,StaticVariables.URL));
                details.add(Details);
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlDownload:
                rpDownload.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Bitmap map = details.get(vpPager.getCurrentItem()).bitmap;
                     if( map != null){
                         functions.shareBook(map);
                     }
                    }
                });
                break;
        }
    }


    class BindAsync extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        /**
         * Creating product
         * */
        @Override
        protected String doInBackground(String... args) {

            try{

                bindData(args[0]);

            }catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }




            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            //vpPager.setPageTransformer(true, new DepthPageTransformer());

            vpPager.setAdapter(mPagerAdapter);
            vpPager.setCurrentItem(pos,true);


        }





    }




    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ImageHolder hold = new ImageHolder();
            Bundle bundle=new Bundle();
            bundle.putInt("pos",position);
            hold.setArguments(bundle);
            return hold;

        }

        @Override
        public int getCount() {
            // System.out.println("-------------------- "+feeds.length());
            return details.size();
        }
    }




    public static class ImageHolder extends Fragment {

        ImageView view;
        int position;
        UserFunctions function;
        ProgressBar pbBarImage;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.image_layout, container, false);
            view = (ImageView) theLayout.findViewById(R.id.ivImage);
            pbBarImage = (ProgressBar) theLayout.findViewById(R.id.pbBarImage);

            position = getArguments().getInt("pos");
            function = new UserFunctions(getActivity());


            try{
                String jsonString = details.get(position).jsonString;



                System.out.println("-------------------------------    "+jsonString);
                        AQuery aq = new AQuery(getActivity());
                        ImageOptions op=new ImageOptions();
                        op.fileCache = true;
                        op.memCache=false;
                        //op.targetWidth = 2000;

                        aq.id(view).image(jsonString, op);

                aq.id(view).image(jsonString, false, true, 2000, 0, new BitmapAjaxCallback() {

                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {

                        pbBarImage.setVisibility(View.VISIBLE);

                   if(bm != null)
                   {
                       iv.setImageBitmap(bm);

                       details.get(position).bitmap = bm;
                   }

                    }

                });

            }catch (Exception e){
                e.printStackTrace();
            }


            return  theLayout;
        }

    }



}
