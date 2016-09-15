package com.piczler.piczler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import ly.img.android.sdk.filter.ColorFilterBW;
import ly.img.android.sdk.filter.ColorFilterBleachedBlue;
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
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by matiyas on 11/24/15.
 */
public class ConfirmProfile extends AppCompatActivity implements View.OnClickListener {
    String profilePic;
    ImageView ivImage;
   // RelativeLayout rlMark;
   // RippleView rpMark;
    private static final int SENDPOST = 9;
    RelativeLayout rlPlayLayout,rlMainView;
    String type;
    RecyclerAdapterImages recyclerAdapter;
    int prev = 0;
    private static Bitmap originalBitmap;

    RecyclerView recyclerView;
    LinearLayoutManager ll;
    ArrayList<GettersAndSetters> details;
    private Toolbar toolbar;
    MenuItem nextDone;
    UserFunctions functions;
    String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profilePic = getIntent().getStringExtra(StaticVariables.PROFILE_PICTURE);
        ivImage = (ImageView) findViewById(R.id.ivImage);
       // rlMark = (RelativeLayout) findViewById(R.id.rlMark);
        //rpMark = (RippleView) findViewById(R.id.rpMark);
        rlPlayLayout = (RelativeLayout) findViewById(R.id.rlPlayLayout);
        rlMainView = (RelativeLayout) findViewById(R.id.rlMainView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        type = getIntent().getStringExtra(StaticVariables.TYPE);
        functions = new UserFunctions(this);
        initRecyclerView();
        if(type.contentEquals("image"))
        {
            rlMainView.setVisibility(View.VISIBLE);

            setCapturedImage(profilePic);
            new BindAsync().execute("0");
        }else if(type.contentEquals("video"))
        {
            source = getIntent().getStringExtra("source");
            rlPlayLayout.setVisibility(View.VISIBLE);
            rlMainView.setVisibility(View.GONE);

            Bitmap samp =  functions.getVideoThumbnail(profilePic,source);

            if(samp != null)
            {
                ivImage.setImageBitmap(samp);
            }else
            {
                Glide.with(this)
                        .load(Uri.fromFile( new File( profilePic ) ) )
                        .into( ivImage );
            }

              /*
            Uri imageUri = Uri.fromFile( new File( profilePic ) );
            if(imageUri == null)
            {
                System.out.println("llllllllllllllllllllllll:  null");
            }else {
                System.out.println("llllllllllllllllllllllll:  not null");
            }
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(profilePic, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);


            ivImage.setImageBitmap(bMap);



            Glide.with(this)
                    .load(Uri.fromFile( new File( profilePic ) ) )
                    .into( ivImage );
            */

            /*
            Glide.with(this)
                    .load( Uri.fromFile(new File(profilePic)) )
                    .into( ivImage );
            */
        }

       // rlMark.setOnClickListener(this);
        rlPlayLayout.setOnClickListener(this);

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

                            try
                            {
                                details.get(position).showIcon = true;
                                details.get(prev).showIcon = false;
                                recyclerAdapter.notifyDataSetChanged();
                                prev = position;
                                if(position>0)
                                    ivImage.setImageBitmap(StaticVariables.getFilter(position).renderImage(originalBitmap));
                                else
                                    ivImage.setImageBitmap(originalBitmap);
                            }catch (Exception ex)
                            {
                              ex.printStackTrace();
                            }

                        }

                    }
                })
        );
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
                 recyclerAdapter = new RecyclerAdapterImages(details, ConfirmProfile.this);

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

    private void setCapturedImage(final String imagePath){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {

                    if(functions.getDeviceName().contains("SAMSUNG"))
                    {
                        return imagePath;
                    }else
                        return getRightAngleImage(imagePath);
                }catch (Throwable e){
                    e.printStackTrace();
                    e.printStackTrace();
                }
                return imagePath;
            }

            @Override
            protected void onPostExecute(String imagePath) {
                super.onPostExecute(imagePath);
                System.out.println("--------------------------------------------- decoding file here ");
                originalBitmap = decodeFile(imagePath);
                ivImage.setImageBitmap(originalBitmap);
            }
        }.execute();
    }

    private String getRightAngleImage(String photoPath) {

        try {
            System.out.println("--------------------------------------------- Right angle image");
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }



    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        System.out.println("--------------------------------------------- rotate image ");
        String data ="degree: "+degree+"\n";
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);
            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                data+="WIDTH GREATE: w: "+b.getWidth()+" H: "+b.getHeight();
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }else if(b.getWidth()>b.getHeight() && degree<=0){
                data+="degree 0: w: "+b.getWidth()+" H: "+b.getHeight();
                matrix.setRotate(90);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }
            final String copy =data;
            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);


            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /*
            case R.id.rlMark:
                rpMark.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                        Intent it = new Intent(ConfirmProfile.this, Post.class);
                        it.putExtra(StaticVariables.PROFILE_PICTURE, profilePic);
                        it.putExtra(StaticVariables.TYPE, type);
                        startActivityForResult(it, SENDPOST);

                    }
                });
                break;
            */
            case R.id.rlPlayLayout:
                /*
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profilePic));
                intent.setDataAndType(Uri.parse(profilePic), "video/mp4");
                startActivity(intent);
                */
                Intent it= new Intent(ConfirmProfile.this,VideoPlayer.class);
                it.putExtra(StaticVariables.URL,profilePic);
                startActivity(it);
                break;
        }
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
            Intent it = new Intent(ConfirmProfile.this, Post.class);
            it.putExtra(StaticVariables.PROFILE_PICTURE, profilePic);
            it.putExtra(StaticVariables.TYPE, type);
            it.putExtra(StaticVariables.POSITION, prev);
            it.putExtra("source", source);
            startActivityForResult(it, SENDPOST);
            return true;
        }else if(id == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }




}
