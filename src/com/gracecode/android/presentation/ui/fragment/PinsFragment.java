package com.gracecode.android.presentation.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.adapter.PinsAdapter;
import com.gracecode.android.presentation.listener.PinsAdapterListener;
import com.gracecode.android.common.Logger;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class PinsFragment extends Fragment {
    private static final int REFRESH_COMPLETE = 0x0001;
    private static final int UPDATE_SET_CHANGED = 0x0002;
    private static final int REFRESH_ERROR = 0x0003;
    private static final int SHOW_MESSAGE = 0x0004;

    private final Context mContext;
    private PinsAdapter mPinsAdapter;
    private PullToRefreshGridView mPinsGridView;

    private final Handler mUIChangedChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPinsGridView.onRefreshComplete();

            switch (msg.what) {
                case UPDATE_SET_CHANGED:
                    mPinsAdapter.notifyDataSetChanged();
                    break;
                case REFRESH_COMPLETE:
                    mPinsGridView.onRefreshComplete();
                    break;
                case REFRESH_ERROR:
                case SHOW_MESSAGE:
                    Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private final PinsAdapterListener mPinsAdapterListener = new PinsAdapterListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Message message = new Message();
            message.what = SHOW_MESSAGE;
            message.obj = error.getMessage();
            mUIChangedChangedHandler.sendMessage(message);
        }

        @Override
        public void onResponse(String response) {
            mUIChangedChangedHandler.sendEmptyMessage(REFRESH_COMPLETE);
        }

        @Override
        public void onSaved(int affectedRows) {
            Logger.e("Get " + affectedRows + " new records.");
            Message message = new Message();
            message.what = SHOW_MESSAGE;
            if (affectedRows > 0) {
                message.obj = String.format(getString(R.string.get_new_records), affectedRows);
            } else {
                message.obj = getString(R.string.no_newer);
            }
            mUIChangedChangedHandler.sendMessage(message);
        }
    };


    public PinsFragment(Context context) {
        mContext = context;
        mPinsAdapter = new PinsAdapter(mContext, mPinsAdapterListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pins, null);
        mPinsGridView = (PullToRefreshGridView) view.findViewById(R.id.pins);
        mPinsGridView.setAdapter(mPinsAdapter);
        mPinsGridView.setOnItemClickListener(mPinsAdapter);
        mPinsGridView.setOnRefreshListener(mPinsAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
