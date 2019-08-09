package com.nakul.wikiapi.network;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public final class VolleyHelper {
    private static VolleyHelper mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static final String TAG = "VolleyHelper";

    private VolleyHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static void initVolley(Context context) {
        new VolleyHelper(context);
    }


    public static synchronized VolleyHelper getInstance() {
        if (mInstance == null) {
            mInstance = new VolleyHelper(mCtx);
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void sendRequestToApi(final int method, final String url, final JSONObject body, final VolleyCallback callback) {


        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response, 200, false);
                VolleyLog.e(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        callback.onErrorResponse(res, response.statusCode);
                    } catch (UnsupportedEncodingException e) {
                        VolleyLog.e(TAG, e.getMessage());
                        callback.onErrorResponse("error", -1);
                    }
                } else if (error instanceof NoConnectionError) {
                    callback.onErrorResponse("error", 408);
                } else if (response != null) {
                    callback.onErrorResponse("error", response.statusCode);
                } else {
                    callback.onErrorResponse("error", -1);
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {

                return body != null ? body.toString().getBytes() : new JSONObject().toString().getBytes();

            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Accept-Charset", "utf-8");
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000;
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String responseStr = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(responseStr, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        addToRequestQueue(stringRequest);
    }


    public interface VolleyCallback {
        void onSuccessResponse(String result, int responseCode, boolean error);

        void onErrorResponse(String result, int responseCode);
    }
}
