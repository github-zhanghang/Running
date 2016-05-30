package com.running.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.running.android_main.R;
import com.running.beans.ApiResult;

import java.util.List;


/**
 * Created by Ezio on 2016/5/26.
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<ApiResult> list;

    public SortAdapter(Context mContext, List<ApiResult> list) {
        this.mContext = mContext;
        this.list = list;
    }
    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ApiResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView txImage;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder =null;
        final ApiResult apiResult = list.get(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact,null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_friend_name);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tv_catagory);
            viewHolder.txImage = (ImageView) convertView.findViewById(R.id.item_friend_tx);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(apiResult.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).getUsername());
        /*Glide.with(mContext)
                    .load(list.get(position).getPortrait())
                    .into(viewHolder.txImage);*/
        viewHolder.txImage.setImageResource(R.drawable.rc_default_portrait);


        return convertView;
    }

   @Override
    public int getSectionForPosition(int position) {

        return list.get(position).getSortLetters().charAt(0);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}
