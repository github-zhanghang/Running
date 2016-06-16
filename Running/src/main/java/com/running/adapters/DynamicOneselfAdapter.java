package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.android_main.DynamicCommentActivity;
import com.running.android_main.DynamicOneselfActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.PersonInformationActivity;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.beans.DynamicOneselfBean;
import com.running.beans.UserInfo;
import com.running.myviews.ninegridview.ImageInfo;
import com.running.myviews.ninegridview.NineGridView;
import com.running.myviews.ninegridview.preview.ClickNineGridViewAdapter;
import com.running.utils.GlideCircleTransform;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

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

    private String url = MyApplication.HOST + "dynamicOperateServlet";

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            final UserInfo userInfo = (UserInfo) mList.get(position).get
                    ("header");
            ((HeaderViewHolder) holder).mBackImageView.setImageResource(R.drawable.bg_dynamic);
            Glide.with(mContext)
                    .load(userInfo.getImageUrl())
                    .transform(new GlideCircleTransform(mContext))
                    .into(((HeaderViewHolder) holder).mHeadImageView);
            ((HeaderViewHolder) holder).mHeadImageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("UserInfo", userInfo);
                            intent.setClass(mContext, PersonInformationActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
            ((HeaderViewHolder) holder).mNameTextView.setText(userInfo.getNickName());
            if (userInfo.getSex().equals("男")) {
                ((HeaderViewHolder) holder).mSexImageView.setImageResource(R.drawable.ic_sex_man);
            } else if (userInfo.getSex().equals("女")) {
                ((HeaderViewHolder) holder).mSexImageView.setImageResource(R.drawable.ic_sex_woman);
            }
        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).mLoadText.setText("加载中");
        } else if (holder instanceof ViewHolder) {
            DynamicOneselfBean bean = (DynamicOneselfBean) mList.get(position).get
                    ("DynamicOneselfBean");
            SpannableStringBuilder time = formatDateTime(bean.getTime());
            ((ViewHolder) holder).mTime.setText(time);
            ((ViewHolder) holder).mContent.setText(bean.getContent());
            List<ImageInfo> infoList = new ArrayList<>();
            for (int i = 0; i < bean.getImgList().size(); i++) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl(bean.getImgList().get(i));
                imageInfo.setBigImageUrl(bean.getImgList().get(i));
                infoList.add(imageInfo);
            }
            ClickNineGridViewAdapter adapter = new ClickNineGridViewAdapter(mContext, infoList);
            ((ViewHolder) holder).mGridView.setAdapter(adapter);
            ((ViewHolder) holder).mPraiseCount.setText(String.valueOf(bean.getPraiseCount()));
            ((ViewHolder) holder).mCommentCount.setText(String.valueOf(bean.getCommentCount()));

            if (((DynamicOneselfBean) mList.get(position).get("DynamicOneselfBean"))
                    .getPraiseStatus() == 1) {
                ((ViewHolder) holder).mPraiseImg.setImageResource(R.drawable.ic_praise_red);
            }
            ((ViewHolder) holder).mPraiseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((DynamicOneselfBean) mList.get(position).get("DynamicOneselfBean"))
                            .getPraiseStatus() == 0) {
                        ((DynamicOneselfBean) mList.get(position).get
                                ("DynamicOneselfBean")).setPraiseStatus(1);
                        ((DynamicOneselfBean) mList.get(position).get
                                ("DynamicOneselfBean")).setPraiseCount(((DynamicOneselfBean)
                                mList.get(position).get
                                        ("DynamicOneselfBean")).getPraiseCount() + 1);
                        ((ViewHolder) holder).mPraiseCount.setText(String.valueOf((
                                (DynamicOneselfBean) mList.get(position).get
                                        ("DynamicOneselfBean"))
                                .getPraiseCount()));
                        ((ViewHolder) holder).mPraiseImg.setImageResource(R.drawable
                                .ic_praise_red);
                        addPraise(((DynamicOneselfBean) mList.get(position).get
                                ("DynamicOneselfBean")).getdId());
                    }
                }
            });

            ((ViewHolder) holder).mPraiseCount.setText(""+((DynamicOneselfBean) mList.get
                    (position).get
                    ("DynamicOneselfBean")).getPraiseCount());

            ((ViewHolder) holder).mCommentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicCommentActivity.class);
                    intent.putExtra("myId", (((MyApplication)((DynamicOneselfActivity)mContext)
                            .getApplication
                            ()).getUserInfo().getUid()));
                    DynamicOneselfBean oneselfBean = (DynamicOneselfBean) mList.get(position).get
                            ("DynamicOneselfBean");
                    DynamicImgBean imgBean = new DynamicImgBean(oneselfBean.getdId(), oneselfBean
                            .getName(), oneselfBean.getHeadPhoto(), oneselfBean.getImgList(),
                            oneselfBean.getContent(), oneselfBean.getTime(), oneselfBean
                            .getLocation(), oneselfBean.getPraiseCount(), oneselfBean
                            .getCommentCount(), oneselfBean.getuId(), oneselfBean.getPraiseStatus());
                    Log.d("TAG",imgBean.toString());
                    intent.putExtra("dynamicBean", imgBean);
                    mContext.startActivity(intent);
                }
            });

            ((ViewHolder) holder).mCommentCount.setText(""+((DynamicOneselfBean)
                    mList.get(position)
                            .get("DynamicOneselfBean")).getCommentCount());

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

    private void addPraise(int i) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("pDId", i);
        //用户id
        //map.put("pUId",mMyApplication.getUserInfo().getUid());
        map.put("pUId", 1);
        Gson gson = new Gson();
        String praiseMap = gson.toJson(map);
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "AddPraise")
                .addParams("praiseMap", praiseMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTime, mContent;
        public NineGridView mGridView;
        TextView mPraiseCount, mCommentCount;
        ImageView mPraiseImg, mCommentImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTime = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_time);
            mContent = (TextView) itemView.findViewById(R.id.dynamic_oneself_imgItem_content);
            mGridView = (NineGridView) itemView.findViewById(R.id.dynamic_oneself_imgItem_gridView);
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
            stringBuilder.setSpan(new AbsoluteSizeSpan(27), 2, stringBuilder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return stringBuilder;
        }
    }
}
