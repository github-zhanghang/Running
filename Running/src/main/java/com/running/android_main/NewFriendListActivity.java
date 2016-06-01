package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.running.adapters.NewFriendListAdapter;
import com.running.beans.ApiResult;
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
 * 1 好友, 2 请求添加, 3 请求被添加,4请求拒绝 ,5 请求被拒绝
 */
public class NewFriendListActivity extends AppCompatActivity {
    TextView mTextView;
    ContactNotificationMessage contactContentMessage;

    private ListView mNewFriendList;
    //ApiResult 是看demo里的改的 方便扩展
    private List<ApiResult> mResultList;
    private NewFriendListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_frined);
        initView();
       // request();
    }

    private void initView() {
        mNewFriendList = (ListView) findViewById(R.id.new_friend_list);
        mResultList = new ArrayList<ApiResult>();
    }
   /* private void request() {
        OkHttpUtils
                .get()
                .url(Api.getGet_Friend())
                .addParams("flag","NEW_FRIEND")
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
                                apiResult.setStatus(object.getInt("status"));
                                mResultList.add(apiResult);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e( "test123: ",mResultList.size()+"");
                        adapter = new NewFriendListAdapter(mResultList, NewFriendListActivity.this);
                        mNewFriendList.setAdapter(adapter);
                        //adapter的点击事件

                        setOnItemButtonClick();
                    }
                });
    }
     private void setOnItemButtonClick() {
        adapter.setOnItemButtonClick(new NewFriendListAdapter.OnItemButtonClick() {
            @Override
            public boolean onButtonClick(int position, View view, int status) {
                switch (status){
                    case 1://好友

                        break;
                    case 2://请求添加

                        break;
                    case 3://请求被添加
                        //发送消息给server
                        sendMessage(mResultList.get(position).getId());
                        mResultList.get(position).setStatus(1);
                        adapter.notifyDataSetChanged();
                        break;
                    case 4://请求被拒绝

                        break;
                    case 5://我被对方删除

                        break;
                }
                return false;
            }
        });
    }

    private void sendMessage(String id) {
        OkHttpUtils
                .get()
                .url(Api.getADD_FRIEND())
                .addParams("flag", ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)
                .addParams("sourceUserId",App.sourceUserId)
                .addParams("targetUserId",id)
                .addParams("message","")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        if (response.equals(ContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)){
                            Toast.makeText(NewFriendListActivity.this, "添加好友成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/


}
