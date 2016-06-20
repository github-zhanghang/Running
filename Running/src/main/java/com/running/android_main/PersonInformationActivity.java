package com.running.android_main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.beans.Friend;
import com.running.beans.UserInfo;
import com.running.myviews.CircleImageView;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import okhttp3.Call;

public class PersonInformationActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.person_information_topBar)
    TopBar mTopBar;
    @Bind(R.id.person_information_headImg)
    CircleImageView mHeadImg;
    @Bind(R.id.person_information_name)
    TextView mName;
    @Bind(R.id.person_information_sexAndAge)
    TextView mSexAndAge;
    @Bind(R.id.person_information_account)
    TextView mAccount;
    @Bind(R.id.person_information_location)
    TextView mLocation;
    @Bind(R.id.person_information_sumDistance)
    TextView mSumDistance;
    @Bind(R.id.person_information_sumTime)
    TextView mSumTime;
    @Bind(R.id.person_information_whoDynamic)
    TextView mWhoDynamic;
    @Bind(R.id.person_information_dynamicLayout)
    LinearLayout mDynamicLayout;
    @Bind(R.id.person_information_sendMessage)
    Button mSendMessage;
    private UserInfo mUserInfo;
    private HashMap<String, String> map;
    private PInformationCallBack mCallBack;
    private String url = MyApplication.HOST + "totalRecordServlet";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    map = mCallBack.map;
                    setData();
                    break;
            }
        }
    };



    public static final String MODIFY_FRIEND = MyApplication.HOST +"ModifyFriendServlet";
    //
    private PopupWindow mPopWindow;
    //弹框
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mAlertDialog;
    private EditText mEditText;
    private int mViewId;
    private Friend mFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_information);

        Intent intent = getIntent();
        //得到个人信息bean
        mUserInfo = (UserInfo) intent.getExtras().get("UserInfo");
        ButterKnife.bind(this);


       // initPopupWindow();
        //得到个人跑步数据总里程数和总时间
        getPersonSumData(mUserInfo.getUid());
        addListener();
    }



    private void getPersonSumData(int uid) {
        OkHttpUtils.post()
                .url(url)
                .addParams("type", "totaldata")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(mCallBack = new PInformationCallBack());
    }

    //显示数据
    private void setData() {
        Glide.with(this)
                .load(mUserInfo.getImageUrl())
                .into(mHeadImg);
        mName.setText(mUserInfo.getNickName());
        mSexAndAge.setText(mUserInfo.getSex() + " " + mUserInfo.getAge());
        mAccount.setText(mUserInfo.getAccount());
        mLocation.setText(mUserInfo.getAddress());
        mSumDistance.setText(map.get("distance"));
        mSumTime.setText(map.get("time"));
        mWhoDynamic.setText(mUserInfo.getNickName()+"的动态");

        mFriend = new Friend();
        mFriend.setMeid(((MyApplication)getApplication()).getUserInfo().getUid());
        mFriend.setFriendid(mUserInfo.getUid());
        mFriend.setRemark(mUserInfo.getNickName());
    }
    //显示PopupWindow
    private void showPopupWindow() {
       //设置contentView
        final View contentView =getLayoutInflater().inflate(R.layout.person_popup,null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        TextView tv1 = (TextView)contentView.findViewById(R.id.beizhu_tv);
        TextView tv2 = (TextView)contentView.findViewById(R.id.shanchu_tv);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);

        //点击其他地方消失
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (contentView != null && contentView.isShown()) {
                    mPopWindow.dismiss();
                }
                return false;
            }
        });
        int x = mTopBar.getWidth()-contentView.getWidth();
        //显示PopupWindow
        mPopWindow.showAsDropDown(mTopBar,x,0);

    }
    private void addListener() {

        //TopBar监听事件
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                PersonInformationActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                showPopupWindow();
            }
        });
        //发送消息
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat
                            (PersonInformationActivity.this, mUserInfo.getAccount(), mUserInfo.getNickName());
                }
                PersonInformationActivity.this.finish();

            }
        });

        //跳转至个人动态
        mDynamicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }




    public class PInformationCallBack extends StringCallback {
        HashMap<String, String> map = new HashMap<>();

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            String[] result = response.split(",");
            map.put("distance", result[0]);
            Log.e("LDDQingQiu", "onResponse: "+ result[0]);
            Long sumTime = Long.valueOf(result[1]);
            Long h = sumTime / (60 * 60 * 1000);
            Long m = (sumTime % (60 * 60 * 1000)) / (60 * 1000);
            Long s = ((sumTime % (60 * 60 * 1000)) % (60 * 1000)) / 1000;
            String time = h + "h" + m + "m" + s + "s";
            map.put("time", time);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            //修改备注
            case R.id.beizhu_tv:{

                mPopWindow.dismiss();
                mViewId = R.id.beizhu_tv;

                mDialogBuilder = new AlertDialog.Builder(this);

                mEditText = new EditText(this);
                String text = mUserInfo.getNickName();
                mEditText.setText(text);
                //最多输入6个字符
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                //文本显示的位置在EditText的最上方
                mEditText.setGravity(Gravity.TOP);
                //改变默认的单行模式
                mEditText.setSingleLine(false);
                //水平滚动设置为False
                mEditText.setHorizontallyScrolling(false);
                mEditText.setTextSize(20);
                if (text != null && !text.equals("")) {
                    //光标在文字末尾
                    mEditText.setSelection(text.length());
                }
                mEditText.setBackgroundResource(R.drawable.ic_editext);

                mDialogBuilder.setView(mEditText);

                showAlertDialog("remark");

            }
            break;
            //删除好友
            case R.id.shanchu_tv:{

                mPopWindow.dismiss();
                mViewId = R.id.shanchu_tv;
                mDialogBuilder = new AlertDialog.Builder(this);
                //Toast.makeText(PersonInformationActivity.this, "删除", Toast.LENGTH_SHORT).show();
                showAlertDialog("delete");


            }
            break;

        }
    }

    private void showAlertDialog(String flag) {
        if (flag.equals("delete")){
            mDialogBuilder.setTitle("确定删除好友？");

        }else {
            mDialogBuilder.setTitle("修改备注");
        }
        mDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mViewId) {
                    case R.id.beizhu_tv:
                        Toast.makeText(PersonInformationActivity.this, "修改ok", Toast.LENGTH_SHORT).show();
                        mFriend.setRemark(mEditText.getText().toString());
                        modifyFriend("remark");
                        break;
                    case R.id.shanchu_tv:

                        modifyFriend("delete");
                        break;
                }
            }
        });
        mDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mAlertDialog = mDialogBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();

    }

    private void modifyFriend(final String flag) {
        Log.e("Ezio123", "modifyFriend: "+new Gson().toJson(mFriend) );
        OkHttpUtils
                .post()
                .url(MODIFY_FRIEND)
                .addParams("flag",flag)
                .addParams("friend", new Gson().toJson(mFriend))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("Eizo123: ", "PersonInformationActivity:" + response);
                        if (flag.equals("remark")){
                            mName.setText(mFriend.getRemark());
                            Toast.makeText(PersonInformationActivity.this, "修改备注成功", Toast.LENGTH_SHORT).show();

                        }else if (flag.equals("delete")){
                            Toast.makeText(PersonInformationActivity.this, "删除好友成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PersonInformationActivity.this,MainActivity.class);
                            setResult(123,intent);
                            PersonInformationActivity.this.finish();
                        }else {
                            Toast.makeText(PersonInformationActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
