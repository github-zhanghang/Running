package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.beans.History;

import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> mHistories;
    private Context mContext;
    //创建一个实现点击接口
    public  interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    //定义完接口，添加接口和设置Adapter接口的方法：
    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener listener){
        this.mOnItemClickListener =listener;
    }

    public HistoryAdapter(List<History> mHistories, Context mContext) {
        this.mHistories = mHistories;
        this.mContext = mContext;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_history,parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, int position) {
        holder.dateTextView.setText(mHistories.get(position).getDate());
        holder.distanceTextView.setText(mHistories.get(position).getDistance());
        holder.timeTextView.setText(mHistories.get(position).getTime());
        if (mOnItemClickListener !=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView,distanceTextView,timeTextView;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            dateTextView= (TextView) itemView.findViewById(R.id.date_histoy);
            distanceTextView= (TextView) itemView.findViewById(R.id.distance_history);
            timeTextView= (TextView) itemView.findViewById(R.id.time_history);
        }
    }
}
