package com.gracecode.android.presentation.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.gracecode.android.common.Logger;
import com.gracecode.android.common.helper.EnvironmentHelper;
import com.gracecode.android.common.helper.FileHelper;
import com.gracecode.android.common.helper.IntentHelper;
import com.gracecode.android.common.helper.UIHelper;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.helper.DatabaseHelper;
import com.gracecode.android.presentation.ui.fragment.PstDetailFragment;
import com.gracecode.android.presentation.util.PstManager;

import java.io.File;
import java.io.IOException;

public class DetailActivity extends BaseActivity {
    private static final int NONE_PIN = -1;
    private int mCurrentPinId;
    private Pin mPin;
    private DatabaseHelper mDatabaseHelper;
    private PstManager mPresentationsManager;
    private String mPresentationUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        mCurrentPinId = getIntent().getIntExtra(DatabaseHelper.FIELD_ID, NONE_PIN);
        if (mCurrentPinId == NONE_PIN) {
            UIHelper.showShortToast(DetailActivity.this, getString(R.string.havent_downloaded));
            finish();
            return;
        }

        mHuabanApp = Huaban.getInstance();
        mDatabaseHelper = mHuabanApp.getDatabaseHelper();
        mPin = mDatabaseHelper.getPin(mCurrentPinId);

        mPresentationsManager = mHuabanApp.getPresentationsManager();
        mPresentationUrl = (mHuabanApp.isDownloadRetinaImage()) ? mPin.getOriginUrl() : mPin.getBigPstUrl();

        if (mPin.getText().length() > 0) {
            setTitle(mPin.getText());
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new PstDetailFragment(DetailActivity.this, mPin))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
            case R.id.action_save:
                try {
                    if (!mPresentationsManager.isDownloaded(mPresentationUrl)) {
                        UIHelper.showShortToast(DetailActivity.this, getString(R.string.havent_downloaded));
                        return true;
                    }
                    File savedFile = copy2PictureDirectory(mPresentationUrl);

                    if (item.getItemId() == R.id.action_share) {
                        IntentHelper.openShareIntentWithImage(DetailActivity.this, "", Uri.fromFile(savedFile));
                    } else {
                        UIHelper.showLongToast(DetailActivity.this,
                                String.format(getString(R.string.copy_finished), savedFile.getAbsoluteFile()));
                    }
                } catch (IOException e) {
                    UIHelper.showShortToast(DetailActivity.this, getString(R.string.copy_error));
                    Logger.e(e.getMessage());
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private File copy2PictureDirectory(String url) throws IOException {
        File save = new File(EnvironmentHelper.getPictureDirectory(),
                mPresentationsManager.getSavedFileName(mPin));
        FileHelper.copyFile(mPresentationsManager.getDownloadFile(url), save);
        return save;
    }
}
