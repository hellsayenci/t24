package com.hellsayenci.t24;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hellsayenci.t24.events.ErrorEvent;
import com.hellsayenci.t24.fragments.StoryDetailFragment;
import com.hellsayenci.t24.models.Story;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class StoryDetailActivity extends AppCompatActivity {


    static ProgressDialog pd;

    List<Story> stories;
    int position;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.loading));
        pd.setCancelable(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Haber İçeriği");

        stories = (List<Story>) getIntent().getExtras().getSerializable("stories");
        position = getIntent().getExtras().getInt("position",0);


        ViewPager vpPager = (ViewPager) findViewById(R.id.viewPager);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(),stories);
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(position);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        private List<Story> stories;

        public MyPagerAdapter(FragmentManager fragmentManager, List<Story> s) {
            super(fragmentManager);
            this.stories = s;
            NUM_ITEMS = stories.size();
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return StoryDetailFragment.newInstance(stories.get(position));
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_facebook) {
            showHideProgressDialog(true);
            openShareIntent("com.facebook.katana", stories.get(position));
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_twitter){
            showHideProgressDialog(true);
            openShareIntent("com.android.twitter", stories.get(position));
        }

        return super.onOptionsItemSelected(item);
    }

    private void openShareIntent(String s, Story story) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent .setType("text/plain");
        intent .setPackage(s);
        intent .putExtra(Intent.EXTRA_TEXT, story.getUrls().getWeb());
        try {
            startActivity(intent );
        } catch (android.content.ActivityNotFoundException ex) {
            EventBus.getDefault().post(new ErrorEvent("Uygulama yüklü değil!"));
        }
        showHideProgressDialog(false);
    }


    public void showHideProgressDialog(boolean value){
        if(value){
            pd.show();
        }
        else{
            pd.dismiss();
        }
    }
}
