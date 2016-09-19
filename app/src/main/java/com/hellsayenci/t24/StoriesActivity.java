package com.hellsayenci.t24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Transformers.ZoomOutTransformer;

import com.hellsayenci.t24.adapters.StoryAdapter;
import com.hellsayenci.t24.events.ErrorEvent;
import com.hellsayenci.t24.events.StoriesEvent;
import com.hellsayenci.t24.models.Category;
import com.hellsayenci.t24.models.StoriesResponse;
import com.hellsayenci.t24.models.Story;
import com.hellsayenci.t24.services.StoriesService;
import com.hellsayenci.t24.utils.Constants;
import com.hellsayenci.t24.utils.EndlessParentScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoriesActivity extends AppCompatActivity{

    String TAG = "StoriesActivity";

    SliderLayout sliderShow;
    RecyclerView rvStories;
    StoryAdapter adapter;
    LinearLayoutManager llm;
    static ProgressDialog pd;
    LinearLayout llLoading;
    NestedScrollView nestedScrollView;

    public List<Story> sliderStories = new ArrayList<>();

    boolean haveMoreStories = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

       /* getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);*/

        getSupportActionBar().setTitle("Bağımsız İnternet Gazetesi");

        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.loading));
        pd.setCancelable(false);
        showHideProgressDialog(true);
        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.setPagerTransformer(false, new ZoomOutTransformer());
        sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShow.setDuration(Constants.SLIDER_DELAY);
        rvStories = (RecyclerView) findViewById(R.id.rvStories);
        rvStories.setNestedScrollingEnabled(false);
        llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        llLoading = (LinearLayout) findViewById(R.id.llLoading);
        rvStories.setLayoutManager(llm);
        adapter = new StoryAdapter(new ArrayList<Story>(), this);
        rvStories.setAdapter(adapter);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        addLoadMore();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStoriesEvent(StoriesEvent event) {
        llLoading.setVisibility(View.GONE);
        handleStories(event.storiesResponse);
    }

    private void handleStories(StoriesResponse storiesResponse) {
        if(storiesResponse.getPaging().getCurrent() == 1) {
            sliderStories = new ArrayList<>();
            for(Story s : storiesResponse.getData()){
                sliderStories.add(s);
            }
            sliderShow.removeAllSliders();
            for (int i = 0; i < storiesResponse.getData().length; i++) {
                Story s = storiesResponse.getData()[i];
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .description(Html.fromHtml(s.getTitle()).toString())
                        .image("http:" + s.getImages().getPage());

                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(BaseSliderView slider) {
                        Intent i = new Intent(StoriesActivity.this, StoryDetailActivity.class);
                        List<Story> forPager = sliderStories;
                        forPager.addAll(adapter.getAllStories());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stories", (Serializable) forPager);
                        bundle.putInt("position", sliderShow.getCurrentPosition());
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
                sliderShow.addSlider(textSliderView);

            }
        }
        else{
            //TODO handle other stories

            adapter.addStories(storiesResponse);
        }

        showHideProgressDialog(false);
    }

    private void addLoadMore() {
        nestedScrollView.setOnScrollChangeListener(new EndlessParentScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(haveMoreStories)
                    loadMore();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        sliderShow.startAutoCycle();
        super.onResume();
    }

    public static void showHideProgressDialog(boolean value){
        if(value){
            pd.show();
        }
        else{
            pd.dismiss();
        }
    }

    private void loadMore() {
        llLoading.setVisibility(View.VISIBLE);
        Log.i(TAG, "loadMore executed");
        final int page = App.getLastPage() + 1;
        Log.e(TAG, "fetching page = " + "" + page);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url;
        if(prefs.getString(Constants.CATEGORY_POSTFIX,"").equals("")){
            url = Constants.STORIES + page;
        }
        else{
            url = Constants.STORIES + page + Constants.CATEGORY_POSTFIX + prefs.getString(Constants.CATEGORY_POSTFIX,"");
        }
        Log.e(TAG, "url = " + url);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                StoriesResponse storiesResponse = App.getGson().fromJson(response.toString(), StoriesResponse.class);

                EventBus.getDefault().post(new StoriesEvent(storiesResponse));

                App.setLastPage(page);
                llLoading.setVisibility(View.GONE);

                if(storiesResponse.getPaging().getCurrent() == storiesResponse.getPaging().getPages()){
                    haveMoreStories = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(new ErrorEvent(getApplication().getResources().getString(R.string.error_general)));
                llLoading.setVisibility(View.GONE);
            }
        });
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(
                Constants.TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.getRequetQueue().add(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_categories) {
            showHideProgressDialog(true);
            getCategories();
        }
        else if (id == R.id.action_reresh){
            showHideProgressDialog(true);
            restartStoryService();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCategories() {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Constants.CATEGORIES, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Category[] categories = App.getGson().fromJson(data.toString(), Category[].class);
                    createListDialog(categories);
                } catch (JSONException e) {
                    EventBus.getDefault().post(new ErrorEvent(getApplication().getResources().getString(R.string.error_general)));
                    e.printStackTrace();
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

    private void createListDialog(final Category[] categories){
        String[] cats = new String[categories.length];
        for(int i=0; i<categories.length; i++){
            cats[i] = categories[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_category);
        builder.setItems(cats, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCategory(categories[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        showHideProgressDialog(false);
    }

    private void openCategory(Category category) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(Constants.CATEGORY_POSTFIX, String.valueOf(category.getId())).commit();
        adapter = new StoryAdapter(new ArrayList<Story>(), this);
        rvStories.setAdapter(adapter);
        App.setLastPage(2);
        showHideProgressDialog(true);
        restartStoryService();
    }


    private void restartStoryService(){
        stopService(new Intent(this, StoriesService.class));
        startService(new Intent(this, StoriesService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().clear().commit();

        //uygulama kapanışında asenkron haber servisi başlatılıyor
        stopService(new Intent(this, StoriesService.class));
    }
}
