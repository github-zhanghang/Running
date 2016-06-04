package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.running.android_main.PersonInformationActivity;
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
    Context mContext;
    List<HashMap<String, Object>> mList;
    LayoutInflater mInflater;

    private static final int IS_HEADER = -1;
    private static final int IS_NORMAL = 0;
    private static final int IS_FOOTER = 1;

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public DynamicOneselfAdapter(Context context, List<HashMap<String, Object>> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return IS_FOOTER;
        } else if (position == 0) {
            return IS_HEADER;
        } else {
            return IS_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == IS_HEADER) {
            View mHeadView = mInflater.inflate(R.layout.dynamic_header, parent, false);
            return new HeaderViewHolder(mHeadView);
        } else if (viewType == IS_FOOTER) {
            View mFootView = mInflater.inflate(R.layout.dynamic_load_foot, parent, false);
            return new FooterViewHolder(mFootView);
        } else {
            View view = mInflater.inflate(R.layout.dynamic_oneself_img_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).mBackImageView.setImageResource(R.drawable.dynamic_test);
            ((HeaderViewHolder) holder).mHeadImageView.setImageResource(R.drawable.head_photo);
            ((HeaderViewHolder) holder).mHeadImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext,
                                    PersonInformationActivity.class));
                        }
                    });
            ((HeaderViewHolder) holder).mNameTextView.setText("桃子");
            ((HeaderViewHolder) holder).mSexImageView.setImageResource(R.drawable.sex_women);
        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).mLoadText.setText("加载中");
        } else if (holder instanceof ViewHolder) {
            HashMap<String, Object> map = mList.get(position);
            SpannableStringBuilder time = formatDateTime(map.get("time").toString());
            ((ViewHolder) holder).mTime.setText(time);
            ((ViewHolder) holder).mContent.setText(map.get("content").toString());
            DynamicImgGridViewAdapter adapter = new DynamicImgGridViewAdapter(mContext,
                    (List<String>) map.get("imgList"),
                    ((ViewHolder) holder).mGridView);
            ((ViewHolder) holder).mGridView.setAdapter(adapter);

            if (mListener == null) {
                return;
            }
            /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position, map);
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTime, mContent;
        public MyGridView mGridView;
        TextView mPraiseCount, mCommentCount;
        ImageView mPraiseImg, mCommentImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTime = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_time);
            mContent = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_content);
            mGridView = (MyGridView) itemView.findViewById(R.id.dynamic_oneself_imgItem_gridView);
            mPraiseImg = (ImageView) itemView.findViewById(R.id.dynamic_oneself_imgItem_praiseImg);
            mPraiseCount = (TextView) itemView.findViewById(R.id
                    .dynamic_oneself_imgItem_praiseCount);
            mCommentImg = (ImageView) itemView.findViewById(R.id
                    .dynamic_oneself_imgItem_commentImg);
            mCommentCount = (TextView) itemView.findViewById(R.id
                    .dynamic_oneself_imgItem_commentCount);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView mBackImageView;
        ImageView mHeadImageView;
        TextView mNameTextView;
        ImageView mSexImageView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mBackImageView = (ImageView) itemView.findViewById(R.id.dynamic_header_background);
            mHeadImageView = (ImageView) itemView.findViewById(R.id.dynamic_header_head_img);
            mNameTextView = (TextView) itemView.findViewById(R.id.dynamic_header_name);
            mSexImageView = (ImageView) itemView.findViewById(R.id.personSex);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView mLoadText;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mLoadText = (TextView) itemView.findViewById(R.id.dynamic_footer_content);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, HashMap<String, Object> map);
    }

    //日期处理
    private SpannableStringBuilder formatDateTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (time == null || "".equals(time)) {
            return new SpannableStringBuilder("");
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);

        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            return new SpannableStringBuilder("今天");
        } else if (current.before(today) && current.after(yesterday)) {
            return new SpannableStringBuilder("昨天");
        } else {
            int index = time.indexOf(" ");
            String day = time.substring(index - 2, index);
            String month = time.substring(index - 5, index - 3);
            String k = time.substring(index - 5, index - 4);
            if (Integer.valueOf(k) == 0) {
                month = month.substring(1);
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(
                    day + month + "月");
            stringBuilder.setSpan(new AbsoluteSizeSpan(25), 2, stringBuilder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return stringBuilder;
        }
    }
}