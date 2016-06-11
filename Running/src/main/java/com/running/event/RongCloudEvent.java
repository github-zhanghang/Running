package com.running.event; /**
 * Created by Ezio on 2016/5/26.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.running.android_main.NewFriendListActivity;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.InformationNotificationMessage;

/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。1
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。1
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。1
 */
public class RongCloudEvent implements Handler.Callback,RongIMClient.OnReceiveMessageListener,
        RongIM.UserInfoProvider,RongIM.ConversationListBehaviorListener{
    private static final String TAG = "RongCloudEvent";

    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;
    private Handler mHandler;

    /**
     * 构造方法。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        initDefaultListener();
        mHandler = new Handler(this);
    }

    /**
     * 初始化 RongCloud.
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }
    /**
     * 获取RongCloud 实例。
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }


    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {
        //RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
        RongIM.setConversationListBehaviorListener(this);//会话列表界面操作的监听器
    }
    /**
     * 连接成功注册。
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener(){
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);//设置消息接收监听器。
    }


    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     * @param message 接收到的消息的实体信息。
     * @param i 剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(io.rong.imlib.model.Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {//好友添加消息
            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
            Log.e(TAG, "加好友消息接收监听-id:" + contactContentMessage.getSourceUserId());
            Log.e(TAG, "加好友消息接收监听-Message:" + contactContentMessage.getMessage().toString());
            //发送广播
            Intent in = new Intent();
            in.setAction("ACTION_RECEIVE_MESSAGE");
            in.putExtra("flag","ContactNotificationMessage");
            in.putExtra("ContactNotificationMessage", contactContentMessage);
            mContext.sendBroadcast(in);
        } else if (messageContent instanceof InformationNotificationMessage){
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            Log.e(TAG, "小灰条消息接收监听:" + informationNotificationMessage.getMessage());

            //发送广播
            Intent in = new Intent();
            in.setAction("ACTION_RECEIVE_MESSAGE");
            in.putExtra("flag","InformationNotificationMessage");
            in.putExtra("InformationNotificationMessage", informationNotificationMessage);
            mContext.sendBroadcast(in);
        }

        return false;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        /**
         * 1.从本地获取
         * 2.从server获取
         */

        return null;
    }

    //会话列表的四个事件
    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        Log.e(TAG, "--------会话列表头像点击事件------- 会话类型"+conversationType );
        /* ContactNotificationMessage 的类型是 SYSTEM
            InformationNotificationMessage 的类型是 PRIVATE
         */
        Log.e(TAG, "--------会话列表头像点击事件-------");
        if (conversationType.equals("SYSTEM")) {
            //跳转到新的好友列表
            context.startActivity(new Intent(context, NewFriendListActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    /**
     * 点击会话列表 item 后执行。
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param uiConversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        MessageContent messageContent = uiConversation.getMessageContent();

        Log.e(TAG, "--------会话列表Item点击事件-------");
        if (messageContent instanceof ContactNotificationMessage) {
            Log.e(TAG, "---会话列表Item点击事件--联系人(好友)通知消息-");

            //跳转到新的好友列表
            context.startActivity(new Intent(context, NewFriendListActivity.class));
            return true;
        }else if (messageContent instanceof  InformationNotificationMessage){
            InformationNotificationMessage ifm =
                    (InformationNotificationMessage) messageContent;

            //Log.e(TAG, "---会话列表点击事件--小灰条通知消息-userId:"+ifm.getUserInfo().getUserId());
        }
        return false;
    }

}
