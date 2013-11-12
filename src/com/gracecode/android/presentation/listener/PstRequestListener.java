package com.gracecode.android.presentation.listener;

import com.android.volley.Response;
import com.android.volley.VolleyError;


public abstract class PstRequestListener implements Response.Listener<String>, Response.ErrorListener {

    @Override
    public abstract void onErrorResponse(VolleyError error);

    @Override
    public abstract void onResponse(String response);

    public abstract void onSaved(int affectedRows);
}
