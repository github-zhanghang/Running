package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.myviews.MyGridView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ldd on 2016/5/30.
 * 个人动态适配器
 */
public class DynamicOneselfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HashMap<String,Object>> mList;
    Context mContext;
    LayoutInflater mInflater;

    private static final int IS_HEADER = 0;
    private static final int IS_FOOTER = 2;
    private static final int IS_NORMAL = 1;

    private View mHeadView;
    private View mFootView;

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public DynamicOneselfAdapter(List<HashMap<String,Object>> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }


    public void setHeadView(View headView) {
        mHeadView = headView;
        notifyItemInserted(0);
    }

    public View getHeadView() {
        return mHeadView;
    }

    public void setFootView(View footView) {
        mFootView = footView;
        notifyItemInserted(getItemCount()-1);
    }

    public View getFootView() {
        return mFootView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView==null && mFootView==null) {
            return IS_NORMAL;
        }
        if (position == 0 && mHeadView!=null) {
            return IS_HEADER;
        }
        if (position == getItemCount()-1) {
            return IS_FOOTER;
        }
        return IS_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeadView != null && viewType == IS_HEADER) {
            return new ViewHolder(mHeadView);
        }
        if (mFootView != null && viewType == IS_FOOTER) {
            return new ViewHolder(mFootView);
        }
        View view = mInflater.inflate(R.layout.dynamic_oneself_img_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == IS_HEADER) {
            return;
        }
        if (getItemViewType(position) == IS_FOOTER) {
            return;
        }
        final int pos = getRealPosition(holder);
        final HashMap<String,Object> map=mList.get(pos);
        if (holder instanceof ViewHolder) {
            SpannableStringBuilder time=formatDateTime(map.get("time").toString());
            ((ViewHolder) holder).mTime.setText(time);
            ((ViewHolder) holder).mContent.setText(map.get("content").toString());
            DynamicImgGridViewAdapter adapter=new DynamicImgGridViewAdapter(mContext,
                    (List<Integer>) map.get("imgList"),
                    ((ViewHolder) holder).mGridView);
            ((ViewHolder) holder).mGridView.setAdapter(adapter);

            if (mListener == null) {
                return;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, map);
                }
            });
        }
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (mHeadView == null) {
            return position;
        } else {
            return position - 1;
        }
    }




    @Override
    public int getItemCount() {
        /*if (mHeadView==null){
            return mList.size();
        } else {
            return mList.size()+1;
        }*/
        if (mHeadView == null && mFootView==null) {
            return mList.size();
        } else if ((mHeadView!=null&& mFootView==null)){
            return mList.size() + 1;
        } else if (mHeadView==null&& mFootView!=null) {
            return mList.size() + 1;
        } else {
            return mList.size() + 2;

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTime,mContent;
        public MyGridView mGridView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeadView) {
                return;
            }
            mTime = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_time);
            mContent = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_content);
            mGridView = (MyGridView) itemView.findViewById(R.id.dynamic_oneself_imgItem_gridView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, HashMap<String,Object> map);
    }

    //日期处理
    private SpannableStringBuilder formatDateTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (time==null || "".equals(time)){
            return new SpannableStringBuilder("");
        }
        Date date=null;
        try{
            date=format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();
        Calendar today= Calendar.getInstance();
        today.set(Calendar.YEAR,current.get(Calendar.YEAR));
        today.set(Calendar.MONTH,current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));

        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);

        Calendar yesterday = Calendar.getInstance();

        yesterday.set(Calendar.YEAR,current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH,current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);

        yesterday.set(Calendar.HOUR_OF_DAY,0);
        yesterday.set(Calendar.MINUTE,0);
        yesterday.set(Calendar.SECOND,0);

        current.setTime(date);

        if (current.after(today)) {
            return new SpannableStringBuilder("今天");
        } else if (current.before(today) && current.after(yesterday)) {
            return new SpannableStringBuilder("昨天");
        } else {
            int index = time.indexOf(" ");
            String day=time.substring(index-2,index);
            String month=time.substring(index-5,index-3);
            String k=time.substring(index-5,index-4);
            if (Integer.valueOf(k)==0) {
                month=month.substring(1);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(
                    day + month + "月");
            stringBuilder.setSpan(new AbsoluteSizeSpan(25), 2, stringBuilder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return stringBuilder;
        }
    }
}
