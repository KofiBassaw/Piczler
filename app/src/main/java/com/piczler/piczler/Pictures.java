package com.piczler.piczler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matiyas on 12/5/15.
 */
public class Pictures extends AppCompatActivity implements View.OnClickListener {

    CustomViewPager vpPager;
    private PagerAdapter mPagerAdapter;
    UserFunctions functions;
    public static  LinearLayout llBottomSlide;
    public static boolean slideDown = true;
    public static  boolean slideRight = true;
    public static  boolean pbBar2Boolean = true;
    //public static  boolean slideTop = true;
    public static  boolean slideComment = false;
    public static  LinearLayout llRight;
    RippleView  rpLike,rpComment,rpSave;
    public static JSONArray feeds;
    public static JSONArray feedsHolder;
    public static TextView tvCaption,tvLikes,tvPhotCredit;
    ProgressBar pbBar;
    ProgressBar pbSendComment;
    String userFeed ="";
    public static ImageView ivLike;
    RelativeLayout rlLike,rlComment,rlSave;
    int selectedPos = 0;
    boolean canLoad = true;
    int offset = 0;
    int limit = 20;
    TextView tvSenderName;
    ImageView ivSenderImage;
    ImageView ivMore;
    Typeface td,ts;
    RelativeLayout rlCommentLayout;
    ImageView comment_cancel;

    int commentLimit = 10;
    int commentOffset = 0;
    ListView lvComment,lvCommentSelected;
    LinearLayout llUserProfile;
    RelativeLayout rlPlayLayout;
    LinearLayout llCover;
    private Toolbar toolbar;


    CustomAdaptor adapter;
    EditText etComment;
    ProgressDialog pDIalogi;

    ArrayList<GettersAndSetters> commentArrayList;
    ArrayList<GettersAndSetters> comentSelectedListID;
    ArrayList<GettersAndSetters> comentSelectedTosend;
    CustomAdaptorSelected selectedAdapter;
    boolean sendingComment = false;
    ImageView ivSaveOrDelete;
    Map<String, String> map2 = new HashMap<String, String>();
String from ="";
    int pos;
    ImageView ivPlayIcon;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_view);
        functions = new UserFunctions(this);
        from = getIntent().getStringExtra(StaticVariables.FROM);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);





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
        rlCommentLayout = (RelativeLayout) findViewById(R.id.rlCommentLayout);
        comment_cancel = (ImageView) findViewById(R.id.comment_cancel);
        rpComment = (RippleView) findViewById(R.id.rpComment);
        rpLike = (RippleView) findViewById(R.id.rpLike);
        rpSave = (RippleView) findViewById(R.id.rpSave);
        tvCaption = (TextView) findViewById(R.id.tvCaption);
        tvCaption = (TextView) findViewById(R.id.tvCaption);
        tvLikes = (TextView) findViewById(R.id.tvLikes);
        llUserProfile = (LinearLayout) findViewById(R.id.llUserProfile);
        ivLike = (ImageView) findViewById(R.id.ivLike);
        ivMore = (ImageView) findViewById(R.id.ivMore);
        rlLike = (RelativeLayout) findViewById(R.id.rlLike);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        rlSave = (RelativeLayout) findViewById(R.id.rlSave);
        llCover = (LinearLayout) findViewById(R.id.llCover);
        pbBar = (ProgressBar) findViewById(R.id.pbBar);
        pbSendComment = (ProgressBar) findViewById(R.id.pbSendComment);
        rlPlayLayout = (RelativeLayout) findViewById(R.id.rlPlayLayout);
        ivPlayIcon = (ImageView) findViewById(R.id.ivPlayIcon);
        lvComment = (ListView) findViewById(R.id.lvComment);
        lvCommentSelected = (ListView) findViewById(R.id.lvCommentSelected);

        ts=Typeface.createFromAsset(getAssets(),"HelveticaNeue Medium.ttf");
        td=Typeface.createFromAsset(getAssets(),"HelveticaNeue Medium.ttf");
        tvSenderName = (TextView) findViewById(R.id.tvSenderName);
        ivSenderImage = (ImageView) findViewById(R.id.ivSenderImage);
        tvPhotCredit = (TextView) findViewById(R.id.tvPhotCredit);
        etComment = (EditText) findViewById(R.id.etComment);
        ivSaveOrDelete = (ImageView) findViewById(R.id.ivSaveOrDelete);


        tvCaption.setTypeface(ts);
        tvPhotCredit.setTypeface(ts);
        tvSenderName.setTypeface(ts);
        tvLikes.setTypeface(td);
        rlLike.setOnClickListener(this);
        rlComment.setOnClickListener(this);
        rlSave.setOnClickListener(this);
        comment_cancel.setOnClickListener(this);
        rlCommentLayout.setOnClickListener(this);
        llUserProfile.setOnClickListener(this);
        rlPlayLayout.setOnClickListener(this);











        if(from.contentEquals(StaticVariables.PICTURES) || from.contentEquals(StaticVariables.MEDIA)|| from.contentEquals(StaticVariables.CATEGORIES)){

            try{
                pos = getIntent().getExtras().getInt(StaticVariables.POSITION);
                userFeed = getIntent().getStringExtra(StaticVariables.MEDIA);
                feedsHolder = new JSONArray(userFeed);
                offset = feedsHolder.length();
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
                vpPager.setCurrentItem(pos+1);
                canLoad = true;

            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }else if(from.contentEquals(StaticVariables.FEED)){

            if(savedInstanceState == null)
            {
                pbBar.setVisibility(View.VISIBLE);
                getFeeds(offset, limit);

            }else
            {
                System.out.println("bbbbbb:    not null");
            }




        }else if(from.contentEquals(StaticVariables.COMMENTTYPE)  ||from.contentEquals(StaticVariables.LIKETYPE))
        {
            try
            {
                Database db = new Database(this);
                db.open();
                Cursor c = db.getSampleDetails(StaticVariables.NOTIFICSTIONS,getIntent().getStringExtra(StaticVariables.ID));
                if(c.getCount()>0)
                {
                    c.moveToFirst();
                    String jsonObj = c.getString(c.getColumnIndex(Database.JSONSTRING));

                    GsonBuilder gsonb = new GsonBuilder();
                    Gson gson = gsonb.create();

                    JsonArray firstArray = new JsonArray();
                    JsonObject firstObj = gson.fromJson(jsonObj, JsonObject.class);
                    firstArray.add(firstObj);
                    feeds = new JSONArray(firstArray.toString());
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                    canLoad = false;
                    vpPager.setAdapter(mPagerAdapter);
                    //vpPager.setCurrentItem(0);
                    canLoad = true;
                    if (!slideDown) {
                        swapView();
                    }



                    commentArrayList = new ArrayList<>();
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                    {
                        new PrepareChange().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"" + selectedPos) ;
                    }else {
                        new PrepareChange().execute("" + selectedPos);
                    }



                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                    {
                        new BindComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getIntent().getStringExtra(StaticVariables.ID)) ;
                    }else {
                        new BindComments().execute(getIntent().getStringExtra(StaticVariables.ID));
                    }





                    if(from.contentEquals(StaticVariables.COMMENTTYPE))
                    {
                        swapComment();

                    }


                }else
                {
                    pbBar.setVisibility(View.VISIBLE);
                    llCover.setVisibility(View.GONE);

                    if (canLoad) {
                        if(slideDown)
                        {
                            swapView();
                        }
                        rlPlayLayout.setVisibility(View.GONE);
                        canLoad = false;
                            getNotify(getIntent().getStringExtra(StaticVariables.ID));



                    }
                }
                c.close();
                db.close();

            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }







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


                        if(from.contentEquals(StaticVariables.MEDIA))
                        {
                            vpPager.setCurrentItem(1,false);
                        }else if(from.contentEquals(StaticVariables.COMMENTTYPE) || from.contentEquals(StaticVariables.LIKETYPE))
                        {
                           // vpPager.setCurrentItem(1,false);
                            new PrepareChange().execute("" + selectedPos);
                        }else {
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

                                    if(from.contentEquals(StaticVariables.PICTURES)){
                                        getMedia(offset,limit) ;
                                    }else if(from.contentEquals(StaticVariables.FEED)|| from.contentEquals(StaticVariables.CATEGORIES))
                                    {
                                        getFeeds(offset, limit);
                                    }



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
                                    if(from.contentEquals(StaticVariables.FEED) || from.contentEquals(StaticVariables.CATEGORIES)){
                                        getFeeds(offset, limit);
                                    }else if(from.contentEquals(StaticVariables.PICTURES))
                                    {
                                        // load the next batch of user pictures here
                                        getMedia(offset,limit);
                                    }

                                }
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


            }
        });


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
                        if (typeWord.length() > 1) {
                            String sentString = typeWord.substring(1, (typeWord.length()));
                            // System.out.println("$$$$$$$$$$$$   " + typeWord + "    $$$ " + sentString);

                            addData(sentString);
                        } else {
                            comentSelectedListID = new ArrayList<GettersAndSetters>();
                            selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, Pictures.this);
                            lvCommentSelected.setAdapter(selectedAdapter);
                        }

                    } else {
                        comentSelectedListID = new ArrayList<GettersAndSetters>();
                        selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, Pictures.this);
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
                selectedAdapter = new CustomAdaptorSelected(comentSelectedListID, Pictures.this);
                lvCommentSelected.setAdapter(selectedAdapter);
                etComment.setSelection(etComment.getText().length());
            }
        });



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





    private void getFeeds(final  int offset, final int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL +"categories/"+getIntent().getStringExtra(StaticVariables.ID)+"?"+ StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
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
                                    System.out.println("---------------------------------- error");
                                }
                                System.out.println("------------------------------- " + result);
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
                                                        System.out.println("aaaaaaaaaaa: " + data.toString());

                                                        feedsHolder = data;


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
                                        functions.showMessage("Unable to retrieve categories");
                                        reduceLimit();
                                        revert();
                                    }

                                } else {
                                    reduceLimit();
                                    revert();
                                    functions.showMessage("Unable to retrieve categories");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                reduceLimit();
                                revert();
                            }
                        }
                    });


        } else {
            reduceLimit();
            revert();
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }





    private void getMedia(final int offset, int limit){


        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later




            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            System.out.println("bbbbbb: "+StaticVariables.BASE_URL +"users/"+getIntent().getStringExtra(StaticVariables.ID)+"/media?"+StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" +limit);
            System.out.println(functions.getCokies());
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "users/"+getIntent().getStringExtra(StaticVariables.ID)+"/media?" + StaticVariables.OFFSET + "=" + offset + "&&" + StaticVariables.LIMIT + "=" + limit)
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
                                // swipyrefreshlayout.setRefreshing(false);
                                System.out.println("------------------------------- " + result);
                                pbBar.setVisibility(View.GONE);
                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONArray data = functions.getJsonArray(json, StaticVariables.DATA);
                                            if (data != null) {
                                                if (offset == 0) {
                                                    map2 = new HashMap<String, String>();
                                                    System.out.println("aaaaaaaaaaa: " + data.toString());

                                                    feedsHolder = data;


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

                                                    new BindAsync().execute(data.toString());
                                                }


                                            } else {
                                                reduceLimit();
                                                revert();
                                                functions.showMessage("Unable to retrieve data");
                                            }

                                        } else if (code == 403 || code == 401) {
                                            reduceLimit();
                                            revert();
                                            pbBar.setVisibility(View.GONE);
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                        } else {
                                            reduceLimit();
                                            revert();
                                            pbBar.setVisibility(View.GONE);
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                        }
                                    } else {
                                        reduceLimit();
                                        revert();
                                        pbBar.setVisibility(View.GONE);
                                        functions.showMessage("Unable to retrieve media");
                                    }

                                } else {
                                    reduceLimit();
                                    revert();
                                    pbBar.setVisibility(View.GONE);
                                    functions.showMessage("Unable to retrieve media");
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            reduceLimit();
            revert();
            pbBar.setVisibility(View.GONE);
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
        }


    }












    private void getNotify(final String userID)
    {

        ConnectionDetector cd=new ConnectionDetector(this);
        if(cd.isConnectingToInternet()){//chang this function later


            //http://admin.assurances.gov.gh/api/v1.0/users/login/


            //
            Ion.with(this)
                    .load("GET", StaticVariables.BASE_URL + "media/" +userID)
                    .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                    .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                    .setHeader(StaticVariables.COKIE, functions.getCokies())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                pbBar.setVisibility(View.GONE);

                                if (result != null) {
                                    JSONObject json = new JSONObject(result);

                                    JSONObject meta = functions.getJsonObject(json, StaticVariables.META);

                                    if (meta != null) {
                                        int code = functions.getInt(meta, StaticVariables.CODE);
                                        if (code == 200) {
                                            JSONObject data = functions.getJsonObject(json, StaticVariables.DATA);
                                            if (data != null) {
                                                Database db = new Database(Pictures.this);
                                                db.open();
                                                db.insertSampleDetails(userID, StaticVariables.NOTIFICSTIONS, data.toString());
                                                db.close();


                                                GsonBuilder gsonb = new GsonBuilder();
                                                Gson gson = gsonb.create();

                                                JsonArray firstArray = new JsonArray();
                                                JsonObject firstObj = gson.fromJson(data.toString(), JsonObject.class);
                                                firstArray.add(firstObj);
                                                feeds = new JSONArray(firstArray.toString());
                                                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                                                canLoad = false;
                                                vpPager.setAdapter(mPagerAdapter);
                                                //vpPager.setCurrentItem(0);
                                                canLoad = true;
                                                if (!slideDown) {
                                                    swapView();
                                                }

                                                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                                                {
                                                    new PrepareChange().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"" + selectedPos) ;
                                                }else {
                                                    new PrepareChange().execute("" + selectedPos);
                                                }



                                                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                                                {
                                                    new BindComments().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getIntent().getStringExtra(StaticVariables.ID)) ;
                                                }else {
                                                    new BindComments().execute(getIntent().getStringExtra(StaticVariables.ID));
                                                }


                                                if(from.contentEquals(StaticVariables.COMMENTTYPE))
                                                swapComment();

                                            }

                                        }
                                    }

                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        }

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

                    }else if(type == 1)
                    {



                        // System.out.println("----------- "+json);
                        //tvLikes



                        String imageUrl ="";
                        JSONObject images = functions.getJsonObject(json, StaticVariables.IMAGES);

                        if(images != null){
                            JSONObject mobileRes = functions.getJsonObject(images,StaticVariables.MOBILE_RESOLUTION);
                            if(mobileRes != null){
                                imageUrl = functions.getJsonString(mobileRes,StaticVariables.URL);

                            }
                        }

                        JSONObject videos = functions.getJsonObject(json,StaticVariables.VIDEOS);

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
                                Intent it= new Intent(Pictures.this,VideoPlayer.class);
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
            case R.id.llUserProfile:
                try{
                    JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());
                    JSONObject user = json.getJSONObject(StaticVariables.USER);
                    String userID = functions.getJsonString(user,StaticVariables.ID);
                    if(!functions.getPref(StaticVariables.ID,"").contentEquals(userID))
                    {
                        Intent it = new Intent(Pictures.this,Profile.class);
                        it.putExtra(StaticVariables.ID,userID);
                        startActivity(it);
                    }else
                    {
                        startActivity(new Intent(Pictures.this,UserProfile.class));
                    }

                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                break;
            case R.id.ivMore:
                try
                {
                    JSONObject json = feeds.getJSONObject(vpPager.getCurrentItem());
                    JSONObject user = json.getJSONObject(StaticVariables.USER);
                    final String blockuserID = functions.getJsonString(user,StaticVariables.ID);

                    final String name = tvSenderName.getText().toString();

                    final PopupMenu menu = new PopupMenu(Pictures.this,ivMore);
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
                                    AlertDialog dd = new AlertDialog.Builder(Pictures.this).create();
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
                                final AlertDialog dd = new AlertDialog.Builder(Pictures.this).create();
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
                selectedAdapter = new CustomAdaptorSelected(comentSelectedListID,Pictures.this);
                lvCommentSelected.setAdapter(selectedAdapter);
                etComment.setText("");
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
            if((position == 0 || position == (feeds.length()-1)) && (!from.contentEquals(StaticVariables.COMMENTTYPE) && !from.contentEquals(StaticVariables.LIKETYPE)) )
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
            /*
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

            */

        }else{

            if(slideDown){
                llBottomSlide.setVisibility(View.GONE);
                llRight.setVisibility(View.GONE);
                slideDown = false;
            }else{

                llBottomSlide.setVisibility(View.VISIBLE);
                llRight.setVisibility(View.VISIBLE);

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
                                                functions.setPref("feed" + feedID, 2);
                                            } else if (function.contentEquals(StaticVariables.POST)) {
                                                //user just liked so change the status to 1

                                                functions.setPref("feed" + feedID, 1);
                                            }
                                        } else {
                                            changeBack(function, position, feedID);
                                        }

                                    } else {
                                        //change it back to the old state
                                        changeBack(function, position, feedID);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                changeBack(function, position, feedID);
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
            map2.put("feed" + feedID, "" + numOfLikes);
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
                Database db = new Database(Pictures.this);
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


            adapter = new CustomAdaptor(commentArrayList,Pictures.this);
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

            AQuery aq = new AQuery(Pictures.this);
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
                        Intent it = new Intent(Pictures.this,Profile.class);
                        it.putExtra(StaticVariables.ID,gt.serverID);
                        startActivity(it);
                    }else
                    {
                        startActivity(new Intent(Pictures.this,UserProfile.class));
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

            AQuery aq = new AQuery(Pictures.this);
            ImageOptions op=new ImageOptions();
            op.fileCache = true;
            op.memCache=false;
            op.targetWidth = 50;
            aq.id(ivProfile).image(gt.imageUrl, op);




            return v;
        }
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
                                            ;

                                        } else {
                                            functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                            ;

                                        }
                                    } else {
                                        functions.showMessage("Unable to save media");
                                        ;

                                    }

                                } else {
                                    functions.showMessage("Unable to save media");
                                    ;
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });


        } else {
            Toast.makeText(this, "No internet Connection Please try again later", Toast.LENGTH_LONG).show();
            ;

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


                tvCaption.setText(functions.getJsonString(json, StaticVariables.CAPTION));

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
                            System.out.println("bbbbbb: elipse gottn");

                            try
                            {

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

                AQuery aq = new AQuery(Pictures.this);
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





    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
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
        if(vpPager.getCurrentItem() == 0)
        {
            vpPager.setCurrentItem(1);
        }else if(vpPager.getCurrentItem() == (feeds.length()-1))
        {
            vpPager.setCurrentItem(feeds.length()-2);
        }

    }
}
