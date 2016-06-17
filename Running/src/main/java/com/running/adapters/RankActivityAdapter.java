package com.running.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.android_main.R;
import com.running.beans.RankItemInfo;

import java.util.List;

/**
 * Created by ZhangHang on 2016/5/25.
 */
public class RankActivityAdapter extends BaseAdapter {
    private Context mContext;
    private List<RankItemInfo> mList;
    private PullToRefreshListView mListView;
    private LayoutInflater mInflater;

    public RankActivityAdapter(Context context, List<RankItemInfo> list, PullToRefreshListView listView) {
        mContext = context;
        mList = list;
        this.mListView = listView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        private TextView positionTV, nicknameTV, distanceTV;
        private ImageView hostIV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.rankitem, null);
            viewHolder.positionTV = (TextView) convertView.findViewById(R.id.rank_position);
            viewHolder.hostIV = (ImageView) convertView.findViewById(R.id.rank_hostimage);
            viewHolder.nicknameTV = (TextView) convertView.findViewById(R.id.rank_nickname);
            viewHolder.distanceTV = (TextView) convertView.findViewById(R.id.rank_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.positionTV.setText(mList.get(position).getPosition());
        viewHolder.hostIV.setImageResource(R.drawable.game);
        viewHolder.nicknameTV.setText(mList.get(position).getNickName());
        viewHolder.distanceTV.setText(mList.get(position).getDistance());
        Glide.with(mContext)
                .load(mList.get(position).getImgUrl())
                .error(R.drawable.fail)
                .into(new GlideDrawableImageViewTarget(viewHolder.hostIV) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        mListView.onRefreshComplete();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        mListView.onRefreshComplete();
                    }
                });
        return convertView;
    }

}
