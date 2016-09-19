package com.hellsayenci.t24;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hellsayenci.t24.events.ErrorEvent;
import com.hellsayenci.t24.services.StoriesService;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class App extends Application {

    private static Context context;
    private static App instance;
    private static RequestQueue mRequetQueue;
    private static Gson gson;
    private static Picasso picasso;
    private static int lastPage = 2;

    @Override
    public void onCreate() {
        context=getApplicationContext();
        instance = this;
        mRequetQueue = Volley.newRequestQueue(context);
        gson = new Gson();
        picasso = Picasso.with(this);

        //uygulama açılışınca asenkron haber servisi başlatılıyor
        startService(new Intent(context, StoriesService.class));

        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
        EventBus.getDefault().unregister(this);
    }

    public static Context getContext() {
        return context;
    }

    public static App getInstance() {
        return instance;
    }

    public static RequestQueue getRequetQueue() {
        return mRequetQueue;
    }

    public static Gson getGson() {
        return gson;
    }

    public static Picasso getPicasso() {
        return picasso;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        Toast.makeText(getApplicationContext(), event.message, Toast.LENGTH_SHORT).show();
    }

    public static int getLastPage() {
        return lastPage;
    }

    public static void setLastPage(int lastPage) {
        App.lastPage = lastPage;
    }
}
