package com.likefunnythings.androiddoublecache.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 网络图片双缓存的工具类(包括内存缓存和硬盘缓存)
 *
 * Created by mayubao on 2016/1/22.
 */
public class DoubleCacheManager {

    private static final String TAG = DoubleCacheManager.class.getSimpleName();

    private static DoubleCacheManager mInstance;

    private Context mContext;

    /**
     * 私有构造方法
     */
    private DoubleCacheManager(Context context) {
        this.mContext = context;
    }

    public static DoubleCacheManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DoubleCacheManager.class) {
                if (null == mInstance) {
                    mInstance = new DoubleCacheManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 存放Bitmap到缓存中去
     *
     * @param key
     * @param bitmap
     * @return
     */
    public boolean putBitmap(String key, Bitmap bitmap) {

        try {
            //1.保存到内存缓存中
            if (null == MemoryLruCache.getInstance(mContext).get(key)) {//不存在内存缓存中
                MemoryLruCache.getInstance(mContext).put(key, bitmap);
            }

            //2.保存到硬盘缓存中
            if (null != getDiskLruCache() && null == getDiskLruCache().get(key)) {//不存在硬盘缓存中
                // 保存到硬盘缓存中
                //Tip:缓存到硬盘上面的key混淆文件（出现'/'导致FileNotFoundException）
                key = hashKeyForDisk(key);
                DiskLruCache.Editor editor = getDiskLruCache().edit(key);
                OutputStream os = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                if (null != os) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (Exception e) {
            // Exception happen
            //操作缓存出现异常
            Log.i(TAG, key + " putBitmap has Exception : " + e.getMessage());
            return false;
        }

        return true;
    }


    /**
     * 获取DiskLruCache实例
     *
     * @return
     */
    private DiskLruCache getDiskLruCache() {
        DiskLruCache diskLruCache = null;
        try {
            File diskCacheDir = getDiskCacheDirectory(mContext, "bitmap");

            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }

            diskLruCache = DiskLruCache.open(diskCacheDir, getAppVersion(mContext), 1, 1024 * 1024 * 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return diskLruCache;
    }

    /**
     * @param context
     * @return
     */
    private File getDiskCacheDirectory(Context context, String uniqueName) {
        String cachePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }


    /**
     * 获取AppVersion
     *
     * @param context
     * @return
     */
    private int getAppVersion(Context context) {
        int appVersion = 1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = packageInfo.versionCode;
            return appVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }


    /**
     * 从缓存中获取Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmap(String key) {

        try {
            //1.从内存缓存中获取Bitmap， 如果存在内存缓存中返回
            if (null != MemoryLruCache.getInstance(mContext).get(key)) {
                return MemoryLruCache.getInstance(mContext).get(key);
            }

            //2.从硬盘缓存中获取Bitmap， 如果存在硬盘缓存中返回
            if (null != getDiskLruCache() && null != getDiskLruCache().get(key)) {
                key = hashKeyForDisk(key);
                DiskLruCache.Snapshot snapshot = getDiskLruCache().get(key);
                InputStream is = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (Exception e) {
            Log.i(TAG, key + " getBitmap has Exception : " + e.getMessage());
        }

        return null;

    }


//============================================================================================
//============================================================================================

    /**
     * 使key符合文件名的标准
     *
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytes2HexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * 将字节数组转十六进制（0-F）
     * @param bytes
     * @return
     */
    private String bytes2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
//============================================================================================
//============================================================================================

}
