package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.cunoraz.tagview.*;
import com.cunoraz.tagview.TagView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by matiyas on 11/27/15.
 */
public class Post extends AppCompatActivity implements TagListView.TagListener {

    ImageView ivImage;
    private Toolbar toolbar;
    String profilePic = "";
    UserFunctions functions;
    //RelativeLayout[] rlLout = new RelativeLayout[11];
    //RippleView[] ripples = new RippleView[11];
    //TextView[] tvViews = new TextView[11];
    ArrayList<GettersAndSetters> details;
    String caption = "", idString = "";
    ProgressDialog pDialog;
    EditText etCaption;
    String type;
    int typer =0;
    String med;
    Bitmap bitmapToSend;
    int pos;
    String source;
    ProgressBar pbReload;

    //TagListView tagListView;

    com.cunoraz.tagview.TagView tagGroup;
    ArrayList<Tag> tags;
    Button bReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_view);
        ivImage = (ImageView) findViewById(R.id.ivImages);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
       // tagListView = (TagListView) findViewById(R.id.tagview);
        tagGroup = (com.cunoraz.tagview.TagView)findViewById(R.id.tag_group);
        pbReload = (ProgressBar) findViewById(R.id.pbReload);
        bReload = (Button) findViewById(R.id.bReload);
        functions = new UserFunctions(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        profilePic = getIntent().getStringExtra(StaticVariables.PROFILE_PICTURE);
        etCaption = (EditText) findViewById(R.id.etCaption);
        type = getIntent().getStringExtra(StaticVariables.TYPE);
        pos = getIntent().getExtras().getInt(StaticVariables.POSITION);

        //File ff = new File(profilePic);
        String ext = profilePic.substring(profilePic.lastIndexOf(".")+1,profilePic.length());
        System.out.println("ooooooooooooooo "+ext);
        if(type.contentEquals("image"))
        {
            setCapturedImage(profilePic);

            med = "image/jpeg";
            //med = "image/"+ext;
            typer = 0;

        }else if(type.contentEquals("video")){
            source = getIntent().getStringExtra("source");
            typer = 1;
            med = "video/mp4";
           // med = "video/"+ext;

            Bitmap samp =  functions.getVideoThumbnail(profilePic,source);

            if(samp != null)
            {
                ivImage.setImageBitmap(samp);
            }else
            {
                Glide.with(this)
                        .load( Uri.fromFile(new File(profilePic)) )
                        .into( ivImage );
            }

        }else if(type.contentEquals("audio"))
        {
            typer = 2;
            //med = "audio/3gp";
            med = "audio/"+ext;
            ivImage.setBackgroundColor(Color.parseColor("#f9b466"));
        }




        details = new ArrayList<>();
        String oldCat = functions.getPref(StaticVariables.CATEGORIESSAVED,"");

        if(!oldCat.contentEquals(""))
        {
            bindTags(oldCat);
        }else {
            pbReload.setVisibility(View.VISIBLE);
            getCategories(0,200);
        }



        bReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bReload.setVisibility(View.GONE);
                pbReload.setVisibility(View.VISIBLE);

                getCategories(0,200);
            }
        });

    }


    private void bindTags(String oldCat)
    {


        try{

            JSONArray arr = new JSONArray(oldCat);
            GettersAndSetters Details;
            tags = new ArrayList<>();
            Tag tg;

            for(int i=0; i<arr.length(); i++){
                JSONObject c = arr.getJSONObject(i);
                Details = new GettersAndSetters();
                Details.setName(functions.getJsonString(c, StaticVariables.NAME));
                Details.setId(functions.getInt(c, StaticVariables.ID));
                Details.setFollowed(false);

                //tagListView.addTag(functions.getJsonString(c, StaticVariables.NAME));

                tg = new Tag(functions.getJsonString(c, StaticVariables.NAME));
                tg.isDeletable = false;
                tg.layoutBorderColor = Color.parseColor("#1f1f1f");
                tg.layoutColorPress = Color.TRANSPARENT;
                tg.radius = 10f;
                tg.layoutColor = Color.parseColor("#ffffff");
                tg.tagTextColor = Color.parseColor("#1f1f1f");
                tg.tagTextSize = 12;

                tg.layoutBorderSize = 1;
                tags.add(tg);
                details.add(Details);
            }
            //  tagListView.addTagListener(this);
            tagGroup.addTags(tags);







            //set click listener
            tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(Tag tag, int position) {

                    if(!details.get(position).followed)
                    {
                        details.get(position).followed = true;
                        tags.get(position).layoutColor = Color.parseColor("#2C4389");
                        tags.get(position).tagTextColor = Color.WHITE;
                    }else
                    {
                        details.get(position).followed = false;
                        tags.get(position).layoutColor  = Color.parseColor("#ffffff");
                        tags.get(position).tagTextColor  = Color.parseColor("#1f1f1f");
                    }


                    tagGroup.addTag(tag);
                    tagGroup.remove(tags.size());





                }
            });

            pbReload.setVisibility(View.GONE);

        }catch (Exception e){
            System.out.println("bbbbbbbbbbb error here: activity "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.idPost) {
     getData();
            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void setCapturedImage(final String imagePath){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    System.out.println("--------------------------------------------- starting ");
                    return getRightAngleImage(imagePath);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                return imagePath;
            }

            @Override
            protected void onPostExecute(String imagePath) {
                super.onPostExecute(imagePath);
                System.out.println("--------------------------------------------- decoding file here ");

                bitmapToSend =    decodeFile(imagePath);

                if(pos > 0)
                {
                  bitmapToSend = StaticVariables.getFilter(pos).renderImage(bitmapToSend);
                }

                    ivImage.setImageBitmap(bitmapToSend);
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
                b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 90, out);
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




    private String getIds(){
        String ids ="";
        for(int i =0; i<details.size(); i++){

            if(details.get(i).followed){
                if(ids.length()>0)
                    ids +=",";
                ids+=details.get(i).id;
            }
        }

        return ids;
    }


    private void getData(){
        idString = getIds();
        caption = functions.getText(etCaption);
        if(functions.isEmpty(idString)){
            functions.showMessage("Please select at least one category");
        }else
        {
            //sendImage to the server
        if(type.contentEquals("video")) {
             sendImageToserver(new File(profilePic));
        }else if(type.contentEquals("image"))
            {
                if(pos == 0)
                {
                 sendImageToserver(new File(profilePic));
                }else
                {
                    try
                    {

                        File path  = Environment.getExternalStorageDirectory();
                        File imageFile = new File(path, StaticVariables.FILENAMEIMAGES+System.currentTimeMillis()+ ".jpg");
                        FileOutputStream fileOutPutStream = new FileOutputStream(imageFile);
                        bitmapToSend.compress(Bitmap.CompressFormat.JPEG, 90, fileOutPutStream);

                        fileOutPutStream.flush();
                        fileOutPutStream.close();
                        sendImageToserver(imageFile);
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }else if(type.contentEquals("audio"))
        {
            sendImageToserver(new File(profilePic));
        }

        }
    }





    private void sendImageToserver(final File image){
        functions = new UserFunctions(this);
        new ProgressDialog(this);
        pDialog = functions.prepareDialog("Sending post...",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "media")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setMultipartParameter(StaticVariables.TYPE, String.valueOf(typer))
                .setMultipartParameter(StaticVariables.CAPTION, caption)
                .setMultipartParameter(StaticVariables.CATEGORYIDS, idString)
               // .setMultipartFile(StaticVariables.MEDIA, image)
                .setMultipartFile(StaticVariables.MEDIA,med,image)
                //.setMultipartContentType("image/jpeg")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error

                        try {
                            pDialog.dismiss();

                            if (result != null) {
                                System.out.println("----------------------- " + result);
                                JSONObject json = new JSONObject(result);

                                JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                if (meta != null) {
                                    int code = functions.getInt(meta, StaticVariables.CODE);
                                    if (code == 200) {
                                        JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                        if (data != null) {
                                            //image.delete();




                                            try
                                            {
                                                MixpanelAPI mixpanel =
                                                        MixpanelAPI.getInstance(Post.this, StaticVariables.MIXPANEL_TOKEN);



                                                if(type.contentEquals("image"))
                                                {



                                                    mixpanel.track("Photo Upload");
                                                    mixpanel.getPeople().increment("Photo Uploads",1);

                                                } else if(type.contentEquals("video")){
                                                    mixpanel.track("Video Upload");
                                                    mixpanel.getPeople().increment("Video Uploads",1);


                                                 }else if(type.contentEquals("audio")) {
                                                    mixpanel.track("Audio Upload");
                                                    mixpanel.getPeople().increment("Audio Uploads",1);
                                            }




                                            }catch (Exception ex)
                                            {
                                                ex.printStackTrace();
                                            }


                                          Intent it = new Intent();
                                            Bundle basket = new Bundle();
                                            basket.putString(StaticVariables.DATA, data.toString());
                                            it.putExtras(basket);
                                            setResult(RESULT_OK,it);
                                            finish();
                                        } else {
                                            functions.showMessage("Unable to send post");
                                        }




                                    } else if (code == 403) {
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                    } else if(code ==  400){
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                    }
                                } else {
                                    functions.showMessage("Unable to send post");
                                }

                            } else {
                                functions.showMessage("Unable to send post");
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }




    @Override
    public void onAddedTag(String tag) {
        Log.d("tagview", "added tag " + tag);
    }

    @Override
    public void onRemovedTag(String tag) {
        Log.d("tagview", "removed tag " + tag);
    }





    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String selected = "0";
            if(intent.getExtras().getBoolean("seleceted"))
            {
                selected = "1";
            }
              new BindAsync().execute(intent.getStringExtra("tag"), selected);

        }
    };



    @Override
    protected void onResume() {
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                "www.sample.test.broadcast.cooool"));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            Log.e("rror", "> " + e.getMessage());
        }
        super.onDestroy();
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

                String tag = args[0];
                String value = args[1];
                boolean selected = false;
                if(value.contentEquals("1"))
                    selected= true;
                for(int i=0; i<details.size(); i++)
                {if(tag.contentEquals(details.get(i).name))
                {
                    details.get(i).followed = selected;
                    break;
                }


                }

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




        }





    }



    private void getCategories(final int offset, final int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println(StaticVariables.CATEGORIES + "?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit);
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + StaticVariables.CATEGORIES + "?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (e != null) {
                                    e.printStackTrace();
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);
                                    functions.setPref(StaticVariables.HASCATEGORY, true);
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {

                                                String oldString = functions.getPref(StaticVariables.CATEGORIESSAVED,"");
                                                functions.setPref(StaticVariables.CATEGORIESSAVED,data.toString());

                                                bindTags(data.toString());





                                            }else {
                                                showError("Unable to load categories, please try again later");
                                            }

                                        }else
                                        {
                                            showError("Unable to load categories, please try again later");
                                        }
                                    }else
                                    {
                                        showError("Unable to load categories, please try again later");
                                    }

                                }else
                                {
                                    showError("Unable to load categories, please try again later");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showError("Unable to load categories, please try again later");
                            }
                        }
                    });


        }else
        {
            showError("No internet connection, please try again later");
        }


    }



    private void showError(String message)
    {
        functions.showMessage(message);
        pbReload.setVisibility(View.GONE);
        bReload.setVisibility(View.VISIBLE);

    }

}
