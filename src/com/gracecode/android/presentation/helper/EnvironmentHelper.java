package com.gracecode.android.presentation.helper;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class EnvironmentHelper {

    public static int getMemoryCacheSize() {
        return (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
    }


    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }


    public static long getDiskCacheSize(Context context) {
        StatFs status = new StatFs(getCacheDir(context).getAbsolutePath());
        return Math.abs(status.getAvailableBlocks() * status.getBlockSize());
    }

    public static File getPictureDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }
}
