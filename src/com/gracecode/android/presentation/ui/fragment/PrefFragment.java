package com.gracecode.android.presentation.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import com.gracecode.android.common.helper.FileHelper;
import com.gracecode.android.common.helper.IntentHelper;
import com.gracecode.android.common.helper.UIHelper;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;

import java.io.IOException;

public class PrefFragment extends PreferenceFragment {


    private final Context mContext;
    private final Huaban mHuabanApp;
    private final PackageInfo mPackageInfo;
    private final SharedPreferences mSharedPreferences;

    public PrefFragment(Context context) {
        mContext = context;
        mHuabanApp = Huaban.getInstance();
        mSharedPreferences = mHuabanApp.getSharedPreferences();
        mPackageInfo = mHuabanApp.getPackageInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public void onStart() {
        super.onStart();
        markVersionNumber();
        markCacheSize();
    }


    private void markVersionNumber() {
        Preference aboutPref = findPreference(Huaban.KEY_ABOUT);
        String versions = String.format(getString(R.string.about_summary), mPackageInfo.versionName,
                mPackageInfo.versionCode);
        aboutPref.setSummary(versions);
    }


    private void markCacheSize() {
        Preference cachePref = findPreference(Huaban.KEY_CLEAR_CACHE);

        String template = getString(R.string.clear_cache_summary);
        float size = FileHelper.getSizeOfDirectory(mHuabanApp.getCacheDir()) / (1024f * 1024f);

        if (size > 0.1f) {
            cachePref.setSummary(String.format(template, size));
        } else {
            cachePref.setSummary(getString(R.string.cache_is_empty));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        PackageInfo packageInfo = mHuabanApp.getPackageInfo();

        switch (preference.getKey()) {
            case Huaban.KEY_ONLY_WIFI_DOWNLOAD:
                if (!mHuabanApp.isOnlyWifiDownload()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.confirm_without_wifi_download)
                            .setIcon(android.R.color.transparent)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mHuabanApp.setOnlyWifiDownload(true);

                                            CheckBoxPreference checkBoxPreference =
                                                    (CheckBoxPreference) findPreference(Huaban.KEY_ONLY_WIFI_DOWNLOAD);
                                            checkBoxPreference.setChecked(true);
                                        }
                                    })
                            .show();
                }
                return true;

            case Huaban.KEY_AUTO_DOWNLOAD:
                return true;

            case Huaban.KEY_CLEAR_CACHE:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.confirm_clear_cache)
                        .setIcon(android.R.color.transparent)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            FileHelper.deleteDirectory(mHuabanApp.getCacheDir());
                                            markCacheSize();
                                        } catch (IOException e) {
                                            UIHelper.showShortToast(mContext, e.getMessage());
                                        }
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return true;

            case Huaban.KEY_DONATE:
                IntentHelper.openWithBrowser(mContext, getString(R.string.url_donate));
                return true;

            case Huaban.KEY_FEEDBACK:
                mHuabanApp.sendEmail(getActivity(),
                        getString(R.string.feedback_subject, getString(R.string.app_name), packageInfo.versionName));
                return true;

            case Huaban.KEY_ABOUT:
                return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
