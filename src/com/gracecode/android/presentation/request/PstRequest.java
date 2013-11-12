package com.gracecode.android.presentation.request;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.helper.DatabaseHelper;
import com.gracecode.android.presentation.listener.PstRequestListener;
import com.gracecode.android.presentation.util.Logger;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PstRequest extends StringRequest {
    private static final String HUABAN_API =
            "https://huaban.com/favorite/data_presentation/?limit=" + (Huaban.PAGE_SIZE * 2);

    private final Gson mGson;
    private final DatabaseHelper mDatabaseHelper;
    private final PstRequestListener mListener;

    private int mAffectedRows = 0;

    public PstRequest(long max, long since, PstRequestListener listener) {
        super(getApiUrl(max, since), listener, listener);

        mGson = Huaban.getInstance().getGson();
        mDatabaseHelper = Huaban.getInstance().getDatabaseHelper();
        mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Response<String> stringResponse = super.parseNetworkResponse(response);
        getPinsFromResponse(stringResponse.result);
        return stringResponse;
    }

    @Override
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        super.setRetryPolicy(new DefaultRetryPolicy(
                Huaban.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private int getPinsFromResponse(String response) {
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response);
        JsonArray jsonArrayPins = jsonObject.getAsJsonArray("pins");

        mAffectedRows = 0;
        for (int i = 0; i < jsonArrayPins.size(); i++) {
            JsonObject tmp = (JsonObject) jsonArrayPins.get(i);
            Pin pin = mGson.fromJson(tmp, Pin.class);
            Pin.File file = mGson.fromJson(tmp.getAsJsonObject("file"), Pin.File.class);
            pin.setKey(file.key);
            pin.setWidth(file.width);
            pin.setHeight(file.height);

            // ignore gif and some 'bad' images.
            if (file.frames == 1 && (file.width / (float) file.height < .38f)) {
                try {
                    Dao.CreateOrUpdateStatus status =
                            mDatabaseHelper.getPinsDAO().createOrUpdate(pin);

                    mAffectedRows += status.getNumLinesChanged();
                } catch (SQLException e) {
                    Logger.e(e.getMessage());
                }
            }
        }

        mListener.onSaved(mAffectedRows);
        return mAffectedRows;
    }


    private static String getApiUrl(long max, long since) {
        String tail = "";
        if (since > 0) {
            tail += "&since=" + since;
        }

        if (max > 0) {
            tail += "&max=" + max;
        }

        String api = HUABAN_API + tail + "&t=" + System.currentTimeMillis();
        Logger.i("Request JSON data from " + api);
        return api;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("X-Requested-With", "XMLHttpRequest");
        params.put("Accept-Encoding", "gzip,deflate");
        params.put("Cache-Control", "no-cache");
        params.put("Referer", Huaban.URL_HUABAN);
        params.put("User-Agent", Huaban.USER_AGENT);
        return params;
    }
}
