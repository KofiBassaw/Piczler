package com.piczler.piczler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by pk on 5/19/15.
 */
public class UserFunctions {
    private static Context _mcontext;
    private static SharedPreferences mSharedPreferences;

    UserFunctions(Context _mcontext){
       this._mcontext=_mcontext;
        mSharedPreferences = this._mcontext.getSharedPreferences("MyPref", 0);
    }

    public void setPref(String title, boolean value){
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putBoolean(title, value);
        ed.commit();

    }

    public void deletePref()
    {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.clear();
        ed.commit();

    }

    public void setPref(String title, String value){
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putString(title, value);
        ed.commit();

    }

    public void setPref(String title, int value){
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putInt(title, value);
        ed.commit();

    }

    public boolean getPref(String title, boolean def){
       return mSharedPreferences.getBoolean(title, def);
    }


    public String getPref(String title, String def){
        return mSharedPreferences.getString(title, def);
    }

    public int getPref(String title, int def){
        return mSharedPreferences.getInt(title, def);
    }


    public void showMessage(String message){
        Toast.makeText(_mcontext, message, Toast.LENGTH_LONG).show();
    }


    public String getText(EditText ettext){
        return  ettext.getText().toString();
    }


    public ProgressDialog prepareDialog(String message, boolean cancel){
        ProgressDialog pDialog=new ProgressDialog(_mcontext);
        pDialog.setMessage(message);
        pDialog.setCancelable(cancel);
        pDialog.setIndeterminate(true);
        return  pDialog;
    }

public String getPhoneID(){
    TelephonyManager tm;
    tm=(TelephonyManager) _mcontext.getSystemService(Context.TELEPHONY_SERVICE);
   String phoneIME=tm.getDeviceId();
    if(phoneIME==null){
        phoneIME=   Settings.Secure.getString(_mcontext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    return phoneIME;
}







    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }



public String getJsonString(JSONObject json, String title){
    try {
        String mess = json.getString(title);
        if(mess == null)
            mess = "";
        return mess;
    }catch (Exception e){
        e.printStackTrace();
        return "";

    }
}


    public int getInt(JSONObject json, String title){
        try {
            return json.getInt(title);
        }catch (Exception e){
            e.printStackTrace();
            return 0;

        }
    }

    public boolean getBoolean(JSONObject json, String title){
        try {
            return json.getBoolean(title);
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public JSONArray getArray(JSONObject json, String title){
        try{
            return json.getJSONArray(title);
        }catch (Exception e){
            e.printStackTrace();
            return null;

        }
    }


    public boolean validateEmail(String strEmail) {
        Matcher matcher;
        String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(strEmail);
        return matcher.matches();

    }






    public int anim(int index){
        switch(index){
		    /*
		     * create animations, and return them to caller.
		     */
            case 0: return  R.anim.shake;
            case 1: return  R.anim.fade;
            case 2: return R.anim.slide_left;
            case 3: return R.anim.wave_scale;
            case 4: return R.anim.hold;
            case 5: return  R.anim.zoom_enter;
            case 6: return R.anim.hold;
            case 7: return R.anim.hyperspace_in;
            case 8: return R.anim.hyperspace_out;
            case 9: return R.anim.slide_top_to_bottom;
            case 10: return  R.anim.zoom_exit;
        }
        return R.anim.slide_top_to_bottom;
    }



    public String getNumberWord(String number){
       String numWord="";
        int num=number.length();
        switch(num){
            case 3:
                numWord="THOUSAND";
           break;
            case 6:
                numWord="MILLION";
                break;
            case 9:
                numWord="BILLION";
                break;
        }

        return numWord;
    }




public boolean isEmpty(String value){

    if(value.contentEquals(""))
        return  true;

    return false;
}






    public  byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public  byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }



public JSONObject getJsonObject(JSONObject json, String title){
    try
    {
       return  json.getJSONObject(title);
    }catch (Exception e){
        e.printStackTrace();
        return  null;

    }
}
    public JSONArray getJsonArray(JSONObject json, String title){
    try
    {
       return  json.getJSONArray(title);
    }catch (Exception e){
        e.printStackTrace();
        return  null;

    }
}

    public String getJsonStringWithNull(JSONObject json, String title){
        try
        {
            return  json.getString(title);
        }catch (Exception e){
            e.printStackTrace();
            return  null;

        }
    }


    public String getUserAgent(){





        String language= Locale.getDefault().toString();
        System.out.println("language: "+language);
        TimeZone tz = TimeZone.getDefault();
        String timeZone=tz.getID();
        System.out.println("time zone: "+timeZone);
        String osLevel= Build.VERSION.RELEASE;
        String sdkLevel = String.valueOf(Build.VERSION.SDK_INT);
        System.out.println("sdk level: "+sdkLevel+" os level: "+osLevel);
        String deviceName=getDeviceName();
        System.out.println("device name: "+deviceName);
      //  String buildString="Skullbooks/1.0 AndroidPhone ("+sdkLevel+"/"+osLevel+"; "+deviceName+"; "+language+")";
        String model = Build.BRAND;

        String agent = "Piczler/"+StaticVariables.APP_VERSION+" Android Phone ("+sdkLevel+"/"+osLevel+";"+deviceName+";"+model+";"+language+")";

        return agent;

    }


    public Map<String, List<String>>  userHeaders(){

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        List<String>oneTest=new ArrayList<String>();
        oneTest.add(getUserAgent());
        params.put(StaticVariables.USERAGENT, oneTest);
        oneTest=new ArrayList<String>();
        oneTest.add(getPhoneID());
        params.put(StaticVariables.DEVICEID, oneTest);
        System.out.println("---------------------------------------User Agent: "+getUserAgent());
        return  params;
    }


    public Bitmap getImageFromUri(Uri imageUri){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(_mcontext.getContentResolver(), imageUri);
            return  bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


    public String getRealPathFromURI(Uri contentURI) {
        // can post image
        String[] proj={MediaStore.Images.Media.DATA};
        Cursor cursor =_mcontext.getContentResolver().query( contentURI,proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }



    public void shareBook(Bitmap image){
        try{
            File path  = Environment.getExternalStorageDirectory();
            File imageFile = new File(path, StaticVariables.FILENAMEIMAGES+System.currentTimeMillis()+ ".jpg");
            FileOutputStream fileOutPutStream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fileOutPutStream);

            fileOutPutStream.flush();
            fileOutPutStream.close();

            Uri photo=Uri.fromFile(imageFile);
            //set photo
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, photo);
            shareIntent.setType("image/*");
            _mcontext.startActivity(Intent.createChooser(shareIntent, "Share"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getCokies(){

        return "token="+getPref(StaticVariables.TOKEN,"");
    }
}
