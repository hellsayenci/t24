package com.hellsayenci.t24.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellsayenci.t24.R;

/**
 * Created by hellsayenci on 15/09/16.
 */
public class StoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvStory;
    public ImageView ivStory;

    public StoryViewHolder(View itemView) {
        super(itemView);
        tvStory = (TextView) itemView.findViewById(R.id.tvStory);
        ivStory = (ImageView) itemView.findViewById(R.id.ivStory);
    }
}
