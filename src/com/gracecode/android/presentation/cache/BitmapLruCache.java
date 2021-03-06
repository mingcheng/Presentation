package com.gracecode.android.presentation.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader;
import com.gracecode.android.common.Logger;
import com.gracecode.android.common.helper.EnvironmentHelper;
import com.gracecode.android.common.helper.NetworkHelper;
import com.gracecode.android.common.helper.StringHelper;
import com.gracecode.android.presentation.Huaban;

import java.io.IOException;
import java.io.InputStream;

public class BitmapLruCache implements ImageLoader.ImageCache {
    private final Context mContext;
    private final Huaban mHuabanApp;
    private final Bitmap mNotAvailableBitmap;
    private static BitmapDiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mLruCache;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public BitmapLruCache(Context context) {
        mContext = context;
        mLruCache = new LruCache<>((int) EnvironmentHelper.getMemoryCacheSize());
        mHuabanApp = Huaban.getInstance();

        try {
            mDiskLruCache = new BitmapDiskLruCache(
                    EnvironmentHelper.getCacheDir(mContext),
                    EnvironmentHelper.getDiskCacheSize(mContext));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mNotAvailableBitmap = getDefaultBitmap();
    }

    private Bitmap getDefaultBitmap() {
        try {
            InputStream bitmap = mContext.getAssets().open("keep-calm.png");
            return BitmapFactory.decodeStream(bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key = StringHelper.md5(url);
        Bitmap data = mLruCache.get(key);
        if (data == null) {
            try {
                data = mDiskLruCache.get(key);
                if (data != null) {
                    Logger.i("Disk cache hitted, put to memory cache.");
                    mLruCache.put(key, data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.i("Memory cache hitted, directly return.");
        }


        if (data == null && !NetworkHelper.isWifiConnected(mContext) && mHuabanApp.isOnlyWifiDownload()) {
            return mNotAvailableBitmap;
        }

        return data;
    }


    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        String key = StringHelper.md5(url);
        mLruCache.put(key, bitmap);

        if (mDiskLruCache.isContains(key)) {
            Logger.i("Local disk cache is exists, ignore.");
            return;
        }

        try {
            Logger.i("Save bitmap data to disk cache.");
            mDiskLruCache.put(key, bitmap);
        } catch (IOException e) {
            Logger.e(e.getMessage());
        }
    }
}
