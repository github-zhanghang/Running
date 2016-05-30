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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.running.adapters.SortAdapter;
import com.running.android_main.NewFriendListActivity;
import com.running.android_main.R;
import com.running.beans.ApiResult;
import com.running.myviews.edittextwithdeel.EditTextWithDel;
import com.running.myviews.sidebar.SideBar;
import com.running.utils.pinyin.PinyinUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.message.ContactNotificationMessage;
import okhttp3.Call;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class XiaoxiRightFragment extends Fragment {
    View mView;
    View header;
    ListView mListView;
    private SortAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;
    private List<ApiResult> mResultList;
    //接受广播
    BroadcastReceiver mReceiver;
    ContactNotificationMessage contactContentMessage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.xiaoxi_right, null);
        initViews();
        getData();
        receiver();
        setOnClickListener();
        mListView.addHeaderView(header);


        return mView;
    }
    private void initViews() {
        mListView = (ListView) mView.findViewById(R.id.lv_contact);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.item_header, null);
        sideBar = (SideBar) mView.findViewById(R.id.sidrbar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        mResultList = new ArrayList<ApiResult>();

        adapter = new SortAdapter(getActivity(), mResultList);
        mListView.setAdapter(adapter);


    }
    private void getData() {
/*
        OkHttpUtils
                .get()
                .url(Api.getGet_Friend())
                .addParams("flag","FRIEND")
                .addParams("sourceUserId",App.sourceUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.e( "test123: ",jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object =  jsonArray.getJSONObject(i);
                                ApiResult apiResult = new ApiResult();
                                apiResult.setId(object.getString("id"));
                                apiResult.setUsername(object.getString("username"));
                                //添加首字母
                                mResultList.add(filledData(apiResult));
                                //根据a-z进行排序源数据
                                Collections.sort(mResultList, new PinyinComparator());

                            }
                            adapter.notifyDataSetChanged();
                            Log.e( "test123: ","好友个数:"+mResultList.size() );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                */
    }

    private ApiResult filledData(ApiResult apiResult) {
        //汉字转换成拼音
        String pinyin = PinyinUtils.getPingYin(apiResult.getUsername());
        String sortString = pinyin.substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if(sortString.matches("[A-Z]")){
            apiResult.setSortLetters(sortString.toUpperCase());
        }else{
            apiResult.setSortLetters("#");
        }
        return apiResult;
    }
    private void receiver() {
       /* mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                contactContentMessage =
                        (ContactNotificationMessage) intent.getExtras().get("rongCloud");
                Log.e("test123: ", "接收到的请求:" + contactContentMessage.getOperation());
                //小红点可见
                header.findViewById(R.id.redPoint).setVisibility(View.VISIBLE);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.ACTION_RECEIVE_MESSAGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
        Log.e("test123: ", "注册接收广播成功");*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*getActivity().unregisterReceiver(mReceiver);
        Log.e("test123: ", "注销接收广播成功");*/
    }

    private void setOnClickListener() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到好友添加

                Intent intent = new Intent(getActivity(), NewFriendListActivity.class);
                if (contactContentMessage != null) {
                    intent.putExtra("friend", contactContentMessage);
                    intent.putExtra("has", true);
                    Log.e("test123:", contactContentMessage.getOperation());
                }
                intent.putExtra("has", false);

                startActivity(intent);
                //消除小红点
                header.findViewById(R.id.redPoint).setVisibility(View.INVISIBLE);
            }
        });
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    mListView.setSelection(position);
                }

            }
        });

        //设置mListView点击事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "position:"+position, Toast.LENGTH_SHORT).show();
                Log.e( "test123: ",mResultList.get(position-1).getUsername() );
                //启动会话界面
                if (RongIM.getInstance() != null){
                    RongIM.getInstance().startPrivateChat
                            (getActivity(), mResultList.get(position-1).getId(), "这是标题");
                }

            }
        });
    }

}
