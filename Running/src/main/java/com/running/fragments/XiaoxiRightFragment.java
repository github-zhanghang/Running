package com.running.fragments;

import android.content.BroadcastReceiver;
import android.content.Intent;
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
    public static final String GetFriendList =
            "http://10.201.1.185:8080/Running/GetFriendList";
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
                .get()
                .url(GetFriendList)
                .addParams("meid","1")
                .addParams("status","1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object =  jsonArray.getJSONObject(i);
                                Log.e( "test123: ","object:"+object.toString());
                                Friend friend =
                                        new Gson().fromJson(object.toString(),Friend.class);
                                /*Friend friend = new Friend();
                                friend.setFriendid(object.getInt("friendid"));
                                friend.setAccount(object.getString("account"));
                                friend.setRemark(object.getString("remark"));
                                friend.setPortrait(object.getString("portrait"));
                                friend.setFriendtime(object.getString("friendtime"));*/
                                //添加首字母
                                mFriendList.add(filledData(friend));
                                //根据a-z进行排序源数据
                                Collections.sort(mFriendList, new PinyinComparator());
                            }
                            adapter.notifyDataSetChanged();
                            Log.e( "test123: ","好友个数:"+ mFriendList.size() );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
                Log.e( "test123: ", mFriendList.get(position-1).getRemark() );
                Toast.makeText(getActivity(),
                        mFriendList.get(position-1).getRemark(), Toast.LENGTH_SHORT).show();
                //启动会话界面
                /*if (RongIM.getInstance() != null){
                    RongIM.getInstance().startPrivateChat
                            (getActivity(), mFriendList.get(position-1).getAccount(), "这是标题");
                }*/
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
        if(sortString.matches("[A-Z]")){
            friend.setSortLetters(sortString.toUpperCase());
        }else{
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
        }else {
            friends.clear();
            for (Friend f:mFriendList) {
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
