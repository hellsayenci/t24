package com.hellsayenci.t24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hellsayenci.t24.App;
import com.hellsayenci.t24.R;
import com.hellsayenci.t24.StoryDetailActivity;
import com.hellsayenci.t24.events.ErrorEvent;
import com.hellsayenci.t24.models.Category;
import com.hellsayenci.t24.models.Story;
import com.hellsayenci.t24.models.StoryDetail;
import com.hellsayenci.t24.utils.Constants;
import com.hellsayenci.t24.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hellsayenci on 16/09/16.
 */
public class StoryDetailFragment extends Fragment {

    Story story;

    ImageView ivStory;
    TextView tvStory, tvTitle, tvTime;
    StoryDetailActivity act;

    // newInstance constructor for creating fragment with arguments
    public static StoryDetailFragment newInstance(Story story) {
        StoryDetailFragment fragmentFirst = new StoryDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("story", story);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        story = (Story) getArguments().getSerializable("story");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_detail, container, false);

        act = (StoryDetailActivity) getActivity();
        act.showHideProgressDialog(true);

        ivStory = (ImageView) view.findViewById(R.id.ivStory);
        tvStory = (TextView) view.findViewById(R.id.tvStory);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTime = (TextView) view.findViewById(R.id.tvTime);

        if(story != null){
            App.getPicasso().load("http:" + story.getImages().getPage()).fit().into(ivStory);
            tvTitle.setText(Html.fromHtml(story.getTitle()));

            getStoryDetail();
        }

        return view;
    }

    private void getStoryDetail() {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Constants.STORY_DETAIL + story.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    StoryDetail storyDetail = App.getGson().fromJson(data.toString(), StoryDetail.class);

                    App.getPicasso().load("http:" + storyDetail.getImages().getPage()).fit().into(ivStory);
                    tvTitle.setText(Html.fromHtml(storyDetail.getTitle()));
                    tvStory.setText(Html.fromHtml(storyDetail.getText()));

                    tvTime.setText(Utils.formattedDateFromString("yyyy-MM-dd hh:mm:ss", "dd.MM.yyy hh:mm", storyDetail.getPublishingDate()));


                    act.showHideProgressDialog(false);
                } catch (JSONException e) {
                    EventBus.getDefault().post(new ErrorEvent(App.getContext().getResources().getString(R.string.error_general)));
                    act.showHideProgressDialog(false);
                    e.printStackTrace();
                } catch (Exception e){
                    act.showHideProgressDialog(false);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(new ErrorEvent(App.getContext().getResources().getString(R.string.error_general)));
                act.showHideProgressDialog(false);
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
