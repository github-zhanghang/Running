package com.running.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.android_main.R;
import com.running.beans.RankItemInfo;
import com.running.utils.ImageLoader;

import java.util.List;

/**
 * Created by ZhangHang on 2016/5/25.
 */
public class RankActivityAdapter extends BaseAdapter {
    private Context mContext;
    private List<RankItemInfo> mList;
    private PullToRefreshListView mListView;
    private LayoutInflater mInflater;
    //缓存图片
    public static LruCache<String, Bitmap> mCache;

    public RankActivityAdapter(Context context, List<RankItemInfo> list, PullToRefreshListView listView) {
        mContext = context;
        mList = list;
        this.mListView = listView;
        mInflater = LayoutInflater.from(context);
        initCache();
    }

    public void initCache() {
        int cacheSize = 4 * 1024 * 1024; // 4MiB
        mCache = new LruCache(cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
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
        //使用异步线程下载图片
        downloadAsyncTask(viewHolder, position);
        return convertView;
    }

    public void downloadAsyncTask(final ViewHolder holder, int position) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap;
                String url = params[0];
                if (mCache.get(url) != null) {
                    bitmap = mCache.get(url);
                } else {
                    bitmap = ImageLoader.getBitmap(url);
                    mCache.put(url, bitmap);
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                holder.hostIV.setImageBitmap(bitmap);
                mListView.onRefreshComplete();
            }
        }.execute(mList.get(position).getImgUrl());
    }

}
