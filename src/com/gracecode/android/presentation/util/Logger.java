package com.gracecode.android.presentation.util;

import android.util.Log;
import com.gracecode.android.presentation.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-11-6
 */
public class Logger {
    static final private String TAG = "com.gracecode.android.presentation";

    static final public void e(String message) {
        log(Log.ERROR, message);
    }

    static final public void v(String message) {
        log(Log.VERBOSE, message);
    }

    static final public void i(String message) {
        log(Log.INFO, message);
    }


    public static void w(String s) {
        log(Log.WARN, s);
    }

    static final private int log(int level, String message) {
        if (BuildConfig.DEBUG) {
            switch (level) {
                case Log.ERROR:
                    return Log.e(TAG, message);

                case Log.INFO:
                    return Log.i(TAG, message);

                case Log.WARN:
                    return Log.w(TAG, message);
            }
        }

        return Log.v(TAG, message);
    }
}
