package com.piczler.piczler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matiyas on 11/18/15.
 */

public class MainHome extends AppCompatActivity implements View.OnClickListener {
     CustomViewPager vpPager;
    private PagerAdapter mPagerAdapter;
    UserFunctions functions;
    public static  LinearLayout llBottomSlide;
   public static boolean slideDown = true;
    public static  boolean slideRight = true;
    public static  boolean pbBar2Boolean = true;
    public static  boolean slideTop = true;
    public static  boolean slideComment = false;
    public static  boolean slideSearch = false;
    public static  LinearLayout llRight;
    public static  RelativeLayout rlMenu,rlProfile,rlCategories,rlSettings,rlSearch;
    RippleView rpProfile,rpCategories, rpLike,rpSettings,rpComment,rpSearch,rpSave,rpHome;
    public static JSONArray feeds;
    public static JSONArray feedsHolder;
    public static TextView tvCaption,tvLikes,tvPhotCredit;
    ProgressBar pbBar;
    ProgressBar pbSendComment,pbSearch;
    String userFeed ="";
    public static ImageView ivLike;
    RelativeLayout rlLike,rlComment,rlSave;
    int selectedPos = 0;
    boolean canLoad = true;
    int offset = 0;
    int limit = 20;
    TextView tvSenderName,tvNoMedia;
    LinearLayout llBottomHold,llRightHolder;
    ImageView ivSenderImage;
    ImageView ivMore;
    Typeface td,ts;
    TextView tvHome,tvCategory,tvProfile,tvSearch,tvSettings;
    RelativeLayout rlCommentLayout,rlSearchLayout,rlHome;
    ImageView comment_cancel,search_cancel;

    int commentLimit = 10;
    int commentOffset = 0;
    ListView  lvComment,lvCommentSelected;
    LinearLayout llUserProfile;
    RelativeLayout rlPlayLayout;
    LinearLayout llCover;
    private Uri fileUri;
    ImageView ivSettings;
    private static final int VIDEO_CAPTURE = 101;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;



    CustomAdaptor adapter;
    EditText etComment,etSearch;
    ProgressDialog pDIalogi;

    ArrayList<GettersAndSetters> commentArrayList;
    ArrayList<GettersAndSetters> comentSelectedListID;
    ArrayList<GettersAndSetters> comentSelectedTosend;
    CustomAdaptorSelected selectedAdapter;
    boolean sendingComment = false;
    ArrayList<GettersAndSetters> searchList;
    TextView tvSearchTItle;
    RecyclerView recyclerView;
    ImageView ivSaveOrDelete;
    Map<String, String> map2 = new HashMap<String, String>();



    private static final int SETTINGSINTENT = 1;
    private static final int SENDPOST = 2;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 3;
    String image_path2 = "";
    Uri selectedImageUri;
    ImageView ivPlayIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functions = new UserFunctions(this);


        String projectToken = "sample token here"; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
       // MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);



        if(!functions.getPref(StaticVariables.HASLOGEDIN,false)){
            //open main activiy
            startActivity(new Intent(this,MainActivity.class));
            finish();

        }else if(!functions.getPref(StaticVariables.HASCATEGORY,false)){
            //open category
            startActivity(new Intent(this,Categories.class));
            finish();
        }else
        {


            setContentView(R.layout.main_home);
            checkPlayServices();
            //set the max number of images (image width > 50) to be cached in memory, default is 20
            BitmapAjaxCallback.setCacheLimit(200);

            //set the max size of an image to be cached in memory, default is 1600 pixels (ie. 400x400)
            BitmapAjaxCallback.setPixelLimit(8000 * 8000);

            //set the max size of the memory cache, default is 1M pixels (4MB)
            BitmapAjaxCallback.setMaxPixelLimit(8000000);



            vpPager = (CustomViewPager) findViewById(R.id.vpPager);
            vpPager.setPageMargin(25);
            vpPager.setPageMarginDrawable(R.color.black);
            llBottomSlide = (LinearLayout) findViewById(R.id.llBottomSlide);
            llRight = (LinearLayout) findViewById(R.id.llRight);
            rlMenu = (RelativeLayout) findViewById(R.id.rlMenu);
            rlCommentLayout = (RelativeLayout) findViewById(R.id.rlCommentLayout);
            rlSearchLayout = (RelativeLayout) findViewById(R.id.rlSearchLayout);
            rlHome = (RelativeLayout) findViewById(R.id.rlHome);
            comment_cancel = (ImageView) findViewById(R.id.comment_cancel);
            search_cancel = (ImageView) findViewById(R.id.search_cancel);
            rlProfile = (RelativeLayout) findViewById(R.id.rlProfile);
            rlCategories = (RelativeLayout) findViewById(R.id.rlCategories);
            rlSettings = (RelativeLayout) findViewById(R.id.rlSettings);
            rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
            rpProfile = (RippleView) findViewById(R.id.rpProfile);
            rpCategories = (RippleView) findViewById(R.id.rpCategories);
            rpSettings = (RippleView) findViewById(R.id.rpSettings);
            rpSearch = (RippleView) findViewById(R.id.rpSearch);
            rpComment = (RippleView) findViewById(R.id.rpComment);
            rpLike = (RippleView) findViewById(R.id.rpLike);
            rpSave = (RippleView) findViewById(R.id.rpSave);
            rpHome = (RippleView) findViewById(R.id.rpHome);
            tvCaption = (TextView) findViewById(R.id.tvCaption);
            tvCaption = (TextView) findViewById(R.id.tvCaption);
            tvLikes = (TextView) findViewById(R.id.tvLikes);
            ivPlayIcon = (ImageView) findViewById(R.id.ivPlayIcon);
            tvNoMedia = (TextView) findViewById(R.id.tvNoMedia);
            tvSearchTItle = (TextView) findViewById(R.id.tvSearchTItle);
            llUserProfile = (LinearLayout) findViewById(R.id.llUserProfile);
            ivLike = (ImageView) findViewById(R.id.ivLike);
            ivMore = (ImageView) findViewById(R.id.ivMore);
            ivSettings = (ImageView) findViewById(R.id.ivSettings);
            rlLike = (RelativeLayout) findViewById(R.id.rlLike);
            rlComment = (RelativeLayout) findViewById(R.id.rlComment);
            rlSave = (RelativeLayout) findViewById(R.id.rlSave);
            llCover = (LinearLayout) findViewById(R.id.llCover);
            pbBar = (ProgressBar) findViewById(R.id.pbBar);
            pbSendComment = (ProgressBar) findViewById(R.id.pbSendComment);
            rlPlayLayout = (RelativeLayout) findViewById(R.id.rlPlayLayout);
            llBottomHold = (LinearLayout) findViewById(R.id.llBottomHold);
            llRightHolder = (LinearLayout) findViewById(R.id.llRightHolder);
            pbSearch = (ProgressBar) findViewById(R.id.pbSearch);
            lvComment = (ListView) findViewById(R.id.lvComment);
            lvCommentSelected = (ListView) findViewById(R.id.lvCommentSelected);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            ts=Typeface.createFromAsset(getAssets(),"HelveticaNeue Medium.ttf");
            td=Typeface.createFromAsset(getAssets(),"HelveticaNeue Medium.ttf");
            tvHome = (TextView) findViewById(R.id.tvHome);
            tvCategory = (TextView) findViewById(R.id.tvCategory);
            tvProfile = (TextView) findViewById(R.id.tvProfile);
            tvSearch = (TextView) findViewById(R.id.tvSearch);
            tvSettings = (TextView) findViewById(R.id.tvSettings);
            tvSenderName = (TextView) findViewById(R.id.tvSenderName);
            ivSenderImage = (ImageView) findViewById(R.id.ivSenderImage);
            tvPhotCredit = (TextView) findViewById(R.id.tvPhotCredit);
            etComment = (EditText) findViewById(R.id.etComment);
            etSearch = (EditText) findViewById(R.id.etSearch);
            ivSaveOrDelete = (ImageView) findViewById(R.id.ivSaveOrDelete);
            initRecyclerView();

            tvHome.setTypeface(ts);
            tvCategory.setTypeface(ts);
            tvProfile.setTypeface(ts);
            tvSearch.setTypeface(ts);
            tvCaption.setTypeface(ts);
            tvPhotCredit.setTypeface(ts);
            tvSenderName.setTypeface(ts);
            tvLikes.setTypeface(td);
            rlProfile.setOnClickListener(this);
            rlCategories.setOnClickListener(this);
            rlLike.setOnClickListener(this);
            rlComment.setOnClickListener(this);
            rlSave.setOnClickListener(this);
            tvSettings.setOnClickListener(this);
            comment_cancel.setOnClickListener(this);
            search_cancel.setOnClickListener(this);
            rlSettings.setOnClickListener(this);
            rlSearch.setOnClickListener(this);
            rlCommentLayout.setOnClickListener(this);
            rlSearchLayout.setOnClickListener(this);
            llUserProfile.setOnClickListener(this);
            rlPlayLayout.setOnClickListener(this);
            rlHome.setOnClickListener(this);
            userFeed= functions.getPref(StaticVariables.FEEDS,"");











            if (!userFeed.contentEquals("")){
                try{

                    feedsHolder = new JSONArray(userFeed);
                    if(feedsHolder.length()>0)
                    {
                        JsonArray firstArray = new JsonArray();
                        JsonObject firstObj = new JsonObject();
                        firstArray.add(firstObj);
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();

                        JsonArray newFeed = gson.fromJson(userFeed, JsonArray.class);
                        firstArray.addAll(newFeed);
                        firstObj = new JsonObject();
                        firstArray.add(firstObj);
                        feeds = new JSONArray(firstArray.toString());

                        // feedsHolder = new JSONArray(userFeed);
                        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                        // if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        //vpPager.setPageTransformer(true, new DepthPageTransformer());

                        canLoad = false;
                        vpPager.setAdapter(mPagerAdapter);
                        vpPager.setCurrentItem(1);
                        canLoad = true;
                    }else
                    {
                        pbBar.setVisibility(View.VISIBLE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else{
                try {
                    feedsHolder = new JSONArray("[]");
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                pbBar.setVisibility(View.VISIBLE);
            }

            getFeeds(offset, limit);




            vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                   // System.out.println("$$$$$$$$$$ outside:  "+selectedPos +":  length: "  + feeds.length());
                if(selectedPos != vpPager.getCurrentItem())
                {
                    selectedPos = vpPager.getCurrentItem();
                    //System.out.println("$$$$$$$$$$ inside:  "+selectedPos +":  length: "  + feeds.length());

                    if(selectedPos != 0 && selectedPos != feeds.length()-1 )
                    {
                        llCover.setVisibility(View.VISIBLE);
                        new PrepareChange().execute("" + selectedPos);

                    }else
                    {

                        llCover.setVisibility(View.GONE);

                        if(position == 0)
                        {
                            if (canLoad) {
                                if(slideDown)
                                {
                                    swapView();
                                }
                                rlPlayLayout.setVisibility(View.GONE);
                                canLoad = false;
                                offset = 0;
                                getFeeds(offset, limit);
                            }

                        }else
                        {
                            if (canLoad) {
                                if(slideDown)
                                {
                                    swapView();
                                }
                                rlPlayLayout.setVisibility(View.GONE);
                                canLoad = false;
                                offset = offset + 20;
                                getFeeds(offset, limit);
                            }
                        }

                    }
                }

                }

                @Override
                public void onPageSelected(int position) {
                    //System.out.println("------------------ onPageSelected: " + position);

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                   // System.out.println("------------------ onPageScrollStateChanged: " + state);
                }
            });


            vpPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
                @Override
                public void onSwipeOutAtStart() {

                }

                @Override
                public void onSwipeOutAtEnd() {
                   // System.out.println("//////////////////////////// swipe to end");

                }
            });



            try{
                File ext = Environment.getExternalStorageDirectory();
                String fileString=ext.getAbsolutePath();
                File target2 = new File(fileString+StaticVariables.FILENAMEIMAGES2);

                if(!target2.exists()){
                    if(target2.mkdir()) {
                       // System.out.println("Directory created target 2--------------------------- ");
                    } else {
                       // System.out.println("Directory is not created target 2--------------------- ");
                    }
                }else{
                    //System.out.println("Aready exist --------------------- ");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            ivMore.setOnClickListener(this);




            etComment.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        if (!sendingComment && !etComment.getText().toString().trim().contentEquals("")) {
                            pbSendComment.setVisibility(View.VISIBLE);
                            sendComment();
                        }


                        return true;
                    }
                    return false;
                }
            });
            etSearch.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String query = etSearch.getText().toString().trim();
                        if (!query.contentEquals(""))
                            makeSearch(query);


                        return true;
                    }
                    return false;
                }
            });

            etComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    String textEntered = s.toString();
                    if (!textEntered.trim().contentEquals("")) {
                        String[] wordsArray = textEntered.split(" ");

                        String typeWord = wordsArray[wordsArray.length - 1];

                        if (typeWord.startsWith("@")) {
                            if (typeWord.length() > 0) {


                                String sentString = typeWord.substring(1, (typeWord.length()));
                               // System.out.println("$$$$$$$$$$$$   " + typeWord + "    $$$ " + sentString);

                                addData(sentString);
                            } else {
                                comentSelectedListID = new ArrayList<GettersAndSetters>();
                                selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, MainHome.this);
                                lvCommentSelected.setAdapter(selectedAdapter);
                            }

                        } else {
                            comentSelectedListID = new ArrayList<GettersAndSetters>();
                            selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, MainHome.this);
                            lvCommentSelected.setAdapter(selectedAdapter);
                        }
                    }


                }
            });






            lvCommentSelected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //j,gfwhvbfvbncnbnxzbnxc
                    GettersAndSetters Details = comentSelectedListID.get(position);

                    if (!existInArray(Details.serverID, comentSelectedTosend)) {
                        Details.setNameWithAt("@" + Details.name);
                        comentSelectedTosend.add(Details);
                    }

                    etComment.setText(etComment.getText().toString() + Details.name + " ");
                    comentSelectedListID = new ArrayList<GettersAndSetters>();
                    selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, MainHome.this);
                    lvCommentSelected.setAdapter(selectedAdapter);
                    etComment.setSelection(etComment.getText().length());
                }
            });







        }



//System.out.println("$$$$$$$$$$$$$$$$$$$$$$: "+functions.getDeviceName());



           }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj={MediaStore.Images.Media.DATA};
        Cursor cursor =getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        try
        {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }catch (Exception er)
        {
            return  null;
        }

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGSINTENT && resultCode == RESULT_OK)
        {
            Bundle bb = data.getExtras();

            String type = bb.getString("type");
            if(type.contentEquals("logout"))
            {
                startActivity(new Intent(this,MainHome.class));
                finish();
            }else if(type.contentEquals("unblock"))
            {
                if(feedsHolder.length()>0)
                vpPager.setCurrentItem(0,false);
                else
                {
                    vpPager.setVisibility(View.GONE);

                    pbBar.setVisibility(View.VISIBLE);
                    tvNoMedia.setVisibility(View.GONE);
                    llBottomHold.setVisibility(View.VISIBLE);
                    llRightHolder.setVisibility(View.VISIBLE);
                    offset = 0;
                    getFeeds(offset, limit);
                }
            }
        }else   if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE&& resultCode == RESULT_OK) {

            final boolean isCamera;
            if (data == null) {
                System.out.println("--------------------------------- data equals null");
                isCamera = true;
            }else if(data.getData() == null){
                System.out.println("--------------------------------- getData is null here");
                isCamera = true;
            } else {
                final String action = data.getAction();
                System.out.println("--------------------------------- action: " + action);
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.contentEquals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }


            if (isCamera) {
                System.out.println("--------------------------- camera here"+image_path2);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri currImageURI = getImageUri(getApplicationContext(), photo);
                //photo=rotate3(currImageURI);

                image_path2 = getRealPathFromURI(currImageURI);
            } else {

                    /*
                    System.out.println("--------------------------- gallery "+image_path2);
                    selectedImageUri = data == null ? null : data.getData();
                    image_path2 = functions.getRealPathFromURI(selectedImageUri);
                    */

                selectedImageUri=data.getData();
                image_path2 = functions.getRealPathFromURI(selectedImageUri);

            }
            // Bitmap bitmap = functions.getImageFromUri(selectedImageUri);
            //ivProfile.setImageBitmap(bitmap);
            Intent it = new Intent(MainHome.this, ConfirmProfile.class);
            it.putExtra(StaticVariables.PROFILE_PICTURE, image_path2);
            it.putExtra(StaticVariables.TYPE, "image");
            // startActivity(it);
            startActivityForResult(it,SENDPOST);

        }else if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK)
        {
            String realUrl = getRealPathFromURI(data.getData());
            if(realUrl == null)
            {
                realUrl = ""+data.getData();
            }
            // Toast.makeText(this, "Video has been saved to:\n" +
            //  realUrl, Toast.LENGTH_LONG).show();

            Intent it = new Intent(MainHome.this, ConfirmProfile.class);
            it.putExtra(StaticVariables.PROFILE_PICTURE, realUrl);
            it.putExtra(StaticVariables.TYPE, "video");
            // startActivity(it);
            startActivityForResult(it, SENDPOST);
        }else if(requestCode == SENDPOST && resultCode == RESULT_OK){
            if(feedsHolder.length()>0)
                vpPager.setCurrentItem(0,false);
            else
            {
                vpPager.setVisibility(View.GONE);

                pbBar.setVisibility(View.VISIBLE);
                tvNoMedia.setVisibility(View.GONE);
                llBottomHold.setVisibility(View.VISIBLE);
                llRightHolder.setVisibility(View.VISIBLE);
                offset = 0;
                getFeeds(offset, limit);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }




    @Override
    public void onBackPressed() {


        if(slideComment)
        {
            if(!slideDown)
            {
                swapView();
            }
            swapComment();
        }else if(slideSearch)
        {
            if(!slideDown)
            {
                swapView();
            }
            swapSearch();
        }else
        {
            if(vpPager.getCurrentItem() > 1)
            {
                vpPager.setCurrentItem(1, false);
            }else
            {
                super.onBackPressed();
            }
        }

    }




    private void sendComment()
    {
      try {
          int position = vpPager.getCurrentItem();
          JSONObject json = feeds.getJSONObject(position);
          final  String feedID =  functions.getJsonString(json, StaticVariables.ID);
           final String message = etComment.getText().toString().trim();
          String ids ="";

          for(int i=0; i<comentSelectedTosend.size(); i++)
          {
              GettersAndSetters Details = comentSelectedTosend.get(i);
              if(message.contains(Details.nameWithAt))
              {
                  if(ids.length()>0)
                  {
                      ids+=",";
                  }
                  ids += Details.serverID;

              }
          }




          ConnectionDetector cd=new ConnectionDetector(this);
          if(cd.isConnectingToInternet()){//chang this function later




              //http://admin.assurances.gov.gh/api/v1.0/users/login/


              //
              //System.out.println(functions.getCokies());
              Ion.with(this)
                      .load("POST", StaticVariables.BASE_URL + "media/"+feedID+"/comments")
                      .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                      .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                      .setHeader(StaticVariables.COKIE, functions.getCokies())
                      .setBodyParameter(StaticVariables.TEXT, message)
                      .setBodyParameter(StaticVariables.IDS, ids)
                      .asString()
                      .setCallback(new FutureCallback<String>() {
                          @Override
                          public void onCompleted(Exception e, String result) {
                              try {
                                  sendingComment = false;
                                  pbSendComment.setVisibility(View.GONE);
                                  if (e != null) {
                                      e.printStackTrace();
                                      //System.out.println("---------------------------------- error");
                                  }


                                  if (result != null) {
                                      JSONObject json = new JSONObject(result);

                                      JSONObject meta = functions.getJsonObject(json, StaticVariables.META);
                                      functions.setPref(StaticVariables.HASCATEGORY, true);

                                      if (meta != null) {
                                          int code = functions.getInt(meta, StaticVariables.CODE);
                                          if (code == 200) {
                                              GettersAndSetters Details = new GettersAndSetters();
                                              Details.setServerID(functions.getPref(StaticVariables.ID, ""));
                                              Details.setName(functions.getPref(StaticVariables.DISPLAYNAME, ""));
                                              Details.setImageUrl(functions.getPref(StaticVariables.THUMBNAIL, ""));
                                              Details.setComment(message);


                                              int positioner = vpPager.getCurrentItem();
                                              JSONObject jsoner = feeds.getJSONObject(positioner);
                                                String feedIDer =  functions.getJsonString(jsoner, StaticVariables.ID);


                                              Intent it  = new Intent("com.piczler.piczler.broadcast");
                                              it.putExtra("type","comment");
                                              it.putExtra(StaticVariables.ID, feedID);
                                              sendBroadcast(it);


                                              if(feedIDer.contentEquals(feedID))
                                                  {
                                                      commentArrayList.add(Details);
                                                      adapter.notifyDataSetInvalidated();
                                                      etComment.setText("");
                                                  }


                                          } else if (code == 403 || code == 401) {
                                              functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                          } else {
                                              functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                          }
                                      } else {
                                          functions.showMessage("Unable to send comment");
                                      }

                                  } else {
                                      functions.showMessage("Unable to send comment");
                                  }

                              } catch (Exception ex) {
                                  ex.printStackTrace();
                              }
                          }
                      });


          } else {
              Toast.makeText(this, "Unable to send comment", Toast.LENGTH_LONG).show();
          }






      }catch (Exception ex)
      {
          ex.printStackTrace();
      }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.rlPlayLayout:
               try
               {
                   JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());


                   int type = functions.getInt(json, StaticVariables.TYPE);


                   if(type == 2)
                   {
                       System.out.println("rrrrrrrrrr:  "+feeds.toString());

                   }else if(type == 1)
                   {
                       JSONObject videos = functions.getJsonObject(json,StaticVariables.VIDEOS);

                       String imageUrl ="";
                       JSONObject images = functions.getJsonObject(json, StaticVariables.IMAGES);

                       if(images != null){
                           JSONObject mobileRes = functions.getJsonObject(images,StaticVariables.MOBILE_RESOLUTION);
                           if(mobileRes != null){
                               imageUrl = functions.getJsonString(mobileRes,StaticVariables.URL);

                           }
                       }


                       if(videos != null)
                       {
                           JSONObject mobRes = functions.getJsonObject(videos,StaticVariables.MOBILE_RESOLUTION);
                           if(mobRes!= null)
                           {
                           /*

                           Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
                           intent.setDataAndType(Uri.parse(newVideoPath), "video/mp4");
                           startActivity(intent);
                    */
                               String newVideoPath = functions.getJsonString(mobRes,StaticVariables.URL);
                               Intent it= new Intent(MainHome.this,VideoPlayer.class);
                               it.putExtra(StaticVariables.URL, newVideoPath);
                               it.putExtra(StaticVariables.IMAGES, imageUrl);
                               startActivity(it);
                           }
                       }
                   }


               }catch (Exception ex)
               {
                   ex.printStackTrace();
               }




                break;
            case R.id.rlHome:
                rpHome.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                       // selectedPos = 0;
                        if(feedsHolder.length()>0)
                            vpPager.setCurrentItem(0,false);
                        else
                        {
                            vpPager.setVisibility(View.GONE);

                            pbBar.setVisibility(View.VISIBLE);
                            tvNoMedia.setVisibility(View.GONE);
                            llBottomHold.setVisibility(View.VISIBLE);
                            llRightHolder.setVisibility(View.VISIBLE);
                            offset = 0;
                            getFeeds(offset, limit);
                        }
                    }
                });
                break;
            case R.id.llUserProfile:
       try{
           JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());
           JSONObject user = json.getJSONObject(StaticVariables.USER);
           String userID = functions.getJsonString(user,StaticVariables.ID);
           if(!functions.getPref(StaticVariables.ID,"").contentEquals(userID))
             {
                 Intent it = new Intent(MainHome.this,Profile.class);
                 it.putExtra(StaticVariables.ID,userID);
                 startActivity(it);
             }else
             {
                 startActivity(new Intent(MainHome.this, UserProfile.class));
             }

       }catch (Exception ex)
       {
           ex.printStackTrace();
       }
                break;
            case R.id.rlProfile:
                rpProfile.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        startActivityForResult(new Intent(MainHome.this, UserProfile.class), SETTINGSINTENT);
                    }
                });
                break;
            case R.id.rlSettings:
                rpSettings.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {




                        final PopupMenu menu = new PopupMenu(MainHome.this,ivSettings);
                        menu.inflate(R.menu.camera);
                        menu.show();
                        //startActivityForResult(new Intent(MainHome.this, Settings.class), SETTINGSINTENT);








                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.action_video:
                                        openVideoIntent();
                                        break;
                                    case R.id.action_photo:

                                        openImageIntent();
                                        break;
                                }

                                return false;
                            }
                        });






                    }
                });
                break;

            case R.id.rlSearch:
                rpSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        if(slideDown)
                        {
                            swapView();
                        }

                        swapSearch();
                    }
                });
                break;
            case R.id.ivMore:
            try
            {
                JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());
                JSONObject user = json.getJSONObject(StaticVariables.USER);
               final String blockuserID = functions.getJsonString(user,StaticVariables.ID);

                final String name = tvSenderName.getText().toString();

                final PopupMenu menu = new PopupMenu(MainHome.this,ivMore);
                menu.inflate(R.menu.morepopup);
                menu.show();

                MenuItem block=menu.getMenu().findItem(R.id.action_Block);
                block.setTitle("Block @"+name);

                if(functions.getPref(StaticVariables.ID,"").contentEquals(blockuserID)){
                    block.setVisible(false);
                }else
                {
                    block.setVisible(true);
                }

                final  int positioner = vpPager.getCurrentItem();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_add_FlagPhoto:
                                try
                                {
                                    //menu.dismiss();
                                    JSONObject json = feeds.getJSONObject(positioner);
                                    String feedID =  functions.getJsonString(json, StaticVariables.ID);
                                    flagUser(feedID);

                                }catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                                break;
                            case R.id.action_Block:
                                AlertDialog dd = new AlertDialog.Builder(MainHome.this).create();
                                dd.setMessage("Are you sure you want block "+name+"?");
                                dd.setButton(Dialog.BUTTON_NEGATIVE, "BLOCK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        blockUser(blockuserID);
                                    }
                                });

                                dd.setButton(Dialog.BUTTON_POSITIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                dd.show();

                                break;
                        }

                        return false;
                    }
                });


            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

                break;
            case R.id.rlCategories:
                rpCategories.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        Intent it = new Intent(MainHome.this, Categories.class);
                        it.putExtra(StaticVariables.FROM, "main");
                        startActivity(it);
                    }
                });
                break;
            case R.id.rlLike:
                rpLike.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        int position = vpPager.getCurrentItem();

                        String likesString = tvLikes.getText().toString();
                        String [] likeArray = likesString.split(" ");
                        int numOfLikes = 0;
                        if(likeArray.length>0)
                        {
                            numOfLikes = Integer.parseInt(likeArray[0]);
                        }
                       try{
                           JSONObject json = feeds.getJSONObject(position);

                           String likeFeed = "";
                           int likeInt = functions.getPref("feed" + functions.getJsonString(json, StaticVariables.ID), 3);
                           if(likeInt == 3){
                               boolean hasLiked = functions.getBoolean(json,StaticVariables.USERHASLIKED);
                               if(hasLiked){
                                   ivLike.setImageResource(R.drawable.whitelike);
                                  likeFeed = StaticVariables.DELETE;
                                   numOfLikes --;
                                   functions.setPref("feed"+functions.getJsonString(json, StaticVariables.ID),2);
                               }else{
                                   ivLike.setImageResource(R.drawable.redlike);
                                   likeFeed = StaticVariables.POST;
                                   numOfLikes ++;
                                   functions.setPref("feed"+functions.getJsonString(json, StaticVariables.ID),1);
                               }
                           }else if(likeInt == 2){
                              likeFeed = StaticVariables.POST;
                               ivLike.setImageResource(R.drawable.redlike);
                               numOfLikes ++;
                               functions.setPref("feed"+functions.getJsonString(json, StaticVariables.ID),1);
                           }else if(likeInt == 1){
                               ivLike.setImageResource(R.drawable.whitelike);
                               likeFeed = StaticVariables.DELETE;
                               numOfLikes --;
                               functions.setPref("feed"+functions.getJsonString(json, StaticVariables.ID),2);

                           }


                           if(numOfLikes == 1)
                           {
                               tvLikes.setText(numOfLikes+" like");
                           }else {
                               tvLikes.setText(numOfLikes+" likes");
                           }
                           map2.put("feed" + functions.getJsonString(json, StaticVariables.ID),""+numOfLikes);
                           likeFeed(likeFeed, functions.getJsonString(json, StaticVariables.ID), position);
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                    }
                });
                break;

            case R.id.rlComment:

                try{
                    commentLimit = 10;
                    commentOffset = 0;
                    commentArrayList = new ArrayList<>();
                    comentSelectedListID = new ArrayList<>();
                    comentSelectedTosend = new ArrayList<>();
                    selectedAdapter = null;
                    int position = vpPager.getCurrentItem();
                    JSONObject json = feeds.getJSONObject(position);
                    String feedID =  functions.getJsonString(json, StaticVariables.ID);
                    new BindComments().execute(feedID);
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                rpComment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        if(slideDown)
                        {
                            swapView();
                        }

                        swapComment();
                    }
                });
                break;
            case R.id.rlSave:
                rpSave.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                        try
                        {
                            final int position = vpPager.getCurrentItem();
                            JSONObject json = feeds.getJSONObject(position);
                            final String feedID =  functions.getJsonString(json, StaticVariables.ID);
                            JSONObject user = json.getJSONObject(StaticVariables.USER);

                            String userID = functions.getJsonString(user,StaticVariables.ID);
                            if(!functions.getPref(StaticVariables.ID,"").contentEquals(userID))
                            {
                                saveMedia(feedID);
                            }else {
                                final AlertDialog dd = new AlertDialog.Builder(MainHome.this).create();
                                dd.setMessage("Are you sure you want to delete feed?");
                                dd.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                dd.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dd.dismiss();
                                        deleteFeed(feedID, position);
                                    }
                                });
                                dd.show();
                            }



                        }catch (Exception ex)
                        {
                        // System.out.println(ex);
                        }
                    }
                });
                break;
            case R.id.comment_cancel:
                swapComment();
                if(!slideDown)
                {
                    swapView();
                }

                comentSelectedListID = new ArrayList<GettersAndSetters>();
                comentSelectedTosend = new ArrayList<GettersAndSetters>();
                selectedAdapter = new CustomAdaptorSelected(comentSelectedListID,MainHome.this);
                lvCommentSelected.setAdapter(selectedAdapter);
                etComment.setText("");
                break;
            case R.id.search_cancel:
                /*
                swapSearch();
                if(!slideDown)
                {
                    swapView();
                }
                */
                etSearch.setText("");
                tvSearchTItle.setVisibility(View.VISIBLE);
                searchList = new ArrayList<>();
                RecyclerAdapterSearch recyclerAdapter = new RecyclerAdapterSearch(searchList, MainHome.this);
                recyclerView.setAdapter(recyclerAdapter);
                break;
        }
    }




    private void  swapComment(){

        if(slideComment){

            rlCommentLayout.setVisibility(View.VISIBLE);
            //  llBottomSlide.setAlpha(0.0f);

            rlCommentLayout.animate()
                    .translationY(rlCommentLayout.getHeight())
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if(slideComment)
                                rlCommentLayout.setVisibility(View.GONE);
                            slideComment = false;
                        }
                    });


        }else{
            rlCommentLayout.setVisibility(View.VISIBLE);
            rlCommentLayout.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            slideComment = true;
                        }
                    });



        }

    }








    private void  swapSearch(){

        if(slideSearch){

            rlSearchLayout.setVisibility(View.VISIBLE);
            //  llBottomSlide.setAlpha(0.0f);

            rlSearchLayout.animate()
                    .translationY(rlSearchLayout.getHeight())
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if(slideSearch)
                                rlSearchLayout.setVisibility(View.GONE);
                            slideSearch = false;
                        }
                    });


        }else{
            rlSearchLayout.setVisibility(View.VISIBLE);
            rlSearchLayout.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            slideSearch = true;
                        }
                    });



        }

    }



    private void getFeeds(final int offset, final int limit){


        llCover.setVisibility(View.GONE);
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            //System.out.println(functions.getCokies());
           // System.out.println("bbbbbbb:  "+StaticVariables.BASE_URL + "users/self/feed?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit);

         String url = "users/self/feed?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit;

            String sep = functions.getPref(StaticVariables.LOCATIONCOMMASEPERATED,"");
            if(!sep.contentEquals(""))
            {
             String sepValue = sep.substring(0, sep.length() - 1);
                url +="&&"+ StaticVariables.COUNTRYCODE + "=" +sepValue;
            }

            System.out.println("bbbbbbb:  "+url);
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + url)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                canLoad = true;
                                if (e != null) {
                                    e.printStackTrace();
                                    // System.out.println("---------------------------------- error");
                                }
                                //System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {

                                                if (!data.toString().contentEquals(userFeed)) {
                                                    if (offset == 0) {
                                                        map2 = new HashMap<String, String>();
                                                        System.out.println("bbbbbbb: data " + data.toString());
                                                        functions.setPref(StaticVariables.FEEDS, data.toString());
                                                        feedsHolder = data;

                                                        if (data.length() > 0) {
                                                            vpPager.setVisibility(View.VISIBLE);
                                                            tvNoMedia.setVisibility(View.GONE);
                                                            llBottomHold.setVisibility(View.VISIBLE);
                                                            llRightHolder.setVisibility(View.VISIBLE);
                                                            JsonArray firstArray = new JsonArray();
                                                            JsonObject firstObj = new JsonObject();
                                                            firstArray.add(firstObj);
                                                            GsonBuilder gsonb = new GsonBuilder();
                                                            Gson gson = gsonb.create();

                                                            JsonArray newFeed = gson.fromJson(data.toString(), JsonArray.class);
                                                            firstArray.addAll(newFeed);
                                                            firstObj = new JsonObject();
                                                            firstArray.add(firstObj);
                                                            feeds = new JSONArray(firstArray.toString());
                                                            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                                                            canLoad = false;
                                                            vpPager.setAdapter(mPagerAdapter);
                                                            vpPager.setCurrentItem(1);
                                                            canLoad = true;
                                                            if (!slideDown) {
                                                                swapView();
                                                            }
                                                        } else {
                                                            //hide progress
                                                            //show no medi text

                                                            vpPager.setVisibility(View.GONE);

                                                            pbBar.setVisibility(View.GONE);
                                                            tvNoMedia.setVisibility(View.VISIBLE);
                                                            llBottomHold.setVisibility(View.GONE);
                                                            llRightHolder.setVisibility(View.GONE);
                                                            if (!slideDown) {
                                                                swapView();
                                                            }
                                                        }


                                                    } else {
                                                        tvNoMedia.setVisibility(View.GONE);
                                                        llBottomHold.setVisibility(View.VISIBLE);
                                                        llRightHolder.setVisibility(View.VISIBLE);

                                                        System.out.println("aaaaaaaaaaa: " + data.toString());

                                                        new BindAsync().execute(data.toString());
                                                    }


                                                } else {
                                                    reduceLimit();
                                                    revert();

                                                }

                                            } else {
                                                functions.showMessage("Unable to retrieve data");
                                                reduceLimit();
                                                revert();
                                            }

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            reduceLimit();
                                            revert();
                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            reduceLimit();
                                            revert();
                                        }
                                    } else {
                                        functions.showMessage("Unable to retrieve media");
                                        reduceLimit();
                                        revert();
                                    }

                                } else {
                                    reduceLimit();
                                    functions.showMessage("Unable to retrieve media");
                                    revert();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }






    class BindAsync extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        int prevPos;
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


               JSONArray dataArray = new JSONArray(args[0]);
                JsonArray newFeed = new JsonArray();
                System.out.println("bbbbbbb: new size: "+dataArray.length());
                System.out.println("bbbbbbb: old size: "+ feedsHolder.length());

                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();

                newFeed = gson.fromJson(feedsHolder.toString(),JsonArray.class);

                newFeed.addAll(gson.fromJson(dataArray.toString(),JsonArray.class));

                feedsHolder = new JSONArray(newFeed.toString());


                JsonArray firstArray = new JsonArray();
                JsonObject firstObj = new JsonObject();
                firstArray.add(firstObj);

                JsonArray newFeeding = gson.fromJson(feedsHolder.toString(), JsonArray.class);

                firstArray.addAll(newFeeding);
                firstObj = new JsonObject();
                firstArray.add(firstObj);
                feeds = new JSONArray(firstArray.toString());

                System.out.println("bbbbbbb: final size: "+feeds.length());




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
            //adapter.setData(data);
           int prevPos = vpPager.getCurrentItem();
            //  llBottomSlide.setAlpha(0.0f);



            canLoad = false;
            mPagerAdapter.notifyDataSetChanged();
            vpPager.invalidate();
            canLoad = true;

            if(prevPos == 0|| prevPos == 1)
            {
                vpPager.setCurrentItem(1);
            }else
            {
                vpPager.setCurrentItem(prevPos-2);
            }
            canLoad = true;
            if(!slideDown)
            {
                swapView();
            }
        }





    }




    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$: "+position);
            if(position == 0 || position == (feeds.length()-1))
            {
                ProgressViewer hold = new ProgressViewer();

                return hold;
            }else
            {
                ImageHolder hold = new ImageHolder();
                Bundle bundle=new Bundle();
                bundle.putInt("pos",position);
                hold.setArguments(bundle);
                return hold;
            }


        }

        @Override
        public int getCount() {
           // System.out.println("-------------------- "+feeds.length());
            return feeds.length();
        }
    }







    public static class ProgressViewer extends Fragment {

        ImageView view;
        int position;
        ProgressBar pbBarImage;
        UserFunctions function;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            View theLayout = inflater.inflate(
                    R.layout.image_layout, container, false);
            view = (ImageView) theLayout.findViewById(R.id.ivImage);



            return  theLayout;
        }

    }



    public static class ImageHolder extends Fragment {

        ImageView view;
        int position;
         ProgressBar pbBarImage;
         UserFunctions function;
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

                JSONObject json = feeds.getJSONObject(position);
                 int type = function.getInt(json,StaticVariables.TYPE);
                if(type ==0 || type == 1)
                {
                    //System.out.println("################: "+json.toString());
                    JSONObject images = function.getJsonObject(json, StaticVariables.IMAGES);
                    // System.out.println("----------- "+json);
                    //tvLikes

                    if(images != null){
                        JSONObject mobileRes = function.getJsonObject(images,StaticVariables.MOBILE_RESOLUTION);
                        if(mobileRes != null){
                            String url = function.getJsonString(mobileRes,StaticVariables.URL);
                            AQuery aq = new AQuery(getActivity());
                            ImageOptions op=new ImageOptions();
                            op.fileCache = true;
                            op.memCache=true;
                            op.targetWidth = 2000;

                            aq.id(view).progress(pbBarImage).image(url, op).progress(pbBarImage);
                        }
                    }
                }else
                {
                    view.setImageResource(R.drawable.audiobackground);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

          view.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 // System.out.println("-------------------- "+position);
                  swapView();
              }
          });
            //container.addView(view);


            return  theLayout;
        }

    }


    private static void swapView()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){

            if(slideDown){

                llBottomSlide.setVisibility(View.VISIBLE);
                //  llBottomSlide.setAlpha(0.0f);

                llBottomSlide.animate()
                        .translationY(llBottomSlide.getHeight())
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if(slideDown)
                                    llBottomSlide.setVisibility(View.GONE);
                                    slideDown = false;
                            }
                        });


            }else{
                llBottomSlide.setVisibility(View.VISIBLE);
                llBottomSlide.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                slideDown = true;
                            }
                        });



            }

            if(slideRight){

                llRight.setVisibility(View.VISIBLE);
                //  llBottomSlide.setAlpha(0.0f);

                llRight.animate()
                        .translationX(llRight.getWidth())
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if(slideRight)
                                    llRight.setVisibility(View.GONE);
                                slideRight = false;
                            }
                        });


            }else{
                llRight.setVisibility(View.VISIBLE);
                llRight.animate()
                        .translationX(0)
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                slideRight = true;
                            }
                        });



            }
            if(slideTop){

                rlMenu.setVisibility(View.VISIBLE);
                //  llBottomSlide.setAlpha(0.0f);

                rlMenu.animate()
                        .translationY(-rlMenu.getHeight())
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if(slideTop)
                                    rlMenu.setVisibility(View.GONE);
                                slideTop = false;
                            }
                        });


            }else{
                rlMenu.setVisibility(View.VISIBLE);
                rlMenu.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                slideTop = true;
                            }
                        });



            }

        }else{

            if(slideDown){
                llBottomSlide.setVisibility(View.GONE);
                llRight.setVisibility(View.GONE);
                rlMenu.setVisibility(View.GONE);
                slideDown = false;
            }else{

                llBottomSlide.setVisibility(View.VISIBLE);
                llRight.setVisibility(View.VISIBLE);
                rlMenu.setVisibility(View.VISIBLE);

                slideDown = true;
            }


        }
    }


    private void likeFeed(final String function, final String feedID, final int position){

        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/

              Ion.with(this)
                    .load(function,StaticVariables.BASE_URL + "media/"+feedID+"/likes")
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
                                    //System.out.println("---------------------------------- error");
                                }
                               // System.out.println("------------------------------- " + result);

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);
                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);
                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == StaticVariables.SUCCESS_CODE) {
                                            if (function.contentEquals(StaticVariables.DELETE)) {
                                               //user unliked change the status to 2
                                                functions.setPref("feed"+feedID,2);
                                            } else if (function.contentEquals(StaticVariables.POST)) {
                                               //user just liked so change the status to 1

                                                functions.setPref("feed"+feedID,1);
                                            }
                                        } else {
                                            changeBack(function, position,feedID);
                                        }

                                    } else {
                                        //change it back to the old state
                                        changeBack(function, position,feedID);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                changeBack(function, position,feedID);
                            }
                        }
                    });


        } else {
            changeBack(function, position, feedID);
        }





    }


    private void changeBack(String function, int position, String feedID) {
        if (position == selectedPos) {
            String likesString = tvLikes.getText().toString();
            String [] likeArray = likesString.split(" ");
            int numOfLikes = 0;
            if(likeArray.length>0)
            {
                numOfLikes =  Integer.parseInt(likeArray[0]);
            }

            if (function.contentEquals(StaticVariables.POST)) {
                //change back to disliked
                numOfLikes--;
                ivLike.setImageResource(R.drawable.whitelike);
                functions.setPref("feed" + feedID, 2);
            } else {
                //change back to like
                numOfLikes++;
                ivLike.setImageResource(R.drawable.redlike);
                functions.setPref("feed" + feedID, 1);
            }
            map2.put("feed" + feedID,""+numOfLikes);
               if(numOfLikes == 1)
               {
                   tvLikes.setText(numOfLikes+" like");
               }else {
                   tvLikes.setText(numOfLikes+" likes");
               }

        }else
        {
          int  numOfLikes = Integer.parseInt(map2.get("feed" + feedID));

            if (function.contentEquals(StaticVariables.POST)) {
                //change back to disliked
                numOfLikes--;
                functions.setPref("feed"+feedID,2);
            } else {
                //change back to like
                numOfLikes++;
                functions.setPref("feed"+feedID,1);
            }
            map2.put("feed" + feedID,""+numOfLikes);

        }




    }


    private void reduceLimit(){
        if(offset>0){
            offset -= 20;
        }
    }





    class BindComments extends AsyncTask<String, String, String> {

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
                String feedID =  args[0];
                Database db = new Database(MainHome.this);
                db.open();
                Cursor c = db.getSampleDetails(StaticVariables.COMMENT_JSON,feedID);
                bindComment(c);
                db.close();

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


            adapter = new CustomAdaptor(commentArrayList,MainHome.this);
            lvComment.setAdapter(adapter);

        }





    }




    private void bindComment(Cursor c)
    {


        try
        {

            GettersAndSetters Details;


            if(c.getCount()>0)
            {
                c.moveToFirst();
                String jsonString = c.getString(c.getColumnIndex(Database.JSONSTRING));


                JSONArray commentArray = new JSONArray(jsonString);
                JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());

                JSONObject user = json.getJSONObject(StaticVariables.USER);
                JSONObject profile_picture = user.getJSONObject(StaticVariables.PROFILE_PICTURE);
                JSONObject thumbnail = profile_picture.getJSONObject(StaticVariables.THUMBNAIL);
                Details = new GettersAndSetters();
                Details.setComment(functions.getJsonString(json, StaticVariables.CAPTION));
                String url = functions.getJsonString(thumbnail, StaticVariables.URL);

                String name  = functions.getJsonString(user, StaticVariables.FULLNAME);
                if(name.contentEquals(""))
                {
                    name = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                }


                Details.setName(name);
                Details.setServerID(functions.getJsonString(user, StaticVariables.ID));
                Details.setImageUrl(url);
                commentArrayList.add(Details);

                c.close();
                for(int i=(commentArray.length()-1); i>=0; i--)
                {
                    Details = new GettersAndSetters();
                    JSONObject jsonComment = commentArray.getJSONObject(i);

                    Details.setComment(functions.getJsonString(jsonComment, StaticVariables.TEXT));
                    Details.setCommentID(functions.getJsonString(jsonComment, StaticVariables.ID));
                    JSONObject from =  functions.getJsonObject(jsonComment,StaticVariables.FROM);



                    String namer  = functions.getJsonString(from, StaticVariables.FULLNAME);

                    if(namer.contentEquals(""))
                    {
                        namer = functions.getJsonString(from, StaticVariables.DISPLAYNAME);
                    }

                    Details.setName(namer);


                    Details.setServerID(functions.getJsonString(from, StaticVariables.ID));
                    JSONObject image = functions.getJsonObject(from, StaticVariables.PROFILE_PICTURE);
                    JSONObject thumbnailImage = functions.getJsonObject(image,StaticVariables.THUMBNAIL);
                    Details.setImageUrl(functions.getJsonString(thumbnailImage, StaticVariables.URL));

                    //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$:  " + functions.getJsonString(thumbnailImage,StaticVariables.URL));
                    commentArrayList.add(Details);
                    c.moveToNext();
                }

            }


        }catch (Exception ex)
        {
            ex.printStackTrace();
        }



        c.close();
    }




private void addData(String message)
{

    if(message.contentEquals(""))
    {
        comentSelectedListID = commentArrayList;
    }else
    {
        comentSelectedListID = new ArrayList<>();
        for(int i=0; i<commentArrayList.size(); i++)
        {
            if(commentArrayList.get(i).name.toLowerCase().contains(message.toLowerCase()))
            {
                //check if the id does not exist
                if(!existInArray(commentArrayList.get(i).serverID,comentSelectedListID))
                {

                    comentSelectedListID.add(commentArrayList.get(i));
                }

            }


        }
    }


       selectedAdapter = new CustomAdaptorSelected(comentSelectedListID,this);
       lvCommentSelected.setAdapter(selectedAdapter);


}


    private boolean existInArray(String id, ArrayList<GettersAndSetters> sample)
    {
        for(int i=0; i<sample.size(); i++)
        {
          if(id.contentEquals(sample.get(i).serverID))
              return true;
        }
        return  false;
    }


    private class CustomAdaptor extends BaseAdapter {
        private ArrayList<GettersAndSetters> _data;
        Context _c;

        public CustomAdaptor(ArrayList<GettersAndSetters> data, Context c) {
            // TODO Auto-generated constructor stub
            _data=data;
            _c=c;

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v=convertView;
            if(v==null){
                LayoutInflater vi=(LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v=vi.inflate(R.layout.comment_row, null);
            }



            TextView tvName= (TextView) v.findViewById(R.id.tvName);
            TextView tvComment= (TextView) v.findViewById(R.id.tvComment);
            ImageView ivProfile= (ImageView) v.findViewById(R.id.ivProfile);
            LinearLayout llProfile= (LinearLayout) v.findViewById(R.id.llProfile);


            final GettersAndSetters gt=_data.get(position);
            //ivImage.setImageResource(gt.images);
            tvName.setText(gt.name);
            tvComment.setText(gt.comment);

            AQuery aq = new AQuery(MainHome.this);
            ImageOptions op=new ImageOptions();
            op.fileCache = true;
            op.memCache=false;
            op.targetWidth = 50;
            aq.id(ivProfile).image(gt.imageUrl, op);



            llProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!functions.getPref(StaticVariables.ID,"").contentEquals(gt.serverID))
                    {
                        Intent it = new Intent(MainHome.this,Profile.class);
                        it.putExtra(StaticVariables.ID,gt.serverID);
                        startActivity(it);
                    }else
                    {
                        startActivity(new Intent(MainHome.this,UserProfile.class));
                    }
                }
            });





            return v;
        }
    }






    private class CustomAdaptorSelected extends BaseAdapter {
        private ArrayList<GettersAndSetters> _data;
        Context _c;

        public CustomAdaptorSelected(ArrayList<GettersAndSetters> data, Context c) {
            // TODO Auto-generated constructor stub
            _data=data;
            _c=c;

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return _data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v=convertView;
            if(v==null){
                LayoutInflater vi=(LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v=vi.inflate(R.layout.selected_comment_row, null);
            }



            TextView tvName= (TextView) v.findViewById(R.id.tvName);
            ImageView ivProfile= (ImageView) v.findViewById(R.id.ivProfile);


            GettersAndSetters gt=_data.get(position);
            //ivImage.setImageResource(gt.images);
            tvName.setText(gt.name);

            AQuery aq = new AQuery(MainHome.this);
            ImageOptions op=new ImageOptions();
            op.fileCache = true;
            op.memCache=false;
            op.targetWidth = 50;
            aq.id(ivProfile).image(gt.imageUrl, op);




            return v;
        }
    }







    private void bindSearchData(String jsonString)
    {
        try
        {
            searchList = new ArrayList<>();
            JSONObject data = new JSONObject(jsonString);
            GettersAndSetters Details;

            JSONArray media = functions.getJsonArray(data,StaticVariables.MEDIA);

            if(media != null)
            {
                if(media.length()>0)
                {
                    System.out.println("bbbbbb: "+media.toString());
                    Details = new GettersAndSetters();
                    Details.setName("Media");
                    Details.setLayouttype(StaticVariables.TITLETYPE);
                    searchList.add(Details);


                    Details = new GettersAndSetters();
                    Details.setContenttype(StaticVariables.PHOTOTYPE);
                    Details.setLayouttype(StaticVariables.MAINTYPE);
                    Details.setJsonString(media.toString());
                    Details.setCounter(media.length());
                    String val = etSearch.getText().toString();
                    if(val.startsWith("#"))
                        val.replaceFirst("#","");

                    Details.setName("#" + val);
                    searchList.add(Details);
                }


            }




            JSONArray users = functions.getJsonArray(data,StaticVariables.USERS);
            if(users!=null)
            {
                if(users.length()>0)
                {
                    Details = new GettersAndSetters();
                    Details.setName("People");
                    Details.setLayouttype(StaticVariables.TITLETYPE);
                    searchList.add(Details);


                    for(int i=0; i<users.length(); i++)
                    {
                       JSONObject oneUser = users.getJSONObject(i);
                         Details = new GettersAndSetters();
                        Details.setContenttype(StaticVariables.PEOPLETYPE);
                        Details.setLayouttype(StaticVariables.MAINTYPE);
                        Details.setServerID(functions.getJsonString(oneUser, StaticVariables.ID));
                        Details.setName(functions.getJsonString(oneUser, StaticVariables.DISPLAYNAME));
                        Details.setJsonString(oneUser.toString());
                        searchList.add(Details);

                    }


                }

            }

            JSONArray categories = functions.getJsonArray(data,StaticVariables.CATEGORIES);
            if(categories!=null)
            {
                if(categories.length()>0)
                {
                    Details = new GettersAndSetters();
                    Details.setName("Categories");
                    Details.setLayouttype(StaticVariables.TITLETYPE);
                    searchList.add(Details);


                    for(int i=0; i<categories.length(); i++)
                    {
                       JSONObject oneCat = categories.getJSONObject(i);
                        System.out.println("RRRRRRRRRRRRRRRRRR: "+oneCat.toString());
                        Details = new GettersAndSetters();
                        Details.setContenttype(StaticVariables.CATEGORIESTYPE);
                        Details.setLayouttype(StaticVariables.MAINTYPE);
                        Details.setJsonString(oneCat.toString());
                        Details.setName(functions.getJsonString(oneCat, StaticVariables.NAME));
                        Details.setServerID(functions.getJsonString(oneCat, StaticVariables.ID));
                        searchList.add(Details);
                    }


                }

            }



        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }





    private void makeSearch(String query){

        pbSearch.setVisibility(View.VISIBLE);
        tvSearchTItle.setVisibility(View.GONE);
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
           // System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "search?q="+query)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                canLoad = true;
                                if (e != null) {
                                    e.printStackTrace();
                                    //System.out.println("---------------------------------- error");
                                }
                                //System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                            if (data != null) {


                                                new BindSearch().execute(data.toString());


                                            } else {
                                                functions.showMessage("Unable to make search");
                                                swapSearchMain();

                                            }

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            swapSearchMain();

                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            swapSearchMain();

                                        }
                                    } else {
                                        functions.showMessage("Unable to make search");
                                        swapSearchMain();

                                    }

                                } else {
                                    functions.showMessage("Unable to make search");
                                    swapSearchMain();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            swapSearchMain();

        }


    }

    private void swapSearchMain()
    {
        pbSearch.setVisibility(View.GONE);
        if(searchList == null)
        {
            tvSearchTItle.setVisibility(View.GONE);
        }else if(searchList.size() == 0)
        {
            tvSearchTItle.setVisibility(View.GONE);
        }
    }



    class BindSearch extends AsyncTask<String, String, String> {

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
               bindSearchData(args[0]);

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
            pbSearch.setVisibility(View.GONE);

           RecyclerAdapterSearch recyclerAdapter = new RecyclerAdapterSearch(searchList, MainHome.this);
                recyclerView.setAdapter(recyclerAdapter);

            if(searchList.size() >0)
            {
                tvSearchTItle.setVisibility(View.GONE);
            }else
            {
                tvSearchTItle.setVisibility(View.VISIBLE);
            }



        }





    }




    private void initRecyclerView() {




        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));






        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        GettersAndSetters Details = searchList.get(position);

                        if(Details.layouttype == StaticVariables.MAINTYPE)
                        {
                            if(Details.contenttype == StaticVariables.PEOPLETYPE)
                            {
                                //open user profile here
                                Intent it = new Intent(MainHome.this,Profile.class);
                                it.putExtra(StaticVariables.ID,Details.serverID);
                                startActivity(it);

                            }else if(Details.contenttype == StaticVariables.CATEGORIESTYPE)
                            {
                                //open category details here
                                Intent it = new Intent(MainHome.this,PictureGrid.class);
                                it.putExtra(StaticVariables.FROM,StaticVariables.CATEGORIES);
                                it.putExtra(StaticVariables.ID,Details.serverID);
                                it.putExtra(StaticVariables.TITLE,Details.name);
                                startActivity(it);

                            }else if(Details.contenttype == StaticVariables.PHOTOTYPE)
                            {
                                //open photo details here
                                if(Details.counter>1)
                                {
                                    Intent it = new Intent(MainHome.this,PictureGrid.class);
                                    it.putExtra(StaticVariables.POSITION,0);
                                    it.putExtra(StaticVariables.FROM,StaticVariables.MEDIA);
                                    it.putExtra(StaticVariables.MEDIA,Details.jsonString);
                                    it.putExtra(StaticVariables.TITLE,"Media");
                                    startActivity(it);

                                }else
                                {
                                    Intent it = new Intent(MainHome.this,Pictures.class);
                                    it.putExtra(StaticVariables.POSITION,0);
                                    it.putExtra(StaticVariables.FROM,StaticVariables.MEDIA);
                                    it.putExtra(StaticVariables.MEDIA,Details.jsonString);
                                    startActivity(it);
                                }


                            }
                        }
                    }
                })
        );
    }




    private void saveMedia(String mediaId)
    {
        final ProgressDialog pDIalog = new ProgressDialog(this);
        pDIalog.setMessage("Saving feed...");
        pDIalog.setCancelable(true);
        pDIalog.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST", StaticVariables.BASE_URL + "media/"+mediaId+"/save")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                pDIalog.dismiss();
                                if (e != null) {
                                    e.printStackTrace();
                                    //System.out.println("---------------------------------- error");
                                }
                                //System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            functions.showMessage("Media Saved");

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            //swapSearchProgress();

                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            //swapSearchProgress();

                                        }
                                    } else {
                                        functions.showMessage("Unable to save media");
                                        //swapSearchProgress();

                                    }

                                } else {
                                    functions.showMessage("Unable to save media");
                                    //swapSearchProgress();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            //swapSearchProgress();

        }

    }









    private void deleteFeed(String mediaId, final int position)
    {
        final ProgressDialog pDIalog = new ProgressDialog(this);
        pDIalog.setMessage("Deleting feed...");
        pDIalog.setCancelable(true);
        pDIalog.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("DELETE", StaticVariables.BASE_URL + "media/"+mediaId)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                pDIalog.dismiss();
                                if (e != null) {
                                    e.printStackTrace();
                                    //System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            functions.showMessage("Media deleted");
                                            GsonBuilder gsonb = new GsonBuilder();
                                            Gson gson = gsonb.create();

                                         JsonArray   newFeed = gson.fromJson(feeds.toString(), JsonArray.class);

                                            newFeed.remove(position);
                                            feeds = new JSONArray(newFeed.toString());
                                            mPagerAdapter.notifyDataSetChanged();
                                            vpPager.invalidate();
                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                           // swapSearchProgress();

                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                           // swapSearchProgress();

                                        }
                                    } else {
                                        functions.showMessage("Unable to delete media");
                                        //swapSearchProgress();

                                    }

                                } else {
                                    functions.showMessage("Unable to delete media");
                                    //swapSearchProgress();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
           // swapSearchProgress();

        }

    }






    private void flagUser(String mediaId)
    {
        final ProgressDialog pDIalog = new ProgressDialog(this);
        pDIalog.setCancelable(true);
        pDIalog.setMessage("Flagging feed...");
        pDIalog.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST", StaticVariables.BASE_URL + "media/"+mediaId+"/flags")
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                pDIalog.dismiss();
                                if (e != null) {
                                    e.printStackTrace();
                                    //System.out.println("---------------------------------- error");
                                }
                                //System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            functions.showMessage("Media flaged");

                                        } else if (code == 403 || code == 401) {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                            //swapSearchProgress();

                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            //swapSearchProgress();

                                        }
                                    } else {
                                        functions.showMessage("Unable to flag media");
                                        //swapSearchProgress();

                                    }

                                } else {
                                    functions.showMessage("Unable to flag media");
                                    //swapSearchProgress();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            //swapSearchProgress();

        }

    }



    private boolean removeAllBlocked(String userId)
    {
        boolean reload = false;
        try
        {

            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();

           JsonArray newFeed = gson.fromJson(feedsHolder.toString(), JsonArray.class);
            JSONArray sampFeed = feedsHolder;

            System.out.println("ccccc: "+sampFeed.toString());
            for(int i=0; i<sampFeed.length(); i++)
            {

                JSONObject json = sampFeed.getJSONObject(i);
                JSONObject user = json.getJSONObject(StaticVariables.USER);
                String userID = functions.getJsonString(user, StaticVariables.ID);
                if(userID.contentEquals(userId))
                {
                    //System.out.println("--------------------- seen one now");
                    newFeed.remove(i);
                    sampFeed = new JSONArray(newFeed.toString());
                    i--;
                    reload = true;
                }
            }
if(reload)
{
    JsonArray firstArray = new JsonArray();
    JsonObject firstObj = new JsonObject();
    firstArray.add(firstObj);

    System.out.println("bbbbbb: initial tempHolder "+feedsHolder.length());

    feedsHolder = new JSONArray(newFeed.toString());

    JsonArray newFeeding = gson.fromJson(feedsHolder.toString(), JsonArray.class);

    firstArray.addAll(newFeeding);
    firstObj = new JsonObject();
    firstArray.add(firstObj);
    feeds = new JSONArray(firstArray.toString());
    functions.setPref(StaticVariables.FEEDS, feedsHolder.toString());
    System.out.println("bbbbbb: final tempHolder " + feedsHolder.length());
}

        }catch (Exception ex)
        {
            System.out.println("bbbbbb: error in bloc");
            ex.printStackTrace();
        }

        return  reload;
    }







    class RemoveBlocked extends AsyncTask<String, String, String> {

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

           boolean status = removeAllBlocked(args[0]);

             String set ="";
            if(status)
                set ="1";
            else set = "0";

            return set;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String set) {
            pDIalogi.dismiss();
            if(set.contentEquals("1"))
            {
                System.out.println("bbbbbb:  should update interface");
                int oldPos = vpPager.getCurrentItem();

                mPagerAdapter.notifyDataSetChanged();
                vpPager.invalidate();

                if(feedsHolder.length()>(oldPos-2))
                {
                    if((oldPos-2)>0)
                    {
                        vpPager.setCurrentItem(oldPos-2);
                    }else
                    {
                        vpPager.setCurrentItem(0);
                    }

                }else if(feedsHolder.length()>2) {
                    vpPager.setCurrentItem(feedsHolder.length()-2);
                }else
                {
                    vpPager.setCurrentItem(0);
                }
            }

        }





    }




    private void blockUser(final String  userID)
    {
          pDIalogi = new ProgressDialog(this);
        pDIalogi.setCancelable(false);
        pDIalogi.setMessage("Blocking ...");
        pDIalogi.show();
        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){
            //System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("POST", StaticVariables.BASE_URL + "users/blocked/"+userID+"")
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
                                    //System.out.println("---------------------------------- error");
                                }
                                System.out.println("bbbbbb: " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            System.out.println("bbbbbb: blocked");

                                          new RemoveBlocked().execute(userID);
                                        } else if (code == 403 || code == 401) {
                                            pDIalogi.dismiss();;
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                           // swapSearchProgress();

                                        } else {
                                            pDIalogi.dismiss();
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                           // swapSearchProgress();

                                        }
                                    } else {
                                        pDIalogi.dismiss();
                                        functions.showMessage("Unable block user");
                                        //swapSearchProgress();

                                    }

                                } else {
                                    pDIalogi.dismiss();
                                    functions.showMessage("Unable to flag media");
                                   // swapSearchProgress();
                                }

                            } catch (Exception ex) {
                                pDIalogi.dismiss();
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            pDIalogi.dismiss();
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            //swapSearchProgress();

        }



    }






    class PrepareChange extends AsyncTask<String, String, String> {

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
            int position = Integer.parseInt(args[0]);

            try{
                JSONObject json = feeds.getJSONObject(position);
                Intent it  = new Intent("com.piczler.piczler.broadcast");
                it.putExtra("type","comment");
                it.putExtra(StaticVariables.ID, functions.getJsonString(json, StaticVariables.ID));
                sendBroadcast(it);
            }catch (Exception ex)
            {

            }
            return ""+position;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String pos) {
            int position = Integer.parseInt(pos);


            try {


                JSONObject json = feeds.getJSONObject(position);

                String feedID = functions.getJsonString(json,StaticVariables.ID);



                int type = functions.getInt(json,StaticVariables.TYPE);
                if(type ==1)
                {
                     rlPlayLayout.setVisibility(View.VISIBLE);
                    ivPlayIcon.setVisibility(View.VISIBLE);
                }else if(type == 0)
                {
                    //hide play icon
                    rlPlayLayout.setVisibility(View.GONE);
                }else if(type == 2)
                {
                    //audio
                    rlPlayLayout.setVisibility(View.VISIBLE);
                    ivPlayIcon.setVisibility(View.GONE);
                }

                int likes = 0;
                System.out.println("bbbbbb: "+map2.get("feed" + functions.getJsonString(json, StaticVariables.ID)));
                if(map2.get("feed" + functions.getJsonString(json, StaticVariables.ID)) == null)
                {
                  likes  = functions.getInt(json,StaticVariables.LIKES);
                }else
                {
                    likes = Integer.parseInt(map2.get("feed" + functions.getJsonString(json, StaticVariables.ID)));
                }

                if(likes == 1)
                {
                    tvLikes.setText(likes+" like");
                }else {
                    tvLikes.setText(likes+" likes");
                }


                tvCaption.setText(functions.getJsonString(json, StaticVariables.CAPTION).trim());

                if (tvCaption.getTag() == null) {
                    tvCaption.setTag(tvCaption.getText());
                }

                ViewTreeObserver vto = tvCaption.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Layout l = tvCaption.getLayout();
                        if (l != null) {
                            ViewTreeObserver obs = tvCaption.getViewTreeObserver();
                            obs.removeGlobalOnLayoutListener(this);

                            String expandText = "...More";

                            try
                            {
                                System.out.println("cccccccccccccccccccc:  " + tvCaption.getLayout().getLineCount());

                                if(tvCaption.getLayout().getLineCount()>2)
                                {
                                    int lineEndIndex = tvCaption.getLayout().getLineEnd(3 - 1);
                                    System.out.println("cccccccccccccccccccc: lineednD: "+lineEndIndex +" expand: "+(lineEndIndex - expandText.length() + 1));
                                    String text = tvCaption.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                                    tvCaption.setText(text);
                                    tvCaption.setMovementMethod(LinkMovementMethod.getInstance());
                                    tvCaption.setText(
                                            addClickablePartTextViewResizable(Html.fromHtml(tvCaption.getText().toString()), tvCaption, 3, expandText), TextView.BufferType.SPANNABLE);


                                }


                            }catch (Exception ex)
                            {
                                System.out.println("cccccccccccccccccccc: lineednD: error");
                                ex.printStackTrace();
                            }



                            int lines = l.getLineCount();
                            if (lines > 0)
                                if (l.getEllipsisCount(lines-1) > 0) {

                                }else
                                {
                                    System.out.println("bbbbbb: ellipse not gotten");
                                }
                        }else
                        {
                            System.out.println("bbbbbb: null layout");
                        }

                    }
                });






                JSONObject user = json.getJSONObject(StaticVariables.USER);
                JSONObject profile_picture = user.getJSONObject(StaticVariables.PROFILE_PICTURE);
                JSONObject thumbnail = profile_picture.getJSONObject(StaticVariables.THUMBNAIL);

                String name  = functions.getJsonString(user, StaticVariables.FULLNAME);
                if(name.contentEquals(""))
                {
                    name = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                }
                tvSenderName.setText(name);

                String url = functions.getJsonString(thumbnail,StaticVariables.URL);

                String feeduserID =  functions.getJsonString(user,StaticVariables.ID);

                if(feeduserID.contentEquals(functions.getPref(StaticVariables.ID,"")))
                {
                    //set image to delete icon
                    ivSaveOrDelete.setImageResource(R.drawable.cancel_icon);
                }else
                {
                    ivSaveOrDelete.setImageResource(R.drawable.save);
                    //set image to save icon
                }

                AQuery aq = new AQuery(MainHome.this);
                ImageOptions op=new ImageOptions();
                op.fileCache = true;
                op.memCache=false;
                op.targetWidth = 50;
                aq.id(ivSenderImage).image(url, op);

                int likeInt = functions.getPref("feed" + functions.getJsonString(json, StaticVariables.ID), 3);
                //System.out.println("like int: " + likeInt);
                if (likeInt == 3) {
                    boolean hasLiked = functions.getBoolean(json, StaticVariables.USERHASLIKED);
                    if (hasLiked) {
                        ivLike.setImageResource(R.drawable.redlike);
                    } else {
                        ivLike.setImageResource(R.drawable.whitelike);
                    }
                } else if (likeInt == 2) {
                    ivLike.setImageResource(R.drawable.whitelike);
                } else if (likeInt == 1) {
                    ivLike.setImageResource(R.drawable.redlike);
                }

                //tvLikes

            } catch (Exception e) {
                e.printStackTrace();
            }

        }





    }






    private  SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {


                    try{
                        commentLimit = 10;
                        commentOffset = 0;
                        commentArrayList = new ArrayList<>();
                        comentSelectedListID = new ArrayList<>();
                        comentSelectedTosend = new ArrayList<>();
                        selectedAdapter = null;
                        int position = vpPager.getCurrentItem();
                        JSONObject json = feeds.getJSONObject(position);
                        String feedID =  functions.getJsonString(json, StaticVariables.ID);
                        new BindComments().execute(feedID);
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    if(slideDown)
                    {
                        swapView();
                    }

                    swapComment();

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }



    private void revert()
    {
        System.out.println("aaaaaaaaaaa reverting: ");
        if(!slideDown)
        {
            swapView();
        }


        if(feedsHolder.length()>0)
        {
            if(vpPager.getCurrentItem() == 0)
            {
                vpPager.setCurrentItem(1);
            }else if(vpPager.getCurrentItem() == (feeds.length()-1))
            {
                vpPager.setCurrentItem(feeds.length()-2);
            }
        }else
        {
            vpPager.setVisibility(View.GONE);

            pbBar.setVisibility(View.GONE);
            tvNoMedia.setVisibility(View.VISIBLE);
            llBottomHold.setVisibility(View.GONE);
            llRightHolder.setVisibility(View.GONE);

        }



    }






    private void openImageIntent() {


        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // galleryIntent.setType("image/*");
        // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }








    private  void openVideoIntent()
    {
        if(hasCamera())
        {
            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

            File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+StaticVariables.FILENAMEIMAGES+ System.currentTimeMillis()+".mp4");
            fileUri = Uri.fromFile(mediaFile);


            for(ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                intent.setType("video/mp4");

                cameraIntents.add(intent);
            }


            // Filesystem.
            final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            // galleryIntent.setType("image/*");
            // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // Chooser of filesystem options.
            galleryIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
            //galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

            // Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, VIDEO_CAPTURE);



        /*
        File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+StaticVariables.FILENAMEIMAGES+ System.currentTimeMillis()+".mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = Uri.fromFile(mediaFile);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
        */

        }else
        {
            functions.showMessage("Device does not support video recording");
        }
    }



    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        } else {
            return false;
        }
    }







    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
               // Log.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }
}
