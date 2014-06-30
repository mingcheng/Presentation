package com.gracecode.android.presentation.ui.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebSettings;
import android.webkit.WebViewFragment;
import com.gracecode.android.common.Logger;
import com.gracecode.android.common.helper.FileHelper;
import com.gracecode.android.common.helper.NetworkHelper;
import com.gracecode.android.common.helper.UIHelper;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.task.DownloadPstTask;
import com.gracecode.android.presentation.util.PstManager;

import java.io.IOException;

public class PstDetailFragment extends WebViewFragment {
    private static final int SHOW_DISPLAY_ERROR = 0x001;
    private static final int SHOW_PRESENTATION = 0x002;
    private static final String TEMPLATE_FILENAME = "detail.html";

    private final Pin mPin;
    private final Context mContext;
    private PstManager mPresentationsManager;
    private Huaban mHuabanApp;
    private String mPresentationUrl;

    Handler mUIHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DISPLAY_ERROR:
                    showDisplayError();
                    String message = (String) msg.obj;
                    if (message != null && message.length() > 0) {
                        UIHelper.showLongToast(mContext, message);
                    }
                    break;

                case SHOW_PRESENTATION:
                    showPresentation();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class DownloadListener implements DownloadPstTask.DownloadListener {
        private final ProgressDialog mDialog;

        DownloadListener() {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage(getString(R.string.stand_by));
            mDialog.setCancelable(false);
        }

        @Override
        public void onStart() {
            mDialog.show();
        }

        @Override
        public void onProgressUpdate() {

        }

        @Override
        public void onError(Exception e) {
            mDialog.dismiss();
            Message message = new Message();
            message.what = SHOW_DISPLAY_ERROR;
            message.obj = e.getMessage();
            mUIHandler.sendMessage(message);
        }

        @Override
        public void onFinished() {
            mDialog.dismiss();
            if (mPresentationsManager.isDownloaded(mPresentationUrl)) {
                mUIHandler.sendEmptyMessage(SHOW_PRESENTATION);
            } else {
                mUIHandler.sendEmptyMessage(SHOW_DISPLAY_ERROR);
            }
        }
    }

    public PstDetailFragment() {
        mPin = null;
        mContext = null;
        init();
    }

    public PstDetailFragment(Context context, Pin pin) {
        mPin = pin;
        mContext = context;
        init();
    }

    private void init() {
        mHuabanApp = Huaban.getInstance();
        mPresentationsManager = mHuabanApp.getPresentationsManager();
        if (mPin != null) {
            mPresentationUrl = (mHuabanApp.isDownloadRetinaImage()) ? mPin.getOriginUrl() : mPin.getBigPstUrl();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mPresentationsManager.isDownloaded(mPresentationUrl)) {
            showPresentation();
        } else {
            if (mHuabanApp.isOnlyWifiDownload() && NetworkHelper.isWifiConnected(mContext)) {
                (new DownloadPstTask(mPresentationsManager, new DownloadListener()))
                        .execute(mPresentationUrl);
            } else {
                showDisplayError();
            }
        }
    }

    private void showDisplayError() {
        showPresentation("file:///android_asset/keep-calm.png");
    }

    private void showPresentation() {
        showPresentation("file://" + mPresentationsManager.getDownloadFileName(mPresentationUrl));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showPresentation(String url) {
        try {
            WebSettings settings = getWebView().getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            getWebView().loadDataWithBaseURL(
                    "file:///android_asset/",
                    String.format(getTemplate(), url),
                    "text/html",
                    "utf-8",
                    null);
        } catch (RuntimeException e) {
            Logger.e(e.getMessage());
        }
    }

    private String getTemplate() {
        try {
            return FileHelper.getFileContent(mContext.getAssets().open(TEMPLATE_FILENAME), "UTF-8");
        } catch (IOException e) {
            return "<img src=\"%s\" />";
        }
    }
}
