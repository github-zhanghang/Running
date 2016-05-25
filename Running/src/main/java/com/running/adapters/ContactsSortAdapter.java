package com.running.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.model.ContactSortModel;

import java.util.List;

/**
 * Created by Ezio on 2016/5/24.
 */
public class ContactsSortAdapter extends BaseAdapter implements SectionIndexer{
    private List<ContactSortModel> list = null;
    private Context mContext;

    public ContactsSortAdapter(Context context, List<ContactSortModel> list) {
        mContext = context;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ContactSortModel> list) {
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



    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView txImage;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final ContactSortModel sortModel = list.get(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact,null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_city_name);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tv_catagory);
            viewHolder.txImage = (ImageView) convertView.findViewById(R.id.tv_city_tx);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(sortModel.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).getName());
        viewHolder.txImage.setImageResource(R.mipmap.ic_launcher);

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
