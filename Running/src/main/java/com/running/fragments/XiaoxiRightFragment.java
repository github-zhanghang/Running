package com.running.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.running.adapters.ContactsSortAdapter;
import com.running.android_main.NewFrinedActivity;
import com.running.android_main.R;
import com.running.myviews.edittextwithdeel.EditTextWithDel;
import com.running.myviews.sidebar.SideBar;

import java.util.ArrayList;
import java.util.List;

import io.rong.message.ContactNotificationMessage;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class XiaoxiRightFragment extends Fragment {
    private View mRightView;
    private View header;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private ContactsSortAdapter adapter;
    private EditTextWithDel mEtSearchName;
    //接受广播
    BroadcastReceiver mReceiver;
    ContactNotificationMessage contactContentMessage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRightView = inflater.inflate(R.layout.xiaoxi_right, null);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.item_header,null);

        receiver();
        initViews();
        initDatas();
        setOnClickListener();

        sortListView.addHeaderView(header);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("春风十里"+i);
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
        sortListView.setAdapter(adapter);
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


    private void setOnClickListener() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到新的朋友
                Intent intent = new Intent(getActivity(), NewFrinedActivity.class);
                if (contactContentMessage != null){
                    intent.putExtra("friend",contactContentMessage);
                    intent.putExtra("has",true);
                }
                intent.putExtra("has",false);
                startActivity(intent);
                //消除小红点
                header.findViewById(R.id.redPoint).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void receiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals("ContactNtf")){
                    //接收到消息（三种任意）
                    contactContentMessage =
                            (ContactNotificationMessage) intent.getExtras().get("Friend");
                    Log.e("test123: ","接收到的请求:"+contactContentMessage.getOperation() );
                    //小红点可见
                    header.findViewById(R.id.redPoint).setVisibility(View.VISIBLE);

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ContactNtf");
        getActivity().registerReceiver(mReceiver,intentFilter);
        Log.e( "test123: ", "注册广播成功");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        Log.e( "test123: ", "注销广播成功");
    }
}
