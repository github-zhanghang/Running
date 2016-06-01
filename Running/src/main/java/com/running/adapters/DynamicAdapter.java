package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.android_main.DynamicCommentActivity;
import com.running.android_main.DynamicOneselfActivity;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.beans.DynamicLinkBean;
import com.running.myviews.MyGridView;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldd on 2016/5/29.
 * 动态适配器
 */
public class DynamicAdapter extends BaseAdapter {
    Context mContext;
    List<HashMap<String, Object>> mList;
    LayoutInflater mInflater;
    //img GridView适配器
    DynamicImgGridViewAdapter mAdapter;
    @Bind(R.id.dynamic_img_item_head_img)
    ImageView mDynamicImgItemHeadImg;
    @Bind(R.id.dynamic_img_item_name)
    TextView mDynamicImgItemName;
    @Bind(R.id.dynamic_img_item_time)
    TextView mDynamicImgItemTime;
    @Bind(R.id.dynamic_img_item_content)
    TextView mDynamicImgItemContent;
    @Bind(R.id.dynamic_img_item_gridView)
    MyGridView mDynamicImgItemGridView;
    @Bind(R.id.dynamic_img_item_p_background)
    ImageView mDynamicImgItemPBackground;
    @Bind(R.id.dynamic_img_item_praise_img)
    ImageView mDynamicImgItemPraiseImg;
    @Bind(R.id.dynamic_img_item_praise_count)
    TextView mDynamicImgItemPraiseCount;
    @Bind(R.id.dynamic_img_item_c_background)
    ImageView mDynamicImgItemCBackground;
    @Bind(R.id.dynamic_img_item_comment_img)
    ImageView mDynamicImgItemCommentImg;
    @Bind(R.id.dynamic_img_item_comment_count)
    TextView mDynamicImgItemCommentCount;

    public DynamicAdapter(Context context, List<HashMap<String, Object>> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
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
        if (mList.get(position).get("type").equals("image")) {
            flag = true;
        } else if (mList.get(position).get("type").equals("link")) {
            flag = false;
        }
        return flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getType(position)) {
            //普通动态（图片+内容）
            ImgViewHolder imgViewHolder;
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
            DynamicImgBean dynamicImgBean = (DynamicImgBean) mList.get(position).get("img");
            //显示普通动态
            showDynamicImg(imgViewHolder, dynamicImgBean);
        } else {
            //分享链接动态
            LinkViewHolder linkViewHolder;
            View linkView = mInflater.inflate(R.layout.dynamic_link_item, parent, false);
            if (convertView == null) {
                convertView = linkView;
                linkViewHolder = new LinkViewHolder(convertView);
                convertView.setTag(linkViewHolder);
            } else if (convertView != linkView) {
                convertView = linkView;
                linkViewHolder = new LinkViewHolder(convertView);
                convertView.setTag(linkViewHolder);
            } else {
                linkViewHolder = (LinkViewHolder) convertView.getTag();
            }
            DynamicLinkBean dynamicLinkBean = (DynamicLinkBean) mList.get(position).get("link");
            //显示链接动态
            showDynamicLink(linkViewHolder, dynamicLinkBean);
        }
        return convertView;
    }

    private void showDynamicLink(LinkViewHolder linkViewHolder, DynamicLinkBean dynamicLinkBean) {
        Glide.with(mContext).
                load(dynamicLinkBean.getHeadImg()).
                placeholder(dynamicLinkBean.getHeadImg()).
                into(linkViewHolder.mDynamicLinkItemHeadImg);
        linkViewHolder.mDynamicLinkItemHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicOneselfActivity.class);
                mContext.startActivity(intent);
            }
        });
        linkViewHolder.mDynamicLinkItemName.setText(dynamicLinkBean.getName());
        linkViewHolder.mDynamicLinkItemContent.setText(dynamicLinkBean.getContent());
        //链接部分内容
        linkViewHolder.mDynamicLinkItemLinkImg.setImageResource((Integer) dynamicLinkBean
                .getLink().get("linkImg"));
        linkViewHolder.mDynamicLinkItemLinkTittle.setText((String) dynamicLinkBean
                .getLink().get("linkName"));
        linkViewHolder.mDynamicLinkItemLinkData.setText((String) dynamicLinkBean.getLink()
                .get("linkData"));
        linkViewHolder.mDynamicLinkItemPraiseCount.setText(String.valueOf(dynamicLinkBean
                .getPraiseCount()));
        linkViewHolder.mDynamicLinkItemCommentImg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicCommentActivity.class);
                mContext.startActivity(intent);
                    }
                });
        linkViewHolder.mDynamicLinkItemPraiseCount.setText(String.valueOf(dynamicLinkBean
                .getCommentCount()));
    }

    //显示普通动态
    private void showDynamicImg(ImgViewHolder imgViewHolder, DynamicImgBean dynamicImgBean) {
        Glide.with(mContext).
                load(dynamicImgBean.getHeadImg()).
                placeholder(dynamicImgBean.getHeadImg()).
                into(imgViewHolder.mDynamicImgItemHeadImg);
        //头像设置点击事件
        imgViewHolder.mDynamicImgItemHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicOneselfActivity.class);
                mContext.startActivity(intent);
            }
        });
        imgViewHolder.mDynamicImgItemName.setText(dynamicImgBean.getName());
        imgViewHolder.mDynamicImgItemContent.setText(dynamicImgBean.getContent());
        //img GridView的添加值
        mAdapter = new DynamicImgGridViewAdapter(mContext, dynamicImgBean.getImgList(),
                imgViewHolder.mDynamicImgItemGridView);
        imgViewHolder.mDynamicImgItemGridView.setAdapter(mAdapter);

        imgViewHolder.mDynamicImgItemPraiseCount.setText(String.valueOf(dynamicImgBean
                .getPraiseCount()));
        imgViewHolder.mDynamicImgItemCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DynamicCommentActivity.class);
                mContext.startActivity(intent);
            }
        });
        imgViewHolder.mDynamicImgItemCommentCount.setText(String.valueOf(dynamicImgBean
                .getCommentCount()));
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
        MyGridView mDynamicImgItemGridView;
        @Bind(R.id.dynamic_img_item_p_background)
        ImageView mDynamicImgItemPBackground;
        @Bind(R.id.dynamic_img_item_praise_img)
        ImageView mDynamicImgItemPraiseImg;
        @Bind(R.id.dynamic_img_item_praise_count)
        TextView mDynamicImgItemPraiseCount;
        @Bind(R.id.dynamic_img_item_c_background)
        ImageView mDynamicImgItemCBackground;
        @Bind(R.id.dynamic_img_item_comment_img)
        ImageView mDynamicImgItemCommentImg;
        @Bind(R.id.dynamic_img_item_comment_count)
        TextView mDynamicImgItemCommentCount;

        ImgViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class LinkViewHolder {
        @Bind(R.id.dynamic_link_item_head_img)
        ImageView mDynamicLinkItemHeadImg;
        @Bind(R.id.dynamic_link_item_name)
        TextView mDynamicLinkItemName;
        @Bind(R.id.dynamic_link_item_time)
        TextView mDynamicLinkItemTime;
        @Bind(R.id.dynamic_link_item_content)
        TextView mDynamicLinkItemContent;
        @Bind(R.id.dynamic_link_item_linkImg)
        ImageView mDynamicLinkItemLinkImg;
        @Bind(R.id.dynamic_link_item_linkTittle)
        TextView mDynamicLinkItemLinkTittle;
        @Bind(R.id.dynamic_link_item_linkData)
        TextView mDynamicLinkItemLinkData;
        @Bind(R.id.dynamic_link_item_praise_img)
        ImageView mDynamicLinkItemPraiseImg;
        @Bind(R.id.dynamic_link_item_praise_count)
        TextView mDynamicLinkItemPraiseCount;
        @Bind(R.id.dynamic_link_item_comment_img)
        ImageView mDynamicLinkItemCommentImg;
        @Bind(R.id.dynamic_link_item_comment_count)
        TextView mDynamicLinkItemCommentCount;

        LinkViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}