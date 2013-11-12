package com.gracecode.android.presentation.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.helper.IntentHelper;

class BaseActivity extends Activity {
    protected Huaban mHuabanApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHuabanApp = Huaban.getInstance();
        getActionBar().setIcon(android.R.color.transparent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pref:
                startActivity(new Intent(BaseActivity.this, PrefActivity.class));
                return true;
            case R.id.action_donate:
                IntentHelper.openWithBrowser(BaseActivity.this, getString(R.string.url_donate));
                return true;
            case R.id.action_feedback:
                mHuabanApp.sendFeedbackEmail();
                return true;
            case R.id.action_about:
                //...
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mHuabanApp.getRequestQueue().stop();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        mHuabanApp.getRequestQueue().start();
    }

}
