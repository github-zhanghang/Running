package com.running.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.running.adapters.ContactsSortAdapter;
import com.running.android_main.R;
import com.running.model.ContactSortModel;
import com.running.myviews.edittextwithdeel.EditTextWithDel;
import com.running.myviews.sidebar.SideBar;

import java.util.List;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class XiaoxiRightFragment extends Fragment {
    private View mRightView;

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private ContactsSortAdapter adapter;
    private EditTextWithDel mEtSearchName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRightView = inflater.inflate(R.layout.xiaoxi_right, null);
        initViews();
        initDatas();
        return mRightView;
    }
    private void initDatas() {
        sideBar.setTextView(dialog);
    }
    private void initViews() {
        mEtSearchName = (EditTextWithDel) mRightView.findViewById(R.id.et_search);
        sideBar = (SideBar) mRightView.findViewById(R.id.sidrbar);
        dialog = (TextView) mRightView.findViewById(R.id.dialog);
        sortListView = (ListView) mRightView.findViewById(R.id.lv_contact);
    }
}
