package com.gracecode.android.presentation.util;

import android.content.Context;
import com.gracecode.android.presentation.helper.EnvironmentHelper;
import com.gracecode.android.presentation.helper.StringHashHelper;

import java.io.File;

public class PstManager {
    private final Context mContext;

    public PstManager(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public String getDownloadFileName(String url) {
        return getDownloadFile(url).getAbsolutePath();
    }

    public File getDownloadFile(String url) {
        return new File(EnvironmentHelper.getCacheDir(mContext), StringHashHelper.md5(url));
    }

    public boolean isDownloaded(String url) {
        return getDownloadFile(url).exists();
    }
}
