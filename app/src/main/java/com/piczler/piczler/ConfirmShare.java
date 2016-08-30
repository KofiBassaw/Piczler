package com.piczler.piczler;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;

import java.util.ArrayList;

import ly.img.android.sdk.filter.ColorFilterBW;
import ly.img.android.sdk.filter.ColorFilterBreeze;
import ly.img.android.sdk.filter.ColorFilterChest;
import ly.img.android.sdk.filter.ColorFilterCottonCandy;
import ly.img.android.sdk.filter.ColorFilterEvening;
import ly.img.android.sdk.filter.ColorFilterFall;
import ly.img.android.sdk.filter.ColorFilterFixie;
import ly.img.android.sdk.filter.ColorFilterFridge;
import ly.img.android.sdk.filter.ColorFilterHighContrast;
import ly.img.android.sdk.filter.ColorFilterLenin;
import ly.img.android.sdk.filter.ColorFilterMellow;
import ly.img.android.sdk.filter.ColorFilterOrchid;
import ly.img.android.sdk.filter.ColorFilterQuozi;
import ly.img.android.sdk.filter.ColorFilterSunset;

/**
 * Created by matiyas on 12/13/15.
 */
public class ConfirmShare extends AppCompatActivity implements View.OnClickListener {

    String profilePic;
    ImageView ivImage;
    //RelativeLayout rlMark;
   // RippleView rpMark;
    RelativeLayout rlMainView;
    private static final int SENDPOST = 9;



    RecyclerAdapterImages recyclerAdapter;
    int prev = 0;
    private static Bitmap originalBitmap;

    RecyclerView recyclerView;
    LinearLayoutManager ll;
    ArrayList<GettersAndSetters> details;
    private Toolbar toolbar;
    MenuItem nextDone;
    UserFunctions functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        ivImage = (ImageView) findViewById(R.id.ivImage);
        rlMainView = (RelativeLayout) findViewById(R.id.rlMainView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        functions = new UserFunctions(this);
        initRecyclerView();
        //rlMark = (RelativeLayout) findViewById(R.id.rlMark);
        //rpMark = (RippleView) findViewById(R.id.rpMark);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
          if (type.startsWith("image/")) {
              rlMainView.setVisibility(View.VISIBLE);
                handleSendImage(intent); // Handle single image being sent
            }
        }

       // rlMark.setOnClickListener(this);
    }



    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared

            profilePic = getRealPathFromURI(imageUri);
            originalBitmap = decodeFile(profilePic);
            ivImage.setImageBitmap(originalBitmap);
            new BindAsync().execute("0");
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
                bindData(Integer.parseInt(args[0]));
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
            try{
                recyclerAdapter = new RecyclerAdapterImages(details, ConfirmShare.this);

                recyclerView.setAdapter(recyclerAdapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    private void bindData(int position)
    {
        details = new ArrayList<>();
        GettersAndSetters
                Details = new GettersAndSetters();
        Details.setName("Normal");
        Details.setShowIcon(true);
        Details.setFilter(null);
        Details.setImageUrl(profilePic);
        details.add(Details);


        Details = new GettersAndSetters();
        Details.setName("Breeze");
        Details.setFilter(new ColorFilterBreeze());
        Details.setImageUrl(profilePic);
        Details.setShowIcon(false);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Orchid");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterOrchid());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Chest");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterChest());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Evening");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterEvening());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Fixie");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterFixie());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Candy");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterCottonCandy());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("BW");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterBW());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Lenin");
        Details.setPosition(position);
        Details.setFilter(new ColorFilterLenin());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Fridge");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterFridge());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Quozi");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterQuozi());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Hicon");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterHighContrast());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Mellow");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterMellow());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Fall");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterFall());
        Details.setImageUrl(profilePic);
        details.add(Details);

        Details = new GettersAndSetters();
        Details.setName("Sunset");
        Details.setShowIcon(false);
        Details.setFilter(new ColorFilterSunset());
        Details.setImageUrl(profilePic);
        details.add(Details);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category, menu);

        nextDone=menu.findItem(R.id.idNext);

        nextDone.setTitle("Next");

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.idNext) {
            Intent it = new Intent(ConfirmShare.this, Post.class);
            it.putExtra(StaticVariables.PROFILE_PICTURE, profilePic);
            it.putExtra(StaticVariables.TYPE, "image");
            it.putExtra(StaticVariables.POSITION, prev);
            startActivityForResult(it, SENDPOST);
            return true;
        }else if(id == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
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


    private void initRecyclerView() {
        ll=new LinearLayoutManager(this);
        ll.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(ll);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if (prev != position) {
                            details.get(position).showIcon = true;
                            details.get(prev).showIcon = false;
                            recyclerAdapter.notifyDataSetChanged();
                            prev = position;
                            if(position>0)
                                ivImage.setImageBitmap(StaticVariables.getFilter(position).renderImage(originalBitmap));
                            else
                                ivImage.setImageBitmap(originalBitmap);
                        }

                    }
                })
        );
    }




    public String getRealPathFromURI(Uri contentUri) {

        try
        {
            // can post image
            String[] proj={MediaStore.Images.Media.DATA};
            Cursor cursor =getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);

        }catch (Exception ex)
        {
            ex.printStackTrace();


            String fullPath = RealPathUtil.getPath(this,contentUri) ;
            return  fullPath;
        }



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /*
            case R.id.rlMark:
                rpMark.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                          if(profilePic != null){
                              Intent it = new Intent(ConfirmShare.this, Post.class);
                              it.putExtra(StaticVariables.PROFILE_PICTURE, profilePic);
                              startActivity(it);
                          }


                    }
                });
                break;
            */
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SENDPOST){
           startActivity(new Intent(this,MainHome.class));
            finish();
        }
    }

}
