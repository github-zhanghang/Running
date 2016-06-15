package com.running.android_main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.beans.Friend;
import com.running.eventandcontext.RongCloudContext;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import okhttp3.Call;

/**
 * 会话界面
 */

public class ConversationActivity extends AppCompatActivity
        implements RongIM.ConversationBehaviorListener,
        RongIM.LocationProvider {
    public static final String TAG = "ConversationActivity";
    private TopBar mTopBar;
    private String friendId;
    private String title;
    private Friend mFriend;
    private com.running.beans.UserInfo mUserInfo;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //设置会话界面操作的监听器。
        RongIM.setConversationBehaviorListener(this);
        RongIM.setLocationProvider(this);
        Intent intent = getIntent();
        //获取 userid 和标题
        friendId = intent.getData().getQueryParameter("targetId");
        title = intent.getData().getQueryParameter("title");
        String type = intent.getData().getQueryParameter("type");
        Log.e("test123", "id: " + friendId + "  title:" + title + "  type:" + type);
        initViews();
        request();
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.conversation_TopBar);
        mTopBar.setLeftText(title);
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                ConversationActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });
    }

    private void request() {
        OkHttpUtils
                .post()
                .url(FriendAddActivity.GetNewFriend)
                .addParams("meid", ((MyApplication) getApplication()).getUserInfo().getUid() + "")//用户id
                .addParams("account", friendId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("test123: ", "ConversationActivity:" + response);
                        mUserInfo = new Gson().fromJson(response, com.running.beans.UserInfo.class);

                    }
                });

    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user) {
        if (user != null) {
            Log.e(TAG, "onUserPortraitClick: " + user.getUserId());
            //点击自己头像 跳转到我的资料 点击好友头像 跳转到好友资料
            if (user.getUserId().equals(friendId)) {
                mUserInfo.setNickName(title);
                Intent intent = new Intent(context, PersonInformationActivity.class);
                intent.putExtra("UserInfo", mUserInfo);
                startActivity(intent);
            } else {
                startActivity(new Intent(context, MyDetailsActivity.class));

            }
        }
        return false;
    }

    //长按头像
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {//
        Log.e("ConversationActivity", "----onUserPortraitLongClick");
        return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        Log.e(TAG, "----onMessageClick");
        if (message.getContent() instanceof LocationMessage) {
            Intent intent = new Intent(context, ChatLocationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("haslocation", true);
            intent.putExtra("location", message.getContent());

            context.startActivity(intent);

        } else if (message.getContent() instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Intent intent = new Intent(context, ChatPhotoActivity.class);
            intent.putExtra("photo", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
            if (imageMessage.getThumUri() != null)
                intent.putExtra("thumbnail", imageMessage.getThumUri());
            context.startActivity(intent);
        }
        return false;
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param s       被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    /**
     *
     * @param context
     * @param locationCallback
     */
    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {

        RongCloudContext.getInstance().setLastLocationCallback(locationCallback);
        Intent intent = new Intent(context, ChatLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("haslocation", false);

        context.startActivity(intent);
    }
}
