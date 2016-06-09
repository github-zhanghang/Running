package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.beans.UserInfo;
import com.running.myviews.edittextwithdeel.EditTextWithDel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class FriendAddActivity extends AppCompatActivity {
    private MyApplication mApplication;
    public static final String GetNewFriend
            = "http://192.168.191.1:8080/Running/GetNewFriend";
    private EditTextWithDel mEditTextWithDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mApplication = (MyApplication) getApplication();
        initViews();
    }

    private void initViews() {
        mEditTextWithDel = (EditTextWithDel) findViewById(R.id.addFirend_et);
    }

    public void addNewFriend(View view) {
        /*应该先去服务端查找有无此账号，没有提示无此用户，
            有的话的返回  跳转到用户资料页，在那里再跳转加为好友?
        */
        String account = mEditTextWithDel.getText().toString();
        Log.e("test123: ", "输入:" + account);
        if (!account.equals("")) {
            request(account);

        } else {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 按条件查找好友的点击事件
     *
     * @param view
     */
    public void searchWithConditions(View view) {
        Toast.makeText(this, "条件查找", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(FriendAddActivity.this, SearchConditionsActivity.class));
    }

    /**
     * 搜索附近的点击事件
     *
     * @param view
     */
    public void searchNearby(View view) {
        Toast.makeText(this, "搜附近", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(FriendAddActivity.this, NearbyActivity.class));
    }

    private void request(String account) {
        OkHttpUtils
                .post()
                .url(GetNewFriend)
                .addParams("meid", mApplication.getUserInfo().getUid() + "")//用户id
                .addParams("account", account)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("test123: ", "查用户:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("test123: ", response);
                        UserInfo userInfo =
                                new Gson().fromJson(response, UserInfo.class);
                        if (userInfo != null) {
                            Intent intent = new Intent(FriendAddActivity.this, NewFriendInfoActivity.class);
                            //NearUserInfo nearUserInfo = (NearUserInfo) userInfo;
                            intent.putExtra("NewFriendInfo", userInfo);
                            startActivity(intent);
                            //Toast.makeText(AddFriendActivity.this, userInfo.getNickName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FriendAddActivity.this, "无此用户", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
