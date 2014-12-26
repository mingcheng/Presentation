package com.gracecode.android.presentation.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.gracecode.android.common.Logger;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.adapter.PinsAdapter;
import com.gracecode.android.presentation.listener.PinsAdapterListener;

public class PinsFragment extends Fragment {
    private static final int REFRESH_COMPLETE = 0x0001;
    private static final int UPDATE_SET_CHANGED = 0x0002;
    private static final int REFRESH_ERROR = 0x0003;
    private static final int SHOW_MESSAGE = 0x0004;

    private PinsAdapter mPinsAdapter;
    private GridView mPinsGridView;
    private SwipeRefreshLayout mSwipeLayout;

    private final Handler mUIChangedChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SET_CHANGED:
                case REFRESH_COMPLETE:
                    break;
                case REFRESH_ERROR:
                case SHOW_MESSAGE:
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }

            mPinsAdapter.notifyDataSetChanged();
            mSwipeLayout.setRefreshing(false);
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


    public PinsFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pins, null);
        mPinsGridView = (GridView) view.findViewById(R.id.pins);
        mPinsAdapter = new PinsAdapter(getActivity(), mPinsAdapterListener);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSwipeLayout.setOnRefreshListener(mPinsAdapter);
        mPinsGridView.setAdapter(mPinsAdapter);
        mPinsGridView.setOnItemClickListener(mPinsAdapter);
    }
}
