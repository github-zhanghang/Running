package com.running.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.adapters.SortAdapter;
import com.running.android_main.MyApplication;
import com.running.android_main.NewFriendListActivity;
import com.running.android_main.R;
import com.running.beans.Friend;
import com.running.myviews.edittextwithdeel.EditTextWithDel;
import com.running.myviews.sidebar.SideBar;
import com.running.utils.pinyin.PinyinComparator;
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
    public String GetFriendList = MyApplication.HOST + "GetFriendList";
    View mView;
    View header;
    ListView mListView;
    private SortAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;
    private List<Friend> mFriendList;
    private EditTextWithDel mEtSearchName;
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

        return mView;
    }

    private void initViews() {
        mEtSearchName = (EditTextWithDel) mView.findViewById(R.id.et_search);

        mListView = (ListView) mView.findViewById(R.id.lv_contact);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.item_header, null);
        mListView.addHeaderView(header);
        sideBar = (SideBar) mView.findViewById(R.id.sidrbar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        mFriendList = new ArrayList<Friend>();
        adapter = new SortAdapter(getActivity(), mFriendList);
        mListView.setAdapter(adapter);

    }

    private void getData() {
        OkHttpUtils
                .post()
                .url(GetFriendList)
                .addParams("meid", ((MyApplication) getActivity().getApplication()).getUserInfo().getUid() + "")
                .addParams("status", "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("test123", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        mFriendList.clear();
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.e("test123: ", "object:" + object.toString());
                                Friend friend =
                                        new Gson().fromJson(object.toString(), Friend.class);
                                //添加首字母
                                mFriendList.add(filledData(friend));
                                //根据a-z进行排序源数据
                                Collections.sort(mFriendList, new PinyinComparator());
                            }
                            adapter.notifyDataSetChanged();
                            Log.e("test123: ", "好友个数:" + mFriendList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void receiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                contactContentMessage =
                        (ContactNotificationMessage) intent.getExtras().get("rongCloud");
                Log.e("test123: ", "接收到的请求:" + contactContentMessage.getOperation());
                String operation = contactContentMessage.getOperation();
                if (operation.equals(ContactNotificationMessage.CONTACT_OPERATION_REQUEST)) {
                    //小红点可见
                    header.findViewById(R.id.redPoint).setVisibility(View.VISIBLE);
                } else if (operation.equals(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)) {
                    //对方同意加为好友 更新好友列表
                    getData();
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ACTION_RECEIVE_MESSAGE");
        getActivity().registerReceiver(mReceiver, intentFilter);
        Log.e("test123: ", "注册加好友接收广播成功");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        Log.e("test123: ", "注销加好友接收广播成功");
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
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });
        //设置mListView点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Friend friend = (Friend) adapter.getItem(position - 1);
                Toast.makeText(getActivity(),
                        friend.getRemark(), Toast.LENGTH_SHORT).show();
                //启动会话界面
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat
                            (getActivity(), friend.getAccount(), friend.getRemark());
                }
            }
        });

        //根据输入框输入值的改变来过滤搜索
        mEtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private Friend filledData(Friend friend) {
        //汉字转换成拼音
        String pinyin = PinyinUtils.getPingYin(friend.getRemark());
        String sortString = pinyin.substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            friend.setSortLetters(sortString.toUpperCase());
        } else {
            friend.setSortLetters("#");
        }
        return friend;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Friend> friends = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            friends = mFriendList;
        } else {
            friends.clear();
            for (Friend f : mFriendList) {
                String name = f.getRemark();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1 || PinyinUtils.getPingYin(name).toUpperCase().startsWith(filterStr.toString().toUpperCase())) {
                    friends.add(f);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(friends, new PinyinComparator());
        adapter.updateListView(friends);

    }


}
