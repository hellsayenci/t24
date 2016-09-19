package com.hellsayenci.t24.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hellsayenci.t24.App;
import com.hellsayenci.t24.R;
import com.hellsayenci.t24.events.ErrorEvent;
import com.hellsayenci.t24.events.StoriesEvent;
import com.hellsayenci.t24.models.StoriesResponse;
import com.hellsayenci.t24.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by hellsayenci on 14/09/16.
 * Son haber listesini uygulama başlangıcında ve her 120 saniyede bir
 * web servisten çekip ana ekranı bilgilendiren servis.
 */
public class StoriesService extends Service {

    String TAG = "StoriesService";

    Handler h;

    private int mInterval = 120000; // 120 seconds for refresing
    private Handler mHandler;
    private boolean firstRun = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(firstRun)
            h = new Handler();

        firstRun = false;

        mHandler = new Handler();
        startRepeatingTask();


        return START_STICKY;
    }

    private void getStories() {
        Log.i(TAG, "started");
        for(int i=1; i<=App.getLastPage(); i++) {
            Log.e(TAG, "fetching page = " + "" + i);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url;
            if(prefs.getString(Constants.CATEGORY_POSTFIX,"").equals("")){
                url = Constants.STORIES + i;
            }
            else{
                url = Constants.STORIES + i + Constants.CATEGORY_POSTFIX + prefs.getString(Constants.CATEGORY_POSTFIX,"");
            }
            Log.e(TAG, "url = " + url);
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final StoriesResponse storiesResponse = App.getGson().fromJson(response.toString(), StoriesResponse.class);
                    if(h != null) {
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new StoriesEvent(storiesResponse));
                            }
                        }, Constants.SPLASH_DELAY);
                    }
                    else {
                        EventBus.getDefault().post(new StoriesEvent(storiesResponse));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    EventBus.getDefault().post(new ErrorEvent(getApplication().getResources().getString(R.string.error_general)));
                }
            });
            req.setShouldCache(false);
            req.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            App.getRequetQueue().add(req);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getStories();
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
}
