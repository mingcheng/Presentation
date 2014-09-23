package com.gracecode.android.presentation.ui;

import android.os.Bundle;
import android.view.Menu;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.ui.fragment.PinsFragment;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        getFragmentManager().beginTransaction()
                .add(R.id.content, new PinsFragment(MainActivity.this))
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
