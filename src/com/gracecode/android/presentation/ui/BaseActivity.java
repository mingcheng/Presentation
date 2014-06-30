package com.gracecode.android.presentation.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;

class BaseActivity extends FragmentActivity {
    protected Huaban mHuabanApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHuabanApp = Huaban.getInstance();
        getActionBar().setIcon(android.R.color.transparent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PackageInfo packageInfo = mHuabanApp.getPackageInfo();

        switch (item.getItemId()) {
            case R.id.action_pref:
                startActivity(new Intent(BaseActivity.this, PrefActivity.class));
                return true;
            case R.id.action_feedback:
                mHuabanApp.sendFeedbackEmail(this,
                        getString(R.string.feedback_subject, getString(R.string.app_name), packageInfo.versionName));
                return true;
            case R.id.action_about:
                mHuabanApp.showAboutDialog(this, packageInfo);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            findViewById(android.R.id.content).getRootView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
