package com.piczler.piczler;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import ly.img.android.sdk.configuration.AbstractConfig;

/**
 * Created by bassaw on 10/01/2015.
 */
public class ImageSatffs {


private LruCache<String, Bitmap> mMemoryCache;


    int left;
int right;

    AbstractConfig.ImageFilterInterface filter;
    public Context context;


public ImageSatffs(int left, int right,AbstractConfig.ImageFilterInterface filter, Context context){
    this.left=left;
    this.right=right;
    this.filter = filter;
    this.context = context;
    init();

}


   private void init(){
       /*
       final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

       // Use 1/8th of the available memory for this memory cache.
       final int divisor = (1024  * maxMemory);
      // final int cacheSize = divisor / 8;


       final int memClass = ((ActivityManager) context.getSystemService(
               Context.ACTIVITY_SERVICE)).getMemoryClass();

       // Use 1/8th of the available memory for this memory cache.
       final int cacheSize = 1024 * 1024 * memClass / 8;



       System.out.println("**************** maximum siz: "+memClass+": CATCH SIZE: "+cacheSize+" divisor: "+divisor);
       mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
           @Override
           protected int sizeOf(String key, Bitmap bitmap) {
               // The cache size will be measured in kilobytes rather than
               // number of items.
               try {

                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                       System.out.println("**************** returned siz: "+bitmap.getByteCount());
                       return bitmap.getByteCount();
                   } else {
                       return  (bitmap.getRowBytes() * bitmap.getHeight())/1024;
                   }


               }catch (Exception e){
                   e.printStackTrace();
                   return  (bitmap.getRowBytes() * bitmap.getHeight())/1024;

               }
           }
       };

*/
   }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = null;
        String key;
        String position;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            position = params[1];
            final Bitmap bitmap= decodeSampledBitmapFromResource( data, left, right);
               key=params[0];






          return  bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
              int pos = Integer.parseInt(position);
                Bitmap maper = bitmap;
                if(pos>0)
                {

                       maper = StaticVariables.getFilter(pos).renderImage(bitmap);


                }

                Cache.getInstance(context).getLru().put(pos + key, maper);

                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(maper);
                }
            }
        }

    }






    public static Bitmap decodeSampledBitmapFromResource( String resId,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }




    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }





    class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }



    }




    public void loadBitmap(String resId, ImageView imageView, Bitmap mPlaceHolderBitmap, String position) {


       // final Bitmap bitmap = getBitmapFromMemCache(position+resId);
        final Bitmap bitmap = (Bitmap)Cache.getInstance(context).getLru().get(position+resId);
        if (bitmap != null) {
            System.out.println("^^^^^^^^^^ exist:  "+position+resId);
            imageView.setImageBitmap(bitmap);
        }else{
            System.out.println("^^^^^^^^^^ new one: "+position+resId);
            if (cancelPotentialWork(resId, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);


                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                {
                     task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,resId, position) ;
                }else {
                    task.execute(resId, position);
                }




            }
        }


    }
    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }




    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            System.out.println("****************************: add to image main: "+key);
           mMemoryCache.put(key, bitmap);
            System.out.println("****************************: finished: " + key);
        }else
        {
            System.out.println("****************************: add to image else: "+key);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
