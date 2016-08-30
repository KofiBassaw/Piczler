package com.piczler.piczler;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bassaw on 23/11/2014.
 */
public class ContactClass {

    private Context mContext;
    public ContactClass(Context mContext){
        this.mContext=mContext;
    }


    public ArrayList<GettersAndSetters> getContactJson(){
        ArrayList<GettersAndSetters> details = new ArrayList<>();


        ContentResolver cr = mContext.getContentResolver();
        Cursor cur=cr.query(ContactsContract.Contacts.CONTENT_URI,
               null, null, null, null);
        GettersAndSetters Details;


            if (cur.getCount() > 0) {
                //System.out.println("bbbb: "+cur.getCount());
               cur.moveToFirst();


            while (!cur.isAfterLast()) {
                //System.out.println("bbbb: cool");
                Cursor mainCUrsor=null;
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));

                //System.out.println("bbbbb: move to next here now 1");
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //System.out.println("bbbbb : "+name);



                    //phone numbers
               // //System.out.println("Displaying phone numbers : ");
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next

                    //System.out.println("bbbbb: move to next here now 2");

                        mainCUrsor=null;

                    mainCUrsor = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
             if(mainCUrsor !=null){
                 while (mainCUrsor.moveToNext()) {
                     String phone = mainCUrsor.getString(
                             mainCUrsor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     int type = mainCUrsor.getInt(
                             mainCUrsor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                     //System.out.println("bbbbb : "+phone);
                     // //System.out.println("label : "+label);
                     if (phone !=null) {
                         Details = new GettersAndSetters();
                         Details.setSelected(false);
                         Details.setName(name);
                         Details.setPhoneNum(phone);
                         details.add(Details);
                     }
                 }
                 mainCUrsor.close();
             }
                }else
                {
                   // mainCUrsor.close();
                    //System.out.println("bbbbb : has no phone number");
                }
                //System.out.println("bbbbb: move to next here now 3");



               // //System.out.println("Displaying email addresses ");
                //email addresses
                    mainCUrsor=null;

                //System.out.println("bbbbb: move to next here now 5");
                cur.moveToNext();
            }
        }
        cur.close();
        cur = null;


        return  details;
    }




}
