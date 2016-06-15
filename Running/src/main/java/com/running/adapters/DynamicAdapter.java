package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.android_main.DynamicCommentActivity;
import com.running.android_main.DynamicOneselfActivity;
import com.running.android_main.MainActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.myviews.ninegridview.ImageInfo;
import com.running.myviews.ninegridview.NineGridView;
import com.running.myviews.ninegridview.preview.ClickNineGridViewAdapter;
import com.running.utils.GlideCircleTransform;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by ldd on 2016/5/29.
 * 动态适配器
 */
public class DynamicAdapter extends BaseAdapter {
    Context mContext;
    List<HashMap<String, Object>> mList;
    LayoutInflater mInflater;
    //img GridView适配器
    ClickNineGridViewAdapter mAdapter;

    MyApplication mMyApplication;

    String url = MyApplication.HOST + "dynamicOperateServlet";

    DynamicImgBean dynamicImgBean;

    int myPosition;

    public DynamicAdapter(Context context, List<HashMap<String, Object>> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mMyApplication = (MyApplication) ((MainActivity) mContext).getApplication();
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

    public boolean getType(int position) {
        boolean flag = true;
        if (mList.get(position).get("type").equals("img")) {
            flag = true;
        } else if (mList.get(position).get("type").equals("link")) {
            flag = false;
        }
        return flag;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (getType(position)) {
            //普通动态（图片+内容）
            final ImgViewHolder imgViewHolder;
            View imgView = mInflater.inflate(R.layout.dynamic_img_item, parent, false);
            if (convertView == null) {
                convertView = imgView;
                imgViewHolder = new ImgViewHolder(convertView);
                convertView.setTag(imgViewHolder);
            } else if (convertView != imgView) {
                //如果convertView复用后不是imgView
                convertView = imgView;
                imgViewHolder = new ImgViewHolder(convertView);
                convertView.setTag(imgViewHolder);
            } else {
                imgViewHolder = (ImgViewHolder) convertView.getTag();
            }
            final DynamicImgBean dynamicImgBean = (DynamicImgBean) mList.get(position).get("DynamicBean");
            //显示普通动态
            myPosition = position;
            Log.e("TAG",position+dynamicImgBean.getdUId()+"Test");
            Glide.with(mContext)
                    .load(dynamicImgBean.getHeadPhoto())
                    .centerCrop()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imgViewHolder.mDynamicImgItemHeadImg);
            //头像设置点击事件
            imgViewHolder.mDynamicImgItemHeadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicOneselfActivity.class);
                    Log.e("TAG","TEST");
                    Log.d("TAG",dynamicImgBean.getdUId()+"test"+dynamicImgBean.toString());
                    intent.putExtra("uId",dynamicImgBean.getdUId());
                    Log.d("TAG",""+((DynamicImgBean)mList.get(myPosition).get
                            ("DynamicBean")).getdUId());
                    mContext.startActivity(intent);
                }
            });
            imgViewHolder.mDynamicImgItemName.setText(dynamicImgBean.getName());
            imgViewHolder.mDynamicImgItemTime.setText(timeChange(dynamicImgBean.getTime()));
            imgViewHolder.mDynamicImgItemContent.setText(dynamicImgBean.getContent());
            //img GridView的添加值
            List<ImageInfo> infoList = new ArrayList<>();
            for (int i = 0; i < dynamicImgBean.getImgList().size(); i++) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl(dynamicImgBean.getImgList().get(i));
                imageInfo.setBigImageUrl(dynamicImgBean.getImgList().get(i));
                infoList.add(imageInfo);
            }
            mAdapter = new ClickNineGridViewAdapter(mContext, infoList);
            imgViewHolder.mDynamicImgItemGridView.setAdapter(mAdapter);
            imgViewHolder.mDynamicImgItemLocation.setText(dynamicImgBean.getLocation());
            if (((DynamicImgBean)mList.get(position).get
                    ("DynamicBean")).getPraiseStatus() == 1) {
                imgViewHolder.mDynamicImgItemPraiseImg.setImageResource(R.drawable.praise_red);
            }
            imgViewHolder.mDynamicImgItemPraiseCount.setText(String.valueOf(dynamicImgBean
                    .getPraiseCount()));
            imgViewHolder.mDynamicImgItemPraiseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((DynamicImgBean)mList.get(position).get
                            ("DynamicBean")).getPraiseStatus() == 0) {
                        ((DynamicImgBean)mList.get(position).get
                                ("DynamicBean")).setPraiseStatus(1);
                        ((DynamicImgBean)mList.get(position).get
                                ("DynamicBean")).setPraiseCount(((DynamicImgBean)mList.get(position).get
                                ("DynamicBean")).getPraiseCount() + 1);
                        imgViewHolder.mDynamicImgItemPraiseCount.setText(String.valueOf(((DynamicImgBean)mList.get(position).get
                                ("DynamicBean"))
                                .getPraiseCount()));
                        imgViewHolder.mDynamicImgItemPraiseImg.setImageResource(R.drawable.praise_red);
                        addPraise(((DynamicImgBean)mList.get(position).get
                                ("DynamicBean")).getdId());
                    } else if (((DynamicImgBean)mList.get(myPosition).get
                            ("DynamicBean")).getPraiseStatus() == 1) {
                        //imgViewHolder.mDynamicImgItemPraiseImg.setImageResource(R.drawable
                        // .praise_red);
                    }
                }
            });


            //跳转到评论界面
            imgViewHolder.mDynamicImgItemCommentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DynamicCommentActivity.class);
                    intent.putExtra("myId", mMyApplication.getUserInfo().getUid());
                    intent.putExtra("dynamicBean", ((DynamicImgBean)mList.get(position).get
                            ("DynamicBean")));
                    mContext.startActivity(intent);
                }
            });
            imgViewHolder.mDynamicImgItemCommentCount.setText(String.valueOf(dynamicImgBean
                    .getCommentCount()));
        } else {
        }
        return convertView;
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


    private String timeChange(String time) {
        String s = "";
        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatTime.parse(time);
            SimpleDateFormat formatString = new SimpleDateFormat("MM-dd HH:mm");
            s = formatString.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    //ImgGridView ViewHolder
    static class ImgViewHolder {
        @Bind(R.id.dynamic_img_item_head_img)
        ImageView mDynamicImgItemHeadImg;
        @Bind(R.id.dynamic_img_item_name)
        TextView mDynamicImgItemName;
        @Bind(R.id.dynamic_img_item_time)
        TextView mDynamicImgItemTime;
        @Bind(R.id.dynamic_img_item_content)
        TextView mDynamicImgItemContent;
        @Bind(R.id.dynamic_img_item_gridView)
        NineGridView mDynamicImgItemGridView;
        @Bind(R.id.dynamic_img_item_location_img)
        ImageView mDynamicImgItemLocationImg;
        @Bind(R.id.dynamic_img_item_location)
        TextView mDynamicImgItemLocation;
        @Bind(R.id.dynamic_img_item_praise_img)
        ImageView mDynamicImgItemPraiseImg;
        @Bind(R.id.dynamic_img_item_praise_count)
        TextView mDynamicImgItemPraiseCount;
        @Bind(R.id.dynamic_img_item_comment_img)
        ImageView mDynamicImgItemCommentImg;
        @Bind(R.id.dynamic_img_item_comment_count)
        TextView mDynamicImgItemCommentCount;

        ImgViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}