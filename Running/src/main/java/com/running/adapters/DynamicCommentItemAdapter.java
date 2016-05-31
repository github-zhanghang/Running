package com.running.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.running.android_main.R;
import com.running.beans.FirstCommentBean;
import com.running.beans.SecondCommentBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ldd on 2016/5/30.
 * 评论界面一级评论适配器
 */
public class DynamicCommentItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<HashMap<String, Object>> mList;
    private LayoutInflater mInflater;

    public DynamicCommentItemAdapter(Context context, List<HashMap<String, Object>> list) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_comment_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FirstCommentBean firstCommentBean = (FirstCommentBean) mList.get(position)
                .get("firstComment");
        viewHolder.firstCommentName.setText(firstCommentBean.getName());
        List<SecondCommentBean> list = (List<SecondCommentBean>) mList.get(position)
                .get("secondComment");
        DynamicReplyItemAdapter replyItemAdapter = new DynamicReplyItemAdapter(mContext, list);
        viewHolder.twoCommentListView.setAdapter(replyItemAdapter);
        return convertView;
    }

    public class ViewHolder {
        public final ImageView img;
        public final TextView firstCommentName;
        public final TextView firstCommentTime;
        public final TextView reply;
        public final TextView firstCommentContent;
        public final LinearListView twoCommentListView;
        public final View root;

        public ViewHolder(View root) {
            img = (ImageView) root.findViewById(R.id.dynamic_comment_item_img);
            firstCommentName = (TextView) root.findViewById(R.id
                    .dynamic_comment_item_firstComment_name);
            firstCommentTime = (TextView) root.findViewById(R.id
                    .dynamic_comment_item_firstComment_time);
            reply = (TextView) root.findViewById(R.id
                    .dynamic_comment_item__reply);
            firstCommentContent = (TextView) root.findViewById(R.id
                    .dynamic_comment_item_firstComment_content);
            twoCommentListView = (LinearListView) root.findViewById(R.id
                    .dynamic_comment_item_twoComment_listView);
            this.root = root;
        }
    }
}
