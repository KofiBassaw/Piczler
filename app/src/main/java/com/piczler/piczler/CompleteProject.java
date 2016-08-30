package com.piczler.piczler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by matiyas on 11/14/15.
 */
public class CompleteProject extends AppCompatActivity implements View.OnClickListener {
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
    RelativeLayout rlMyOval;
    RippleView rpMyOval,rpComplet,rpSkip;
    UserFunctions functions;
    RelativeLayout rloverContainer,rlSkip;
    ImageView ivProfile;
    RelativeLayout rlComplet,rlAdd;
    String fullName;
    EditText etCompleteProfile;
    Uri selectedImageUri;
    ProgressDialog pDialog;
    String image_path2 ="";
    RippleView rpAdd;
    String from;
    TextView etStatus,tvtkTimeTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        rlMyOval = (RelativeLayout) findViewById(R.id.rlMyOval);
        rlSkip = (RelativeLayout) findViewById(R.id.rlSkip);
        rpMyOval = (RippleView) findViewById(R.id.rpMyOval);
        rlComplet = (RelativeLayout) findViewById(R.id.rlComplet);
        rlAdd = (RelativeLayout) findViewById(R.id.rlAdd);
        rpComplet = (RippleView) findViewById(R.id.rpComplet);
        rpSkip = (RippleView) findViewById(R.id.rpSkip);
        etStatus = (TextView) findViewById(R.id.etStatus);
        tvtkTimeTitle = (TextView) findViewById(R.id.tvtkTimeTitle);
        etCompleteProfile = (EditText) findViewById(R.id.etCompleteProfile);
        functions = new UserFunctions(this);
        rloverContainer = (RelativeLayout) findViewById(R.id.rloverContainer);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        rpAdd = (RippleView) findViewById(R.id.rpAdd);

        String name = functions.getPref(StaticVariables.FULLNAME,"");
        if(name != null){
            if(!name.toLowerCase().contentEquals("null")){
                etCompleteProfile.setText(name);
            }
        }

        /*

        if (Build.VERSION.SDK_INT < 16)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        */

        rlMyOval.setOnClickListener(this);
        rlComplet.setOnClickListener(this);
        rlSkip.setOnClickListener(this);
        rlAdd.setOnClickListener(this);



        from = getIntent().getStringExtra(StaticVariables.FROM);

        if(from !=null)
        {
            if(from.contentEquals("settings"))
            {
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //do the screen manupulations here
                rpSkip.setVisibility(View.GONE);
                tvtkTimeTitle.setVisibility(View.GONE);
                etStatus.setText("UPDATE PROFILE");
                String profilePic = functions.getPref(StaticVariables.THUMBNAIL,"");
                if(!profilePic.contentEquals(""))
                {
                    //Already have photo
                    rloverContainer.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.VISIBLE);
                    rpAdd.setVisibility(View.VISIBLE);


                    AQuery aq = new AQuery(this);
                    ImageOptions op=new ImageOptions();
                    op.fileCache = true;
                    op.memCache=true;
                    op.targetWidth = 90;
                   // op.fallback = R.drawable.adele;
                    aq.id(ivProfile).image(profilePic, op);
                }

            }
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
           // intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //galleryIntent.setType("image/*");
        //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                rloverContainer.setVisibility(View.GONE);
                ivProfile.setVisibility(View.VISIBLE);
                rpAdd.setVisibility(View.VISIBLE);
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
                   // System.out.println("--------------------------- camera here"+image_path2);
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    selectedImageUri = getImageUri(getApplicationContext(), photo);
                    //photo=rotate3(currImageURI);

                    image_path2 = getRealPathFromURI(selectedImageUri);

                    setCapturedImage(image_path2);
                } else {


                    /*System.out.println("--------------------------- gallery "+image_path2);
                    selectedImageUri = data == null ? null : data.getData();
                    image_path2 = functions.getRealPathFromURI(selectedImageUri);
                    */

                    selectedImageUri=data.getData();
                    image_path2 = functions.getRealPathFromURI(selectedImageUri);
                    ivProfile.setImageURI(selectedImageUri);

                }
               // Bitmap bitmap = functions.getImageFromUri(selectedImageUri);
                //ivProfile.setImageBitmap(bitmap);


            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlMyOval:
                rpMyOval.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        openImageIntent();
                    }
                });
                break;
            case R.id.rlAdd:
                rpAdd.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        openImageIntent();
                    }
                });
                break;
            case R.id.rlSkip:
                rpSkip.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        skipToCat();
                    }
                });
                break;
            case R.id.rlComplet:
                rpComplet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        fullName = functions.getText(etCompleteProfile);
                        if(fullName.contentEquals("")){
                            etCompleteProfile.setError("Provide your name");
                        }else if(selectedImageUri == null && from == null){
                            functions.showMessage("Provide profile picture");
                        }else{
                            if(selectedImageUri != null)
                            sendImageToserver(new File(image_path2));
                            else
                                sendImageToserver2();
                        }
                    }
                });
                break;

        }
    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("outputFileUri",outputFileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        outputFileUri = savedInstanceState.getString("outputFileUri");
    }

    */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() ==  android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendImageToserver(final File image){
        pDialog = functions.prepareDialog("Updating profile",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/self")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setMultipartParameter(StaticVariables.DISPLAYNAME, functions.getPref(StaticVariables.DISPLAYNAME, ""))
                .setMultipartParameter(StaticVariables.FULLNAME, fullName)
                .setMultipartFile(StaticVariables.PROFILE_PICTURE, "image/jpeg", image)
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
                                            JSONObject user = functions.getJsonObject(data, StaticVariables.USER);
                                            if (user != null) {
                                                String instaID = functions.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                                String displayName = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                                                String locale = functions.getJsonString(user, StaticVariables.LOCALE);
                                                String country = functions.getJsonString(user, StaticVariables.COUNTRY);
                                                String email = functions.getJsonString(user, StaticVariables.EMAIL);
                                                String fullname = functions.getJsonString(user, StaticVariables.FULLNAME);
                                                String facebookID = functions.getJsonString(user, StaticVariables.FACEBOOKEID);
                                                String id = functions.getJsonString(user, StaticVariables.ID);
                                                functions.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                functions.setPref(StaticVariables.DISPLAYNAME, displayName);
                                                functions.setPref(StaticVariables.LOCALE, locale);
                                                functions.setPref(StaticVariables.COUNTRY, country);
                                                functions.setPref(StaticVariables.EMAIL, email);
                                                functions.setPref(StaticVariables.FULLNAME, fullname);
                                                functions.setPref(StaticVariables.FACEBOOKEID, facebookID);
                                                functions.setPref(StaticVariables.ID, id);

                                                JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

                                                if (picture != null) {

                                                    JSONObject thumbNail = functions.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                    if (thumbNail != null) {
                                                        String thumbURL = functions.getJsonString(thumbNail, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                    }
                                                    JSONObject mrJson = functions.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                    if (mrJson != null) {
                                                        String mr = functions.getJsonString(mrJson, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.MOBILE_RESOLUTION, mr);

                                                    }


                                                }


                                            }

                                        } else {
                                            functions.showMessage("Unable to update profile picture");
                                        }

                                        skipToCat();


                                    } else if (code == 403) {
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                    } else if(code ==  400){
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                    }
                                } else {
                                    functions.showMessage("Unable to create account");
                                }

                            } else {
                                functions.showMessage("Unable to create account");
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }



    private void skipToCat() {
        if (from == null) {
            startActivity(new Intent(this, Categories.class));
            finish();
        } else{
            functions.showMessage("Profile updated");
            onBackPressed();
    }




    }




    public String getImagePath() {
        return image_path2;
    }



    private void setCapturedImage(final String imagePath){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {
                try {
                    System.out.println("--------------------------------------------- starting ");

                    if(functions.getDeviceName().contains("SAMSUNG"))
                    {
                        return imagePath;
                    }else
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
                ivProfile.setImageBitmap(decodeFile(imagePath));
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
//            int imageHeigt = map.getHeight();
          //  int imageWidth = map.getWidth();

            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }



    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj={MediaStore.Images.Media.DATA};
        Cursor cursor =getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }







    private void sendImageToserver2(){
        pDialog = functions.prepareDialog("Updating profile",false);
        pDialog.show();
        Ion.with(this)
                .load(StaticVariables.BASE_URL + "users/self")
                .setHeader(StaticVariables.USERAGENT, functions.getUserAgent())
                .setHeader(StaticVariables.DEVICEID, functions.getPhoneID())
                .setHeader(StaticVariables.COKIE, functions.getCokies())
                .setMultipartParameter(StaticVariables.DISPLAYNAME, functions.getPref(StaticVariables.DISPLAYNAME, ""))
                .setMultipartParameter(StaticVariables.FULLNAME, fullName)
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
                                            JSONObject user = functions.getJsonObject(data, StaticVariables.USER);
                                            if (user != null) {
                                                String instaID = functions.getJsonString(user, StaticVariables.INSTAGRAM_ID);
                                                String displayName = functions.getJsonString(user, StaticVariables.DISPLAYNAME);
                                                String locale = functions.getJsonString(user, StaticVariables.LOCALE);
                                                String country = functions.getJsonString(user, StaticVariables.COUNTRY);
                                                String email = functions.getJsonString(user, StaticVariables.EMAIL);
                                                String fullname = functions.getJsonString(user, StaticVariables.FULLNAME);
                                                String facebookID = functions.getJsonString(user, StaticVariables.FACEBOOKEID);
                                                String id = functions.getJsonString(user, StaticVariables.ID);
                                                functions.setPref(StaticVariables.INSTAGRAM_ID, instaID);
                                                functions.setPref(StaticVariables.DISPLAYNAME, displayName);
                                                functions.setPref(StaticVariables.LOCALE, locale);
                                                functions.setPref(StaticVariables.COUNTRY, country);
                                                functions.setPref(StaticVariables.EMAIL, email);
                                                functions.setPref(StaticVariables.FULLNAME, fullname);
                                                functions.setPref(StaticVariables.FACEBOOKEID, facebookID);
                                                functions.setPref(StaticVariables.ID, id);

                                                JSONObject picture = functions.getJsonObject(user, StaticVariables.PROFILE_PICTURE);

                                                if (picture != null) {

                                                    JSONObject thumbNail = functions.getJsonObject(picture, StaticVariables.THUMBNAIL);
                                                    if (thumbNail != null) {
                                                        String thumbURL = functions.getJsonString(thumbNail, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.THUMBNAIL, thumbURL);

                                                    }
                                                    JSONObject mrJson = functions.getJsonObject(picture, StaticVariables.MOBILE_RESOLUTION);
                                                    if (mrJson != null) {
                                                        String mr = functions.getJsonString(mrJson, StaticVariables.URL);
                                                        functions.setPref(StaticVariables.MOBILE_RESOLUTION, mr);

                                                    }


                                                }


                                            }

                                        } else {
                                            functions.showMessage("Unable to update profile picture");
                                        }

                                        skipToCat();


                                    } else if (code == 403) {
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.ERROR_MESSAGE));
                                    } else if(code ==  400){
                                        functions.showMessage(functions.getJsonString(meta, StaticVariables.DEBUG));
                                    }
                                } else {
                                    functions.showMessage("Unable to create account");
                                }

                            } else {
                                functions.showMessage("Unable to create account");
                            }

//                            e.printStackTrace();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                });
    }


}
