package com.hellsayenci.t24.events;

import com.hellsayenci.t24.models.StoriesResponse;

/**
 * Created by hellsayenci on 14/09/16.
 */
public class StoriesEvent {

    public final StoriesResponse storiesResponse;

    public StoriesEvent(StoriesResponse storiesResponse1) {
        this.storiesResponse = storiesResponse1;
    }
}
