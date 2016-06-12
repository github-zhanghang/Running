package com.running.utils;

import com.bumptech.glide.Glide;

import cn.finalteam.galleryfinal.PauseOnScrollListener;

/**
 * Created by ldd on 2016/6/10.
 */
public class GlidePauseOnScrollListener extends PauseOnScrollListener {
    public GlidePauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        super(pauseOnScroll, pauseOnFling);
    }

    @Override
    public void resume() {
        Glide.with(getActivity()).resumeRequests();
    }

    @Override
    public void pause() {
        Glide.with(getActivity()).pauseRequests();
    }
}
