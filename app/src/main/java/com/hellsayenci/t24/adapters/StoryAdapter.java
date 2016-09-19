package com.hellsayenci.t24.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellsayenci.t24.App;
import com.hellsayenci.t24.R;
import com.hellsayenci.t24.StoriesActivity;
import com.hellsayenci.t24.StoryDetailActivity;
import com.hellsayenci.t24.models.StoriesResponse;
import com.hellsayenci.t24.models.Story;
import com.hellsayenci.t24.utils.Constants;
import com.hellsayenci.t24.viewholders.StoryViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hellsayenci on 15/09/16.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolder> {

    List<Story> stories;
    StoriesActivity mContext;

    public StoryAdapter(List<Story> stories1, StoriesActivity context){
        this.stories = stories1;
        this.mContext = context;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story,
                parent, false);
        return new StoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, final int position) {
        holder.tvStory.setText(Html.fromHtml(stories.get(position).getTitle()).toString());
        App.getPicasso().load("http:" + stories.get(position).getImages().getBox()).into(holder.ivStory);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, StoryDetailActivity.class);
                List<Story> forPager = mContext.sliderStories;
                forPager.addAll(getAllStories());
                Bundle bundle = new Bundle();
                bundle.putSerializable("stories", (Serializable) forPager);
                bundle.putInt("position", position + Constants.LIMIT);
                i.putExtras(bundle);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void addStories(StoriesResponse response){
        if(stories.size() == 0) {
            for(Story s : response.getData()){
                stories.add(s);
            }
            notifyDataSetChanged();
        }
        else if(stories.size() + Constants.LIMIT < response.getPaging().getCurrent() * Constants.LIMIT){
            int start = stories.size();
            for(int i=0; i<response.getData().length;i++){
                stories.add(response.getData()[i]);
            }
            notifyItemRangeInserted(start, stories.size() - 1);
        }
        else{
            final int start = ( response.getPaging().getCurrent() - 2 ) * Constants.LIMIT;
            int current = start;
            int limit = response.getPaging().getLimit();
            for(int i=0; i<limit; i++){
                stories.set(current, response.getData()[i]);
                current++;
            }
            notifyItemRangeChanged(start, start + limit - 1);
        }
    }

    public List<Story> getAllStories(){
        if(stories == null)
            return new ArrayList<Story>();
        else
            return stories;
    }
}
