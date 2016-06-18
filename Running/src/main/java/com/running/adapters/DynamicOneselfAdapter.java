package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.running.android_main.DynamicCommentActivity;
import com.running.android_main.DynamicOneselfActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.beans.DynamicOneselfBean;
import com.running.myviews.ninegridview.ImageInfo;
import com.running.myviews.ninegridview.NineGridView;
import com.running.myviews.ninegridview.preview.ClickNineGridViewAdapter;
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
public class DynamicOneselfAdapter extends BaseAdapter {
    Context mContext;
    List<HashMap<String, Object>> mList;
    LayoutInflater mInflater;

    private String url = MyApplication.HOST + "dynamicOperateServlet";

    public DynamicOneselfAdapter(Context context, List<HashMap<String, Object>> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    private void addPraise(int i) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("pDId", i);
        //用户id
        map.put("pUId",((MyApplication)(((DynamicOneselfActivity)mContext).getApplication()))
                .getUserInfo()
                .getUid());
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_oneself_img_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DynamicOneselfBean bean = (DynamicOneselfBean) mList.get(position).get
                ("DynamicOneselfBean");
        Log.e("LDD",bean.toString());
        SpannableStringBuilder time = formatDateTime(bean.getTime());
        holder.mTime.setText(time);
        holder.mContent.setText(bean.getContent());
        List<ImageInfo> infoList = new ArrayList<>();
        for (int i = 0; i < bean.getImgList().size(); i++) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(bean.getImgList().get(i));
            imageInfo.setBigImageUrl(bean.getImgList().get(i));
            infoList.add(imageInfo);
        }
        ClickNineGridViewAdapter adapter = new ClickNineGridViewAdapter(mContext, infoList);
        holder.mGridView.setAdapter(adapter);
        if (((DynamicOneselfBean) mList.get(position).get("DynamicOneselfBean"))
                .getPraiseStatus() == 1) {
            holder.mPraiseImg.setImageResource(R.drawable.ic_praise_red);
        }
        holder.mPraiseImg.setOnClickListener(new View.OnClickListener() {
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
                    holder.mPraiseCount.setText(String.valueOf((
                            (DynamicOneselfBean) mList.get(position).get
                                    ("DynamicOneselfBean"))
                            .getPraiseCount()));
                    holder.mPraiseImg.setImageResource(R.drawable
                            .ic_praise_red);
                    addPraise(((DynamicOneselfBean) mList.get(position).get
                            ("DynamicOneselfBean")).getdId());
                } else if (((DynamicOneselfBean) mList.get(position).get
                        ("DynamicOneselfBean")).getPraiseStatus()==1) {
                }
            }
        });

        holder.mPraiseCount.setText("" + ((DynamicOneselfBean) mList.get
                (position).get
                ("DynamicOneselfBean")).getPraiseCount());

        holder.mCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicCommentActivity.class);
                intent.putExtra("myId", (((MyApplication) ((DynamicOneselfActivity) mContext)
                        .getApplication
                                ()).getUserInfo().getUid()));
                DynamicOneselfBean oneselfBean = (DynamicOneselfBean) mList.get(position).get
                        ("DynamicOneselfBean");
                DynamicImgBean imgBean = new DynamicImgBean(oneselfBean.getdId(), oneselfBean
                        .getName(), oneselfBean.getHeadPhoto(), oneselfBean.getImgList(),
                        oneselfBean.getContent(), oneselfBean.getTime(), oneselfBean
                        .getLocation(), oneselfBean.getPraiseCount(), oneselfBean
                        .getCommentCount(), oneselfBean.getuId(), oneselfBean.getPraiseStatus());
                Log.d("TAG", imgBean.toString());
                intent.putExtra("dynamicBean", imgBean);
                mContext.startActivity(intent);
            }
        });

        holder.mCommentCount.setText("" + ((DynamicOneselfBean)
                mList.get(position)
                        .get("DynamicOneselfBean")).getCommentCount());
        return convertView;
    }

    class ViewHolder {
        public TextView mTime, mContent;
        public NineGridView mGridView;
        TextView mPraiseCount, mCommentCount;
        ImageView mPraiseImg, mCommentImg;

        public ViewHolder(View itemView) {
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
