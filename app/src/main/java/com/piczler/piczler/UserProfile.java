package com.piczler.piczler;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by matiyas on 11/24/15.
 */
public class UserProfile extends AppCompatActivity implements  AppBarLayout.OnOffsetChangedListener{

    ViewPager pager;
    ViewPagerAdapter adapter;
   // SlidingTabLayout tabs;
    int Numboftabs = 3;
    CharSequence Titles[]={"Piczler","Facebook","Instagram"};
    public static RelativeLayout mToolbarContainer;
    public static FragmentManager fragmentManager;
    private Toolbar toolbar;
    RelativeLayout rlOverLay;
    FloatingActionsMenu multiple_actions;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
    private static final int SENDPOST = 2;
    private static final int SETTINGSINTENT = 13;
    String image_path2 = "";
    Uri selectedImageUri;
    UserFunctions functions;
    TextView tvUserName;
    ImageView ivProfile;
    private int[] imageResId = { R.drawable.piczler, R.drawable.facebook,R.drawable.camerasample};
    private static final int VIDEO_CAPTURE = 101;
    private Uri fileUri;

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    AppBarLayout appbarLayout;
    boolean canReload = false;
    public  static int offset = 0;
    ImageView ivSettings;
    ImageView ivNotifiaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mToolbarContainer = (RelativeLayout) findViewById(R.id.toolbarContainer);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fragmentManager = getSupportFragmentManager();
        functions = new UserFunctions(this);
        rlOverLay = (RelativeLayout) findViewById(R.id.rlOverLay);
        multiple_actions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        ivNotifiaction = (ImageView) findViewById(R.id.ivNotifiaction);

       /* // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);


        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.signFacebook);
            }
        });


        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setCustomTabView(R.layout.sliding_layout, 0);
        tabs.setViewPager(pager);


*/


        appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);

        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        System.out.println("bbbbbb: maximum "+mMaxScrollSize);
       // mMaxScrollSize = appbarLayout.get;


        adapter =  new ViewPagerAdapter(fragmentManager,Titles,Numboftabs,this);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.materialup_tabs);
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        // tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(pager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }


        multiple_actions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                rlOverLay.setAlpha(0.9f);
            }

            @Override
            public void onMenuCollapsed() {
                rlOverLay.setAlpha(0.0f);
            }
        });


           /*
        FloatingActionButton photo = (FloatingActionButton) findViewById(R.id.photo);
        //photo.setIcon(R.drawable.uploadicon);
        photo.setBackgroundResource(R.drawable.uploadicon);;
        photo.setStrokeVisible(true);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiple_actions.collapse();
                startActivityForResult(new Intent(UserProfile.this, Settings.class), SETTINGSINTENT);

                //openImageIntent();

            }
        });



        FloatingActionButton video = (FloatingActionButton) findViewById(R.id.video);
        video.setStrokeVisible(true);
        video.setBackgroundResource(R.drawable.uploadvideo);
       // video.setIcon(R.drawable.uploadvideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                multiple_actions.collapse();
                openVideoIntent();
            }
        });

           */

        FloatingActionButton ordio = (FloatingActionButton) findViewById(R.id.ordio);
        //ordio.setIcon(R.drawable.camerasample);
        ordio.setBackgroundResource(R.drawable.uploadaudio);
        ordio.setStrokeVisible(true);
        ordio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiple_actions.collapse();
                startActivityForResult(new Intent(UserProfile.this,RecordAudio.class),SENDPOST);
            }
        });




        FloatingActionButton orderMagazine = (FloatingActionButton) findViewById(R.id.orderMagazine);
        //orderMagazine.setIcon(R.drawable.camerasample);
        orderMagazine.setBackgroundResource(R.drawable.uploadmagazine);
        orderMagazine.setStrokeVisible(true);

        orderMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiple_actions.collapse();
                Intent it = new Intent(UserProfile.this, OrderMagazine.class);
                it.putExtra("offset", offset);
                startActivity(it);
            }
        });


        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UserProfile.this, Settings.class), SETTINGSINTENT);

            }
        });



        String name  = functions.getPref(StaticVariables.FULLNAME, "");
        if(name.contentEquals(""))
        {
            name = functions.getPref(StaticVariables.DISPLAYNAME, "");
        }




        tvUserName.setText(name);
        AQuery aq = new AQuery(this);
        ImageOptions op=new ImageOptions();
        op.fileCache = true;
        op.memCache=true;
        op.targetWidth = 90;
        op.fallback = R.drawable.adele;
        aq.id(ivProfile).image(functions.getPref(StaticVariables.THUMBNAIL,""), op);




        try{
            File ext = Environment.getExternalStorageDirectory();
            String fileString=ext.getAbsolutePath();
            File target = new File(fileString+StaticVariables.FILENAMEIMAGES2);
            if(!target.exists()){
                if(target.mkdir()) {
                    System.out.println("Directory created --------------------------- ");
                } else {
                    System.out.println("Directory is not created --------------------- ");
                }
            }else{
                System.out.println("Aready exist --------------------- ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        ivNotifiaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,NotificationsActivity.class));
            }
        });

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




    public Uri setImageUri() {
        Uri imgUri;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/",getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis() + ".jpeg");
            imgUri = Uri.fromFile(file);
            image_path2 = file.getAbsolutePath();
        }else {
            File file = new File(getFilesDir() ,getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis()+ ".jpeg");
            imgUri = Uri.fromFile(file);
            this.image_path2 = file.getAbsolutePath();
        }
        selectedImageUri = imgUri;
        return imgUri;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {

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
                 Intent it = new Intent(UserProfile.this, ConfirmProfile.class);
                it.putExtra(StaticVariables.PROFILE_PICTURE, image_path2);
                it.putExtra(StaticVariables.TYPE, "image");
               // startActivity(it);
                startActivityForResult(it,SENDPOST);

            }else if(requestCode == SENDPOST){
                System.out.println("---------------------------------------- called");
                Bundle busket = data.getExtras();
                String dataString = busket.getString(StaticVariables.DATA);
                Intent it = new Intent(StaticVariables.RELOADIMAGES);
                it.putExtra(StaticVariables.TYPE, StaticVariables.RELOAD);
                it.putExtra(StaticVariables.DATA,dataString);
                sendBroadcast(it);
            }else if (requestCode == VIDEO_CAPTURE)
            {
                String realUrl = getRealPathFromURI(data.getData());
                if(realUrl == null)
                {
                    realUrl = ""+data.getData();
                }
               // Toast.makeText(this, "Video has been saved to:\n" +
                      //  realUrl, Toast.LENGTH_LONG).show();

                Intent it = new Intent(UserProfile.this, ConfirmProfile.class);
                it.putExtra(StaticVariables.PROFILE_PICTURE, realUrl);
                it.putExtra(StaticVariables.TYPE, "video");
                // startActivity(it);
                startActivityForResult(it, SENDPOST);
            }else if(requestCode == SETTINGSINTENT )
            {
                Bundle bb = data.getExtras();

                String type = bb.getString("type");

                if(type.contentEquals("logout"))
                {
                    Bundle bbc = new Bundle();
                    bbc.putString("type", "logout");
                    Intent it = new Intent();
                    it.putExtras(bbc);
                    setResult(RESULT_OK, it);
                   finish();
                }else {
                    canReload = true;
                }

            }
        }
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appbarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        appbarLayout.addOnOffsetChangedListener(this);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                StaticVariables.PAGERCHANGED));
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

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

          String type = intent.getStringExtra(StaticVariables.TYPE);
            if(type.contentEquals("page")){
                int pos = intent.getExtras().getInt("pos");
                if(pos == 0){
                    multiple_actions.setVisibility(View.VISIBLE);
                }else{
                    multiple_actions.setVisibility(View.GONE);
                }
            }

        }
    };


            @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                } else if (id == android.R.id.home) {
                    onBackPressed();
                }

                return super.onOptionsItemSelected(item);
            }


    @Override
    public void onBackPressed() {

        if(canReload)
        {
            Bundle bbc = new Bundle();
            bbc.putString("type", "unblock");
            Intent it = new Intent();
            it.putExtras(bbc);
            setResult(RESULT_OK,it);
        }
        super.onBackPressed();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        /*
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            ivProfile.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            ivProfile.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }

        */

      int  verticalOffset = Math.abs(i);
        int difference = appBarLayout.getTotalScrollRange();

        System.out.println("bbbbbb: difference: " + difference + " offset: " + verticalOffset);

        if (difference <= verticalOffset)
        {
            //enable
            Intent it = new Intent(StaticVariables.RELOADIMAGES);
            it.putExtra(StaticVariables.TYPE,"shift");
            it.putExtra("i",1);
            sendBroadcast(it);

        }else {
            //disable
            Intent it = new Intent(StaticVariables.RELOADIMAGES);
            it.putExtra(StaticVariables.TYPE,"shift");
            it.putExtra("i",0);
            sendBroadcast(it);
        }


    }
}
