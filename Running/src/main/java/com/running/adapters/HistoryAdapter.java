package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.beans.History;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<History> mHistories;
    private Context mContext;

    //创建一个实现点击接口
    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    //定义完接口，添加接口和设置Adapter接口的方法：
    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public HistoryAdapter(List<History> mHistories, Context mContext) {
        this.mHistories = mHistories;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_foot, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryViewHolder) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
            ((HistoryViewHolder) holder).dateTextView.setText(dateFormat.format(mHistories.get(position).getRunstarttime()));
            ((HistoryViewHolder) holder).distanceTextView.setText(mHistories.get(position).getRundistance() + "");
            ((HistoryViewHolder) holder).timeTextView.setText(dateFormat2.format(mHistories.get(position).getRuntime() - 28800000));
            if (mHistories.get(position).getComplete()==(-1)){
                ((HistoryViewHolder) holder).targetImageView.setVisibility(View.GONE);
            }
            if (mOnItemClickListener != null) {
                ((HistoryViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, distanceTextView, timeTextView;
        ImageView targetImageView;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_histoy);
            distanceTextView = (TextView) itemView.findViewById(R.id.distance_history);
            timeTextView = (TextView) itemView.findViewById(R.id.time_history);
            targetImageView= (ImageView) itemView.findViewById(R.id.img_target);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}
