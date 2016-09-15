package com.piczler.piczler;

import com.viethoa.models.AlphabetItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.img.android.sdk.configuration.AbstractConfig;
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
 * Created by matiyas on 11/16/15.
 */
public class StaticVariables {
   // public static final String INSTAGRAM_CALLBACK_BASE = "http://www.piczler.com/auth/instagram/callback";
    public static final String INSTAGRAM_CALLBACK_BASE = "http://www.piczler.com";
   public static final String INSTAGRAM_CLIENT_SECRET = "f23916b95de14b358ae13196a68749d6";
    //public static final String INSTAGRAM_CLIENT_SECRET = "d1d8c88f3eda4f7bb35a4364d82f1ba0";
    public static final String  INSTAGRAM_CLIENT_ID  = "cc5bc6bf4eb14d79a7835e1133ed5356";
   // public static final String  INSTAGRAM_CLIENT_ID  = "5b57cc5a70324faa8fde0c7d6e873704";
    public static final String  INSTAGRAM_AUTH_URL  = "https://api.instagram.com/oauth/authorize/?client_id="+INSTAGRAM_CLIENT_ID+"&client_secret="+INSTAGRAM_CLIENT_SECRET+"&redirect_uri="+INSTAGRAM_CALLBACK_BASE+"&response_type=code";
    public static final String BASE_URL = "http://api.piczler.com/beta/";
    public static final String DEVICEID = "x-dev-id";
    public static final String USERAGENT = "User-Agent";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String CONFIRM = "confirm";
    public static final String SENDERID = "141610";
    public static final String TOKEN = "token";
    public static final String MEDIA = "media";
    public static final String COMMENTTYPE = "commentType";
    public static final String LIKETYPE = "likeType";
    public static final String USER = "user";
    public static final String META = "meta";
    public static final String CODE = "code";
    public static final String DEBUG = "debug";
    public static final String DATA = "data";
    public static final String IMAGES = "images";
    public static final String SOURCE = "source";
    public static final String TEXT = "text";
    public static final String MOBILE_RESULUTION = "mobile_resolution";
    public static final String INSTAGRAM_ID = "instagram_id";
    public static final String INSTAUPDATED = "inst_updated";
    public static final String DISPLAYNAME = "display_name";
    public static final String LOCALE = "locale";
    public static final String COUNTRY = "country";
    public static final String THUMBNAILS = "thumbnail";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String THUMBNAIL = "thumbnail";
    public static final String MOBILE_RESOLUTION = "mobile_resolution";
    public static final String URL = "url";
    public static final String FULLNAME = "full_name";
    public static final String LIKES = "likes";
    public static final String FACEBOOKEID = "facebook_id";
    public static final String COKIE = "Cookie";
    public static final String METHOD = "method";
    public static final String FOLLOWERS = "followers";
    public static final String COVER = "cover";
    public static final String FOLLOW = "follow";
    public static final String UNFOLLOW = "unfollow";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String POSITION = "position";
    public static final String CATEGORIES = "categories";
    public static final String CATEGORIESSAVED = "categoriesSaved";
    public static final String ID = "id";
    public static final String USERFOLLOW = "user_followed";
    public static final String FEEDS = "FEEDS";
    public static final String TYPE = "type";
    public static final String CAPTION = "caption";
    public static final String CATEGORYIDS = "category_ids";
    public static final String USERHASLIKED = "user_has_liked";
    public static final String HASGCM = "hasGCM";
    public static final String GCM = "gcm";
    public static final String VIDEOS = "videos";
    public static final String AUDIOS = "audio";
    public static final String FROM = "from";
    public static final String RELOAD = "reload";
    public static final String HASLOGEDIN = "haslogedIn";
    public static final String HASFACEBOOKLOGIN = "hasFacebooklogin";
    public static final String HASINSTALINKED = "hasInstLinked";
    public static final String INSTAACCESSTOKEN = "instaAccessToken";
    public static final String INSTAID = "instaID";

    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String HASCATEGORY = "hascompleted";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PICTURES = "pictures";
    public static final String IDS = "ids";
    public static final String ARRRAYLIST = "arrayList";
    public static final String USERS = "users";
    public static final String FEED = "feed";
    public static final String FACEBOOKRESPONSECODE = "responseCode";
    public static final String ACCESSTOKEN = "facebook_token";
    public static final String TITLE = "title";
    public static final String HASLOCATION = "haslocation";
    public static final String LOCATIONCOMMASEPERATED = "locationComma";
    public static final String COUNTRYCODE = "country_code";
    public static final String LOCATIONMAESSEPERATED = "locNameSeperated";

    public static final Double APP_VERSION = 1.0;
    public static final int SUCCESS_CODE = 200;
    public static final String FOLLOWNOTIFY = "com.piczler.piczler.follownotify";
    public static final String PAGERCHANGED = "com.piczler.piczler.pagerchanged";
    public static final String RELOADIMAGES = "com.piczler.piczler.reloadImages";
    public static final String IMAGEMESSAGE = "com.piczler.piczler.imageMessage";
    public static final String UNBLOCK = "com.piczler.piczler.unblock";
    public static final String FRIEND = "com.piczler.piczler.friend";
    public static final String FINISHEDLOADING = "com.piczler.piczler.loading";
    public static final String COUNTRIES = "com.piczler.piczler.countries";
    public static final int FIRSTFOUR = 1;
    public static final int FIRSTTHREE = 2;
    public final static String FILENAMEIMAGES="/PICZLER/";
    public final static String FILENAMEIMAGES2="/PICZLER";

    public static final String CLIENT_ID = "your client id";
    public static final String CLIENT_SECRET = "your client secret";
    public static final String CALLBACK_URL = "redirect uri here";
    public static final String MIXPANEL_TOKEN = "0eb20fc6a32ebc013e3126243e1e823a";



    //comment keys
    public static final String COMMENTARRAY = "comments";


    public static final int COMMENT_JSON = 1;
    public static final int USERJSON = 2;
    public static final int USERMEDIAJSON = 3;
    public static final int INSTAGRAMPICS = 4;
    public static final int NOTIFICSTIONS = 5;
    public static ArrayList<GettersAndSetters> details;
    public static ArrayList<GettersAndSetters> countries = new ArrayList<>();



    /*
    public static ArrayList<GettersAndSetters> facebookMag = new ArrayList<>();
    public static ArrayList<GettersAndSetters> piczlerMag = new ArrayList<>();
    public static ArrayList<GettersAndSetters> instaMag = new ArrayList<>();
    */



    public static Map<String, String> facebookMag = new HashMap<String,String>();
    public static Map<String, String> piczlerMag = new HashMap<String,String>();
    public static Map<String, String> instaMag = new HashMap<String,String>();




    public static Boolean loading =false;
    public static Boolean loadingCOUNTRIES =false;



    //layout type
    public static final int TITLETYPE = 1;
    public static final int MAINTYPE = 2;

    //content type
    public static final int PHOTOTYPE = 1;
    public static final int PEOPLETYPE = 2;
    public static final int CATEGORIESTYPE = 3;




    public static ArrayList<AbstractConfig.ImageFilterInterface> colorFilterConfig = new ArrayList<>();
    public static  ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
    public static   List<String> strAlphabets = new ArrayList<>();


    public static AbstractConfig.ImageFilterInterface getFilter(int position)
    {



        System.out.println("*************************************** : "+position);
        switch(position)
        {
            case 1:
                return  new ColorFilterBreeze();
            case 2:
                return  new ColorFilterOrchid();

            case 3:
                return new ColorFilterChest();

            case 4:
                return  new ColorFilterEvening();

            case 5:
              return  new ColorFilterFixie();

            case 6:
                return new ColorFilterCottonCandy();

            case 7:
                return  new ColorFilterBW();

            case 8:
                return new ColorFilterLenin();

            case 9:
                return new ColorFilterFridge();

            case 10:
                return new ColorFilterQuozi();

            case 11:
                return new ColorFilterHighContrast();

            case 12:
                return new ColorFilterMellow();

            case 13:
                return new ColorFilterFall();

            case 14:
                return new  ColorFilterSunset();

            default:
                return null;
        }
    }


}
