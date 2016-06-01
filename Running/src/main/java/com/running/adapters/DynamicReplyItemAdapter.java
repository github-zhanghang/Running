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
        SpannableStringBuilder stringBuilder=addMyClickableSpan(
                secondCommentBean.getId(),
                secondCommentBean.getToId(),
                secondCommentBean.getcId(),
                secondCommentBean.getName(),
                secondCommentBean.getToName(),
                secondCommentBean.getContent(),
                (MySpan.OnClickListener) mContext);
        viewHolder.mTextView.setText(stringBuilder);
        viewHolder.mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.mTextView.setHighlightColor(Color.TRANSPARENT);
        return convertView;
    }

    //对回复textView格式进行设置
    private SpannableStringBuilder addMyClickableSpan(Integer nId, Integer tNid, Integer cId,
                                                      String name, String toName, String content,
                                                      MySpan.OnClickListener context) {
        mOnClickListener=context;
        String s=name+" 回复 "+toName+" : "+content;
        SpannableStringBuilder stringBuilder=new SpannableStringBuilder(s);
        MySpan span=new MySpan(mContext,0xffFF4081,mOnClickListener);
        span.setId(nId);
        span.setContent(name);
        stringBuilder.setSpan(span,
                0, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span=new MySpan(mContext,0xffFF4081,mOnClickListener);
        span.setId(tNid);
        span.setContent(toName);
        stringBuilder.setSpan(span,
                name.length()+4,
                name.length()+4+toName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span=new MySpan(mContext,mOnClickListener);
        span.setId(cId);
        span.setContent(content);
        stringBuilder.setSpan(span,
                name.length()+7+toName.length(),
                name.length()+content.length()+toName.length()+7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }
}
