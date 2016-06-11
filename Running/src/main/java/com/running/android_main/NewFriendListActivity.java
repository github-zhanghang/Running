package com.running.android_main;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.adapters.NewFriendListAdapter;
import com.running.beans.Friend;
import com.running.beans.UserInfo;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.message.ContactNotificationMessage;
import okhttp3.Call;

/**
 * 获取添加过的好友信息 ,
 * 新好友添加显示 2种思路吧
 * 1.从广播中获得
 * 2.从自己server维护的好友关系
 * 选第二种吧 现在每次进这个页面都去获取
 * 好友关系状态status :
 * 1 好友, 2 请求添加, 3 请求被添加,4 拒绝 ,5 请求被拒绝
 */
public class NewFriendListActivity extends AppCompatActivity {
    public static final String GetNewFriendList = MyApplication.HOST + "GetFriendList";
    public static final int requestCode = 100;
    private TopBar mTopBar;
    ContactNotificationMessage contactContentMessage;

    private ListView mNewFriendList;

    private List<Friend> mResultList;
    private NewFriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_frined);
        initView();
        request();
        clickListener();
    }


    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.new_topbar);
        mNewFriendList = (ListView) findViewById(R.id.new_friend_list);
        mResultList = new ArrayList<Friend>();
        adapter = new NewFriendListAdapter(mResultList, NewFriendListActivity.this);
        mNewFriendList.setAdapter(adapter);
    }

    private void request() {
        OkHttpUtils
                .post()
                .url(GetNewFriendList)
                .addParams("meid", ((MyApplication) getApplication()).getUserInfo().getUid() + "")
                .addParams("status", "0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("test123", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("test123: ", "NewFriendListActivity:" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Friend friend = new Friend();
                                friend = new Gson().fromJson(object.toString(),Friend.class);

                                mResultList.add(friend);
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //adapter的点击事件
                        setOnItemButtonClick();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == requestCode) {
            int p = (int) data.getExtras().get("position");
            boolean flag = (boolean) data.getExtras().get("flag");
            Log.e("NewFriendListActivity", "flag: "+flag );

        }*/
        if (requestCode == requestCode) {
            int p = (int) data.getExtras().get("position");
            boolean flag = (boolean) data.getExtras().get("flag");
            Friend friend = mResultList.get(p);
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(friend.getFriendid());
            userInfo.setNickName(friend.getRemark());
            userInfo.setAccount(friend.getAccount());
            if (flag) {
                sendMessage(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE, userInfo);
                mResultList.get(p).setStatus(1);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, " 同意该请求", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(ContactNotificationMessage.CONTACT_OPERATION_REJECT_RESPONSE, userInfo);
                mResultList.get(p).setStatus(4);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, " 拒绝该请求", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clickListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                NewFriendListActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
        mNewFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mResultList.get(position).getStatus() == 1){
                    //如果是好友， 跳转到好友资料PersonInformationActivity
                    Friend friend = mResultList.get(position);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUid(friend.getFriendid());
                    userInfo.setAccount(friend.getAccount());
                    userInfo.setNickName(friend.getRemark());
                    userInfo.setImageUrl(friend.getPortrait());
                    userInfo.setAge(friend.getAge());
                    userInfo.setSex(friend.getSex());
                    userInfo.setAddress(friend.getAddress());
                    Intent intent = new Intent(NewFriendListActivity.this, PersonInformationActivity.class);
                    intent.putExtra("UserInfo", userInfo);
                    startActivity(intent);
                }else if (mResultList.get(position).getStatus() == 2){
                    Toast.makeText(NewFriendListActivity.this,"请求已经发送", Toast.LENGTH_SHORT).show();
                }else if (mResultList.get(position).getStatus() == 5){
                    Toast.makeText(NewFriendListActivity.this,"对方拒绝了你的请求", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(NewFriendListActivity.this, mResultList.get(position).getRemark(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewFriendListActivity.this, RejectAddActivity.class);
                    intent.putExtra("Friend", mResultList.get(position));
                    intent.putExtra("Position", position);
                    startActivityForResult(intent, requestCode);
                }

            }
        });
    }

    //adapter的点击事件
    private void setOnItemButtonClick() {
        adapter.setOnItemButtonClick(new NewFriendListAdapter.OnItemButtonClick() {
            @Override
            public boolean onButtonClick(int position, View view, int status) {
                switch (status) {
                    case 1://好友

                        break;
                    case 2://请求添加

                        break;
                    case 3://请求被添加
                        //发送消息给server
                        Friend friend = mResultList.get(position);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUid(friend.getFriendid());
                        userInfo.setNickName(friend.getRemark());
                        userInfo.setAccount(friend.getAccount());
                        sendMessage(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE, userInfo);
                        mResultList.get(position).setStatus(1);
                        adapter.notifyDataSetChanged();
                        break;
                    case 4://拒绝

                        break;
                    case 5://请求被拒绝

                        break;
                }
                return false;
            }
        });
    }


    private void sendMessage(String mark, UserInfo userInfo) {
        String message = null;
        if (mark.equals(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)){
            message = ((MyApplication) getApplication()).getUserInfo().getNickName()+"同意了你的请求";
        }else {
            message = ((MyApplication) getApplication()).getUserInfo().getNickName()+"拒绝了你的请求";
        }

        OkHttpUtils
                .post()
                .url(NewFriendInfoActivity.ADD_FRIEND)
                .addParams("flag", mark)
                .addParams("sourceUserId", new Gson().toJson(((MyApplication) getApplication()).getUserInfo()))
                .addParams("targetUserId", new Gson().toJson(userInfo))
                .addParams("message", message)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        if (response.equals(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)) {
                            Toast.makeText(NewFriendListActivity.this, "添加好友成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
