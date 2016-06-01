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
import com.running.beans.GoodsData;

import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {
    private List<GoodsData> mGoodsDatas;
    Context mContext;

    public GoodsAdapter(Context context, List<GoodsData> goodsDatas) {
        mContext = context;
        mGoodsDatas = goodsDatas;
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
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_goods,null);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GoodsViewHolder holder, int position) {
        //holder.mImageView.setImageResource(mGoodsDatas.get(position).getPic());
        Glide.with(mContext)
                .load(mGoodsDatas.get(position).getPic())
                .transform(new GlideRoundTransform(mContext,6))
                .placeholder(R.color.colorOrange)//显示一个占位符
                .error(R.drawable.fail)//错误占位符
                .crossFade()//入淡出动画
                .into(holder.mImageView);
        holder.goodTextView.setText(mGoodsDatas.get(position).getGoodname());
        holder.priceTextView.setText(mGoodsDatas.get(position).getPrice());

        if (mOnItemClickListener!=null){
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
        return mGoodsDatas.size();
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView goodTextView,priceTextView;
        public GoodsViewHolder(View itemView) {
            super(itemView);
            mImageView= (ImageView) itemView.findViewById(R.id.img_good);
            goodTextView= (TextView) itemView.findViewById(R.id.name_good);
            priceTextView= (TextView) itemView.findViewById(R.id.name_price);
        }
    }
}
