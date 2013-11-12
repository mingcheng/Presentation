package com.gracecode.android.presentation.ui;

import android.os.Bundle;
import com.gracecode.android.presentation.ui.fragment.PrefFragment;

public class PrefActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PrefFragment(PrefActivity.this))
                .commit();
    }
}
