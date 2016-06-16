package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.beans.RaceData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C5-0 on 2016/6/15.
 */
public class RaceForecastAdapter extends RecyclerView.Adapter<RaceForecastAdapter.RaceForecastViewHolder> {
    private List<RaceData> raceList;
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

    public RaceForecastAdapter(List<RaceData> raceList, Context mContext) {
        this.raceList = raceList;
        this.mContext = mContext;
        Log.e("taoziadapter", "size=" + raceList.size());
    }

    @Override
    public RaceForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.item_forecast,null);
        return new RaceForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RaceForecastViewHolder holder, int position) {
        holder.dateTextView.setText(raceList.get(position).getTime());
        holder.raceTextView.setText(raceList.get(position).getName());
        if (mOnItemClickListener !=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return raceList.size();
    }
    public class RaceForecastViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView,raceTextView;
        public RaceForecastViewHolder(View itemView) {
            super(itemView);
            dateTextView= (TextView) itemView.findViewById(R.id.date_forecast);
            raceTextView= (TextView) itemView.findViewById(R.id.race_forecast);
        }
    }



}
