package com.piczler.piczler;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;
/**
 * Created by matiyas on 5/9/16.
 */
public class Cache {

    private static Cache instance;

    private LruCache<Object, Object> lru;

    private Cache(Context context) {

        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;

        lru = new LruCache<Object, Object>(cacheSize);

    }

    public static Cache getInstance(Context context) {

        if (instance == null) {

            instance = new Cache(context);
        }

        return instance;

    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
}
