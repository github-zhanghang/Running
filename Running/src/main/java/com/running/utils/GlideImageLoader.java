package com.running.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Created by ldd on 2016/6/10.
 */
public class GlideImageLoader implements cn.finalteam.galleryfinal.ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, GFImageView imageView, Drawable
            defaultDrawable, int width, int height) {
        Glide.with(activity)
                .load("file://" + path)
                .placeholder(defaultDrawable)
                .error(defaultDrawable)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
