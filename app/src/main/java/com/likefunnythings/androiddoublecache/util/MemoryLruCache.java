package com.likefunnythings.androiddoublecache.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 内存缓存
 * Created by mayubao on 2016/1/22.
 */
public class MemoryLruCache extends LruCache<String, Bitmap>{

    /**
     * 应用内存大小
     */
    private static int mMemorySize = 0;


    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    private MemoryLruCache(int maxSize) {
        super(maxSize);
    }

    private static MemoryLruCache mInstance;

    public static MemoryLruCache getInstance(Context context){
        if(null == mInstance){
            synchronized (MemoryLruCache.class){
                if(null == mInstance){
                    mMemorySize = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024 / 8;
                    mInstance = new MemoryLruCache(mMemorySize);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取每一个Bitmap的大小
     *
     * @param key
     * @param bitmap
     * @return
     */
    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getWidth() * bitmap.getHeight();
    }
}
