package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.GlideRoundTransform;
import com.running.android_main.R;
import com.running.beans.RaceData;

import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class RaceAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    Context mContext;
    List<RaceData> mData=null;
    //LayoutInflater mInflater;

    public RaceAdapter(Context context, List<RaceData> data) {
        mData = data;
        mContext = context;
        //mInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //创建一个实现点击接口
    public  interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    //定义完接口，添加接口和设置Adapter接口的方法：
    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(onItemClickListener listener){
        this.mOnItemClickListener =listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_ITEM){
            View view= LayoutInflater.from(mContext).inflate(R.layout.item_race,parent,false);
            return new RaceViewHolder(view);
        }else if (viewType==TYPE_FOOTER){
            View view= LayoutInflater.from(mContext).inflate(R.layout.recycler_item_foot,parent,false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RaceViewHolder){
            //((RaceViewHolder) holder).mImageView.setImageResource(mData.get(position).getPic());
            Glide.with(mContext)
                    .load(mData.get(position).getPicUrl())
                    .transform(new GlideRoundTransform(mContext,20))
                    .placeholder(R.color.colorOrange)
                    .error(R.drawable.fail)
                    .crossFade()
                    .centerCrop()
                    .into(((RaceViewHolder) holder).mImageView);
            ((RaceViewHolder) holder).titleTextView.setText(mData.get(position).getTitle());
            ((RaceViewHolder) holder).timeTextView.setText(mData.get(position).getTime());
            ((RaceViewHolder) holder).numTextView.setText(mData.get(position).getNum());
            //将数据保存在itemView的Tag中，以便点击时进行获取
            // holder.itemView.setTag(mData.get(position));
            if (mOnItemClickListener !=null){
                ((RaceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position=holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position +1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
    public class RaceViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView titleTextView,timeTextView,numTextView;
        public RaceViewHolder(View itemView) {
            super(itemView);
            mImageView= (ImageView) itemView.findViewById(R.id.img_race);
            titleTextView= (TextView) itemView.findViewById(R.id.title_race);
            timeTextView= (TextView) itemView.findViewById(R.id.time);
            numTextView= (TextView) itemView.findViewById(R.id.num);
        }
    }
    public class FootViewHolder extends RecyclerView.ViewHolder{

        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }

}
