package com.gracecode.android.presentation;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.gracecode.android.presentation.cache.BitmapLruCache;
import com.gracecode.android.presentation.helper.DatabaseHelper;
import com.gracecode.android.presentation.helper.IntentHelper;
import com.gracecode.android.presentation.helper.UIHelper;
import com.gracecode.android.presentation.util.Logger;
import com.gracecode.android.presentation.util.PstManager;

public class Huaban extends Application {
    public static final String KEY_CLEAR_CACHE = "KEY_CLEAR_CACHE";
    public static final String KEY_ABOUT = "KEY_ABOUT";
    public static final String KEY_DONATE = "KEY_DONATE";
    public static final String KEY_ONLY_WIFI_DOWNLOAD = "KEY_ONLY_WIFI_DOWNLOAD";
    public static final String KEY_AUTO_DOWNLOAD = "KEY_AUTO_DOWNLOAD";
    public static final String KEY_FEEDBACK = "KEY_FEEDBACK";
    private static final String KEY_DOWNLOAD_RETINA_IMAGE = "KEY_DOWNLOAD_RETINA_IMAGE";

    public static final String URL_HUABAN = "http://huaban.com/";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) " +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.69 Safari/537.36";
    public static final long PAGE_SIZE = 25;
    public static final int APP_VERSION = 1;
    public static final String DATABASE_NAME = "pin.sqlite";
    public static final int TIMEOUT = 5000;

    private static Huaban mInstance;
    private RequestQueue mRequestQueue;
    private Gson mGson;
    private DatabaseHelper mDatabaseHelper;
    private PstManager mPresentationsManager;
    private ImageLoader mImageLoader;

    public static Huaban getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = Huaban.this;

        mGson = new Gson();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(getApplicationContext()));
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        mPresentationsManager = new PstManager(getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public Gson getGson() {
        return mGson;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDatabaseHelper.close();
    }

    public PackageInfo getPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(Huaban.this);
    }

    public void setOnlyWifiDownload(boolean flag) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(KEY_ONLY_WIFI_DOWNLOAD, flag);
        editor.commit();
    }

    public boolean isOnlyWifiDownload() {
        return getSharedPreferences().getBoolean(Huaban.KEY_ONLY_WIFI_DOWNLOAD, true);
    }

    public void sendFeedbackEmail() {
        String subject = String.format(
                getString(R.string.feedback_title), getString(R.string.app_name), getPackageInfo().versionName);

        try {
            IntentHelper.sendMail(Huaban.this, getString(R.string.email), subject, "");
        } catch (RuntimeException e) {
            Logger.e(e.getMessage());
            UIHelper.showShortToast(Huaban.this, getString(R.string.send_email_faild));
        }
    }

    public PstManager getPresentationsManager() {
        return mPresentationsManager;
    }

    public boolean isDownloadRetinaImage() {
        return getSharedPreferences().getBoolean(Huaban.KEY_DOWNLOAD_RETINA_IMAGE, true);
    }
}
