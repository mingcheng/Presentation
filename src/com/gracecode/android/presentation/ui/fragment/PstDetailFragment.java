package com.gracecode.android.presentation.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebViewFragment;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.helper.FileHelper;
import com.gracecode.android.presentation.helper.NetworkHelper;
import com.gracecode.android.presentation.helper.UIHelper;
import com.gracecode.android.presentation.task.DownloadPstTask;
import com.gracecode.android.presentation.util.PstManager;

import java.io.IOException;

public class PstDetailFragment extends WebViewFragment {
    private static final String TEMPLATE_FILENAME = "detail.html";
    private final Pin mPin;
    private final Context mContext;
    private final PstManager mPresentationsManager;
    private final Huaban mHuabanApp;
    private final String mPresentationUrl;

    private class DownloadListener implements DownloadPstTask.DownloadListener {
        private final ProgressDialog mDialog;

        DownloadListener() {
            mDialog = new ProgressDialog(mContext);
//            mDialog.setTitle(getString(R.string.app_name));
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
            showDisplayError();
            UIHelper.showLongToast(mContext, e.getMessage());
        }

        @Override
        public void onFinished() {
            mDialog.dismiss();
            if (mPresentationsManager.isDownloaded(mPresentationUrl)) {
                showPresentation();
            } else {
                showDisplayError();
            }
        }
    }


    public PstDetailFragment(Context context, Pin pin) {
        mPin = pin;
        mContext = context;
        mHuabanApp = Huaban.getInstance();
        mPresentationsManager = mHuabanApp.getPresentationsManager();
        mPresentationUrl = (mHuabanApp.isDownloadRetinaImage()) ? mPin.getOriginUrl() : mPin.getBigPstUrl();
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

    private void showPresentation(String url) {
        try {
            WebSettings settings = getWebView().getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLightTouchEnabled(true);

            getWebView().loadDataWithBaseURL(
                    "file:///android_asset/",
                    String.format(getTemplate(), url),
                    "text/html",
                    "utf-8",
                    null);
        } catch (RuntimeException e) {
//            Logger.e(e.getMessage());
        }
    }

    private String getTemplate() {
        try {
            return FileHelper.getFileContent(mContext.getAssets().open(TEMPLATE_FILENAME));
        } catch (IOException e) {
            return "%s";
        }
    }
}
