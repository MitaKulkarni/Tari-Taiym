package com.sales.tandt.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.sales.tandt.activity.AddMenuItemsActivity;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by Mita on 8/10/2017.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;
    private static Typeface mTypeface;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mTypeface = Typeface.createFromAsset(getAssets(), "icomoon1.ttf");
        CookieHandler.setDefault(new CookieManager());

    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void deleteRequest(Request<T> req, String tag) {

        if (req.getTag().equals(tag))
            req.cancel();
        Log.i("cancelled", String.valueOf(req.isCanceled()));
    }

    public <T> void setRetryPolicy(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    //Method to set custom timeout and max retry - to fix multiple request
    public <T> void setRetryPolicy(Request<T> req, int timeInMs, int maxRetry){
        req.setRetryPolicy(new DefaultRetryPolicy(timeInMs, maxRetry, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(final Object tag) {
        Log.i(TAG, tag.toString());
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static <T> void releaseMemory(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            objects[i] = null;
        }
    }

    public void sendToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public void setTypeface(TextView... views) {
        for (TextView view : views) {
            view.setTypeface(mTypeface);
        }
    }

    public Typeface getTypeface() {
        return mTypeface;
    }
}


