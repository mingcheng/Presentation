package com.gracecode.android.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.R;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.helper.DatabaseHelper;
import com.gracecode.android.presentation.listener.PinsAdapterListener;
import com.gracecode.android.presentation.listener.PstRequestListener;
import com.gracecode.android.presentation.request.PstRequest;
import com.gracecode.android.presentation.ui.DetailActivity;
import com.gracecode.android.common.Logger;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.sql.SQLException;
import java.util.ArrayList;

public class PinsAdapter extends BaseAdapter
        implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<GridView> {
    private static final int MAX_PINS_NUM = 1024;
    private static final int UPDATE_SET_CHANGED = 0x001;

    private final Context mContext;
    private final DatabaseHelper mDatabaseHelper;
    private final PinsAdapterListener mListener;
    private ArrayList<Pin> mPins = new ArrayList<Pin>();
    private RequestQueue mRequestQueue;
    private boolean isRequesting = false;
    private Huaban mHuabanApp;

    private static final class Holder {
        private final NetworkImageView mImageView;
        private final TextView mTitle;

        private Holder(View v) {
            mImageView = (NetworkImageView) v.findViewById(R.id.url);
            mTitle = (TextView) v.findViewById(R.id.title);
            v.setTag(this);
        }

        public static Holder get(View v) {
            if (v.getTag() instanceof Holder) {
                return (Holder) v.getTag();
            }

            return new Holder(v);
        }
    }

    private PstRequestListener mLoadHeadResponseListener = new PstRequestListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onErrorResponse(error);
        }

        @Override
        public void onResponse(String response) {
            mListener.onResponse(response);
        }

        @Override
        public void onSaved(int affectedRows) {
            if (affectedRows > 0) {
                loadNewestPins(false);
                mUIChangedChangedHandler.sendEmptyMessage(UPDATE_SET_CHANGED);
            }

            mListener.onSaved(affectedRows);
            isRequesting = false;
        }
    };

    private PstRequestListener mLoadFootResponseListener = new PstRequestListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onErrorResponse(error);
        }

        @Override
        public void onResponse(String response) {
            mListener.onResponse(response);
        }

        @Override
        public void onSaved(int affectedRows) {
            if (affectedRows > 0) {
                loadPinsByMaxId(false);
                notifyDataSetChanged();
            }
            mListener.onSaved(affectedRows);
            isRequesting = false;
        }
    };

    public PinsAdapter(Context context, PinsAdapterListener listener) {
        mRequestQueue = Huaban.getInstance().getRequestQueue();
        mContext = context;
        mDatabaseHelper = Huaban.getInstance().getDatabaseHelper();
        mListener = listener;
        mHuabanApp = Huaban.getInstance();

        loadNewestPins(true);
    }

    // @see https://dev.twitter.com/docs/working-with-timelines
    private int getBeforeMaxId() {
        try {
            int tmp = mPins.get(0).getId();
            for (int i = 0; i < getCount(); i++) {
                int id = mPins.get(i).getId();
                if (tmp > id) tmp = id;
            }
            return tmp;
        } catch (RuntimeException e) {
            return 0;
        }
    }

    private int getAfterSinceId() {
        try {
            int id = 0;
            for (int i = 0; i < getCount(); i++) {
                int tmp = mPins.get(i).getId();
                if (tmp > id) id = tmp;
            }
            return id;
        } catch (RuntimeException e) {
            return 0;
        }
    }

    public void loadNewestPinsFromNetwork() {
        PstRequest request = new PstRequest(0, getAfterSinceId(), mLoadHeadResponseListener);
        isRequesting = true;
        mRequestQueue.add(request);
    }

    public void loadPinsByMaxIdFromNetwork() {
        PstRequest request = new PstRequest(getBeforeMaxId(), 0, mLoadFootResponseListener);
        isRequesting = true;
        mRequestQueue.add(request);
    }

    private final Handler mUIChangedChangedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SET_CHANGED:
                    notifyDataSetChanged();
                    break;
            }
        }
    };

    private void loadPinsByMaxId(boolean allowNetworkFetch) {
        if (mPins.size() > MAX_PINS_NUM) {
            Logger.w("Max " + MAX_PINS_NUM + " Pins Limited.");
            return;
        }

        try {
            ArrayList<Pin> pins = mDatabaseHelper.getPinsBeforeMaxId(getBeforeMaxId());
            if (allowNetworkFetch && !isRequesting && pins.size() < Huaban.PAGE_SIZE) {
                Logger.i("Query from network, with max " + getBeforeMaxId());
                loadPinsByMaxIdFromNetwork();
                return;
            }
            mPins.addAll(pins);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mUIChangedChangedHandler.sendEmptyMessage(UPDATE_SET_CHANGED);
        }
    }

    private void loadNewestPins(boolean allowNetworkFetch) {
        try {
            ArrayList<Pin> pins = mDatabaseHelper.getPinsAfterSinceId(getAfterSinceId());
            if (allowNetworkFetch && !isRequesting && pins.size() < Huaban.PAGE_SIZE) {
                Logger.i("There is no newer Pins exists. Query with since id " + getAfterSinceId());
                loadNewestPinsFromNetwork();
                return;
            }
            mPins.addAll(0, pins);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mUIChangedChangedHandler.sendEmptyMessage(UPDATE_SET_CHANGED);
        }
    }

    @Override
    public int getCount() {
        return mPins.size();
    }

    @Override
    public Pin getItem(int i) {
        return mPins.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Pin pin = getItem(i);
        String imageUrl = mHuabanApp.isDownloadRetinaImage() ?
                pin.getRetinaSquareThumbUrl() : pin.getSquareThumbUrl();

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.grid, null);
        }

        if (i > getCount() - 2) {
            loadPinsByMaxId(true);
        }

        Holder h = Holder.get(view);
        Logger.v("Load image from " + imageUrl);
        h.mImageView.setImageUrl(imageUrl, mHuabanApp.getImageLoader());

        h.mTitle.setText(pin.getText());
        if (pin.getText().trim().length() > 0) {
            h.mTitle.setVisibility(View.VISIBLE);
        } else {
            h.mTitle.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(DatabaseHelper.FIELD_ID, getItem(i).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        loadNewestPinsFromNetwork();
    }
}
