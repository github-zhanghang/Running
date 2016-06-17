package com.running.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.running.android_main.R;
import com.running.beans.SecondCommentBean;
import com.running.utils.MySpan;

import java.util.List;

/**
 * Created by ldd on 2016/5/30.
 * 评论界面二级评论适配器
 */
public class DynamicReplyItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<SecondCommentBean> mList;
    private LayoutInflater mInflater;

    private MySpan.OnClickListener mOnClickListener;

    public DynamicReplyItemAdapter(Context context, List<SecondCommentBean> list) {
        mContext = context;
        mList = list;
        mInflater=LayoutInflater.from(mContext);
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

    class ViewHolder {
        TextView mTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null) {
            convertView=mInflater.inflate(R.layout.dynamic_comment_reply_item,parent,false);
            viewHolder=new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView= (TextView) convertView.findViewById(
                R.id.dynamic_comment_reply_content);
        SecondCommentBean secondCommentBean=mList.get(position);
        SpannableStringBuilder stringBuilder=addMyClickableSpan(secondCommentBean,
                (MySpan.OnClickListener) mContext);
        viewHolder.mTextView.setText(stringBuilder);
        viewHolder.mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.mTextView.setHighlightColor(Color.TRANSPARENT);
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "TEST", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    //对回复textView格式进行设置
    private SpannableStringBuilder addMyClickableSpan(SecondCommentBean bean ,
                                                      MySpan.OnClickListener context) {
        mOnClickListener=context;
        String s=bean.getuName0()+" 回复 "+bean.getuName1()+" : "+bean.getsContent();
        SpannableStringBuilder stringBuilder=new SpannableStringBuilder(s);
        MySpan span=new MySpan(mContext,0xFFEB4F38,mOnClickListener);
        span.setId(bean.getuId0());
        span.setContent(bean.getuName0());
        span.setBean(bean);
        stringBuilder.setSpan(span,
                0, bean.getuName0().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span=new MySpan(mContext,0xFFEB4F38,mOnClickListener);
        span.setId(bean.getuId1());
        span.setContent(bean.getuName1());
        span.setBean(bean);
        stringBuilder.setSpan(span,
                bean.getuName0().length()+4,
                bean.getuName0().length()+4+bean.getuName1().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span=new MySpan(mContext,mOnClickListener);
        span.setId(bean.getsFCId());
        span.setContent(bean.getsContent());
        span.setBean(bean);
        stringBuilder.setSpan(span,
                bean.getuName0().length()+7+bean.getuName1().length(),
                bean.getuName0().length()+bean.getsContent().length()+bean.getuName1().length()+7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }
}
